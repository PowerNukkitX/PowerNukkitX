package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDeepslateIronOre;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockIronOre;
import cn.nukkit.block.BlockState;

public class IronOreUpperGenerationFeature extends OreGeneratorFeature {

    private static final BlockState TYPE_STONE = BlockIronOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateIronOre.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_iron_ore_upper_feature";

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
        return 90;
    }

    @Override
    public int getClusterSize() {
        return 10;
    }

    @Override
    public int getMinHeight() {
        return 80;
    }

    @Override
    public int getMaxHeight() {
        return 384;
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
