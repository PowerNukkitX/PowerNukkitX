package org.powernukkitx.network;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.event.network.NetworkRegisterEvent;
import org.powernukkitx.network.process.NetworkState;
import org.powernukkitx.network.security.BotnetDetector;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import oshi.hardware.NetworkIF;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Interfaces for network classes
 *
 * @see NetworkRegisterEvent
 * @since 20/02/2026
 */
public interface NetworkInterface {

    NetworkState getState();

    void setState(NetworkState state);

    void shutdown();

    double getUpload();

    double getDownload();

    void resetStatistics();

    void process();

    void processInterfaces();

    Server getServer();

    @Nullable
    List<NetworkIF> getHardWareNetworkInterfaces();

    BedrockServerSession getSession(InetSocketAddress address);

    void replaceSessionAddress(InetSocketAddress oldAddress, InetSocketAddress newAddress, BedrockServerSession newSession);

    void onSessionDisconnect(InetSocketAddress address);

    int getNetworkLatency(Player player);

    void blockAddress(InetAddress address);

    void blockAddress(InetAddress address, int timeout);

    void unblockAddress(InetAddress address);

    boolean isAddressBlocked(InetSocketAddress address);

    BedrockPong getPong();

    void updatePong(BedrockPong pong);

    BotnetDetector getBotnetDetector();
}