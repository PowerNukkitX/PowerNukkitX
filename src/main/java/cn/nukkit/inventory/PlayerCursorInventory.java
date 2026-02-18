package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;

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

    public Item getUnclonedItem() {
        return getUnclonedItem(0);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> slotTypeMap = super.slotTypeMap();
        slotTypeMap.put(0, ContainerSlotType.CURSOR);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        InventorySlotPacket pk = new InventorySlotPacket();
        pk.setItem(toNetworkItem(this.getUnclonedItem(index)));
        pk.setSlot(index);

        for (Player player : players) {
            int id = SpecialWindowId.CURSOR.getId();
            pk.setContainerId(id);
            pk.setContainerNameData(new FullContainerName(
                    ContainerSlotType.CURSOR,
                    id
            ));
            player.dataPacket(pk);
        }
    }

    @Override
    public void sendContents(Player... players) {
        InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
        int id = SpecialWindowId.CURSOR.getId();
        inventorySlotPacket.setContainerId(id);
        inventorySlotPacket.setItem(toNetworkItem(getUnclonedItem(0)));
        inventorySlotPacket.setSlot(0);
        inventorySlotPacket.setContainerNameData(new FullContainerName(
                ContainerSlotType.CURSOR,
                id
        ));
        Server.broadcastPacket(players, inventorySlotPacket);
    }
}
