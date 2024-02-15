package cn.nukkit.blockentity;

import cn.nukkit.api.DoNotModify;
import cn.nukkit.block.BlockChiseledBookshelf;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.List;

public class BlockEntityChiseledBookshelf extends BlockEntitySpawnable {
    public static final String LAST_INTERACTED_SLOT = "LastInteractedSlot";
    private Integer lastInteractedSlot;
    private Item[] items;

    public BlockEntityChiseledBookshelf(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock() instanceof BlockChiseledBookshelf;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        addBookshelfNbt(namedTag);
    }

    public Item removeBook(int index) {
        Preconditions.checkArgument(index >= 0 && index <= 5);
        Item remove = this.items[index];
        lastInteractedSlot = index;
        setBook(null, index);
        return remove;
    }

    public boolean hasBook(int index) {
        Preconditions.checkArgument(index >= 0 && index <= 5);
        return this.items[index] != null && !this.items[index].isNull();
    }

    public int getBooksStoredBit() {
        int sum = 0;
        for (int i = 0; i < 6; i++) {
            if (items[i] != null && !items[i].isNull()) {
                sum += Math.pow(2, i);
            }
        }
        return sum;
    }

    public void setBook(@Nullable Item item, int index) {
        Preconditions.checkArgument(index >= 0 && index <= 5);
        this.items[index] = item;
        setDirty();
    }

    @DoNotModify
    public Item[] getItems() {
        return items;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag compoundTag = super.getSpawnCompound().putBoolean("isMovable", this.isMovable());
        addBookshelfNbt(compoundTag);
        return compoundTag;
    }

    @Override
    public void setDirty() {
        this.spawnToAll();
        super.setDirty();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        items = new Item[]{Item.AIR, Item.AIR, Item.AIR, Item.AIR, Item.AIR, Item.AIR};
        if (namedTag.containsInt(LAST_INTERACTED_SLOT)) {
            this.lastInteractedSlot = namedTag.getInt(LAST_INTERACTED_SLOT);
        }
        if (namedTag.containsList("Items")) {
            ListTag<CompoundTag> items = namedTag.getList("Items", CompoundTag.class);
            if (items.size() > 6) return;
            List<CompoundTag> all = items.getAll();
            for (int i = 0; i < all.size(); i++) {
                CompoundTag compoundTag = all.get(i);
                String name = compoundTag.getString("Name");
                if (name.equals("")) {
                    this.items[i] = null;
                    continue;
                }
                Item item = Item.get(name);
                item.setDamage(compoundTag.getByte("Damage"));
                item.setCount(compoundTag.getByte("Count"));
                if (compoundTag.containsCompound("tag")) {
                    item.setNamedTag(compoundTag.getCompound("tag"));
                }
                this.items[i] = item;
            }
        }
    }

    private void addBookshelfNbt(CompoundTag namedTag) {
        if (lastInteractedSlot != null) {
            namedTag.putInt(LAST_INTERACTED_SLOT, lastInteractedSlot);
        }
        ListTag<CompoundTag> compoundTagListTag = new ListTag<>();
        for (var item : items) {
            if (item == null || item.isNull()) {
                compoundTagListTag.add(new CompoundTag()
                        .putByte("Count", 0)
                        .putString("Name", "")
                        .putByte("Damage", 0)
                        .putBoolean("WasPickedUp", false)
                );
            } else {
                CompoundTag compoundTag = new CompoundTag()
                        .putByte("Count", item.getCount())
                        .putString("Name", item.getId())
                        .putByte("Damage", item.getDamage())
                        .putBoolean("WasPickedUp", false);
                if (item.hasCompoundTag()) {
                    compoundTag.putCompound("tag", item.getNamedTag());
                }
                compoundTagListTag.add(compoundTag);
            }

        }
        namedTag.putList("Items", compoundTagListTag);
    }
}
