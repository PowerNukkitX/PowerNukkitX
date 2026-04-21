package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
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

        Map<Integer, Item> items = new HashMap<>();
        // First we add the items from the left chest
        for (int idx = 0; idx < this.left.getSize(); idx++) {
            if (this.left.getContents().containsKey(idx)) { // Don't forget to skip empty slots!
                items.put(idx, this.left.getContents().get(idx));
            }
        }
        // And them the items from the right chest
        for (int idx = 0; idx < this.right.getSize(); idx++) {
            if (this.right.getContents().containsKey(idx)) { // Don't forget to skip empty slots!
                items.put(idx + this.left.getSize(), this.right.getContents().get(idx)); // idx + this.left.getSize() so we don't overlap left chest items
            }
        }

        this.setContents(items);
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
            final org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket pk1 = new org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket();
            pk1.setBlockPosition(Vector3i.from(this.left.getHolder().getX(), this.left.getHolder().getY(), this.left.getHolder().getZ()));
            pk1.setEventType(1);
            pk1.setEventValue(2);

            Level level = this.left.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.left.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTOPEN);
                level.addChunkPacket((int) this.left.getHolder().getX() >> 4, (int) this.left.getHolder().getZ() >> 4, pk1);
            }

            final org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket pk2 = new org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket();
            pk2.setBlockPosition(Vector3i.from(this.right.getHolder().getX(), this.right.getHolder().getY(), this.right.getHolder().getZ()));
            pk2.setEventType(1);
            pk2.setEventValue(2);

            level = this.right.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.right.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTOPEN);
                level.addChunkPacket((int) this.right.getHolder().getX() >> 4, (int) this.right.getHolder().getZ() >> 4, pk2);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getVisibleViewersCount() == 1) {
            final org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket pk1 = new org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket();
            pk1.setBlockPosition(Vector3i.from(this.right.getHolder().getX(), this.right.getHolder().getY(), this.right.getHolder().getZ()));
            pk1.setEventType(1);
            pk1.setEventValue(0);

            Level level = this.right.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.right.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTCLOSED);
                level.addChunkPacket((int) this.right.getHolder().getX() >> 4, (int) this.right.getHolder().getZ() >> 4, pk1);
            }

            final org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket pk2 = new org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket();
            pk2.setBlockPosition(Vector3i.from(this.left.getHolder().getX(), this.left.getHolder().getY(), this.left.getHolder().getZ()));
            pk2.setEventType(1);
            pk2.setEventValue(0);

            level = this.left.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.left.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTCLOSED);
                level.addChunkPacket((int) this.left.getHolder().getX() >> 4, (int) this.left.getHolder().getZ() >> 4, pk2);
            }
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
