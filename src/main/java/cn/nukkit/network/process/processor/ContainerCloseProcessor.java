package cn.nukkit.network.process.processor;

import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class ContainerCloseProcessor extends DataPacketProcessor<ContainerClosePacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ContainerClosePacket pk) {
        Player player = playerHandle.getPlayer();
        if (!player.isSpawned() || pk.windowId == ContainerIds.INVENTORY && !playerHandle.getInventoryOpen()) {
            return;
        }

        if (playerHandle.getWindowIndex().containsKey(pk.windowId)) {
            new InventoryCloseEvent(playerHandle.getWindowIndex().get(pk.windowId), player).call();
            if (pk.windowId == ContainerIds.INVENTORY) playerHandle.setInventoryOpen(false);
            playerHandle.setClosingWindowId(pk.windowId);
            playerHandle.removeWindow(playerHandle.getWindowIndex().get(pk.windowId), true);
            playerHandle.setClosingWindowId(Integer.MIN_VALUE);
        }
        if (pk.windowId == -1) {
            player.craftingType = Player.CRAFTING_SMALL;
            player.resetCraftingGridType();
            player.addWindow(player.getCraftingGrid(), ContainerIds.NONE);
            ContainerClosePacket pk2 = new ContainerClosePacket();
            pk2.wasServerInitiated = false;
            pk2.windowId = -1;
            player.sendPacket(pk2);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.CONTAINER_CLOSE_PACKET);
    }
}
