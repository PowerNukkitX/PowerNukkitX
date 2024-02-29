package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public abstract class BlockGlazedTerracotta extends BlockSolid implements Faceable {
    public BlockGlazedTerracotta(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getResistance() {
        return 7;
    }

    @Override
    public double getHardness() {
        return 1.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setBlockFace(BlockFace.fromIndex(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]));
        return this.getLevel().setBlock(block, this, true, true);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face.getIndex());
    }

    //带釉陶瓦可以被推动但不能被收回
    //see: https://zh.minecraft.wiki/w/%E5%B8%A6%E9%87%89%E9%99%B6%E7%93%A6
    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }
}
