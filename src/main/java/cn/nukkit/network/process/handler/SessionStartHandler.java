package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

public class SessionStartHandler extends BedrockSessionPacketHandler {
    public SessionStartHandler(BedrockSession session) {
        super(session);
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket pk) {
        int protocol = pk.getProtocolVersion();
        if (protocol != ProtocolInfo.CURRENT_PROTOCOL) {
            session.sendPlayStatus(protocol < ProtocolInfo.CURRENT_PROTOCOL ? 1 : 2, true);
            var message = protocol < ProtocolInfo.CURRENT_PROTOCOL ? "disconnectionScreen.outdatedClient" : "disconnectionScreen.outdatedServer";
            session.close(message);
            return PacketSignal.HANDLED;
        }

        var server = session.getServer();
        if (server.getIPBans().isBanned(session.getAddressString())) {
            String reason = server.getIPBans().getEntires().get(session.getAddressString()).getReason();
            session.close(!reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return PacketSignal.HANDLED;
        }
        session.setCodec(ProtocolInfo.CURRENT_CODEC);

        var settingsPacket = new NetworkSettingsPacket();
        //FIXME there is no way out there to disable compression
        PacketCompressionAlgorithm algorithm;
        if (Server.getInstance().getSettings().networkSettings().snappy()) {
            algorithm = PacketCompressionAlgorithm.SNAPPY;
        } else {
            algorithm = PacketCompressionAlgorithm.ZLIB;
        }
        settingsPacket.setCompressionAlgorithm(algorithm == PacketCompressionAlgorithm.SNAPPY
                ? org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm.SNAPPY
                : org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm.ZLIB);
        settingsPacket.setCompressionThreshold(1); // compress everything
        //In raknet version 11, the client does not enable packet compression by default,but the server will tell client what the
        //compression algorithm through NetworkSettingsPacket
        session.sendNetworkSettingsPacket(settingsPacket);
        session.setCompression(algorithm);//so send the NetworkSettingsPacket packet before set the session compression
        this.session.getMachine().fire(SessionState.LOGIN);
        return PacketSignal.HANDLED;
    }
}
