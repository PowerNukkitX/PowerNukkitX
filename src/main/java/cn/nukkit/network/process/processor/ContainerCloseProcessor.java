package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.inventory.InventoryCloseEvent;
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

        if (playerHandle.getWindowIndex().containsKey(pk.windowId)) {
            player.getServer().getPluginManager().callEvent(new InventoryCloseEvent(playerHandle.getWindowIndex().get(pk.windowId), player));
            if (pk.windowId == SpecialWindowId.PLAYER.getId()) playerHandle.setInventoryOpen(false);
            playerHandle.setClosingWindowId(pk.windowId);
            playerHandle.removeWindow(playerHandle.getWindowIndex().get(pk.windowId));
            playerHandle.setClosingWindowId(Integer.MIN_VALUE);
        }
        if (pk.windowId == -1) {
            player.resetCraftingGridType();
            player.addWindow(player.getCraftingGrid(), SpecialWindowId.NONE.getId());
            ContainerClosePacket pk2 = new ContainerClosePacket();
            pk2.wasServerInitiated = false;
            pk2.windowId = -1;
            player.dataPacket(pk2);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.CONTAINER_CLOSE_PACKET;
    }
}
