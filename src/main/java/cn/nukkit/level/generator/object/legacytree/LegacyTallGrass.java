package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.*;
import cn.nukkit.block.property.enums.TallGrassType;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSource;

import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.TALL_GRASS_TYPE;

public class LegacyTallGrass {
    public static void growGrass(BlockManager level, Vector3 pos, RandomSource random) {
        for (int i = 0; i < 128; ++i) {
            int num = 0;

            //todo fix grass bug spawn
            int x = pos.getFloorX();
            int y = pos.getFloorY() + 1;
            int z = pos.getFloorZ();

            while (true) {
                if (num >= i / 16) {
                    if (Objects.equals(level.getBlockIdAt(x, y, z), Block.AIR)) {
                        if (random.nextInt(8) == 0) {
                            //porktodo: biomes have specific flower types that can grow in them
                            if (random.nextBoolean()) {
                                level.setBlockStateAt(x, y, z, BlockYellowFlower.PROPERTIES.getDefaultState());
                            } else {
                                level.setBlockStateAt(x, y, z, BlockRedFlower.PROPERTIES.getDefaultState());
                            }
                        } else {
                            level.setBlockStateAt(x, y, z, BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.TALL));
                        }
                    }

                    break;
                }

                x += random.nextInt(-1, 1);
                y += random.nextInt(-1, 1) * random.nextInt(3) / 2;
                z += random.nextInt(-1, 1);

                if (!Objects.equals(level.getBlockIdAt(x, y - 1, z), Block.GRASS) || y > level.getMaxHeight() || y < level.getMinHeight()) {
                    break;
                }

                ++num;
            }
        }
    }

    private static final BlockState[] arr = {
            BlockYellowFlower.PROPERTIES.getDefaultState(),
            BlockRedFlower.PROPERTIES.getDefaultState(),
            BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.TALL),
            BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.TALL),
            BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.TALL),
            BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.TALL)
    };

    public static void growGrass(BlockManager level, Vector3 pos, RandomSource random, int count, int radius) {

        int arrC = arr.length - 1;
        for (int c = 0; c < count; c++) {
            int x = random.nextInt((int) (pos.x - radius), (int) (pos.x + radius));
            int z = random.nextInt((int) (pos.z) - radius, (int) (pos.z + radius));

            if (level.getBlockIdAt(x, (int) (pos.y + 1), z) == Block.AIR && level.getBlockIdAt(x, (int) (pos.y), z) == Block.GRASS) {
                BlockState t = arr[random.nextInt(0, arrC)];
                level.setBlockStateAt(x, (int) (pos.y + 1), z, t);
            }
        }
    }
}

