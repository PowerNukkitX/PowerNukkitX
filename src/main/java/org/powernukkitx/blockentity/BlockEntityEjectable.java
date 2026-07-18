package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockAir;
import org.powernukkitx.inventory.EjectableInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.ItemHelper;

public abstract class BlockEntityEjectable extends BlockEntitySpawnable implements BlockEntityInventoryHolder {

    protected EjectableInventory inventory;


    public BlockEntityEjectable(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected abstract EjectableInventory createInventory();

    protected abstract String getBlockEntityName();

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.inventory = createInventory();

        if (!this.nbt.containsList("Items", Tag.TAG_Compound)) {
            this.nbt.putList("Items", new ListTag<>(Tag.TAG_Compound));
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }
    }

    public int getSize() {
        return 9;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.getNbt().getList("Items", CompoundTag.class);
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
            CompoundTag data = this.getNbt().getList("Items", CompoundTag.class).get(i);
            return ItemHelper.read(data);
        }
    }

    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = ItemHelper.write(item, index);

        final ListTag<CompoundTag> items = this.getNbt().getList("Items", CompoundTag.class);
        if (item.isNull() || item.getCount() <= 0) {
            if (i >= 0) {
                items.remove(i);
            }
        } else if (i < 0) {
            items.add(d);
        } else {
            items.add(i, d);
        }
        this.nbt.putList("Items", items);
    }

    @Override
    public EjectableInventory getInventory() {
        return inventory;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = super.getSpawnCompound();

        if (this.hasName()) {
            c.put("CustomName", this.nbt.get("CustomName").copy());
        }

        return c;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putList("Items", new ListTag<>(Tag.TAG_Compound));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : getBlockEntityName();
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            this.nbt.remove("CustomName");
            return;
        }

        this.nbt.putString("CustomName", name);
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
    }
}
