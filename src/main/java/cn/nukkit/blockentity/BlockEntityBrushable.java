package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author Buddelbubi
 * @since 2026/03/31
 */
public class BlockEntityBrushable extends BlockEntitySpawnable {
    public BlockEntityBrushable(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.SUSPICIOUS_GRAVEL || getBlock().getId() == Block.SUSPICIOUS_SAND;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!nbt.containsKey("item")) {
            this.nbt.putCompound("item", ItemHelper.write(new ItemBlock(Block.get(BlockID.AIR)), null));
        }
    }

    public Item getItem() {
        NbtMap tag = this.getNbt().getCompound("item");
        return ItemHelper.read(tag);
    }

    public void setItem(Item item) {
        this.nbt.putCompound("item", ItemHelper.write(item, null));
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putCompound("item", this.getNbt().getCompound("item"))
                .build();
    }
}
