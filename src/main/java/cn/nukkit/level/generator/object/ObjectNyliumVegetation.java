package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectNyliumVegetation {
    public static void growVegetation(BlockManager level, Vector3 pos, RandomSourceProvider random) {
        for (int i = 0; i < 128; ++i) {
            int num = 0;

            int x = pos.getFloorX();
            int y = pos.getFloorY() + 1;
            int z = pos.getFloorZ();

            boolean crimson = level.getBlockIdIfCachedOrLoaded(x, y - 1, z) == BlockID.CRIMSON_NYLIUM;

            while (true) {
                if (num >= i / 16) {
                    if (level.getBlockIdIfCachedOrLoaded(x, y, z) == BlockID.AIR) {
                        if (crimson) {
                            if (random.nextInt(8) == 0) {
                                if (random.nextInt(8) == 0) {
                                    level.setBlockStateAt(x, y, z, BlockID.WARPED_FUNGUS);
                                } else {
                                    level.setBlockStateAt(x, y, z, BlockID.CRIMSON_FUNGUS);
                                }
                            } else {
                                level.setBlockStateAt(x, y, z, BlockID.CRIMSON_ROOTS);
                            }
                        } else {
                            if (random.nextInt(8) == 0) {
                                if (random.nextInt(8) == 0) {
                                    level.setBlockStateAt(x, y, z, BlockID.CRIMSON_FUNGUS);
                                } else {
                                    level.setBlockStateAt(x, y, z, BlockID.WARPED_FUNGUS);
                                }
                            } else {
                                if (random.nextBoolean()) {
                                    level.setBlockStateAt(x, y, z, BlockID.WARPED_ROOTS);
                                } else {
                                    level.setBlockStateAt(x, y, z, BlockID.NETHER_SPROUTS);
                                }
                            }
                        }
                    }

                    break;
                }

                x += random.nextInt(-1, 1);
                y += random.nextInt(-1, 1) * random.nextInt(3) / 2;
                z += random.nextInt(-1, 1);

                String id = level.getBlockIdIfCachedOrLoaded(x, y - 1, z);
                crimson = id == BlockID.CRIMSON_NYLIUM;
                if ((!crimson && id != BlockID.WARPED_NYLIUM) || y > 255 || y < 0) {
                    break;
                }

                ++num;
            }
        }
    }
}
