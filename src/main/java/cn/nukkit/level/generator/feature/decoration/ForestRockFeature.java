package cn.nukkit.level.generator.feature.decoration;


import cn.nukkit.block.Block;
import cn.nukkit.block.BlockMossyCobblestone;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.NukkitRandom;

public class ForestRockFeature extends SurfaceGenerateFeature {

    protected static final BlockState MOSSY_COBBLESTONE = BlockMossyCobblestone.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:legacy:forest_rock_feature";

    @Override
    public int getBase() {
        return -1;
    }

    @Override
    public int getRandom() {
        return 2;
    }

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        this.random.setSeed(x + y + z);
        int sizeX = 3 + random.nextInt(1);
        int sizeZ = 3 + random.nextInt(1);
        for(int _x = 0; _x < sizeX; _x++) {
            for(int _y = -1; _y <= 1; _y++) {
                for(int _z = 0; _z < sizeX; _z++) {
                    boolean corner = (_x == 0 || _x == sizeX-1) && (_z == 0 || _z == sizeZ-1) && _y != 0;
                    if(!corner) {
                        manager.setBlockStateAt(x + _x, y + _y, z + _z, MOSSY_COBBLESTONE);
                    }
                }
            }
        }
    }

    @Override
    public boolean isSupportValid(Block support) {
        return support.is(BlockTags.DIRT) &&
                Registries.BIOME.get(support.getLevel().getBiomeId(support.getFloorX(), support.getFloorY(), support.getFloorZ())).getTags().contains(BiomeTags.TAIGA);
    }

    @Override
    public String name() {
        return NAME;
    }
}
