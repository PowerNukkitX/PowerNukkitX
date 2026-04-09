package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUndyedShulkerBox;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.ShulkerBoxInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import java.util.HashSet;
import java.util.List;

/**
 * @author PetteriM1
 */
public class BlockEntityShulkerBox extends BlockEntitySpawnable implements BlockEntityInventoryHolder {

    protected ShulkerBoxInventory inventory;

    public BlockEntityShulkerBox(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.inventory = new ShulkerBoxInventory(this);

        final NbtMapBuilder builder = this.namedTag.toBuilder();

        if (!this.namedTag.containsKey("Items") || !(this.namedTag.get("Items") instanceof List<?>)) {
            builder.putList("Items", NbtType.COMPOUND, new ObjectArrayList<>());
        }

        final List<NbtMap> list = this.namedTag.getList("Items", NbtType.COMPOUND);
        for (NbtMap compound : list) {
            Item item = ItemHelper.read(compound);
            this.inventory.setItemInternal(compound.getByte("Slot"), item);
        }

        if (!this.namedTag.containsKey("facing")) {
            builder.putByte("facing", (byte) 0);
        }
        this.namedTag = builder.build();
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
        this.namedTag = this.namedTag.toBuilder().putList("Items", NbtType.COMPOUND, new ObjectArrayList<>()).build();
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
        List<NbtMap> list = this.namedTag.getList("Items", NbtType.COMPOUND);
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
            NbtMap data = this.namedTag.getList("Items", NbtType.COMPOUND).get(i);
            return ItemHelper.read(data);
        }
    }

    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        NbtMap d = ItemHelper.write(item, index);

        if (item.isNull() || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items", NbtType.COMPOUND).remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", NbtType.COMPOUND)).add(d);
        } else {
            (this.namedTag.getList("Items", NbtType.COMPOUND)).add(i, d);
        }
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
        return this.hasName() ? this.namedTag.getString("CustomName") : "Shulker Box";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag = this.namedTag.toBuilder().putString("CustomName", name).build();
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder c = getDefaultCompound(this, SHULKER_BOX).toBuilder()
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Findable", false)
                .putList("Items", NbtType.COMPOUND, this.namedTag.getList("Items", NbtType.COMPOUND))
                .putByte("facing", this.namedTag.getByte("facing"));

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c.build();
    }
}
