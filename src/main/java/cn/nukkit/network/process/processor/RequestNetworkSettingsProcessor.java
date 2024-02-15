package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.NetworkSettingsPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestNetworkSettingsPacket;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class RequestNetworkSettingsProcessor extends DataPacketProcessor<RequestNetworkSettingsPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestNetworkSettingsPacket pk) {
        var player = playerHandle.player;
        int protocolVersion = pk.protocolVersion;

        String message;
        if (protocolVersion < ProtocolInfo.CURRENT_PROTOCOL) {
            message = "disconnectionScreen.outdatedClient";
            player.close("", message, true);
            return;
        } else if (protocolVersion > ProtocolInfo.CURRENT_PROTOCOL) {
            message = "disconnectionScreen.outdatedServer";
            player.close("", message, true);
            return;
        }

        if (player.loggedIn) {
            log.warn("the player {} has been loggedIn,Processor RequestNetworkSettingsProcessor", player.getName());
            return;
        }
        var settingsPacket = new NetworkSettingsPacket();
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
        player.getNetworkSession().flush();
        playerHandle.getNetworkSession().setCompression(algorithm);
        player.getNetworkSession().sendNetWorkSettingsPacket(settingsPacket);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET;
    }
}
