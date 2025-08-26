package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockGranite;
import cn.nukkit.block.BlockState;

public class GraniteOreGenerationFeature extends NonDeepslateGeneratorFeature {

    public static final String NAME = "minecraft:granite_feature";
    
    private static final BlockState STATE = BlockGranite.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(STATE, 10, 64, 0, 80),
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
