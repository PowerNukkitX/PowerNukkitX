package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.level.Sound;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.BRUSHED_PROGRESS;
import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;


public class BlockSuspiciousSand extends BlockBrushable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SUSPICIOUS_SAND, HANGING, BRUSHED_PROGRESS);
    public static final BlockDefinition DEFINITION = BlockBrushable.DEFINITION.toBuilder()
            .hardness(0.25)
            .resistance(1.25)
            .build();

    public BlockSuspiciousSand() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSuspiciousSand(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public String getName() {
        return "Suspicious Sand";
    }

    @Override
    public Block getFinalState() {
        return BlockSand.PROPERTIES.getDefaultState().toBlock();
    }

    @Override
    protected Sound getHitSound() {
        return Sound.HIT_SUSPICIOUS_SAND;
    }

    @Override
    protected Sound getBreakSound() {
        return Sound.BREAK_SUSPICIOUS_SAND;
    }
}