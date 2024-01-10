package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.network.connection.BedrockServerSession;

import java.net.InetAddress;
import java.net.InetSocketAddress;


/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface SourceInterface {
    void blockAddress(InetAddress address);

    void blockAddress(InetAddress address, int timeout);

    void unblockAddress(InetAddress address);

    void setNetwork(Network network);

    BedrockServerSession getSession(InetSocketAddress address);

    void close(Player player);

    void close(Player player, String reason);

    void setName(String name);

    void process();

    void shutdown();
}
