package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDeepslateGoldOre;
import cn.nukkit.block.BlockGoldOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public class GoldOreGenerationFeature extends OreGeneratorFeature {

    public static final String NAME = "minecraft:gold_ore_feature";
    
    private static final BlockState TYPE_STONE = BlockGoldOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateGoldOre.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(TYPE_STONE, 2, 9, 0, 32),
                new OrePopulation(TYPE_DEEPSLATE, 2, 9, -64, 8)
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
