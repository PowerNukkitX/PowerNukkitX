package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockAir;
import org.powernukkitx.inventory.ContainerInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.ItemHelper;
public abstract class BlockEntitySpawnableContainer extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    protected ContainerInventory inventory;


    public BlockEntitySpawnableContainer(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.inventory = requireContainerInventory();
        if (!this.nbt.containsList("Items")) {
            this.nbt.putList("Items", new ListTag<>(Tag.TAG_Compound));
        }

        ListTag<CompoundTag> list = this.getNbt().getList("Items", CompoundTag.class);
        for (CompoundTag compound : list.getAll()) {
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
        this.nbt.putList("Items", new ListTag<>(Tag.TAG_Compound));
        for (int index = 0; index < this.inventory.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = getNbt().getList("Items", CompoundTag.class);
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
            CompoundTag data = getNbt().getList("Items", CompoundTag.class).get(i);
            return ItemHelper.read(data);
        }
    }

    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = ItemHelper.write(item, index);

        // If item is air or count less than 0, remove the item from the "Items" list
        final ListTag<CompoundTag> items = getNbt().getList("Items", CompoundTag.class);
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
        this.nbt.putList("Items", items);
    }

    /**
     * 继承于此类的容器方块实体必须实现此方法
     *
     * @return ContainerInventory
     */
    protected abstract ContainerInventory requireContainerInventory();
}
