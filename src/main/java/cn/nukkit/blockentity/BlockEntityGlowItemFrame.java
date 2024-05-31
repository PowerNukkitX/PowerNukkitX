package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityGlowItemFrame extends BlockEntityItemFrame {
    /**
     * @deprecated 
     */
    

    public BlockEntityGlowItemFrame(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Glow Item Frame";
    }
    /**
     * @deprecated 
     */
    

    public boolean hasName() {
        return namedTag.contains("CustomName");
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (!this.namedTag.contains("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        Item $1 = getItem();
        CompoundTag $2 = super.getSpawnCompound();

        if (!item.isNull()) {
            CompoundTag $3 = NBTIO.putItemHelper(item);
            int $4 = item.getDamage();
            String $5 = item.getId();
            if (namespacedId != null) {
                itemTag.remove("id");
                itemTag.putShort("Damage", networkDamage);
                itemTag.putString("Name", namespacedId);
            }
            if (item.isBlock()) {
                itemTag.putCompound("Block", item.getBlockUnsafe().getBlockState().getBlockStateTag());
            }
            tag.putCompound("Item", itemTag)
                    .putByte("ItemRotation", this.getItemRotation());
        }
        return tag;
    }
}
