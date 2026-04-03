package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Buddelbubi
 * @since 2026/03/31
 */
public class BlockEntityBrushable extends BlockEntitySpawnable {
    public BlockEntityBrushable(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.SUSPICIOUS_GRAVEL || getBlock().getId() == Block.SUSPICIOUS_SAND;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!namedTag.contains("item")) {
            namedTag.putCompound("item", NBTIO.putItemHelper(new ItemBlock(Block.get(BlockID.AIR))));
        }
    }

    public Item getItem() {
        CompoundTag NBTTag = this.namedTag.getCompound("item");
        return NBTIO.getItemHelper(NBTTag);
    }

    public void setItem(Item item) {
        this.namedTag.putCompound("item", NBTIO.putItemHelper(item));
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = super.getSpawnCompound();
        tag.putCompound("item", this.namedTag.getCompound("item"));
        return tag;
    }
}
