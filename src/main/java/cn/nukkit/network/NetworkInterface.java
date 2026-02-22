package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.connection.BedrockPong;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.NetworkState;
import oshi.hardware.NetworkIF;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Interfaces for network classes
 *
 * @see cn.nukkit.event.network.NetworkRegisterEvent
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

    BedrockSession getSession(InetSocketAddress address);

    void replaceSessionAddress(InetSocketAddress oldAddress, InetSocketAddress newAddress, BedrockSession newSession);

    void onSessionDisconnect(InetSocketAddress address);

    int getNetworkLatency(Player player);

    void blockAddress(InetAddress address);

    void blockAddress(InetAddress address, int timeout);

    void unblockAddress(InetAddress address);

    boolean isAddressBlocked(InetSocketAddress address);

    BedrockPong getPong();
}