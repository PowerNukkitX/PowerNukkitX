package cn.nukkit.blockentity;

import cn.nukkit.block.BlockAir;
import cn.nukkit.inventory.EjectableInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import cn.nukkit.utils.NbtHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import java.util.List;

public abstract class BlockEntityEjectable extends BlockEntitySpawnable implements BlockEntityInventoryHolder {

    protected EjectableInventory inventory;


    public BlockEntityEjectable(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    protected abstract EjectableInventory createInventory();

    protected abstract String getBlockEntityName();

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.inventory = createInventory();

        if (!this.namedTag.containsKey("Items") || !(this.namedTag.get("Items") instanceof List)) {
            this.namedTag = this.namedTag.toBuilder().putList("Items", NbtType.LIST, new ObjectArrayList<>()).build();
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }
    }

    public int getSize() {
        return 9;
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
            return new ItemBlock(new BlockAir(), 0, 0);
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
    public EjectableInventory getInventory() {
        return inventory;
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder c = super.getSpawnCompound().toBuilder();

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c.build();
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
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : getBlockEntityName();
    }

    @Override
    public boolean hasName() {
        return this.namedTag.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
           this.namedTag = NbtHelper.remove(this.namedTag, "CustomName");
            return;
        }

        this.namedTag = this.namedTag.toBuilder().putString("CustomName", name).build();
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
    }
}
