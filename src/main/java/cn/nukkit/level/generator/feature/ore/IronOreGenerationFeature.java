package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDeepslateIronOre;
import cn.nukkit.block.BlockIronOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public class IronOreGenerationFeature extends OreGeneratorFeature {

    public static final String NAME = "minecraft:iron_ore_feature";
    
    private static final BlockState TYPE_STONE = BlockIronOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateIronOre.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(TYPE_STONE, 20, 9, 0, 64),
                new OrePopulation(TYPE_DEEPSLATE, 20, 9, -64, 8)
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
