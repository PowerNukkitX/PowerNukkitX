package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.process.DataPacketProcessor;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class InventoryTransactionProcessor extends DataPacketProcessor<InventoryTransactionPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull InventoryTransactionPacket pk) {
        Player player = playerHandle.player;
        if (player.isSpectator()) {
            player.sendAllInventories();
            return;
        }

        if (pk.getTransactionType() != InventoryTransactionType.NORMAL) {
            return;
        }

        var actions = pk.getActions();
        if (actions.size() != 2) {
            return;
        }

        var worldAction = actions.get(0);
        var containerAction = actions.get(1);
        if (worldAction.getSource().getType() != InventorySource.Type.WORLD_INTERACTION
                || worldAction.getSource().getFlag() != InventorySource.Flag.DROP_ITEM
                || containerAction.getSource().getType() != InventorySource.Type.CONTAINER) {
            return;
        }

        int slot = containerAction.getSlot();
        int dropCount = Math.max(0, worldAction.getToItem().getCount() - worldAction.getFromItem().getCount());
        if (dropCount <= 0) {
            dropCount = Math.max(0, containerAction.getFromItem().getCount() - containerAction.getToItem().getCount());
        }
        if (dropCount <= 0) {
            return;
        }

        dropHotBarItemForPlayer(slot, dropCount, player);
    }

    private static void dropHotBarItemForPlayer(int hotbarSlot, int dropCount, Player player) {
        final HumanInventory inventory = player.getInventory();
        Item item = inventory.getItem(hotbarSlot);
        if (item.isNull()) {
            return;
        }

        int c = item.getCount() - dropCount;
        if (c < 0) {
            player.getInventory().sendContents(player);
            log.warn("cannot drop more items than the current amount");
            return;
        }

        PlayerDropItemEvent ev = new PlayerDropItemEvent(player, item);
        player.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            player.getInventory().sendContents(player);
            return;
        }

        if (c == 0) {
            inventory.clear(hotbarSlot);
        } else {
            item.setCount(c);
            inventory.setItem(hotbarSlot, item);
        }
        item.setCount(dropCount);
        player.dropItem(item);
    }

    @Override
    public Class<InventoryTransactionPacket> getPacketClass() {
        return InventoryTransactionPacket.class;
    }
}
