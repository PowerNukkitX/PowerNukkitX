package cn.nukkit.network.connection;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
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
import cn.nukkit.network.protocol.AvailableCommandsPacket;
import cn.nukkit.network.protocol.CreativeContentPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.NetworkSettingsPacket;
import cn.nukkit.network.protocol.PacketHandler;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetCommandsEnabledPacket;
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
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
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
                        this.close("Failed to crate player");
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
        this.peer.sendPacket(this.subClientId, 0, packet);
        this.logOutbound(packet);
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
        BedrockPacketCodec bedrockPacketCodec = this.peer.channel.pipeline().get(BedrockPacketCodec.class);
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
            BedrockPacketCodec bedrockPacketCodec = this.peer.channel.pipeline().get(BedrockPacketCodec.class);
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
            //TODO: structure checking
            pk.commands = data;
            this.sendPacket(pk);
        }
    }

    public void syncCraftingData() {
        this.sendRawPacket(ProtocolInfo.CRAFTING_DATA_PACKET, Registries.RECIPE.getCraftingPacket());
    }

    public void syncCreativeContent() {
        var pk = new CreativeContentPacket();
        pk.entries = Registries.CREATIVE.getCreativeItems();
        this.sendPacket(pk);
    }

    public void syncInventory() {
        var player = getPlayer();
        if (player != null) {
            player.getInventory().sendHeldItem(player);
            player.getInventory().sendContents(player);
            player.getInventory().sendArmorContents(player);
            player.getCursorInventory().sendContents(player);
            player.getOffhandInventory().sendContents(player);
            player.getEnderChestInventory().sendContents(player);
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
}
