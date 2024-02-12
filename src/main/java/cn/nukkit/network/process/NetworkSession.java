package cn.nukkit.network.process;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.connection.BedrockServerSession;
import cn.nukkit.network.process.handler.LoginHandler;
import cn.nukkit.network.process.handler.ResourcePackHandler;
import cn.nukkit.network.process.handler.SessionStartHandler;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
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
    @Setter
    protected @Nullable PacketHandler packetHandler;

    public NetworkSession(BedrockServerSession session, Player player) {
        this.session = session;
        this.player = player;
        log.debug("creating session {}", session.getPeer().getSocketAddress().toString());
        this.setPacketHandler(new SessionStartHandler(this, this::onSessionStartSuccess));
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
        this.setPacketHandler(new LoginHandler(this, this::onServerLoginSuccess));
    }

    private void onServerLoginSuccess() {
        log.debug("Login completed");

        player.processLogin();
        log.debug("Initiating resource packs phase");
        this.setPacketHandler(new ResourcePackHandler(this, () -> {
            var playerHandle = player.getPlayerHandle();
            playerHandle.setShouldLogin(true);
            if (playerHandle.getPreLoginEventTask().isFinished()) {
                playerHandle.getPreLoginEventTask().onCompletion(player.getServer());
            }
        }));
    }
}
