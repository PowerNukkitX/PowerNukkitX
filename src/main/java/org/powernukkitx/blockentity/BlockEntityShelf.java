package org.powernukkitx.blockentity;

import org.powernukkitx.block.shelf.AbstractBlockShelf;
import org.powernukkitx.inventory.ContainerInventory;
import org.powernukkitx.inventory.ShelfInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.ItemHelper;

/**
 * @author Buddelbubi
 * @since 2025/11/07
 */
public class BlockEntityShelf extends BlockEntitySpawnableContainer {

    public BlockEntityShelf(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();

        this.inventory = new ShelfInventory(this);

        ListTag<CompoundTag> itemsTag = this.getNbt().getList("Items", CompoundTag.class);
        for (int i = 0; i < itemsTag.size(); i++) {
            if (i >= this.getSize()) break;
            this.inventory.setItemInternal(i, ItemHelper.read(itemsTag.get(i)));
        }

        if (itemsTag.size() == 0) {
            this.nbt.remove("Items");
        }

        this.scheduleComparatorOutputUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        ListTag<CompoundTag> items = new ListTag<>();
        boolean hasItems = false;

        for (int index = 0; index < this.getSize(); index++) {
            Item item = this.inventory.getItem(index);
            if (!item.isNull() && item.getCount() > 0) {
                hasItems = true;
            }
            items.add(ItemHelper.write(item));
        }

        if (hasItems) {
            this.nbt.putList("Items", items);
        } else {
            this.nbt.remove("Items");
        }
    }

    public int getSize() {
        return getInventory().getSize();
    }

    @Override
    protected ContainerInventory requireContainerInventory() {
        return new ShelfInventory(this);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock() instanceof AbstractBlockShelf;
    }

    @Override
    public void setDirty() {
        this.saveNBT();
        this.spawnToAll();
        super.setDirty();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = super.getSpawnCompound();
        ListTag<CompoundTag> items = new ListTag<>();
        for (int i = 0; i < getSize(); i++) {
            Item item = this.inventory.getItem(i);
            items.add(ItemHelper.write(item, i));
        }
        tag.putList("Items", items);
        return tag;
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        for (Item content : getInventory().getContents().values()) {
            level.dropItem(this, content);
        }
        this.getInventory().clearAll();
    }

    @Override
    public String getName() {
        return "Shelf";
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("You cannot name a Shelf");
    }

    @Override
    public boolean hasName() {
        return false;
    }

    @Override
    public ShelfInventory getInventory() {
        return (ShelfInventory) this.inventory;
    }
}
