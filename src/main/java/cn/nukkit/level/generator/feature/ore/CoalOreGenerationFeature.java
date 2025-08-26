package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockCoalOre;
import cn.nukkit.block.BlockDeepslateCoalOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public class CoalOreGenerationFeature extends OreGeneratorFeature {

    public static final String NAME = "minecraft:coal_ore_feature";
    
    private static final BlockState TYPE_STONE = BlockCoalOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateCoalOre.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(TYPE_STONE, 20, 17, 0, 128),
                new OrePopulation(TYPE_DEEPSLATE, 20, 17, -64, 8)
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
