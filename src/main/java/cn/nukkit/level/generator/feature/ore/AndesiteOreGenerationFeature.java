package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockAndesite;
import cn.nukkit.block.BlockGranite;
import cn.nukkit.block.BlockState;

public class AndesiteOreGenerationFeature extends NonDeepslateGeneratorFeature {

    public static final String NAME = "minecraft:andesite_feature";
    
    private static final BlockState STATE = BlockAndesite.PROPERTIES.getDefaultState();

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
