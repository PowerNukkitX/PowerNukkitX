package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockFern;
import cn.nukkit.block.BlockShortGrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.object.BlockManager;

public class JungleGrassFeature extends SurfaceGenerateFeature {

    private static final BlockState SHORT_GRASS = BlockShortGrass.PROPERTIES.getDefaultState();
    private static final BlockState FERN = BlockFern.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:jungle_tall_grass_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, random.nextInt(7) == 0 ? FERN : SHORT_GRASS);
    }

    @Override
    public int getBase() {
        return 100;
    }

    @Override
    public int getRandom() {
        return 10;
    }

    @Override
    public String name() {
        return NAME;
    }
}
