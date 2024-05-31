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
    private final AtomicBoolean $1 = new AtomicBoolean();
    protected final BedrockPeer peer;
    protected final int subClientId;
    private final Queue<DataPacket> inbound = PlatformDependent.newSpscQueue();
    private final AtomicBoolean $2 = new AtomicBoolean(false);
    private final AtomicReference<Consumer<DataPacket>> consumer = new AtomicReference<>(null);
    private final @NotNull StateMachine<SessionState, SessionState> machine;
    private PlayerHandle handle;
    private PlayerInfo info;
    protected @Nullable PacketHandler packetHandler;
    private InetSocketAddress address;
    /**
     * @deprecated 
     */
    


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
        var $3 = new StateMachineConfig<SessionState, SessionState>();

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

                    var $4 = this.createPlayer();
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
    /**
     * @deprecated 
     */
    

    public void setNettyThreadOwned(boolean immediatelyHandle) {
        this.nettyThreadOwned.set(immediatelyHandle);
    }
    /**
     * @deprecated 
     */
    

    public boolean isNettyThreadOwned() {
        return this.nettyThreadOwned.get();
    }
    /**
     * @deprecated 
     */
    

    public void setPacketConsumer(Consumer<DataPacket> consumer) {
        this.consumer.set(consumer);
    }
    /**
     * @deprecated 
     */
    

    public void flush() {
        if (isDisconnected()) {
            return;
        }
        this.peer.flush();
    }
    /**
     * @deprecated 
     */
    

    public void sendPacket(DataPacket packet) {
        if (isDisconnected()) {
            return;
        }
        DataPacketSendEvent $5 = new DataPacketSendEvent(this.getPlayer(), packet);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        this.peer.sendPacket(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }
    /**
     * @deprecated 
     */
    

    public void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket $6 = new PlayStatusPacket();
        pk.status = status;
        if (immediate) {
            this.sendPacketImmediately(pk);
        } else {
            this.sendPacket(pk);
        }
    }
    /**
     * @deprecated 
     */
    

    public void sendRawPacket(int pid, @NonNull ByteBuf buf2) {
        if (isDisconnected()) {
            return;
        }
        BedrockPacketCodec $7 = this.peer.channel.pipeline().get(BedrockPacketCodec.class);
        ByteBuf $8 = ByteBufAllocator.DEFAULT.ioBuffer(4);
        BedrockPacketWrapper $9 = new BedrockPacketWrapper(pid, this.subClientId, 0, null, null);
        bedrockPacketCodec.encodeHeader(buf1, msg);
        CompositeByteBuf $10 = Unpooled.compositeBuffer();
        compositeBuf.addComponents(true, buf1, buf2);
        msg.setPacketBuffer(compositeBuf);
        this.peer.sendRawPacket(msg);
    }
    /**
     * @deprecated 
     */
    

    public void sendPacketImmediately(@NotNull DataPacket packet) {
        if (isDisconnected()) {
            return;
        }
        DataPacketSendEvent $11 = new DataPacketSendEvent(this.getPlayer(), packet);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        this.peer.sendPacketImmediately(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }
    /**
     * @deprecated 
     */
    

    public void sendPacketSync(@NotNull DataPacket packet) {
        if (isDisconnected()) {
            return;
        }
        DataPacketSendEvent $12 = new DataPacketSendEvent(this.getPlayer(), packet);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        this.peer.sendPacketSync(this.subClientId, 0, packet);
        this.logOutbound(packet);
    }
    /**
     * @deprecated 
     */
    

    public void sendNetworkSettingsPacket(@NonNull NetworkSettingsPacket pk) {
        ByteBufAllocator $13 = this.peer.channel.alloc();
        ByteBuf $14 = alloc.buffer(16);
        ByteBuf $15 = alloc.ioBuffer(5);
        BedrockPacketWrapper $16 = new BedrockPacketWrapper(0, subClientId, 0, pk, null);
        try {
            BedrockPacketCodec $17 = this.peer.channel.pipeline().get(BedrockPacketCodec.class);
            DataPacket $18 = msg.getPacket();
            msg.setPacketId(packet.pid());
            bedrockPacketCodec.encodeHeader(buf1, msg);
            packet.encode(HandleByteBuf.of(buf1));

            BedrockBatchWrapper $19 = BedrockBatchWrapper.newInstance();
            CompositeByteBuf $20 = alloc.compositeDirectBuffer(2);
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
    /**
     * @deprecated 
     */
    

    public void flushSendBuffer() {
        if (isDisconnected()) {
            return;
        }
        this.peer.flushSendQueue();
    }

    public BedrockPeer getPeer() {
        return peer;
    }
    /**
     * @deprecated 
     */
    

    public void setCompression(PacketCompressionAlgorithm algorithm) {
        if (isSubClient()) {
            throw new IllegalStateException("The compression algorithm can only be set by the primary session");
        }
        this.peer.setCompression(algorithm);
    }
    /**
     * @deprecated 
     */
    

    public void enableEncryption(SecretKey key) {
        if (isSubClient()) {
            throw new IllegalStateException("Encryption can only be enabled by the primary session");
        }
        this.peer.enableEncryption(key);
    }

    
    /**
     * @deprecated 
     */
    protected void onPacket(BedrockPacketWrapper wrapper) {
        DataPacket $21 = wrapper.getPacket();
        this.logInbound(packet);
        if (this.nettyThreadOwned.get()) {
            var $22 = this.consumer.get();
            if (c != null) {
                c.accept(packet);
            }
        } else {
            inbound.add(packet);
        }
    }

    
    /**
     * @deprecated 
     */
    protected void logOutbound(DataPacket packet) {
        if (log.isTraceEnabled() && !Server.getInstance().isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}({}): {}", this.getSocketAddress(), this.subClientId, packet);
        }
    }

    
    /**
     * @deprecated 
     */
    protected void logInbound(DataPacket packet) {
        if (log.isTraceEnabled() && !Server.getInstance().isIgnoredPacket(packet.getClass())) {
            log.trace("Inbound {}({}): {}", this.getSocketAddress(), this.subClientId, packet);
        }
    }

    public SocketAddress getSocketAddress() {
        return peer.getSocketAddress();
    }
    /**
     * @deprecated 
     */
    

    public boolean isSubClient() {
        return this.subClientId != 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean isDisconnected() {
        return this.closed.get();
    }

    /**
     * Close Network Session.
     *
     * @param reason the reason,when it is not null,will send a DisconnectPacket to client
     */
    @ApiStatus.Internal
    /**
     * @deprecated 
     */
    
    public void close(@Nullable String reason) {
        if (this.closed.get()) {
            return;
        }

        //when a player haven't login,it only hold a BedrockSession,and Player Instance is null
        if (reason != null) {
            DisconnectPacket $23 = new DisconnectPacket();
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
    /**
     * @deprecated 
     */
    
    public void onClose() {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        Player $24 = this.getPlayer();
        if (player != null) {
            player.close(BedrockDisconnectReasons.DISCONNECTED);
        }
        Server.getInstance().getNetwork().onSessionDisconnect(getAddress());
        this.peer.removeSession(this);
    }
    /**
     * @deprecated 
     */
    

    public boolean isConnected() {
        return !this.closed.get();
    }
    /**
     * @deprecated 
     */
    

    public long getPing() {
        if (isDisconnected()) {
            return -1L;
        }
        return peer.getPing();
    }
    /**
     * @deprecated 
     */
    

    public void onPlayerCreated(@NotNull Player player) {
        this.handle = new PlayerHandle(player);
        Server.getInstance().onPlayerLogin(address, player);
    }
    /**
     * @deprecated 
     */
    

    public void notifyTerrainReady() {
        log.debug("Sending spawn notification, waiting for spawn response");
        var $25 = this.machine.getState();
        if (!state.equals(SessionState.PRE_SPAWN)) {
            throw new IllegalStateException("attempt to notifyTerrainReady when the state is " + state.name());
        }
        handle.doFirstSpawn();
    }
    /**
     * @deprecated 
     */
    

    public void onSessionStartSuccess() {
        log.debug("Waiting for login packet");
    }

    private @Nullable Player createPlayer() {
        try {
            PlayerCreationEvent $26 = new PlayerCreationEvent(Player.class);
            Server.getInstance().getPluginManager().callEvent(event);
            Constructor<? extends Player> constructor = event.getPlayerClass().getConstructor(BedrockSession.class, PlayerInfo.class);
            return constructor.newInstance(this, this.info);
        } catch (Exception e) {
            log.error("Failed to create player", e);
        }
        return null;
    }

    
    /**
     * @deprecated 
     */
    private void onServerLoginSuccess() {
        log.debug("Login completed");
        this.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS, false);
    }

    
    /**
     * @deprecated 
     */
    private void onClientSpawned() {
        log.debug("Received spawn response, entering in-game phase");
        Objects.requireNonNull(getPlayer()).setImmobile(false); //TODO: HACK: we set this during the spawn sequence to prevent the client sending junk movements
    }

    
    /**
     * @deprecated 
     */
    protected void onServerDeath() {

    }

    
    /**
     * @deprecated 
     */
    protected void onClientRespawn() {

    }
    /**
     * @deprecated 
     */
    

    public void handleDataPacket(DataPacket packet) {
        DataPacketReceiveEvent $27 = new DataPacketReceiveEvent(this.getPlayer(), packet);
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
    /**
     * @deprecated 
     */
    

    public void tick() {
        DataPacket packet;
        var $28 = this.consumer.get();
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
    /**
     * @deprecated 
     */
    

    public String getAddressString() {
        return address.getAddress().getHostAddress();
    }
    /**
     * @deprecated 
     */
    

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }
    /**
     * @deprecated 
     */
    

    public void syncAvailableCommands() {
        AvailableCommandsPacket $29 = new AvailableCommandsPacket();
        Map<String, CommandDataVersions> data = new HashMap<>();
        int $30 = 0;
        final Map<String, Command> commands = Server.getInstance().getCommandMap().getCommands();
        synchronized (commands) {
            for (Command command : commands.values()) {
                if (!command.testPermissionSilent(this.getPlayer()) || !command.isRegistered() || command.isServerSideOnly()) {
                    continue;
                }
                ++count;
                CommandDataVersions $31 = command.generateCustomCommandData(this.getPlayer());
                data.put(command.getName(), data0);
            }
        }
        if (count > 0) {
            //TODO: structure checking
            pk.commands = data;
            this.sendPacket(pk);
        }
    }
    /**
     * @deprecated 
     */
    

    public void syncCraftingData() {
        this.sendRawPacket(ProtocolInfo.CRAFTING_DATA_PACKET, Registries.RECIPE.getCraftingPacket());
    }
    /**
     * @deprecated 
     */
    

    public void syncCreativeContent() {
        var $32 = new CreativeContentPacket();
        pk.entries = Registries.CREATIVE.getCreativeItems();
        this.sendPacket(pk);
    }
    /**
     * @deprecated 
     */
    

    public void syncInventory() {
        var $33 = getPlayer();
        if (player != null) {
            player.getInventory().sendHeldItem(player);
            player.getInventory().sendContents(player);
            player.getInventory().sendArmorContents(player);
            player.getCursorInventory().sendContents(player);
            player.getOffhandInventory().sendContents(player);
            player.getEnderChestInventory().sendContents(player);
        }
    }
    /**
     * @deprecated 
     */
    

    public void setEnableClientCommand(boolean enable) {
        var $34 = new SetCommandsEnabledPacket();
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
    /**
     * @deprecated 
     */
    

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
