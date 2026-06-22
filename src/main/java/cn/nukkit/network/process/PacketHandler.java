package cn.nukkit.network.process;

import cn.nukkit.Server;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

/**
 * @author Kaooot
 */
public interface PacketHandler<T extends BedrockPacket> {

    void handle(T packet, PlayerSessionHolder holder, Server server);
}