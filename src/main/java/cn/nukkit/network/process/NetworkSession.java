package cn.nukkit.network.process;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.connection.BedrockServerSession;
import cn.nukkit.network.process.handler.HandshakePacketHandler;
import cn.nukkit.network.process.handler.InGamePacketHandler;
import cn.nukkit.network.process.handler.LoginHandler;
import cn.nukkit.network.process.handler.PrespawnHandler;
import cn.nukkit.network.process.handler.ResourcePackHandler;
import cn.nukkit.network.process.handler.SessionStartHandler;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.player.info.PlayerInfo;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

@Slf4j
public class NetworkSession {
    @Getter
    private final @NotNull BedrockServerSession session;
    @Getter
    private final @NotNull Server server = Server.getInstance();
    @Getter
    private Player player;
    @Getter
    private PlayerHandle handle;
    private PlayerInfo info;
    @Getter
    private final @NotNull StateMachine<NetworkSessionState, NetworkSessionState> machine;

    @Setter
    protected @Nullable PacketHandler packetHandler;
    protected boolean disconnected = false;
    private InetSocketAddress address;

    public void onPlayerCreated(@NotNull Player player) {
        this.player = player;
        this.handle = new PlayerHandle(player);
        this.server.addPlayer(address, player);
    }

    public NetworkSession(BedrockServerSession session) {
        this.session = session;
        this.address = (InetSocketAddress) this.session.getSocketAddress();
        log.debug("creating session {}", session.getPeer().getSocketAddress().toString());
        var cfg = new StateMachineConfig<NetworkSessionState, NetworkSessionState>();

        cfg.configure(NetworkSessionState.START)
                .onExit(this::onSessionStartSuccess)
                .permit(NetworkSessionState.LOGIN, NetworkSessionState.LOGIN);

        cfg.configure(NetworkSessionState.LOGIN).onEntry(() -> this.setPacketHandler(new LoginHandler(this, (info) -> {
                    this.info = info;
                    log.debug("Creating player");

                    var player = this.createPlayer();
                    if (player == null) {
                        this.disconnect("Failed to crate player");
                        return;
                    }
                    this.onPlayerCreated(player);
                })))
                .onExit(this::onServerLoginSuccess)
                .permitIf(NetworkSessionState.ENCRYPTION, NetworkSessionState.ENCRYPTION, () -> Server.getInstance().enabledNetworkEncryption)
                .permit(NetworkSessionState.RESOURCE_PACK, NetworkSessionState.RESOURCE_PACK);

        cfg.configure(NetworkSessionState.ENCRYPTION)
                .onEntry(() -> this.setPacketHandler(new HandshakePacketHandler(this)))
                .permit(NetworkSessionState.RESOURCE_PACK, NetworkSessionState.RESOURCE_PACK);

        cfg.configure(NetworkSessionState.RESOURCE_PACK)
                .onEntry(() -> this.setPacketHandler(new ResourcePackHandler(this)))
                .onExit(this::onServerLoginCompletion)
                .permit(NetworkSessionState.PRE_SPAWN, NetworkSessionState.PRE_SPAWN);

        cfg.configure(NetworkSessionState.PRE_SPAWN)
                .onEntry(() -> {
                    this.setPacketHandler(new PrespawnHandler(this));
                    handle.completeLoginSequence();
                    log.info("start game");
                    handle.doFirstSpawn();
                    log.info("first spawn");
                })
                .onExit(this::onClientSpawned)
                .permit(NetworkSessionState.IN_GAME, NetworkSessionState.IN_GAME);

        cfg.configure(NetworkSessionState.IN_GAME)
                .onEntry(() -> this.setPacketHandler(new InGamePacketHandler(this)))
                .onExit(this::onServerDeath)
                .permit(NetworkSessionState.DEATH, NetworkSessionState.DEATH);

        cfg.configure(NetworkSessionState.DEATH)
                //.onEntry(()->this.setPacketHandler(new DeathHandler()))
                .onExit(this::onClientRespawn)
                .permit(NetworkSessionState.IN_GAME, NetworkSessionState.IN_GAME);


        machine = new StateMachine<>(NetworkSessionState.START, cfg);
        this.setPacketHandler(new SessionStartHandler(this));
    }

    public void flushSendBuffer() {
        this.session.flushSendBuffer();
    }

    public void sendPacketImmediately(DataPacket packet) {
        DataPacketSendEvent ev = new DataPacketSendEvent(this.player, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }/*
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Immediate Outbound {}: {}", this.player.getName(), packet);
        }*/
        this.session.sendPacketImmediately(packet);
    }

    public void sendDataPacket(DataPacket packet) {
        DataPacketSendEvent ev = new DataPacketSendEvent(this.player, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }/*
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", this.player.getName(), packet);
        }*/
        this.session.sendPacket(packet);
    }

    public void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;
        if (immediate) {
            this.sendPacketImmediately(pk);
        } else {
            this.sendDataPacket(pk);
        }
    }

    public void onSessionStartSuccess() {
        log.debug("Waiting for login packet");
    }

    private @Nullable Player createPlayer() {
        try {
            PlayerCreationEvent event = new PlayerCreationEvent(Player.class);
            this.server.getPluginManager().callEvent(event);

            Constructor<? extends Player> constructor = event.getPlayerClass().getConstructor(NetworkSession.class, PlayerInfo.class);
            return constructor.newInstance(this, this.info);
        } catch (Exception e) {
            log.error("Failed to create player", e);
        }
        return null;
    }

    private void onServerLoginSuccess() {
        log.debug("Login completed");
        player.processLogin();
    }

    private void onServerLoginCompletion() {
        handle.setShouldLogin(true);
        if (handle.getPreLoginEventTask().isFinished()) {
            handle.getPreLoginEventTask().onCompletion(player.getServer());
        }
    }

    private void onClientSpawned() {
        /*
        if (handle.player.locallyInitialized) {
            return;
        }
        handle.player.locallyInitialized = true;*/
        handle.onPlayerLocallyInitialized();
        PlayerLocallyInitializedEvent locallyInitializedEvent = new PlayerLocallyInitializedEvent(handle.player);
        handle.player.getServer().getPluginManager().callEvent(locallyInitializedEvent);
    }

    private void onServerDeath() {

    }

    private void onClientRespawn() {

    }

    public void handleDataPacket(DataPacket packet) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (packet instanceof DisconnectPacket d) {
            this.session.close(d.message);
            return;
        }
        DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this.player, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        if (this.packetHandler != null) {
            if (this.packetHandler instanceof InGamePacketHandler i) {
                i.managerHandle(packet);
            } else {
                var method = this.packetHandler.getClass().getMethod("handle", packet.getClass());
                method.setAccessible(true);
                method.invoke(this.packetHandler, packet);
            }
        }
    }

    public void tick() {
        for (var packet : this.session.readPackets()) {
            try {
                this.handleDataPacket(packet);
            } catch (Exception e) {
                log.error("An error occurred whilst handling {} for {}", packet.getClass().getSimpleName(), this.session.getSocketAddress().toString(), e);
                this.disconnect("packet handling error");
            }
        }
        this.session.readPackets();
        if (this.session.getDisconnectReason() != null) {
            this.disconnect(this.session.getDisconnectReason());
        }
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void disconnect(String reason) {
        if (this.disconnected || this.player == null) {
            return;
        }
        this.setPacketHandler(null);
        this.disconnected = true;
        this.player.close(reason);
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public long getPing() {
        return this.session.getPing();
    }

    public void sendPacketImmediatelyAndCallBack(DataPacket packet, Runnable callback) {
        this.session.sendPacketImmediatelyAndCallBack(packet, callback);
    }
}
