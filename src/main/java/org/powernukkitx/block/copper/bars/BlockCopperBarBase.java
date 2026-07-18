package org.powernukkitx.block.copper.bars;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.*;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author keksdev
 * @since 1.21.110
 */
public abstract class BlockCopperBarBase extends BlockTransparent implements Oxidizable, Waxable {
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canBeActivated(true)
            .canHarvestWithHand(false)
            .build();
    public BlockCopperBarBase(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockCopperBarBase(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
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
        return Registries.BLOCK.getBlockProperties(getCopperId(isWaxed(), oxidizationLevel)).getDefaultState().toBlock();
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel)));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(waxed, getOxidizationLevel())));
    }

    @Override
    public boolean isWaxed() {
        return false;
    }

    protected String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_COPPER_BARS : COPPER_BARS;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_BARS : EXPOSED_COPPER_BARS;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_BARS : WEATHERED_COPPER_BARS;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_BARS : OXIDIZED_COPPER_BARS;
        };
    }
}
