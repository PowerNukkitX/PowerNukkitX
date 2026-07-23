package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-15
 */
public abstract class BlockStairsCopperBase extends BlockStairs implements Waxable, Oxidizable {
    public static final BlockDefinition DEFINITION = BlockStairs.DEFINITION.toBuilder()
            .hardness(3)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .canBeActivated(true)
            .build();
    public BlockStairsCopperBase(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockStairsCopperBase(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return Waxable.super.onActivate(item, player, blockFace, fx, fy, fz)
                || Oxidizable.super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type);
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return Block.get(getCopperId(isWaxed(), oxidizationLevel)).setPropertyValues(getPropertyValues());
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }

        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel)).setPropertyValues(getPropertyValues()));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), getOxidizationLevel())).setPropertyValues(getPropertyValues()));
    }

    @Override
    public boolean isWaxed() {
        return false;
    }

    protected abstract String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel);
}
