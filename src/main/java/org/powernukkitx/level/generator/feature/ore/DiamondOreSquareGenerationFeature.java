package org.powernukkitx.level.generator.feature.ore;

import org.powernukkitx.block.BlockDeepslateDiamondOre;
import org.powernukkitx.block.BlockDiamondOre;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;

public class DiamondOreSquareGenerationFeature extends OreGeneratorFeature {

    private static final BlockState TYPE_STONE = BlockDiamondOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateDiamondOre.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_diamond_ore_feature_square";

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
        return 8;
    }

    @Override
    public int getClusterSize() {
        return 2;
    }

    @Override
    public int getMinHeight() {
        return -64;
    }

    @Override
    public int getMaxHeight() {
        return -4;
    }

    @Override
    public float getSkipAir() {
        return 0.5f;
    }

    @Override
    public String name() {
        return NAME;
    }
}
