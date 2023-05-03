package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.NetworkSettingsPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestNetworkSettingsPacket;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import org.jetbrains.annotations.NotNull;

public class RequestNetworkSettingsProcessor extends DataPacketProcessor<RequestNetworkSettingsPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestNetworkSettingsPacket pk) {
        var player = playerHandle.player;
        if (player.loggedIn) {
            return;
        }
        var protocolVersion = pk.protocolVersion;
        String message;
        var settingsPacket = new NetworkSettingsPacket();
        settingsPacket.compressionAlgorithm = PacketCompressionAlgorithm.ZLIB;
        settingsPacket.compressionThreshold = 1; // compress everything
        player.forceDataPacket(settingsPacket, () -> playerHandle.getNetworkSession().setCompression(CompressionProvider.ZLIB));
        if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(protocolVersion)) {
            if (protocolVersion < ProtocolInfo.CURRENT_PROTOCOL) {
                message = "disconnectionScreen.outdatedClient";
            } else {
                message = "disconnectionScreen.outdatedServer";
            }
            player.close("", message, true);
            return;
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET);
    }
}
