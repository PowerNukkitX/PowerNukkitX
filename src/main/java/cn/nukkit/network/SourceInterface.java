package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.network.connection.BedrockServerSession;
import cn.nukkit.network.process.NetworkSession;

import java.net.InetAddress;
import java.net.InetSocketAddress;


/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface SourceInterface {
    int getNetworkLatency(Player player);

    void blockAddress(InetAddress address);

    void blockAddress(InetAddress address, int timeout);

    void unblockAddress(InetAddress address);

    void setNetwork(Network network);

    NetworkSession getSession(InetSocketAddress address);

    void close(Player player);

    void close(Player player, String reason);

    void setName(String name);

    void process();

    void shutdown();
}
