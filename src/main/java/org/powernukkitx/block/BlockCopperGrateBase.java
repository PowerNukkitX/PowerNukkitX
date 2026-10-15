package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.enums.OxidizationLevel;

import javax.annotation.Nullable;

public abstract class BlockCopperGrateBase extends BlockCopperBase implements Oxidizable, Waxable {
    public static final BlockDefinition DEFINITION = BlockCopperBase.DEFINITION.toBuilder()
            .isTransparent(true)
            .waterloggingLevel(1)
            .build();
    public BlockCopperGrateBase(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockCopperGrateBase(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    protected String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_COPPER_GRATE : COPPER_GRATE;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_GRATE : EXPOSED_COPPER_GRATE;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_GRATE : WEATHERED_COPPER_GRATE;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_GRATE : OXIDIZED_COPPER_GRATE;
        };
    }
}
