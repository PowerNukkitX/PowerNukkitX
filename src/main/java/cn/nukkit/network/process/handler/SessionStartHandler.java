package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.protocol.NetworkSettingsPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestNetworkSettingsPacket;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import lombok.extern.slf4j.Slf4j;

public class SessionStartHandler extends NetworkSessionPacketHandler {

    private final Runnable onSuccess;

    public SessionStartHandler(NetworkSession session, Runnable onSuccess) {
        super(session);
        this.onSuccess = onSuccess;
    }

    @Override
    public void handle(RequestNetworkSettingsPacket pk) {
        int protocol = pk.protocolVersion;
        if (protocol != ProtocolInfo.CURRENT_PROTOCOL) {
            session.sendPlayStatus(protocol < ProtocolInfo.CURRENT_PROTOCOL ? PlayStatusPacket.LOGIN_FAILED_CLIENT : PlayStatusPacket.LOGIN_FAILED_SERVER, true);
            var message = protocol < ProtocolInfo.CURRENT_PROTOCOL ? "disconnectionScreen.outdatedClient" : "disconnectionScreen.outdatedServer";
            player.close("", message, true);
        }
/*
        if (player.loggedIn) {
            return;
        }
*/
        var settingsPacket = new NetworkSettingsPacket();
        //FIXME there is no way out disable compression
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
        player.getNetworkSession().sendPacketImmediatelyAndCallBack(settingsPacket, () -> {
            player.getPlayerHandle().getNetworkSession().setCompression(algorithm);//so send the NetworkSettingsPacket packet before set the session compression
            this.onSuccess.run();
        });
    }
}
