package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public abstract class BlockCopperBase extends BlockSolid implements Oxidizable, Waxable {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperBase() {
        // Does nothing
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
    protected int getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed? WAXED_COPPER : COPPER_BLOCK;
            case EXPOSED:
                return waxed? WAXED_EXPOSED_COPPER : EXPOSED_COPPER;
            case WEATHERED:
                return waxed? WAXED_WEATHERED_COPPER : WEATHERED_COPPER;
            case OXIDIZED:
                return waxed? WAXED_OXIDIZED_COPPER : OXIDIZED_COPPER;
            default:
                return getId();
        }
    }
}
