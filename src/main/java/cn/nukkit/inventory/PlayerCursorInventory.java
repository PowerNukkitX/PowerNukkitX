package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;

/**
 * @author CreeperFace
 */
public class PlayerCursorInventory extends BaseInventory {
    public PlayerCursorInventory(Player player) {
        super(player, InventoryType.INVENTORY, 1);
    }

    /**
     * This override is here for documentation and code completion purposes only.
     *
     * @return Player
     */
    @Override
    public Player getHolder() {
        return (Player) super.getHolder();
    }

    public Item getItem() {
        return getItem(0);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> slotTypeMap = super.slotTypeMap();
        slotTypeMap.put(0, ContainerSlotType.CURSOR);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        InventorySlotPacket pk = new InventorySlotPacket();
        pk.item = this.getUnclonedItem(index);
        pk.slot = index;

        for (Player player : players) {
            pk.inventoryId = SpecialWindowId.CURSOR.getId();
            player.dataPacket(pk);
        }
    }

    @Override
    public void sendContents(Player... players) {
        InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
        inventorySlotPacket.inventoryId = SpecialWindowId.CURSOR.getId();
        inventorySlotPacket.item = getUnclonedItem(0);
        inventorySlotPacket.slot = 0;
        Server.broadcastPacket(players, inventorySlotPacket);
    }
}
