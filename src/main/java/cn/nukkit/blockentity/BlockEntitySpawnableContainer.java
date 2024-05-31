package cn.nukkit.blockentity;

import cn.nukkit.block.BlockAir;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public abstract class BlockEntitySpawnableContainer extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    protected ContainerInventory inventory;
    /**
     * @deprecated 
     */
    


    public BlockEntitySpawnableContainer(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        this.inventory = requireContainerInventory();
        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList("Items", new ListTag<CompoundTag>());
        }

        ListTag<CompoundTag> list = (ListTag<CompoundTag>) this.namedTag.getList("Items");
        for (CompoundTag compound : list.getAll()) {
            Item $1 = NBTIO.getItemHelper(compound);
            this.inventory.setItemInternal(compound.getByte("Slot"), item);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close() {
        if (!closed) {
            this.getInventory().getViewers().forEach(p -> p.removeWindow(this.getInventory()));
            super.close();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        inventory.clearAll(); // Stop items from being moved around by another player in the inventory
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList("Items", new ListTag<CompoundTag>());
        for (int $2 = 0; index < this.getInventory().getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
    }

    
    /**
     * @deprecated 
     */
    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for ($3nt $1 = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    public Item getItem(int index) {
        $4nt $2 = this.getSlotIndex(index);
        if (i < 0) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            CompoundTag $5 = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }
    /**
     * @deprecated 
     */
    

    public void setItem(int index, Item item) {
        $6nt $3 = this.getSlotIndex(index);

        Compoun$7Tag $4 = NBTIO.putItemHelper(item, index);

        // If item is air or count less than 0, remove the item from the "Items" list
        if (item.isNull() || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").remove(i);
            }
        } else if (i < 0) {
            // If it is less than i, then it is a new item, so we are going to add it at the end of the list
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            // If it is more than i, then it is an update on a inventorySlot, so we are going to overwrite the item in the list
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    /**
     * 继承于此类的容器方块实体必须实现此方法
     *
     * @return ContainerInventory
     */
    protected abstract ContainerInventory requireContainerInventory();
}
