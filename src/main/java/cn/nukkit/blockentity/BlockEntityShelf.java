package cn.nukkit.blockentity;

import cn.nukkit.block.shelf.AbstractBlockShelf;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.ShelfInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import java.util.List;

/**
 * @author Buddelbubi
 * @since 2025/11/07
 */
public class BlockEntityShelf extends BlockEntitySpawnableContainer {

    public BlockEntityShelf(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();

        this.inventory = new ShelfInventory(this);

        if (!this.namedTag.containsKey("Items") || !(this.namedTag.get("Items") instanceof List<?>)) {
            this.namedTag = this.namedTag.toBuilder().putList("Items", NbtType.COMPOUND, new ObjectArrayList<>()).build();
        }

        List<NbtMap> itemsTag = this.namedTag.getList("Items", NbtType.COMPOUND);
        for (int i = 0; i < itemsTag.size(); i++) {
            this.inventory.setItem(i, ItemHelper.read(itemsTag.get(i)));
        }
        this.level.updateComparatorOutputLevel(this);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag = this.namedTag.toBuilder().putList("Items", NbtType.COMPOUND, new ObjectArrayList<>()).build();
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
    public NbtMap getSpawnCompound() {
        NbtMapBuilder tag = super.getSpawnCompound().toBuilder();
        List<NbtMap> items = new ObjectArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            Item item = this.inventory.getItem(i);
            items.add(ItemHelper.write(item, i));
        }
        tag.put("Items", items);
        return tag.build();
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
