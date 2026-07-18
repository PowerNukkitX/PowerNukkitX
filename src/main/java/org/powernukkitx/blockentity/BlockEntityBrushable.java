package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;

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
        if (!nbt.contains("item")) {
            this.nbt.putCompound("item", ItemHelper.write(new ItemBlock(Block.get(BlockID.AIR)), null));
        }
    }

    public Item getItem() {
        CompoundTag tag = this.getNbt().getCompound("item");
        return ItemHelper.read(tag);
    }

    public void setItem(Item item) {
        this.nbt.putCompound("item", ItemHelper.write(item, null));
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putCompound("item", this.getNbt().getCompound("item").copy());
    }
}
