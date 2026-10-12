package org.powernukkitx.inventory;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntityChest;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class DoubleChestInventory extends ContainerInventory {
    private final ChestInventory left;
    private final ChestInventory right;

    public DoubleChestInventory(BlockEntityChest left, BlockEntityChest right) {
        super(null, ContainerType.CONTAINER, 27 + 27);

        this.left = left.getRealInventory();
        this.left.setDoubleInventory(this);

        this.right = right.getRealInventory();
        this.right.setDoubleInventory(this);
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerEnumName.LEVEL_ENTITY_CONTAINER);
        }
    }

    @Override
    public BlockEntityChest getHolder() {
        return this.left.getHolder();
    }

    @NotNull
    @Override
    public Item getItem(int index) {
        return index < this.left.getSize() ? this.left.getItem(index) : this.right.getItem(index - this.right.getSize());
    }


    @Override
    public Item getUnclonedItem(int index) {
        return index < this.left.getSize() ? this.left.getUnclonedItem(index) : this.right.getUnclonedItem(index - this.right.getSize());
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return index < this.left.getSize() ? this.left.setItem(index, item, send) : this.right.setItem(index - this.right.getSize(), item, send);
    }

    @Override
    public boolean clear(int index, boolean send) {
        return index < this.left.getSize() ? this.left.clear(index, send) : this.right.clear(index - this.right.getSize(), send);
    }

    @Override
    public Map<Integer, Item> getContents() {
        Map<Integer, Item> contents = new HashMap<>();

        for (int i = 0; i < this.getSize(); i++) {
            contents.put(i, this.getItem(i));
        }

        return contents;
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        if (items.size() > this.size) {
            items.keySet().removeIf(slot -> slot >= this.size);
        }

        for (int i = 0; i < this.size; i++) {
            Item item = items.get(i);
            boolean isSet = false;

            if (item != null) {
                if (i < this.left.size) {
                    isSet = this.left.setItem(i, item);
                } else {
                    isSet = this.right.setItem(i - this.left.size, item);
                }
            }

            if (!isSet) {
                this.clear(i);
            }
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        this.left.viewers.add(who);
        this.right.viewers.add(who);

        if (this.getVisibleViewersCount() == 1) {
            this.left.getHolder().broadcastLidState(true);
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getVisibleViewersCount() == 1) {
            this.left.getHolder().broadcastLidState(false);
        }

        this.left.viewers.remove(who);
        this.right.viewers.remove(who);
        super.onClose(who);
    }

    public ChestInventory getLeftSide() {
        return this.left;
    }

    public ChestInventory getRightSide() {
        return this.right;
    }

    public void sendSlot(Inventory inv, int index, Player... players) {
        final InventorySlotPacket pk = new InventorySlotPacket();
        int i = inv == this.right ? this.left.getSize() + index : index;
        pk.setSlot(this.toNetworkSlot(i));
        pk.setItem(inv.getUnclonedItem(index).toNetwork());

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1) {
                this.close(player);
                continue;
            }
            pk.setContainerID(id);
            pk.setFullContainerName(
                    new FullContainerName(
                            this.getContainerEnumName(pk.getSlot()),
                            id
                    )
            );
            player.sendPacket(pk);
        }
    }
}
