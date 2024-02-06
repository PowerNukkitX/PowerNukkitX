package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.FlowerType;
import cn.nukkit.block.property.enums.TallGrassType;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandomSource;
import cn.nukkit.utils.random.RandomSource;

import java.util.List;
import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.TALL_GRASS_TYPE;

public class LegacyTallGrass {
    private static final BlockState[] places = {//total 106
            BlockYellowFlower.PROPERTIES.getDefaultState(),// 10
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.ALLIUM),// 2
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.CORNFLOWER),// 2
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.ORCHID),// 2
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.HOUSTONIA),// 5
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.LILY_OF_THE_VALLEY),// 2
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.OXEYE),// 5
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.TULIP_RED),// 2
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.TULIP_ORANGE),// 2
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.TULIP_PINK),// 2
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.TULIP_WHITE),// 2
            BlockRedFlower.PROPERTIES.getBlockState(CommonBlockProperties.FLOWER_TYPE, FlowerType.POPPY),// 10
            BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.DEFAULT),// 50
            BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.TALL)// 30
    };

    public static void growGrass(BlockManager level, Vector3 pos, RandomSource random) {
        int y = pos.getFloorY() + 1;
        int minx = pos.getFloorX() - 2;
        int minz = pos.getFloorZ() - 2;
        int maxx = pos.getFloorX() + 2;
        int maxz = pos.getFloorZ() + 2;
        for (int x = minx; x <= maxx; x++) {
            for (int z = minz; z <= maxz; z++) {
                int newY = y + random.nextInt(2) * (random.nextBoolean() ? -1 : 1);
                if (random.nextBoolean()) {
                    if (Objects.equals(level.getBlockIdAt(x, newY, z), Block.AIR) && Objects.equals(level.getBlockIdAt(x, newY - 1, z), Block.GRASS)) {
                        int ranNumber = random.nextInt(0, 105);
                        if (0 <= ranNumber && ranNumber < 10) {
                            level.setBlockStateAt(x, newY, z, places[0]);
                        } else if (10 <= ranNumber && ranNumber < 12) {
                            level.setBlockStateAt(x, newY, z, places[1]);
                        } else if (12 <= ranNumber && ranNumber < 14) {
                            level.setBlockStateAt(x, newY, z, places[2]);
                        } else if (14 <= ranNumber && ranNumber < 16) {
                            level.setBlockStateAt(x, newY, z, places[3]);
                        } else if (16 <= ranNumber && ranNumber < 21) {
                            level.setBlockStateAt(x, newY, z, places[4]);
                        } else if (21 <= ranNumber && ranNumber < 23) {
                            level.setBlockStateAt(x, newY, z, places[5]);
                        } else if (23 <= ranNumber && ranNumber < 28) {
                            level.setBlockStateAt(x, newY, z, places[6]);
                        } else if (28 <= ranNumber && ranNumber < 30) {
                            level.setBlockStateAt(x, newY, z, places[7]);
                        } else if (30 <= ranNumber && ranNumber < 32) {
                            level.setBlockStateAt(x, newY, z, places[8]);
                        } else if (32 <= ranNumber && ranNumber < 34) {
                            level.setBlockStateAt(x, newY, z, places[9]);
                        } else if (34 <= ranNumber && ranNumber < 36) {
                            level.setBlockStateAt(x, newY, z, places[10]);
                        } else if (36 <= ranNumber && ranNumber < 46) {
                            level.setBlockStateAt(x, newY, z, places[11]);
                        } else if (46 <= ranNumber && ranNumber < 86) {
                            level.setBlockStateAt(x, newY, z, places[12]);
                        } else if (86 <= ranNumber && ranNumber < 106) {
                            level.setBlockStateAt(x, newY, z, places[13]);
                        }
                    }
                }
            }
        }
    }
}

