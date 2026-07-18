package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.network.NetworkConstants;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.SessionState;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.data.PlayStatus;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;

import java.net.InetSocketAddress;

/**
 * @author Kaooot
 */
public class RequestNetworkSettingsHandler implements PacketHandler<RequestNetworkSettingsPacket> {

    @Override
    public void handle(RequestNetworkSettingsPacket packet, PlayerSessionHolder holder, Server server) {
        final int clientNetworkVersion = packet.getClientNetworkVersion();
        final int serverNetworkVersion = NetworkConstants.CODEC.getProtocolVersion();
        final BedrockServerSession session = holder.getSession();

        if (clientNetworkVersion != serverNetworkVersion) {
            final boolean serverOutdated = clientNetworkVersion > serverNetworkVersion;
            holder.sendPlayStatus(
                    serverOutdated ?
                            PlayStatus.LOGIN_FAILED_SERVER_OLD : PlayStatus.LOGIN_FAILED_CLIENT_OLD
            );
            holder.disconnect(
                    serverOutdated ? DisconnectFailReason.OUTDATED_SERVER : DisconnectFailReason.OUTDATED_CLIENT
            );
            return;
        }

        if (holder.getState().equals(SessionState.REQUESTED_NETWORK_SETTINGS)) {
            holder.disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
            return;
        }

        if (this.addressBanCheck(server, session)) {
            return;
        }

        holder.setState(SessionState.REQUESTED_NETWORK_SETTINGS);

        final PacketCompressionAlgorithm algorithm = Server.getInstance().getSettings().networkSettings().snappy() ?
                PacketCompressionAlgorithm.SNAPPY : PacketCompressionAlgorithm.ZLIB;
        final NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
        networkSettingsPacket.setCompressionThreshold(1);
        networkSettingsPacket.setCompressionAlgorithm(algorithm);

        session.sendPacketImmediately(networkSettingsPacket);
        session.setCompression(algorithm);

        if (server.getSettings().debugSettings().disableEncodingLimits()) {
            session.getPeer().getCodecHelper().setEncodingSettings(EncodingSettings.UNLIMITED);
        }

        holder.setState(SessionState.LOGIN);
    }

    private boolean addressBanCheck(Server server, BedrockServerSession session) {
        final String address = ((InetSocketAddress) session.getSocketAddress()).getAddress().getHostAddress();
        if (server.getIPBans().isBanned(address)) {
            final String reason = server.getIPBans().getEntires().get(address).getReason();
            session.close(!reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return true;
        }
        return false;
    }
}