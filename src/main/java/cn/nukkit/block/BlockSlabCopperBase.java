package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
public abstract class BlockSlabCopperBase extends BlockSlab implements Waxable, Oxidizable {
    /**
     * @deprecated 
     */
    

    public BlockSlabCopperBase(BlockState blockState, String doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return Waxable.super.onActivate(item, player, blockFace, fx, fy, fz)
                || Oxidizable.super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return Block.get(getCopperId(isWaxed(), oxidizationLevel)).setPropertyValues(getPropertyValues());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel)).setPropertyValues(getPropertyValues()));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), getOxidizationLevel())).setPropertyValues(getPropertyValues()));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isWaxed() {
        return false;
    }

    protected abstract String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel);
}
