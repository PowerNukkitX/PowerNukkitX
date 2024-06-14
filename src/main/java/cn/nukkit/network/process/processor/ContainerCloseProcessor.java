package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class ContainerCloseProcessor extends DataPacketProcessor<ContainerClosePacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ContainerClosePacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || pk.windowId == SpecialWindowId.PLAYER.getId() && !playerHandle.getInventoryOpen()) {
            return;
        }

        Inventory inventory = player.getWindowById(pk.windowId);

        if (playerHandle.getWindowIndex().containsKey(pk.windowId)) {
            if (pk.windowId == SpecialWindowId.PLAYER.getId()) {
                playerHandle.setClosingWindowId(pk.windowId);
                player.getInventory().close(player);
                playerHandle.setInventoryOpen(false);
            } else if (pk.windowId == SpecialWindowId.ENDER_CHEST.getId()) {
                playerHandle.setClosingWindowId(pk.windowId);
                player.getEnderChestInventory().close(player);
            } else {
                playerHandle.removeWindow(playerHandle.getWindowIndex().get(pk.windowId));
            }
        }
        
        if (pk.windowId == -1) {
            player.addWindow(player.getCraftingGrid(), SpecialWindowId.NONE.getId());
        }
        if (inventory != null) {
            ContainerClosePacket pk2 = new ContainerClosePacket();
            pk2.wasServerInitiated = false;
            pk2.windowId = pk.windowId;
            pk2.type = inventory.getType();
            player.dataPacket(pk2);
            player.resetInventory();
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.CONTAINER_CLOSE_PACKET;
    }
}
