package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-15
 */
@PowerNukkitOnly
@Since("FUTURE")
public abstract class BlockStairsCopperBase extends BlockStairs implements Waxable, Oxidizable {
    public BlockStairsCopperBase(int meta) {
        super(meta);
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
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
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }


    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public BlockState getStateWithOxidizationLevel(@Nonnull OxidizationLevel oxidizationLevel) {
        return getCurrentState().withBlockId(getCopperId(isWaxed(), oxidizationLevel));
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean setOxidizationLevel(@Nonnull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, getCurrentState().withBlockId(getCopperId(isWaxed(), oxidizationLevel)).getBlock());
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, getCurrentState().withBlockId(getCopperId(waxed, getOxidizationLevel())).getBlock());
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean isWaxed() {
        return false;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    protected abstract int getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel);
}
