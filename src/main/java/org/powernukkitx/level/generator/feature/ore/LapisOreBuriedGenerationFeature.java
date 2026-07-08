package org.powernukkitx.level.generator.feature.ore;

import org.powernukkitx.block.BlockDeepslateLapisOre;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLapisOre;
import org.powernukkitx.block.BlockState;

public class LapisOreBuriedGenerationFeature extends OreGeneratorFeature {

    private static final BlockState TYPE_STONE = BlockLapisOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateLapisOre.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_lapis_ore_buried_feature";

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
        return 4;
    }

    @Override
    public int getClusterSize() {
        return 7;
    }

    @Override
    public int getMinHeight() {
        return -64;
    }

    @Override
    public int getMaxHeight() {
        return 64;
    }

    @Override
    public float getSkipAir() {
        return 1f;
    }

    @Override
    public String name() {
        return NAME;
    }
}
