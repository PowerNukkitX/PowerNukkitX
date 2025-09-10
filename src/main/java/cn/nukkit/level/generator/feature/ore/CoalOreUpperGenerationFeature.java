package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockCoalOre;
import cn.nukkit.block.BlockDeepslateCoalOre;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;

public class CoalOreUpperGenerationFeature extends OreGeneratorFeature {

    private static final BlockState TYPE_STONE = BlockCoalOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateCoalOre.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_coal_ore_upper_feature";

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
        return 30;
    }

    @Override
    public int getClusterSize() {
        return 17;
    }

    @Override
    public int getMinHeight() {
        return 136;
    }

    @Override
    public int getMaxHeight() {
        return 320;
    }

    @Override
    public String name() {
        return NAME;
    }
}
