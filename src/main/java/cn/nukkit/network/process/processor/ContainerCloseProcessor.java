package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ContainerCloseProcessor extends DataPacketProcessor<ContainerClosePacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ContainerClosePacket pk) {
        Player player = playerHandle.player;
        int windowId = pk.getId();
        if (!player.spawned || windowId == SpecialWindowId.PLAYER.getId() && !playerHandle.getInventoryOpen()) {
            sendClose(playerHandle, pk);
            return;
        }


        if (playerHandle.getWindowIndex().containsKey(windowId)) {
            if (windowId == SpecialWindowId.PLAYER.getId()) {
                playerHandle.setClosingWindowId(windowId);
                player.getInventory().close(player);
                playerHandle.setInventoryOpen(false);
            } else {
                playerHandle.removeWindow(playerHandle.getWindowIndex().get(windowId));
            }
        }

        if (windowId == -1) {
            player.addWindow(player.getCraftingGrid(), SpecialWindowId.NONE.getId());
        }
        sendClose(playerHandle, pk);
    }

    //Client always wants a response. If not sent, inventores won't open anymore.
    private void sendClose(PlayerHandle playerHandle, ContainerClosePacket pk) {
        Player player = playerHandle.player;
        int windowId = pk.getId();
        ContainerClosePacket pk2 = new ContainerClosePacket();
        pk2.setServerInitiated(false);
        pk2.setId((byte) windowId);
        Inventory inventory = player.getWindowById(windowId);
        if (inventory != null) {
            pk2.setType(ContainerType.from(inventory.getType().getNetworkType()));
            player.resetInventory();
        } else {
            pk2.setType(pk.getType());
        }
        player.dataPacket(pk2);
    }
    @Override
    public Class<ContainerClosePacket> getPacketClass() {
        return ContainerClosePacket.class;
    }
}
