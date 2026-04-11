package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.NbtHelper;
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
        return this.hasName() ? this.namedTag.getString("CustomName") : "Enchanting Table";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.namedTag = NbtHelper.remove(this.namedTag, "CustomName");
            return;
        }

      this.namedTag = this.namedTag.toBuilder().putString("CustomName", name).build();
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder c = super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", false);

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c.build();
    }

    @Override
    public EnchantInventory getInventory() {
        return new EnchantInventory(this);
    }
}
