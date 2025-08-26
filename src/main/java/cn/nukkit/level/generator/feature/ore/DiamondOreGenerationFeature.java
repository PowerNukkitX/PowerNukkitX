package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDeepslateDiamondOre;
import cn.nukkit.block.BlockDiamondOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public class DiamondOreGenerationFeature extends OreGeneratorFeature {

    public static final String NAME = "minecraft:diamond_ore_feature";
    
    private static final BlockState TYPE_STONE = BlockDiamondOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateDiamondOre.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(TYPE_STONE, 1, 6, 0, 16),
                new OrePopulation(TYPE_DEEPSLATE, 1, 8, -64, 8)
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
