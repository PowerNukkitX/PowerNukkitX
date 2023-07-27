package cn.nukkit.network.process.processor;

import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class EntityEventProcessor extends DataPacketProcessor<EntityEventPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull EntityEventPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }
        if (player.craftingType != Player.CRAFTING_ANVIL && pk.event != EntityEventPacket.ENCHANT) {
            player.craftingType = Player.CRAFTING_SMALL;
            // player.resetCraftingGridType();
        }

        if (pk.event == EntityEventPacket.EATING_ITEM) {
            if (pk.data == 0 || pk.eid != player.getId()) {
                return;
            }

            pk.eid = player.getId();
            pk.isEncoded = false;

            player.dataPacket(pk);
            Server.broadcastPacket(player.getViewers().values(), pk);
        } else if (pk.event == EntityEventPacket.ENCHANT) {
            if (pk.eid != player.getId()) {
                return;
            }

            Inventory inventory = player.getWindowById(Player.ANVIL_WINDOW_ID);
            if (inventory instanceof AnvilInventory anvilInventory) {
                anvilInventory.setCost(-pk.data);
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.ENTITY_EVENT_PACKET);
    }
}
