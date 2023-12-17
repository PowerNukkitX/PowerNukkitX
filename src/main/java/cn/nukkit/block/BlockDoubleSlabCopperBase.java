package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public abstract class BlockDoubleSlabCopperBase extends BlockDoubleSlabBase implements Waxable, Oxidizable {


    public BlockDoubleSlabCopperBase(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        return Waxable.super.onActivate(item, player)
                || Oxidizable.super.onActivate(item, player);
    }

    @Override
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }


    @Override
    public BlockState getStateWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return getCurrentState().withBlockId(getCopperId(isWaxed(), oxidizationLevel));
    }


    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, getCurrentState().withBlockId(getCopperId(isWaxed(), oxidizationLevel)).getBlock());
    }


    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, getCurrentState().withBlockId(getCopperId(waxed, getOxidizationLevel())).getBlock());
    }


    @Override
    public boolean isWaxed() {
        return false;
    }


    protected abstract int getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel);
}
