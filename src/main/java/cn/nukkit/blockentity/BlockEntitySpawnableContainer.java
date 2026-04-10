package cn.nukkit.blockentity;

import cn.nukkit.block.BlockAir;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.List;

public abstract class BlockEntitySpawnableContainer extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    protected ContainerInventory inventory;


    public BlockEntitySpawnableContainer(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.inventory = requireContainerInventory();
        if (!this.namedTag.containsKey("Items") || !(this.namedTag.get("Items") instanceof LightningStrikeEvent)) {
            this.namedTag = this.namedTag.toBuilder().putList("Items", NbtType.COMPOUND, new ObjectArrayList<>()).build();
        }

        List<NbtMap> list = this.namedTag.getList("Items", NbtType.COMPOUND);
        for (NbtMap compound : list) {
            Item item = ItemHelper.read(compound);
            this.inventory.setItemInternal(compound.getByte("Slot"), item);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            this.getInventory().getViewers().forEach(p -> p.removeWindow(this.getInventory()));
            super.close();
        }
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        inventory.clearAll(); // Stop items from being moved around by another player in the inventory
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag = this.namedTag.toBuilder().putList("Items", NbtType.COMPOUND, new ObjectArrayList<>()).build();
        for (int index = 0; index < this.inventory.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
    }

    protected int getSlotIndex(int index) {
        List<NbtMap> list = this.namedTag.getList("Items", NbtType.COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            NbtMap data = this.namedTag.getList("Items", NbtType.COMPOUND).get(i);
            return ItemHelper.read(data);
        }
    }

    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        NbtMap d = ItemHelper.write(item, index);

        // If item is air or count less than 0, remove the item from the "Items" list
        final List<NbtMap> items = new ObjectArrayList<>(this.namedTag.getList("Items", NbtType.COMPOUND));
        if (item.isNull() || item.getCount() <= 0) {
            if (i >= 0) {
                items.remove(i);
            }
        } else if (i < 0) {
            // If it is less than i, then it is a new item, so we are going to add it at the end of the list
            items.add(d);
        } else {
            // If it is more than i, then it is an update on a inventorySlot, so we are going to overwrite the item in the list
            items.add(i, d);
        }
        this.namedTag = this.namedTag.toBuilder().putList("Items", NbtType.COMPOUND, items).build();
    }

    /**
     * 继承于此类的容器方块实体必须实现此方法
     *
     * @return ContainerInventory
     */
    protected abstract ContainerInventory requireContainerInventory();
}
