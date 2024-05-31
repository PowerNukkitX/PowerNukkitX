package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.*;
import cn.nukkit.block.property.enums.TallGrassType;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.TALL_GRASS_TYPE;

public class LegacyTallGrass {
    private static final BlockState[] places = {//total 106
            BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.DEFAULT),// 50
            BlockTallgrass.PROPERTIES.getBlockState(TALL_GRASS_TYPE, TallGrassType.TALL),// 30
            BlockYellowFlower.PROPERTIES.getDefaultState(),// 10
            BlockPoppy.PROPERTIES.getDefaultState(),// 10
            BlockAzureBluet.PROPERTIES.getDefaultState(),// 5
            BlockOxeyeDaisy.PROPERTIES.getDefaultState(),// 5
            BlockAllium.PROPERTIES.getDefaultState(),// 2
            BlockCornflower.PROPERTIES.getDefaultState(),// 2
            BlockBlueOrchid.PROPERTIES.getDefaultState(),// 2
            BlockLilyOfTheValley.PROPERTIES.getDefaultState(),// 2
            BlockRedTulip.PROPERTIES.getDefaultState(),// 2
            BlockOrangeTulip.PROPERTIES.getDefaultState(),// 2
            BlockPinkTulip.PROPERTIES.getDefaultState(),// 2
            BlockWhiteTulip.PROPERTIES.getDefaultState(),// 2
    };
    /**
     * @deprecated 
     */
    

    public static void growGrass(BlockManager level, Vector3 pos, RandomSourceProvider random) {
        int $1 = pos.getFloorY() + 1;
        int $2 = pos.getFloorX() - 2;
        int $3 = pos.getFloorZ() - 2;
        int $4 = pos.getFloorX() + 2;
        int $5 = pos.getFloorZ() + 2;
        for (int $6 = minx; x <= maxx; x++) {
            for (int $7 = minz; z <= maxz; z++) {
                int $8 = y + random.nextInt(2) * (random.nextBoolean() ? -1 : 1);
                if (random.nextBoolean()) {
                    if (Objects.equals(level.getBlockIdAt(x, newY, z), Block.AIR) && Objects.equals(level.getBlockIdAt(x, newY - 1, z), Block.GRASS_BLOCK)) {
                        int $9 = (int) Math.round(random.nextGaussian() * 1000);
                        int $10 = Math.abs(ranNumber);
                        if (-300 <= ranNumber && ranNumber <= 300) {
                            level.setBlockStateAt(x, newY, z, places[0]);
                        } else if (300 <= absRn && absRn <= 500) {//-300 ~ -500 + 300 ~ 500
                            level.setBlockStateAt(x, newY, z, places[1]);
                        } else if (500 <= ranNumber && ranNumber < 600) {
                            level.setBlockStateAt(x, newY, z, places[2]);
                        } else if (-600 <= ranNumber && ranNumber <= -500) {
                            level.setBlockStateAt(x, newY, z, places[3]);
                        } else if (600 <= ranNumber && ranNumber < 700) {
                            level.setBlockStateAt(x, newY, z, places[4]);
                        } else if (-700 <= ranNumber && ranNumber < -600) {
                            level.setBlockStateAt(x, newY, z, places[5]);
                        } else if (-750 <= ranNumber && ranNumber < -700) {
                            level.setBlockStateAt(x, newY, z, places[6]);
                        } else if (-800 <= ranNumber && ranNumber < -750) {
                            level.setBlockStateAt(x, newY, z, places[7]);
                        } else if (-850 <= ranNumber && ranNumber < -800) {
                            level.setBlockStateAt(x, newY, z, places[8]);
                        } else if (-900 <= ranNumber && ranNumber < -850) {
                            level.setBlockStateAt(x, newY, z, places[9]);
                        } else if (-1000 <= ranNumber && ranNumber < -900) {
                            level.setBlockStateAt(x, newY, z, places[10]);
                        } else if (700 <= ranNumber && ranNumber < 800) {
                            level.setBlockStateAt(x, newY, z, places[11]);
                        } else if (800 <= ranNumber && ranNumber < 900) {
                            level.setBlockStateAt(x, newY, z, places[12]);
                        } else if (900 <= ranNumber && ranNumber < 1000) {
                            level.setBlockStateAt(x, newY, z, places[13]);
                        }
                    }
                }
            }
        }
    }
}

