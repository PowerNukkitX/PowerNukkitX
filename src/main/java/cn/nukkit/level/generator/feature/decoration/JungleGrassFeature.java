package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockFern;
import cn.nukkit.block.BlockShortGrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.NukkitRandom;

public class JungleGrassFeature extends SurfaceGenerateFeature {

    private static final BlockState SHORT_GRASS = BlockShortGrass.PROPERTIES.getDefaultState();
    private static final BlockState FERN = BlockFern.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:jungle_tall_grass_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        random.setSeed(x + y + z);
        manager.setBlockStateAt(x, y, z, random.nextInt(7) == 0 ? FERN : SHORT_GRASS);
    }

    @Override
    public int getBase() {
        return 10;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }
}
