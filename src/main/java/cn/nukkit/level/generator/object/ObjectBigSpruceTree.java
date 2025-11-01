package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockPodzol;
import cn.nukkit.block.BlockSpruceLeaves;
import cn.nukkit.block.BlockSpruceLog;
import cn.nukkit.block.BlockSpruceWood;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectBigSpruceTree extends TreeGenerator {

    private final BlockState SPRUCE_LOG = BlockSpruceLog.PROPERTIES.getDefaultState();
    private final BlockState PODZOL = BlockPodzol.PROPERTIES.getDefaultState();


    protected static final int[][] foliages = {
            {1, 0, 0, 1, 2, 1, 1, 2, 3, 2, 2, 3, 4, 3},
            {1, 0, 1, 2, 1, 2, 1, 1, 2, 3, 2, 2, 3, 4, 3},
            {1, 2, 3},
            {1, 2, 1, 3, 2, 4, 3}
    };

    private final BlockState SPRUCE_LEAVES = BlockSpruceLeaves.PROPERTIES.getDefaultState();

    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        int height = 24 + rand.nextInt(8);
        int baseX = position.getFloorX();
        int baseY = position.getFloorY();
        int baseZ = position.getFloorZ();
        int midX = baseX + 1;
        int midZ = baseZ + 1;

        if (baseY < 1 || baseY + height + 5 >= 256) return false;

        int rad = 6;
        for(int x = -rad - 1; x <= rad; x++) {
            for (int z = -rad - 1; z <= rad; z++) {
                float calcX = x + 0.5f;
                float calcZ = z + 0.5f;
                float calcRad = rad + 0.8f;
                int px = midX + x;
                int pz = midZ + z;
                if((calcX * calcX) + (calcZ * calcZ) < (calcRad * calcRad))
                    placePodzolAt(level, px, level.getLevel().getHeightMap(px, pz), pz);
            }
        }

        Vector3 below = position.down();
        String ground = level.getBlockIdIfCachedOrLoaded(below.getFloorX(), below.getFloorY(), below.getFloorZ());
        if (!ground.equals(Block.GRASS_BLOCK) && !ground.equals(Block.DIRT) && !ground.equals(Block.PODZOL)) {
            return false;
        }

        int[] leafRadii = foliages[rand.nextInt(foliages.length-1)];

        placeLeafAt(level, baseX,baseY + height + 1, baseZ);
        placeLeafAt(level, baseX + 1, baseY + height + 1, baseZ);
        placeLeafAt(level, baseX,     baseY + height + 1, baseZ + 1);
        placeLeafAt(level, baseX + 1, baseY + height + 1, baseZ + 1);
        for (int y = height; y >= 0; y--) {
            placeLogAt(level, new Vector3(baseX,     baseY + y, baseZ));
            placeLogAt(level, new Vector3(baseX + 1, baseY + y, baseZ));
            placeLogAt(level, new Vector3(baseX,     baseY + y, baseZ + 1));
            placeLogAt(level, new Vector3(baseX + 1, baseY + y, baseZ + 1));
            int index = height - y;
            if (index < leafRadii.length) {
                int radius = leafRadii[index];
                for(int x = -radius - 1; x <= radius; x++) {
                    for (int z = -radius - 1; z <= radius; z++) {
                        float calcX = x + 0.5f;
                        float calcZ = z + 0.5f;
                        float calcRad = radius + 0.7f;
                        if((calcX * calcX) + (calcZ * calcZ) < (calcRad * calcRad))
                        placeLeafAt(level,  midX + x, baseY + y,  midZ + z);
                    }
                }
            }
        }

        return true;
    }


    private void placeLogAt(BlockManager world, Vector3 pos) {
        if (this.canGrowInto(world.getBlockIdIfCachedOrLoaded(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()))) {
            world.setBlockStateAt(pos, SPRUCE_LOG);
        }
    }

    private void placeLeafAt(BlockManager world, int x, int y, int z) {
        String material = world.getBlockIdIfCachedOrLoaded(x, y, z);
        if (material.equals(Block.AIR) || material.equals(Block.SNOW_LAYER)) {
            world.setBlockStateAt(new Vector3(x, y, z), SPRUCE_LEAVES);
        }
    }


    private void placePodzolAt(BlockManager world, int x, int y, int z) {
        if (world.getBlockIfCachedOrLoaded(x, y, z).is(BlockTags.DIRT)) {
            world.setBlockStateAt(new Vector3(x, y, z), PODZOL);
        }
    }

}
