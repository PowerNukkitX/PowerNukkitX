package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockSeagrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.SeaGrassType;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.feature.river.DiscGenerateFeature.STATE_STILL_WATER;

public class SeagrassRiverGenerateFeature extends WaterFoliageFeature {

    private final static BlockState STATE_SEAGRASS = BlockSeagrass.PROPERTIES.getDefaultState();
    private final static BlockState STATE_TALL_SEAGRASS_TOP = BlockSeagrass.PROPERTIES.getBlockState(CommonBlockProperties.SEA_GRASS_TYPE.createValue(SeaGrassType.DOUBLE_TOP));
    private final static BlockState STATE_TALL_SEAGRASS_BOT = BlockSeagrass.PROPERTIES.getBlockState(CommonBlockProperties.SEA_GRASS_TYPE.createValue(SeaGrassType.DOUBLE_BOT));

    public static final String NAME = "minecraft:river_after_surface_seagrass_feature_rules";


    @Override
    protected boolean canStay(int x, int y, int z, IChunk chunk) {
        return canStay(x, y, z, chunk, false);
    }

    protected boolean canStay(int x, int y, int z, IChunk chunk, boolean tallSeagrass) {
        if (tallSeagrass) {
            return chunk.getBlockState(x, y, z).equals(STATE_STILL_WATER);
        } else {
            return chunk.getBlockState(x, y, z).equals(STATE_STILL_WATER) && chunk.getBlockState(x, y - 1, z).toBlock().isSolid();
        }
    }

    @Override
    protected void placeBlock(int x, int y, int z, IChunk chunk, RandomSourceProvider random) {
        if (y > 0 && canStay(x, y, z, chunk)) {
            if (random.nextDouble() < this.getTallSeagrassProbability()) {
                if (canStay(x, y + 1, z, chunk, true)) {
                    chunk.setBlockState(x, y, z, STATE_TALL_SEAGRASS_BOT);
                    chunk.setBlockState(x, y, z, STATE_STILL_WATER, 1);
                    chunk.setBlockState(x, y + 1, z, STATE_TALL_SEAGRASS_TOP);
                    chunk.setBlockState(x, y + 1, z, STATE_STILL_WATER, 1);
                }
            } else {
                chunk.setBlockState(x, y, z, STATE_SEAGRASS);
                chunk.setBlockState(x, y, z, STATE_STILL_WATER, 1);
            }
        }
    }

    @Override
    public int getBase() {
        return 24;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    public float getTallSeagrassProbability() {
        return 0.3f;
    }

    @Override
    public String name() {
        return NAME;
    }
}
