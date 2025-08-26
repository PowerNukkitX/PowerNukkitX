package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockGravel;
import cn.nukkit.block.BlockState;

public class GravelOreGenerationFeature extends NonDeepslateGeneratorFeature {

    public static final String NAME = "minecraft:gravel_ore_feature";
    
    private static final BlockState STATE = BlockGravel.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(STATE, 8, 33, 0, 128),
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
