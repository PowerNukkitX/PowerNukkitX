package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUndyedShulkerBox;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.ShulkerBoxInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.ItemHelper;

import java.util.HashSet;

/**
 * @author PetteriM1
 */
public class BlockEntityShulkerBox extends BlockEntitySpawnable implements BlockEntityInventoryHolder {

    protected ShulkerBoxInventory inventory;

    public BlockEntityShulkerBox(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.inventory = new ShulkerBoxInventory(this);

        if (!this.nbt.contains("Items") || !(this.nbt.get("Items") instanceof ListTag<?>)) {
            this.nbt.putList("Items", new ListTag<>(Tag.TAG_Compound));
        }

        final ListTag<CompoundTag> list = this.getNbt().getList("Items", CompoundTag.class);
        for (CompoundTag compound : list.getAll()) {
            Item item = ItemHelper.read(compound);
            this.inventory.setItemInternal(compound.getByte("Slot"), item);
        }

        if (!this.nbt.contains("facing")) {
            this.nbt.putByte("facing", (byte) 0);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
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
    public boolean isBlockEntityValid() {
        Block block = this.getBlock();
        return block instanceof BlockUndyedShulkerBox;
    }

    public int getSize() {
        return 27;
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
            return Item.AIR;
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
    public BaseInventory getInventory() {
        return this.inventory;
    }

    public ShulkerBoxInventory getRealInventory() {
        return inventory;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Shulker Box";
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.nbt.remove("CustomName");
            return;
        }

        this.nbt.putString("CustomName", name);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = getDefaultCompound(this, SHULKER_BOX)
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Findable", false)
                .putList("Items", this.getNbt().getList("Items", CompoundTag.class))
                .putByte("facing", this.getNbt().getByte("facing"));

        if (this.hasName()) {
            c.put("CustomName", this.nbt.get("CustomName").copy());
        }

        return c;
    }
}
