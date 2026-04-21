package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.random.RandomSourceProvider;

public class CoralMushroomFeature extends AbstractCoralFeature {
    public static final String NAME = "minecraft:coral_crust_feature";

    @Override
    public int getBase() {
        return 1;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected boolean placeFeature(IChunk chunk, RandomSourceProvider random, int x, int y, int z, BlockState coralState) {
        int height = random.nextInt(3) + 3;
        int width = random.nextInt(3) + 3;
        int length = random.nextInt(3) + 3;
        int sink = random.nextInt(3) + 1;

        for (int ix = 0; ix <= width; ix++) {
            for (int iy = 0; iy <= height; iy++) {
                for (int iz = 0; iz <= length; iz++) {
                    int px = x + ix;
                    int py = y + iy - sink;
                    int pz = z + iz;

                    if ((ix != 0 && ix != width || iy != 0 && iy != height)
                            && (iz != 0 && iz != length || iy != 0 && iy != height)
                            && (ix != 0 && ix != width || iz != 0 && iz != length)
                            && (ix == 0 || ix == width || iy == 0 || iy == height || iz == 0 || iz == length)
                            && (random.nextFloat() >= 0.1f)) {
                        placeCoralBlock(chunk, random, px, py, pz, coralState);
                    }
                }
            }
        }
        return true;
    }
}
