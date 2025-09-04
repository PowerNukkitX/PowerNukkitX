package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSpruceLeaves;
import cn.nukkit.block.BlockSpruceLog;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectSmallSpruceTree extends TreeGenerator {

    private final BlockState SPRUCE_LOG = BlockSpruceLog.PROPERTIES
            .getBlockState(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Y);

    private final BlockState SPRUCE_LEAVES = BlockSpruceLeaves.PROPERTIES.getDefaultState();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        int height = 6 + rand.nextInt(4);
        int baseX = position.getFloorX();
        int baseY = position.getFloorY();
        int baseZ = position.getFloorZ();

        if (baseY < 1 || baseY + height + 2 >= 256) return false;

        Vector3 below = position.down();
        String ground = level.getBlockIdIfCachedOrLoaded(below.getFloorX(), below.getFloorY(), below.getFloorZ());
        if (!ground.equals(Block.GRASS_BLOCK) && !ground.equals(Block.DIRT) && !ground.equals(Block.PODZOL)) {
            return false;
        }

        if (!this.placeTreeOfHeight(level, position, height)) return false;

        int trunkHeight = height - rand.nextInt(3);
        for (int y = 0; y < trunkHeight; y++) {
            placeLogAt(level, new Vector3(baseX, baseY + y, baseZ));
        }

        int topSize = height - (1 + rand.nextInt(2));
        int lRadius = 2 + rand.nextInt(2);
        int radius = rand.nextInt(2);
        int maxR = 1;
        int minR = 0;

        for (int yy = 0; yy <= topSize; yy++) {
            int yyy = baseY + height - yy;

            for (int xx = baseX - radius; xx <= baseX + radius; xx++) {
                int xOff = Math.abs(xx - baseX);
                for (int zz = baseZ - radius; zz <= baseZ + radius; zz++) {
                    int zOff = Math.abs(zz - baseZ);

                    if (xOff == radius && zOff == radius && radius > 0) {
                        continue;
                    }

                    if (!level.getBlockIdIfCachedOrLoaded(xx, yyy, zz).equals(Block.AIR)) {
                        continue;
                    }

                    placeLeafAt(level, xx, yyy, zz);
                }
            }

            if (radius >= maxR) {
                radius = minR;
                minR = 1;
                if (++maxR > lRadius) {
                    maxR = lRadius;
                }
            } else {
                ++radius;
            }
        }

        return true;
    }

    private boolean placeTreeOfHeight(BlockManager world, Vector3 pos, int height) {
        int i = pos.getFloorX();
        int j = pos.getFloorY();
        int k = pos.getFloorZ();
        Vector3 tmp = new Vector3();

        for (int y = 0; y <= height + 1; ++y) {
            int r = (y == 0) ? 0 : (y >= height - 1 ? 2 : 1);
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    tmp.setComponents(i + dx, j + y, k + dz);
                    if (!this.canGrowInto(world.getBlockIdIfCachedOrLoaded(tmp.getFloorX(), tmp.getFloorY(), tmp.getFloorZ()))) {
                        return false;
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

    private void fillDiscLeaves(BlockManager world, RandomSourceProvider rand,
                                int cx, int cz, int y, int radius) {
        int r2 = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int dist2 = dx * dx + dz * dz;
                if (dist2 <= r2) {
                    if (dist2 > (radius - 1) * (radius - 1)) {
                        if (rand.nextBoolean()) continue;
                    }
                    placeLeafAt(world, cx + dx, y, cz + dz);
                }
            }
        }
    }
}
