package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;

/**
 * @author Kaooot
 */
public class SetLocalPlayerAsInitializedHandler implements PacketHandler<SetLocalPlayerAsInitializedPacket> {

    @Override
    public void handle(SetLocalPlayerAsInitializedPacket packet, PlayerSessionHolder holder, Server server) {
        holder.getPlayer().onPlayerLocallyInitialized();
    }
}