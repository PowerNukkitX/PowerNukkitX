package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventorySlotPacket;

/**
 * @author CreeperFace
 */
public class PlayerCursorInventory extends BaseInventory {
    PlayerCursorInventory(Player player) {
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
    public void sendContents(Player... players) {
        InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
        inventorySlotPacket.inventoryId = SpecialWindowId.CURSOR_DEPRECATED.getId();
        inventorySlotPacket.item = getItem();
        inventorySlotPacket.slot = 0;
        Server.broadcastPacket(players, inventorySlotPacket);
    }
}
