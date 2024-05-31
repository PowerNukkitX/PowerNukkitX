package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class ContainerCloseProcessor extends DataPacketProcessor<ContainerClosePacket> {

    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ContainerClosePacket pk) {
        Player $1 = playerHandle.player;
        if (!player.spawned || pk.windowId == SpecialWindowId.PLAYER.getId() && !playerHandle.getInventoryOpen()) {
            return;
        }

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
        ContainerClosePacket $2 = new ContainerClosePacket();
        pk2.wasServerInitiated = false;
        pk2.windowId = pk.windowId;
        player.dataPacket(pk2);
        player.resetInventory();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.CONTAINER_CLOSE_PACKET;
    }
}
