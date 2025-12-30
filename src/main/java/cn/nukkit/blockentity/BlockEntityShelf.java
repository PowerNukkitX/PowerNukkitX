package cn.nukkit.blockentity;

import cn.nukkit.block.shelf.AbstractBlockShelf;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.ShelfInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;

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

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList("Items", new ListTag<CompoundTag>());
        }

        ListTag<CompoundTag> itemsTag = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < itemsTag.size(); i++) {
            this.inventory.setItem(i, NBTIO.getItemHelper(itemsTag.get(i)));
        }
        this.level.updateComparatorOutputLevel(this);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList("Items", new ListTag<CompoundTag>());
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
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
        ListTag<CompoundTag> items = new ListTag<>(Tag.TAG_Compound);
        for(int i = 0; i < getSize(); i++) {
            Item item = this.inventory.getItem(i);
            items.add(NBTIO.putItemHelper(item, i));
        }
        tag.put("Items", items);
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
