package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDeepslateRedstoneOre;
import cn.nukkit.block.BlockRedstoneOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public class RedstoneOreGenerationFeature extends OreGeneratorFeature {

    public static final String NAME = "minecraft:redstone_ore_feature";
    
    private static final BlockState TYPE_STONE = BlockRedstoneOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateRedstoneOre.PROPERTIES.getDefaultState();

    @Override
    public OrePopulation[] getPopulators() {
        return new OrePopulation[] {
                new OrePopulation(TYPE_STONE, 8, 8, 0, 16),
                new OrePopulation(TYPE_DEEPSLATE, 8, 8, -64, 8)
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
