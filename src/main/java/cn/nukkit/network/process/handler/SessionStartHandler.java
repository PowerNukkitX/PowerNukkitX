package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.protocol.NetworkSettingsPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestNetworkSettingsPacket;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;

public class SessionStartHandler extends BedrockSessionPacketHandler {
    public SessionStartHandler(BedrockSession session) {
        super(session);
    }

    @Override
    public void handle(RequestNetworkSettingsPacket pk) {
        int protocol = pk.protocolVersion;
        if (protocol != ProtocolInfo.CURRENT_PROTOCOL) {
            session.sendPlayStatus(protocol < ProtocolInfo.CURRENT_PROTOCOL ? PlayStatusPacket.LOGIN_FAILED_CLIENT : PlayStatusPacket.LOGIN_FAILED_SERVER, true);
            var message = protocol < ProtocolInfo.CURRENT_PROTOCOL ? "disconnectionScreen.outdatedClient" : "disconnectionScreen.outdatedServer";
            session.close(message);
            return;
        }

        var server = session.getServer();
        if (server.getIPBans().isBanned(session.getAddressString())) {
            String reason = server.getIPBans().getEntires().get(session.getAddressString()).getReason();
            session.close(!reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        }

        var settingsPacket = new NetworkSettingsPacket();
        //FIXME there is no way out there to disable compression
        PacketCompressionAlgorithm algorithm;
        if (Server.getInstance().isEnableSnappy()) {
            algorithm = PacketCompressionAlgorithm.SNAPPY;
        } else {
            algorithm = PacketCompressionAlgorithm.ZLIB;
        }
        settingsPacket.compressionAlgorithm = algorithm;
        settingsPacket.compressionThreshold = 1; // compress everything
        //In raknet version 11, the client does not enable packet compression by default,but the server will tell client what the
        //compression algorithm through NetworkSettingsPacket
        session.sendNetworkSettingsPacket(settingsPacket);
        session.setCompression(algorithm);//so send the NetworkSettingsPacket packet before set the session compression
        this.session.getMachine().fire(SessionState.LOGIN);
    }
}
