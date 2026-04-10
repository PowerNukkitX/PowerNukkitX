package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.PlayerHotbarPacket;

/**
 * @author Kaooot
 */
public class PlayerHotbarHandler implements PacketHandler<PlayerHotbarPacket> {

    @Override
    public void handle(PlayerHotbarPacket packet, PlayerSessionHolder holder, Server server) {
        if (packet.getContainerID() != SpecialWindowId.PLAYER.getId()) {
            return; //In PE this should never happen
        }
        holder.getPlayer().getInventory().equipItem(packet.getSelectedSlot());
    }
}