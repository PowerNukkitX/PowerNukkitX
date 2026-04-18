package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.selector.args.impl.R;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.PlayerRespawnState;
import org.cloudburstmc.protocol.bedrock.packet.RespawnPacket;

/**
 * @author Kaooot
 */
public class RespawnHandler implements PacketHandler<RespawnPacket> {

    @Override
    public void handle(RespawnPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        if (player.isAlive()) {
            return;
        }
        if (packet.getState().equals(PlayerRespawnState.CLIENT_READY_TO_SPAWN)) {
            final RespawnPacket respawnPacket = new RespawnPacket();
            respawnPacket.setPosition(player.getPosition().toNetwork());
            respawnPacket.setState(PlayerRespawnState.READY_TO_SPAWN);
            respawnPacket.setPlayerRuntimeId(player.getId());

            player.dataPacket(respawnPacket);
        }
    }
}