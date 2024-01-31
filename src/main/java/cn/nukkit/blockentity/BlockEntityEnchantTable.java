package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntityEnchantTable extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    protected EnchantInventory inventory;

    public BlockEntityEnchantTable(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.inventory = new EnchantInventory(this);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.ENCHANTING_TABLE;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Enchanting Table";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.ENCHANT_TABLE)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

    @Override
    public EnchantInventory getInventory() {
        return inventory;
    }
}
