package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDiorite;
import cn.nukkit.block.BlockState;

public class DioriteOreGenerationFeature extends NonDeepslateGeneratorFeature {

    public static final String NAME = "minecraft:diorite_feature";
    
    private static final BlockState STATE = BlockDiorite.PROPERTIES.getDefaultState();

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
