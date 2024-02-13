package cn.nukkit.network.process;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.connection.BedrockServerSession;
import cn.nukkit.network.process.handler.HandshakePacketHandler;
import cn.nukkit.network.process.handler.LoginHandler;
import cn.nukkit.network.process.handler.ResourcePackHandler;
import cn.nukkit.network.process.handler.SessionStartHandler;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

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
    @Setter
    protected @Nullable PacketHandler packetHandler;

    public NetworkSession(BedrockServerSession session, Player player) {
        this.session = session;
        this.player = player;
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
                .onExit(this::onServerLoginCompletion)
                .permit(NetworkSessionState.RESOURCE_PACK, NetworkSessionState.RESOURCE_PACK);

        cfg.configure(NetworkSessionState.RESOURCE_PACK)
                .onEntry(() -> this.setPacketHandler(new ResourcePackHandler(this)))
                .onExit(this::onServerLoginCompletion)
                .permit(NetworkSessionState.SPAWN_SEQUENCE, NetworkSessionState.SPAWN_SEQUENCE);

        cfg.configure(NetworkSessionState.SPAWN_SEQUENCE)
                //.onEntry(()->this.setPacketHandler(new PrespawnHandler()))
                .onExit(this::onServerSpawnSequenceCompletion)
                .permit(NetworkSessionState.RESOURCE_PACK, NetworkSessionState.RESOURCE_PACK);

        cfg.configure(NetworkSessionState.SPAWN)
                //.onEntry(()->this.setPacketHandler(new SpawnResponseHandler()))
                .onExit(this::onClientSpawnResponse)
                .permit(NetworkSessionState.SPAWN_SEQUENCE, NetworkSessionState.SPAWN_SEQUENCE);

        cfg.configure(NetworkSessionState.IN_GAME)
                //.onEntry(()->this.setPacketHandler(new InGameHandler()))
                .onExit(this::onServerDeath)
                .permit(NetworkSessionState.DEATH, NetworkSessionState.DEATH)
                .permit(NetworkSessionState.SPAWN, NetworkSessionState.SPAWN);

        cfg.configure(NetworkSessionState.DEATH)
                //.onEntry(()->this.setPacketHandler(new DeathHandler()))
                .onExit(this::onClientRespawn)
                .permit(NetworkSessionState.SPAWN, NetworkSessionState.SPAWN)
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
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Immediate Outbound {}: {}", this.player.getName(), packet);
        }
        this.session.sendPacketImmediately(packet);
    }

    public void sendDataPacket(DataPacket packet) {
        DataPacketSendEvent ev = new DataPacketSendEvent(this.player, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", this.player.getName(), packet);
        }
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
        var playerHandle = player.getPlayerHandle();
        playerHandle.setShouldLogin(true);
        if (playerHandle.getPreLoginEventTask().isFinished()) {
            playerHandle.getPreLoginEventTask().onCompletion(player.getServer());
        }
    }

    private void onServerSpawnSequenceCompletion() {
        log.debug("StartGamePacket sent");
    }

    private void onClientSpawnResponse() {

    }

    private void onServerDeath() {

    }


    private void onClientRespawn() {

    }
}
