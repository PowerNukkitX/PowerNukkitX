package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDeepslateRedstoneOre;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockRedstoneOre;
import cn.nukkit.block.BlockState;

public class RedstoneOreGenerationFeature extends OreGeneratorFeature {

    private static final BlockState TYPE_STONE = BlockRedstoneOre.PROPERTIES.getDefaultState();
    private static final BlockState TYPE_DEEPSLATE = BlockDeepslateRedstoneOre.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_redstone_ore_feature";

    @Override
    public BlockState getState(BlockState original) {
        return switch (original.getIdentifier())  {
            case BlockID.STONE -> TYPE_STONE;
            case BlockID.DEEPSLATE -> TYPE_DEEPSLATE;
            default -> original;
        };
    }

    @Override
    public int getClusterCount() {
        return 4;
    }

    @Override
    public int getClusterSize() {
        return 8;
    }

    @Override
    public int getMinHeight() {
        return -64;
    }

    @Override
    public int getMaxHeight() {
        return 15;
    }

    @Override
    public String name() {
        return NAME;
    }
}
