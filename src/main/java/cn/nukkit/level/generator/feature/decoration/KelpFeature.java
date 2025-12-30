package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockKelp;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.KELP_AGE;
import static cn.nukkit.level.generator.feature.river.DiscGenerateFeature.STATE_STILL_WATER;

public class KelpFeature extends WaterFoliageFeature {

    public final static String NAME = "minecraft:kelp_feature";

    private static final BlockProperties KELP_PROPERTIES = BlockKelp.PROPERTIES;
    private static final BlockState STATE_KELP_MAX_AGE = BlockKelp.PROPERTIES.getBlockState(KELP_AGE.createValue(KELP_AGE.getMax()));

    private SimplexNoise noise;

    @Override
    public int getBase() {
        return 30;
    }

    @Override
    public int getRandom() {
        return 15;
    }

    @Override
    protected void placeBlock(int x, int y, int z, IChunk chunk, RandomSourceProvider random) {

        if(noise == null) noise = new SimplexNoise(random, -7, new float[]{ 1 });

        if(noise.getValue(x, 0, z) < 0) return;

        if (!canStay(x, y + 1, z, chunk)) {
            return;
        }

        if (!chunk.getBlockState(x, y - 1, z).toBlock().isSolid()) {
            if (!Objects.equals(chunk.getBlockState(x, y - 1, z).getIdentifier(), BlockID.KELP)) {
                return;
            }
        }

        int height = random.nextBoundedInt(10) + 1;
        for (int h = 0; h <= height; h++) {
            if (canStay(x, y + h, z, chunk)) {
                if (h == height || !chunk.getBlockState(x, y + h + 2, z).equals(STATE_STILL_WATER)) {
                    chunk.setBlockState(x, y + h, z, KELP_PROPERTIES.getBlockState(KELP_AGE.createValue(20 + random.nextBoundedInt(4))));
                    chunk.getAndSetBlockState(x, y + h, z, STATE_STILL_WATER, 1);
                    return;
                } else {
                    chunk.setBlockState(x, y + h, z, STATE_KELP_MAX_AGE);
                    chunk.getAndSetBlockState(x, y + h, z, STATE_STILL_WATER, 1);
                }
            } else {
                return;
            }
        }
    }

    protected boolean canStay(int x, int y, int z, IChunk chunk) {
        return chunk.getBlockState(x, y, z).equals(STATE_STILL_WATER);
    }

    @Override
    public String name() {
        return NAME;
    }
}
