package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDeepslateLapisOre;
import cn.nukkit.block.BlockLapisOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public class LapisOreGenerationFeature extends OreGeneratorFeature {

    public static final String NAME = "minecraft:lapis_ore_feature";
    
    private static final BlockState TYPE_STONE = BlockLapisOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateLapisOre.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(TYPE_STONE, 1, 7, 0, 16),
                new OrePopulation(TYPE_DEEPSLATE, 1, 7, -64, 8)
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
