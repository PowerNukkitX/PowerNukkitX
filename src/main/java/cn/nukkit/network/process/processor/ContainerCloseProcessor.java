package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ContainerCloseProcessor extends DataPacketProcessor<ContainerClosePacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ContainerClosePacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || pk.windowId == SpecialWindowId.PLAYER.getId() && !playerHandle.getInventoryOpen()) {
            sendClose(playerHandle, pk);
            return;
        }


        if (playerHandle.getWindowIndex().containsKey(pk.windowId)) {
            if (pk.windowId == SpecialWindowId.PLAYER.getId()) {
                playerHandle.setClosingWindowId(pk.windowId);
                player.getInventory().close(player);
                playerHandle.setInventoryOpen(false);
            } else {
                playerHandle.removeWindow(playerHandle.getWindowIndex().get(pk.windowId));
            }
        }

        if (pk.windowId == -1) {
            player.addWindow(player.getCraftingGrid(), SpecialWindowId.NONE.getId());
        }
        sendClose(playerHandle, pk);
    }

    //Client always wants a response. If not sent, inventores won't open anymore.
    private void sendClose(PlayerHandle playerHandle, ContainerClosePacket pk) {
        Player player = playerHandle.player;
        ContainerClosePacket pk2 = new ContainerClosePacket();
        pk2.wasServerInitiated = false;
        pk2.windowId = pk.windowId;
        Inventory inventory = player.getWindowById(pk.windowId);
        if (inventory != null) {
            pk2.type = inventory.getType();
            player.resetInventory();
        } else {
            pk2.type = pk.type;
        }
        player.dataPacket(pk2);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.CONTAINER_CLOSE_PACKET;
    }
}
