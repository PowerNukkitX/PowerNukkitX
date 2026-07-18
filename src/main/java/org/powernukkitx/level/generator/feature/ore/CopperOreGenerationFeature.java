package org.powernukkitx.level.generator.feature.ore;

import org.powernukkitx.block.BlockCopperOre;
import org.powernukkitx.block.BlockDeepslateCopperOre;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;

public class CopperOreGenerationFeature extends OreGeneratorFeature {

    private static final BlockState TYPE_STONE = BlockCopperOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateCopperOre.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_copper_ore_feature";

    @Override
    public BlockState getState(BlockState original) {
        return switch (original.getIdentifier())  {
            case BlockID.STONE -> TYPE_STONE;
            case BlockID.DEEPSLATE -> TYPE_DEEPSLATE;
            default -> original;
        };
    }

    @Override
    public int getClusterCount() {
        return 16;
    }

    @Override
    public int getClusterSize() {
        return 10;
    }

    @Override
    public int getMinHeight() {
        return -16;
    }

    @Override
    public int getMaxHeight() {
        return 112;
    }

    @Override
    public ConcentrationType getConcentration() {
        return ConcentrationType.TRIANGLE;
    }

    @Override
    public String name() {
        return NAME;
    }
}
