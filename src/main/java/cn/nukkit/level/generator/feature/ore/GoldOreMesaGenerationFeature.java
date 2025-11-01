package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDeepslateGoldOre;
import cn.nukkit.block.BlockGoldOre;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;

public class GoldOreMesaGenerationFeature extends OreGeneratorFeature {

    private static final BlockState TYPE_STONE = BlockGoldOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateGoldOre.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:mesa_underground_gold_ore_feature";

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
        return 50;
    }

    @Override
    public int getClusterSize() {
        return 9;
    }

    @Override
    public int getMinHeight() {
        return 32;
    }

    @Override
    public int getMaxHeight() {
        return 256;
    }

    @Override
    public String name() {
        return NAME;
    }
}
