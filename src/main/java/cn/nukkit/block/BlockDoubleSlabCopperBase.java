package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public abstract class BlockDoubleSlabCopperBase extends BlockDoubleSlabBase implements Waxable, Oxidizable {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockDoubleSlabCopperBase(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return BlockSlab.SIMPLE_SLAB_PROPERTIES;
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
    public boolean setOxidizationLevel(@Nonnull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel)));
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(waxed, getOxidizationLevel())));
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
