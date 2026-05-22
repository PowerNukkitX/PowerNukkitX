package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntityEnchantTable extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    public BlockEntityEnchantTable(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId().equals(Block.ENCHANTING_TABLE);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Enchanting Table";
    }

    @Override
    public boolean hasName() {
        return this.nbt.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.nbt.remove("CustomName");
            return;
        }

        this.nbt.putString("CustomName", name);
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder c = super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", false);

        if (this.hasName()) {
            c.putString("CustomName", this.getNbt().getString("CustomName"));
        }

        return c.build();
    }

    @Override
    public EnchantInventory getInventory() {
        return new EnchantInventory(this);
    }
}
