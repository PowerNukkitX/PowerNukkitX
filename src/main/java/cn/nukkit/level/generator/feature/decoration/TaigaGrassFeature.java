package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockFern;
import cn.nukkit.block.BlockShortGrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.NukkitRandom;

public class TaigaGrassFeature extends SurfaceGenerateFeature {

    private static final BlockState SHORT_GRASS = BlockShortGrass.PROPERTIES.getDefaultState();
    private static final BlockState FERN = BlockFern.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:taiga_tall_grass_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, random.setSeed(x + y + z).nextInt(7) == 0 ? SHORT_GRASS : FERN);
    }

    @Override
    public int getBase() {
        return 8;
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
