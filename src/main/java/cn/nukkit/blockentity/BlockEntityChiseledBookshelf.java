package cn.nukkit.blockentity;

import cn.nukkit.api.DoNotModify;
import cn.nukkit.block.BlockChiseledBookshelf;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import javax.annotation.Nullable;
import java.util.List;

public class BlockEntityChiseledBookshelf extends BlockEntitySpawnable {
    public static final String LAST_INTERACTED_SLOT = "LastInteractedSlot";
    private Integer lastInteractedSlot;
    private Item[] items;

    public BlockEntityChiseledBookshelf(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock() instanceof BlockChiseledBookshelf;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        addBookshelfNbt(namedTag, true);
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
    public NbtMap getSpawnCompound() {
        NbtMap compoundTag = super.getSpawnCompound().toBuilder().putBoolean("isMovable", this.isMovable()).build();
        addBookshelfNbt(compoundTag, false);
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
        if (namedTag.containsKey(LAST_INTERACTED_SLOT, NbtType.INT)) {
            this.lastInteractedSlot = namedTag.getInt(LAST_INTERACTED_SLOT);
        }
        if (namedTag.containsKey("Items", NbtType.LIST)) {
            List<NbtMap> items = namedTag.getList("Items", NbtType.COMPOUND);
            if (items.size() > 6) return;
            for (int i = 0; i < items.size(); i++) {
                NbtMap compoundTag = items.get(i);
                String name = compoundTag.getString("Name");
                if (name.equals("")) {
                    this.items[i] = null;
                    continue;
                }
                Item item = Item.get(name);
                item.setDamage(compoundTag.getByte("Damage"));
                item.setCount(compoundTag.getByte("Count"));
                if (compoundTag.containsKey("tag", NbtType.COMPOUND)) {
                    item.setNamedTag(compoundTag.getCompound("tag"));
                }
                this.items[i] = item;
            }
        }
    }

    private void addBookshelfNbt(NbtMap namedTag, boolean setSelf) {
        if (lastInteractedSlot != null) {
            namedTag = namedTag.toBuilder().putInt(LAST_INTERACTED_SLOT, lastInteractedSlot).build();
        }
        List<NbtMap> compoundTagListTag = new ObjectArrayList<>();
        for (var item : items) {
            if (item == null || item.isNull()) {
                compoundTagListTag.add(NbtMap.builder()
                        .putByte("Count", (byte) 0)
                        .putString("Name", "")
                        .putByte("Damage", (byte) 0)
                        .putBoolean("WasPickedUp", false)
                        .build()
                );
            } else {
                NbtMapBuilder compoundTag = NbtMap.builder()
                        .putByte("Count", (byte) item.getCount())
                        .putString("Name", item.getId())
                        .putByte("Damage", (byte) item.getDamage())
                        .putBoolean("WasPickedUp", false);
                if (item.hasCompoundTag()) {
                    compoundTag.putCompound("tag", item.getNamedTag());
                }
                compoundTagListTag.add(compoundTag.build());
            }

        }
        namedTag = namedTag.toBuilder().putList("Items", NbtType.COMPOUND, compoundTagListTag).build();
        if (setSelf)
            this.namedTag = namedTag;
    }
}
