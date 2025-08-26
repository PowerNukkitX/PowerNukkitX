package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public class DirtOreGenerationFeature extends OreGeneratorFeature {

    public static final String NAME = "minecraft:dirt_feature";
    
    private static final BlockState STATE = BlockDirt.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(STATE, 10, 33, 0, 128),
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
