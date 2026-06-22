package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.packet.PlayerHotbarPacket;

/**
 * @author Kaooot
 */
public class PlayerHotbarHandler implements PacketHandler<PlayerHotbarPacket> {

    @Override
    public void handle(PlayerHotbarPacket packet, PlayerSessionHolder holder, Server server) {
        if (packet.getContainerID() != ContainerId.INVENTORY) {
            return; //In PE this should never happen
        }
        holder.getPlayer().getInventory().equipItem(packet.getSelectedSlot());
    }
}