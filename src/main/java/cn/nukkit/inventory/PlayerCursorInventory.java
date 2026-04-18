package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;

import java.util.Map;

/**
 * @author CreeperFace
 */
public class PlayerCursorInventory extends BaseInventory {
    public PlayerCursorInventory(Player player) {
        super(player, ContainerType.INVENTORY, 1);
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
        Map<Integer, ContainerEnumName> slotTypeMap = super.slotTypeMap();
        slotTypeMap.put(0, ContainerEnumName.CURSOR_CONTAINER);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        final InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
        inventorySlotPacket.setContainerID(ContainerId.UI);
        inventorySlotPacket.setSlot(index);
        inventorySlotPacket.setItem(this.getUnclonedItem(index).toNetwork());
        inventorySlotPacket.setFullContainerName(
                new FullContainerName(
                        ContainerEnumName.CURSOR_CONTAINER,
                        null
                )
        );
        Server.broadcastPacket(players, inventorySlotPacket);
    }

    @Override
    public void sendContents(Player... players) {
        final InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
        inventorySlotPacket.setContainerID(ContainerId.UI);
        inventorySlotPacket.setSlot(0);
        inventorySlotPacket.setItem(this.getUnclonedItem(0).toNetwork());
        inventorySlotPacket.setFullContainerName(
                new FullContainerName(
                        ContainerEnumName.CURSOR_CONTAINER,
                        null
                )
        );
        Server.broadcastPacket(players, inventorySlotPacket);
    }
}