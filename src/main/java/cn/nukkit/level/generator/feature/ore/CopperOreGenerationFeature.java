package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockCopperOre;
import cn.nukkit.block.BlockDeepslateCopperOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public class CopperOreGenerationFeature extends OreGeneratorFeature {

    public static final String NAME = "minecraft:copper_ore_feature";
    
    private static final BlockState TYPE_STONE = BlockCopperOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateCopperOre.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(TYPE_STONE, 17, 9, 0, 64),
                new OrePopulation(TYPE_DEEPSLATE, 20, 9, -64, 8)
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
