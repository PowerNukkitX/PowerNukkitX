package cn.nukkit.network.process;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
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
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class NetworkSession {
    @Getter
    private final Server server = Server.getInstance();
    @Getter
    private final Player player;
    @Getter
    private final BedrockServerSession session;
    @Getter
    private final StateMachine<NetworkSessionState, NetworkSessionState> machine;
    @Getter
    private final PlayerHandle handle;
    @Setter
    protected @Nullable PacketHandler packetHandler;

    public NetworkSession(BedrockServerSession session, Player player, PlayerHandle handle) {
        this.session = session;
        this.player = player;
        this.handle = handle;
        log.debug("creating session {}", session.getPeer().getSocketAddress().toString());
        var cfg = new StateMachineConfig<NetworkSessionState, NetworkSessionState>();

        cfg.configure(NetworkSessionState.START)
                .onExit(this::onSessionStartSuccess)
                .permit(NetworkSessionState.LOGIN, NetworkSessionState.LOGIN);

        cfg.configure(NetworkSessionState.LOGIN)
                .onEntry(() -> this.setPacketHandler(new LoginHandler(this)))
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
}
