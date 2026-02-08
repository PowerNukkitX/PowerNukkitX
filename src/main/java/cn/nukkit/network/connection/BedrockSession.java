package cn.nukkit.network.connection;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandOverload;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.DataPacketDecodeEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.inventory.CreativeOutputInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBundle;
import cn.nukkit.network.connection.netty.BedrockBatchWrapper;
import cn.nukkit.network.connection.netty.BedrockPacketWrapper;
import cn.nukkit.network.connection.netty.codec.packet.BedrockPacketCodec;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.process.handler.HandshakePacketHandler;
import cn.nukkit.network.process.handler.InGamePacketHandler;
import cn.nukkit.network.process.handler.LoginHandler;
import cn.nukkit.network.process.handler.ResourcePackHandler;
import cn.nukkit.network.process.handler.SessionStartHandler;
import cn.nukkit.network.process.handler.SpawnResponseHandler;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.network.protocol.types.PlayerInfo;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.ByteBufVarInt;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
public class BedrockSession {
    private final AtomicBoolean closed = new AtomicBoolean();
    protected final BedrockPeer peer;
    protected final int subClientId;
    private final Queue<DataPacket> inbound = PlatformDependent.newSpscQueue();
    private final AtomicBoolean nettyThreadOwned = new AtomicBoolean(false);
    private final AtomicReference<Consumer<DataPacket>> consumer = new AtomicReference<>(null);
    private final @NotNull StateMachine<SessionState, SessionState> machine;
    private PlayerHandle handle;
    private PlayerInfo info;
    protected @Nullable PacketHandler packetHandler;
    private InetSocketAddress address;
    @Getter
    protected boolean authenticated = false;
    @Getter @Setter
    protected int protocolVersion;

    /* ---------------- Pacing heavy packets, reduce bursting and esure client sync ------------- */
    private final boolean pacingEnabled;
    private final OutboundScheduler scheduler;

    public BedrockSession(BedrockPeer peer, int subClientId) {
        this.peer = peer;
        this.subClientId = subClientId;
        // from session start to log in sequence complete, netty threads own the session
        this.setNettyThreadOwned(true);

        this.setPacketConsumer((pk) -> {
            try {
                this.handleDataPacket(pk);
            } catch (Exception e) {
                log.error("An error occurred whilst handling {} for {}", pk.getClass().getSimpleName(), this.getSocketAddress().toString(), e);
            }
        });

        this.address = (InetSocketAddress) this.getSocketAddress();
        log.debug("creating session {}", getPeer().getSocketAddress().toString());

        /* ---- Load pacing settings safely ---- */
        PacingConfig pc = loadPacingConfigSafely();
        this.pacingEnabled = pc.enabled;
        int pacingFlushIntervalMillis = pc.flushMs;
        int pacingMaxBytesPerSecond = pc.maxBytesPerSec;
        this.scheduler = new OutboundScheduler(
                pacingMaxBytesPerSecond,
                1200,
                256,
                pacingFlushIntervalMillis
        );

        var cfg = new StateMachineConfig<SessionState, SessionState>();

        cfg.configure(SessionState.START)
                .onExit(this::onSessionStartSuccess)
                .permit(SessionState.LOGIN, SessionState.LOGIN);

        cfg.configure(SessionState.LOGIN).onEntry(() -> this.setPacketHandler(new LoginHandler(this, (info) -> {
                    this.info = info;
                })))
                .onExit(this::onServerLoginSuccess)
                .permitIf(SessionState.ENCRYPTION, SessionState.ENCRYPTION, () -> Server.getInstance().enabledNetworkEncryption)
                .permit(SessionState.RESOURCE_PACK, SessionState.RESOURCE_PACK);

        cfg.configure(SessionState.ENCRYPTION)
                .onEntry(() -> {
                    log.debug("Player {} enter ENCRYPTION stage", getPeer().getSocketAddress().toString());
                    this.setPacketHandler(new HandshakePacketHandler(this));
                })
                .permit(SessionState.RESOURCE_PACK, SessionState.RESOURCE_PACK);

        cfg.configure(SessionState.RESOURCE_PACK)
                .onEntry(() -> {
                    log.debug("Player {} enter RESOURCE_PACK stage", getPeer().getSocketAddress().toString());
                    this.setPacketHandler(new ResourcePackHandler(this));
                })
                .permit(SessionState.PRE_SPAWN, SessionState.PRE_SPAWN);

        cfg.configure(SessionState.PRE_SPAWN)
                .onEntry(() -> {
                    // now the main thread owns the session
                    this.setNettyThreadOwned(false);

                    log.debug("Creating player");

                    var player = this.createPlayer();
                    if (player == null) {
                        this.close("Failed to create player");
                        return;
                    }
                    this.onPlayerCreated(player);
                    player.processLogin();
                    this.setPacketHandler(new SpawnResponseHandler(this));
                    // The reason why teleport player to their position is for gracefully client-side spawn,
                    // although we need some hacks, It is definitely a fairly worthy trade.
                    handle.player.setImmobile(true); //TODO: HACK: fix client-side falling pre-spawn
                })
                .onExit(this::onClientSpawned)
                .permit(SessionState.IN_GAME, SessionState.IN_GAME);

        cfg.configure(SessionState.IN_GAME)
                .onEntry(() -> this.setPacketHandler(new InGamePacketHandler(this)))
                .onExit(this::onServerDeath)
                .permit(SessionState.DEATH, SessionState.DEATH);

        cfg.configure(SessionState.DEATH)
                //.onEntry(()->this.setPacketHandler(new DeathHandler()))
                .onExit(this::onClientRespawn)
                .permit(SessionState.IN_GAME, SessionState.IN_GAME);

        machine = new StateMachine<>(SessionState.START, cfg);
        this.setPacketHandler(new SessionStartHandler(this));
    }

    public void setNettyThreadOwned(boolean immediatelyHandle) {
        this.nettyThreadOwned.set(immediatelyHandle);
    }

    public boolean isNettyThreadOwned() {
        return this.nettyThreadOwned.get();
    }

    public void setPacketConsumer(Consumer<DataPacket> consumer) {
        this.consumer.set(consumer);
    }

    public void flush() {
        if (isDisconnected()) {
            return;
        }
        this.peer.flush();
    }

    public void sendPacket(DataPacket packet) {
        if (isDisconnected()) {
            return;
        }
        DataPacketSendEvent ev = new DataPacketSendEvent(this.getPlayer(), packet);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        // If pacing is off or this packet is not one of the critical bulk types, behave as usual
        if (!this.pacingEnabled || !isPacedBulk(packet)) {
            this.peer.sendPacket(this.subClientId, 0, packet);
            this.logOutbound(packet);
            return;
        }

        // Only pace critical bulk (RP chunks, creative/item registry).
        int est = estimateBytes(packet);
        if (packet.pid() == ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET) {
            this.scheduler.enqueueResourcePackChunk(packet, est);
        } else {
            this.scheduler.enqueueRegistryBulk(packet, est);
        }
        this.scheduler.tryPump(this.peer, this.subClientId, this);
    }

    public void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;
        if (immediate) {
            this.sendPacketImmediately(pk);
        } else {
            this.sendPacket(pk);
        }
    }

    public void sendRawPacket(int pid, @NonNull ByteBuf buf2) {
        if (isDisconnected()) {
            return;
        }
        BedrockPacketCodec bedrockPacketCodec = getPacketCodec();

        ByteBuf buf1 = ByteBufAllocator.DEFAULT.ioBuffer(4);
        BedrockPacketWrapper msg = new BedrockPacketWrapper(pid, this.subClientId, 0, null, null);
        bedrockPacketCodec.encodeHeader(buf1, msg);
        CompositeByteBuf compositeBuf = Unpooled.compositeBuffer();
        compositeBuf.addComponents(true, buf1, buf2);
        msg.setPacketBuffer(compositeBuf);
        this.peer.sendRawPacket(msg);
    }

    public void sendPacketImmediately(@NotNull DataPacket packet) {
        if (isDisconnected()) {
            return;
        }
        DataPacketSendEvent ev = new DataPacketSendEvent(this.getPlayer(), packet);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        this.peer.sendPacketImmediately(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }

    public void sendPacketSync(@NotNull DataPacket packet) {
        if (isDisconnected()) {
            return;
        }
        DataPacketSendEvent ev = new DataPacketSendEvent(this.getPlayer(), packet);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        this.peer.sendPacketSync(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }

    public void sendNetworkSettingsPacket(@NonNull NetworkSettingsPacket pk) {
        ByteBufAllocator alloc = this.peer.channel.alloc();
        ByteBuf buf1 = alloc.buffer(16);
        ByteBuf header = alloc.ioBuffer(5);
        BedrockPacketWrapper msg = new BedrockPacketWrapper(0, subClientId, 0, pk, null);
        try {
            BedrockPacketCodec bedrockPacketCodec = getPacketCodec();
            DataPacket packet = msg.getPacket();
            msg.setPacketId(packet.pid());
            bedrockPacketCodec.encodeHeader(buf1, msg);
            packet.encode(HandleByteBuf.of(buf1));

            BedrockBatchWrapper batch = BedrockBatchWrapper.newInstance();
            CompositeByteBuf buf2 = alloc.compositeDirectBuffer(2);
            ByteBufVarInt.writeUnsignedInt(header, buf1.readableBytes());
            buf2.addComponent(true, header);
            buf2.addComponent(true, buf1);
            batch.setCompressed(buf2);
            this.peer.channel.writeAndFlush(batch);
        } catch (Throwable t) {
            log.error("Error send", t);
        } finally {
            msg.release();
        }
    }

    public void flushSendBuffer() {
        if (isDisconnected()) {
            return;
        }
        this.peer.flushSendQueue();
    }

    public BedrockPeer getPeer() {
        return peer;
    }

    public void setCompression(PacketCompressionAlgorithm algorithm) {
        if (isSubClient()) {
            throw new IllegalStateException("The compression algorithm can only be set by the primary session");
        }
        this.peer.setCompression(algorithm);
    }

    public void enableEncryption(SecretKey key) {
        if (isSubClient()) {
            throw new IllegalStateException("Encryption can only be enabled by the primary session");
        }
        this.peer.enableEncryption(key);
    }

    protected void onPacket(BedrockPacketWrapper wrapper) {
        DataPacket packet = wrapper.getPacket();
        this.logInbound(packet);

        DataPacketDecodeEvent ev = new DataPacketDecodeEvent(this.getPlayer(), wrapper);
        Server.getInstance().getPluginManager().callEvent(ev);

        int predictMaxBuffer = switch (ev.getPacketId()) {
            case ProtocolInfo.LOGIN_PACKET -> 10_000_000;
            case ProtocolInfo.PLAYER_SKIN_PACKET -> 5_000_000;
            default -> 25_000;
        };
        if (ev.getPacketBuffer().length() > predictMaxBuffer) {
            ev.setCancelled();
        }

        if (ev.isCancelled())
            return;

        if (this.nettyThreadOwned.get()) {
            var c = this.consumer.get();
            if (c != null) {
                c.accept(packet);
            }
        } else {
            inbound.add(packet);
        }
    }

    protected void logOutbound(DataPacket packet) {
        if (log.isTraceEnabled() && !Server.getInstance().isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}({}): {}", this.getSocketAddress(), this.subClientId, packet);
        }
    }

    protected void logInbound(DataPacket packet) {
        if (log.isTraceEnabled() && !Server.getInstance().isIgnoredPacket(packet.getClass())) {
            log.trace("Inbound {}({}): {}", this.getSocketAddress(), this.subClientId, packet);
        }
    }

    public SocketAddress getSocketAddress() {
        return peer.getSocketAddress();
    }

    public boolean isSubClient() {
        return this.subClientId != 0;
    }

    public boolean isDisconnected() {
        return this.closed.get();
    }

    /**
     * Close Network Session.
     *
     * @param reason the reason,when it is not null,will send a DisconnectPacket to client
     */
    @ApiStatus.Internal
    public void close(@Nullable String reason) {
        if (this.closed.get()) {
            return;
        }

        //when a player haven't login,it only hold a BedrockSession,and Player Instance is null
        if (reason != null) {
            DisconnectPacket packet = new DisconnectPacket();
            packet.message = reason;
            this.sendPacketImmediately(packet);
        }

        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            if (isSubClient()) {
                // FIXME: Do sub-clients send a server-bound DisconnectPacket?
            } else {
                // Primary sub-client controls the connection
                this.peer.close();
            }
        }, 5);
    }

    /**
     * Player disconnection process
     * <p>
     * 1.BedrockSession#close -> channel#disconnect -> channelInactive-> BedrockPeer#onClose -> all BedrockSession#onClose -> tickFuture#cancel -> free
     * <p>
     * 2.onRakNetDisconnect -> channel#disconnect -> channelInactive-> BedrockPeer#onClose -> all BedrockSession#onClose -> tickFuture#cancel -> free
     * <p>
     * 3.Player#close -> BedrockSession#close
     */
    public void onClose() {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        Player player = this.getPlayer();
        if (player != null) {
            player.close(BedrockDisconnectReasons.DISCONNECTED);
        }
        Server.getInstance().getNetwork().onSessionDisconnect(getAddress());
        this.peer.removeSession(this);
    }

    public boolean isConnected() {
        return !this.closed.get();
    }

    public long getPing() {
        if (isDisconnected()) {
            return -1L;
        }
        return peer.getPing();
    }

    public void onPlayerCreated(@NotNull Player player) {
        this.handle = new PlayerHandle(player);
        Server.getInstance().onPlayerLogin(address, player);
    }

    public void notifyTerrainReady() {
        log.debug("Sending spawn notification, waiting for spawn response");
        var state = this.machine.getState();
        if (!state.equals(SessionState.PRE_SPAWN)) {
            throw new IllegalStateException("attempt to notifyTerrainReady when the state is " + state.name());
        }
        handle.doFirstSpawn();
    }

    public void onSessionStartSuccess() {
        log.debug("Waiting for login packet");
    }

    private @Nullable Player createPlayer() {
        try {
            PlayerCreationEvent event = new PlayerCreationEvent(Player.class);
            Server.getInstance().getPluginManager().callEvent(event);
            Constructor<? extends Player> constructor = event.getPlayerClass().getConstructor(BedrockSession.class, PlayerInfo.class);
            return constructor.newInstance(this, this.info);
        } catch (Exception e) {
            log.error("Failed to create player", e);
        }
        return null;
    }

    private void onServerLoginSuccess() {
        log.debug("Login completed");
        this.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS, false);
    }

    private void onClientSpawned() {
        log.debug("Received spawn response, entering in-game phase");
        Objects.requireNonNull(getPlayer()).setImmobile(false); //TODO: HACK: we set this during the spawn sequence to prevent the client sending junk movements
    }

    protected void onServerDeath() {

    }

    protected void onClientRespawn() {

    }

    public void handleDataPacket(DataPacket packet) {
        DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this.getPlayer(), packet);
        Server.getInstance().getPluginManager().callEvent(ev);

        if (ev.isCancelled())
            return;

        if (this.packetHandler == null)
            return;

        if (this.packetHandler instanceof InGamePacketHandler i) {
            i.managerHandle(packet);
        } else {
            packet.handle(this.packetHandler);
        }
    }

    public void tick() {
        DataPacket packet;
        var c = this.consumer.get();
        if (c != null) {
            while ((packet = this.inbound.poll()) != null) {
                c.accept(packet);
            }
        } else {
            this.inbound.clear();
        }

        if (this.pacingEnabled) {
            boolean sent = this.scheduler.pump(this.peer, this.subClientId, this);
            if (sent) {
                this.peer.flushSendQueue();
            }
        }
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getAddressString() {
        return address.getAddress().getHostAddress();
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public void syncAvailableCommands() {
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        Map<String, CommandDataVersions> data = new HashMap<>();
        int count = 0;
        final Map<String, Command> commands = Server.getInstance().getCommandMap().getCommands();
        synchronized (commands) {
            for (Command command : commands.values()) {
                if (!command.testPermissionSilent(this.getPlayer()) || !command.isRegistered() || command.isServerSideOnly()) {
                    continue;
                }
                ++count;
                CommandDataVersions data0 = command.generateCustomCommandData(this.getPlayer());
                data.put(command.getName(), data0);
            }
        }
        if (count > 0) {
            Map<String, CommandDataVersions> filtered = getStringCommandDataVersionsMap(data);

            if (!filtered.isEmpty()) {
                pk.commands = filtered;
                this.sendPacket(pk);
            }
        }

    }

    private @NotNull Map<String, CommandDataVersions> getStringCommandDataVersionsMap(Map<String, CommandDataVersions> data) {
        Map<String, CommandDataVersions> filtered = new HashMap<>();

        for (Map.Entry<String, CommandDataVersions> entry : data.entrySet()) {
            CommandDataVersions versions = entry.getValue();
            if (versions == null) continue;

            if (versions.versions == null || versions.versions.isEmpty()) continue;

            boolean valid = false;

            for (CommandData v : versions.versions) {
                if (v == null) continue;
                if (v.overloads == null || v.overloads.isEmpty()) continue;

                boolean overloadValid = false;
                for (CommandOverload overload : v.overloads.values()) {
                    if (overload == null) continue;

                    overloadValid = true;
                    break;
                }

                if (overloadValid) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                filtered.put(entry.getKey(), versions);
            }
        }
        return filtered;
    }

    public void syncCraftingData() {
        this.sendRawPacket(ProtocolInfo.CRAFTING_DATA_PACKET, Registries.RECIPE.getCraftingPacket());
    }

    public void syncCreativeContent() {
        var pk = new CreativeContentPacket();

        this.sendPacket(pk);
    }

    public void syncInventory() {
        Player player = getPlayer();
        if (player != null) {
            player.getInventory().sendHeldItem(player);
            player.getInventory().sendContents(player);
            player.getInventory().sendArmorContents(player);
            player.getCursorInventory().sendContents(player);
            player.getOffhandInventory().sendContents(player);
            player.getEnderChestInventory().sendContents(player);

            //Send bundle content
            PlayerHandle handle = new PlayerHandle(player);
            handle.getWindows().keySet().stream().filter(inv -> !(inv instanceof CreativeOutputInventory)).forEach(inventory -> {
                for (int index : inventory.getContents().keySet()) {
                    Item item = inventory.getUnclonedItem(index);
                    if (item instanceof ItemBundle bundle) {
                        if (bundle.hasCompoundTag()) {
                            bundle.onChange(inventory);
                            inventory.sendSlot(index, player);
                        }
                    }
                }
            });
        }
    }

    public void setEnableClientCommand(boolean enable) {
        var pk = new SetCommandsEnabledPacket();
        pk.enabled = enable;
        this.sendPacket(pk);
        if (enable) {
            this.syncAvailableCommands();
        }
    }

    public void setAuthenticated() {
        authenticated = true;
    }

    public @NotNull Server getServer() {
        return Server.getInstance();
    }

    @Nullable
    public Player getPlayer() {
        return this.handle == null ? null : this.handle.player;
    }

    public PlayerHandle getHandle() {
        return this.handle;
    }

    public @NotNull StateMachine<SessionState, SessionState> getMachine() {
        return this.machine;
    }

    public void setPacketHandler(@javax.annotation.Nullable final PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    @Nullable
    public DataPacketManager getDataPacketManager() {
        if (packetHandler != null && packetHandler instanceof InGamePacketHandler inGamePacketHandler) {
            return inGamePacketHandler.getManager();
        } else {
            return null;
        }
    }

    /* --------------------- Helpers (only pace critical bulk) --------------------- */
    private static boolean isPacedBulk(DataPacket pk) {
        int id = pk.pid();
        return  id == ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET     // RP zip chunks (big)
            ||  id == ProtocolInfo.CREATIVE_CONTENT_PACKET             // creative items list
            ||  id == ProtocolInfo.ITEM_REGISTRY_PACKET;               // item components/registry
    }

    private static int estimateBytes(DataPacket pk) {
        if (pk instanceof ResourcePackChunkDataPacket rp) {
            return (rp.data != null ? rp.data.length : 64 * 1024);
        }
        switch (pk.pid()) {
            case ProtocolInfo.CREATIVE_CONTENT_PACKET:        return 192 * 1024;
            case ProtocolInfo.ITEM_REGISTRY_PACKET:           return 8 * 1024;
            default:                                          return 256;
        }
    }

    private static final class PacingConfig {
        final boolean enabled;
        final int flushMs;
        final int maxBytesPerSec;
        PacingConfig(boolean en, int f, int bps) {
            this.enabled = en;
            this.flushMs = f;
            this.maxBytesPerSec = bps;
        }
    }

    private PacingConfig loadPacingConfigSafely() {
        var net = Server.getInstance().getSettings().networkSettings();

        boolean en = net.pacingEnabled();
        int flush = clamp(net.pacingFlushIntervalMillis(), 1, 20);
        int bps = clamp(net.pacingMaxBytesPerSecond(), 64 * 1024, 64 * 1024 * 1024); // allow up to 64 MiB/s

        return new PacingConfig(en, flush, bps);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /**
     * Minimal per-session outbound scheduler (bytes-only):
     * - token bucket for bytes/sec
     * - micro-batching on a short flush interval
     * - two bulk lanes: RP chunks (highest) and registry/creative (next)
     */
    private static final class OutboundScheduler {
        private final int maxBytesPerSec;
        private final int burstBytes;
        private final int coalesceTargetBytes;
        private final int coalesceMinBytes;
        private final int flushIntervalMillis;
        private boolean reschedulePending = false;

        private double byteTokens;
        private long lastRefillNs = System.nanoTime();
        private long nextFlushNs = lastRefillNs;

        private final Deque<DataPacket> rpQ = new ArrayDeque<>();
        private final Deque<DataPacket> registryQ = new ArrayDeque<>();

        private int headBytes = 0;

        OutboundScheduler(int maxBytesPerSec,
                          int coalesceTargetBytes,
                          int coalesceMinBytes,
                          int flushIntervalMillis) {
            this.maxBytesPerSec = Math.max(1024, maxBytesPerSec);
            this.burstBytes = this.maxBytesPerSec; // burst = 1 second budget
            this.coalesceTargetBytes = coalesceTargetBytes;
            this.coalesceMinBytes = coalesceMinBytes;
            this.flushIntervalMillis = Math.max(1, flushIntervalMillis);
            this.byteTokens = this.burstBytes;
        }

        synchronized void enqueueResourcePackChunk(DataPacket pk, int estimatedBytes) {
            this.rpQ.addLast(pk);
            this.headBytes += Math.max(estimatedBytes, 32);
        }

        synchronized void enqueueRegistryBulk(DataPacket pk, int estimatedBytes) {
            this.registryQ.addLast(pk);
            this.headBytes += Math.max(estimatedBytes, 32);
        }

        synchronized void tryPump(BedrockPeer peer, int subClientId, BedrockSession session) {
            long now = System.nanoTime();
            boolean haveBulk = !this.rpQ.isEmpty() || !this.registryQ.isEmpty();
            if (this.headBytes >= this.coalesceTargetBytes || now >= this.nextFlushNs || haveBulk) {
                if (pump(peer, subClientId, session)) {
                    peer.flushSendQueue();
                } else if (!reschedulePending) {
                    reschedulePending = true;
                    peer.channel.eventLoop().schedule(() -> {
                        synchronized (OutboundScheduler.this) {
                            reschedulePending = false;
                            if (pump(peer, subClientId, session)) {
                                peer.flushSendQueue();
                            }
                        }
                    }, this.flushIntervalMillis, TimeUnit.MILLISECONDS);
                }
            }
        }

        synchronized boolean pump(BedrockPeer peer, int subClientId, BedrockSession session) {
            refill();
            long now = System.nanoTime();

            int allowBytes = (int) Math.floor(this.byteTokens);
            if (allowBytes <= 0) return false;

            boolean haveBulk = !this.rpQ.isEmpty() || !this.registryQ.isEmpty();
            if (haveBulk) {
                if (now < nextFlushNs && headBytes < coalesceMinBytes) {
                    return false;
                }
            }
            nextFlushNs = now + (long) flushIntervalMillis * 1_000_000L;

            int sentBytes = 0;

            while (sentBytes < allowBytes) {
                DataPacket next;

                if (!this.rpQ.isEmpty()) {
                    next = this.rpQ.peekFirst();
                } else if (!this.registryQ.isEmpty()) {
                    next = this.registryQ.peekFirst();
                } else {
                    break;
                }

                int est = BedrockSession.estimateBytes(next);
                if (est + sentBytes > allowBytes) break;

                peer.sendPacket(subClientId, 0, next);
                session.logOutbound(next);

                if (!this.rpQ.isEmpty() && next == this.rpQ.peekFirst()) {
                    this.rpQ.removeFirst();
                } else if (!this.registryQ.isEmpty() && next == this.registryQ.peekFirst()) {
                    this.registryQ.removeFirst();
                }

                sentBytes += est;
            }

            this.byteTokens -= sentBytes;
            this.headBytes = Math.max(0, this.headBytes - sentBytes);
            return sentBytes > 0;
        }

        private void refill() {
            long now = System.nanoTime();
            double secs = (now - lastRefillNs) / 1_000_000_000.0;
            this.byteTokens = Math.min(burstBytes, this.byteTokens + secs * maxBytesPerSec);
            this.lastRefillNs = now;
        }
    }

    private BedrockPacketCodec getPacketCodec() {
        io.netty.channel.ChannelHandler h = this.peer.channel.pipeline().get(BedrockPacketCodec.NAME);
        if (h instanceof BedrockPacketCodec) {
            return (BedrockPacketCodec) h;
        }
        for (java.util.Map.Entry<String, io.netty.channel.ChannelHandler> e : this.peer.channel.pipeline()) {
            if (e.getValue() instanceof BedrockPacketCodec) {
                return (BedrockPacketCodec) e.getValue();
            }
        }
        throw new IllegalStateException("BedrockPacketCodec not found in channel pipeline");
    }

    public void nudgePacer() {
        if (!this.pacingEnabled) return;
        this.scheduler.tryPump(this.peer, this.subClientId, this);
    }
}