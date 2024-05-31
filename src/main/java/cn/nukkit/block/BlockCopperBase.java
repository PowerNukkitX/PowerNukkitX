package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */
public abstract class BlockCopperBase extends BlockSolid implements Oxidizable, Waxable {
    /**
     * @deprecated 
     */
    
    public BlockCopperBase(BlockState blockState) {
        super(blockState);
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
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return Registries.BLOCK.getBlockProperties(getCopperId(isWaxed(), oxidizationLevel)).getDefaultState().toBlock();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel)));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(waxed, getOxidizationLevel())));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isWaxed() {
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_COPPER : COPPER_BLOCK;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER : EXPOSED_COPPER;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER : WEATHERED_COPPER;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER : OXIDIZED_COPPER;
        };
    }
}
