package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;

/**
 * @author Kaooot
 */
public class ContainerCloseHandler implements PacketHandler<ContainerClosePacket> {

    @Override
    public void handle(ContainerClosePacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();
        if (!player.spawned || packet.getContainerID() == ContainerId.INVENTORY && !player.isInventoryOpen()) {
            this.sendClose(player, packet);
            player.setClosingWindowId(Integer.MIN_VALUE);
            return;
        }


        if (player.getWindowIndex().containsKey(packet.getContainerID())) {
            if (packet.getContainerID() == ContainerId.INVENTORY) {
                player.setClosingWindowId(packet.getContainerID());
                player.getInventory().close(player);
                player.setInventoryOpen(false);
            } else {
                player.removeWindow(player.getWindowIndex().get(packet.getContainerID()));
            }
        }

        if (packet.getContainerID() == -1) {
            player.addWindow(player.getCraftingGrid(), (byte) ContainerId.NONE);
        }
        sendClose(player, packet);
        player.setClosingWindowId(Integer.MIN_VALUE);
    }

    //Client always wants a response. If not sent, inventores won't open anymore.
    private void sendClose(Player player, ContainerClosePacket pk) {
        ContainerClosePacket pk2 = new ContainerClosePacket();
        pk2.setServerInitiatedClose(false);
        pk2.setContainerID(pk.getContainerID());
        Inventory inventory = player.getWindowById(pk.getContainerID());
        if (inventory != null) {
            pk2.setContainerType(inventory.getType());
            player.resetInventory();
        } else {
            pk2.setContainerType(pk.getContainerType());
        }
        player.dataPacket(pk2);
    }
}