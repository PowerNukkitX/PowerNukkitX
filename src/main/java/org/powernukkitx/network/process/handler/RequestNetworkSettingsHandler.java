package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.config.legacy.LegacyServerSettings;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestNetworkSettingsHandler implements PacketHandler<RequestNetworkSettingsPacket> {

    private static final ConcurrentHashMap<String, Integer> ATTEMPTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> FIRST_ATTEMPT = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = Server.getInstance().getSettings().networkSettings().maxConnectionsPerIp();
    private static final long TIME_WINDOW = Server.getInstance().getSettings().networkSettings().connectionWindow();

    @Override
    public void handle(RequestNetworkSettingsPacket packet, PlayerSessionHolder holder, Server server) {
        BedrockServerSession session = holder.getSession();
        String ip = holder.getIp();

        long now = System.currentTimeMillis();

        FIRST_ATTEMPT.putIfAbsent(ip, now);

        if (now - FIRST_ATTEMPT.get(ip) > TIME_WINDOW) {
            FIRST_ATTEMPT.put(ip, now);
            ATTEMPTS.put(ip, 0);
        }

        int attempts = ATTEMPTS.merge(ip, 1, Integer::sum);

        if (attempts >= MAX_ATTEMPTS) {
            if (Server.getInstance().getSettings().networkSettings().connectionLimiter()) {
                server.getLogger().info("Server detect too many connection. ip " + ip + " got banned.");
                server.getIPBans().addBan(ip, "Too many connections", null, "Anti-Bot");
                session.close("You have been automatically banned.");
                ATTEMPTS.remove(ip);
                FIRST_ATTEMPT.remove(ip);
            }
            return;
        }

        final int clientNetworkVersion = packet.getClientNetworkVersion();
        final int serverNetworkVersion = NetworkConstants.CODEC.getProtocolVersion();

        if (clientNetworkVersion != serverNetworkVersion) {
            final boolean serverOutdated = clientNetworkVersion > serverNetworkVersion;
            holder.sendPlayStatus(
                serverOutdated ?
                    PlayStatus.LOGIN_FAILED_SERVER_OLD :
                    PlayStatus.LOGIN_FAILED_CLIENT_OLD
            );
            holder.disconnect(
                serverOutdated ?
                    DisconnectFailReason.OUTDATED_SERVER :
                    DisconnectFailReason.OUTDATED_CLIENT
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

        PacketCompressionAlgorithm algorithm =
            Server.getInstance().getSettings().networkSettings().snappy()
                ? PacketCompressionAlgorithm.SNAPPY
                : PacketCompressionAlgorithm.ZLIB;

        NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
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
        String address = ((InetSocketAddress) session.getSocketAddress())
            .getAddress()
            .getHostAddress();

        if (server.getIPBans().isBanned(address)) {
            String reason = server.getIPBans().getEntires().get(address).getReason();
            session.close(!reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return true;
        }
        return false;
    }
}
