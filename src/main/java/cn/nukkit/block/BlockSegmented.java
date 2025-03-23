package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.tags.BlockTags;
import org.jetbrains.annotations.NotNull;


public abstract class BlockSegmented extends BlockFlowable {

    public BlockSegmented(BlockState blockState) {
        super(blockState);
    }

    public static boolean isSupportValid(Block block) {
        return block.is(BlockTags.DIRT);
    }

    public boolean canPlantOn(Block block) {
        return isSupportValid(block);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if(down.getId().equals(item.getId()))  {
            block = down;
            down = block.down();
        }
        if (canPlantOn(down)) {
            int currentMeta = -1;
            if (block instanceof BlockSegmented) {
                currentMeta = block.getPropertyValue(CommonBlockProperties.GROWTH);
            }
            if(currentMeta >= 3) return false;
            setPropertyValue(CommonBlockProperties.GROWTH, currentMeta+1);
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }
}
