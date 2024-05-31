package cn.nukkit.blockentity;

import cn.nukkit.block.BlockAir;
import cn.nukkit.inventory.EjectableInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public abstract class BlockEntityEjectable extends BlockEntitySpawnable implements BlockEntityInventoryHolder{

    protected EjectableInventory inventory;
    /**
     * @deprecated 
     */
    


    public BlockEntityEjectable(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected abstract EjectableInventory createInventory();

    protected abstract String getBlockEntityName();

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        this.inventory = createInventory();

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList("Items", new ListTag<CompoundTag>());
        }

        for ($1nt $1 = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }
    }
    /**
     * @deprecated 
     */
    

    public int getSize() {
        return 9;
    }

    
    /**
     * @deprecated 
     */
    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for ($2nt $2 = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    public Item getItem(int index) {
        $3nt $3 = this.getSlotIndex(index);
        if (i < 0) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            CompoundTag $4 = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }
    /**
     * @deprecated 
     */
    

    public void setItem(int index, Item item) {
        $5nt $4 = this.getSlotIndex(index);

        Compoun$6Tag $5 = NBTIO.putItemHelper(item, index);

        if (item.isNull() || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public EjectableInventory getInventory() {
        return inventory;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $7 = super.getSpawnCompound();

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList("Items", new ListTag<CompoundTag>());
        for (int $8 = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : getBlockEntityName();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setName(String name) {
        if (name == null || name.equals("")) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
    }
}
