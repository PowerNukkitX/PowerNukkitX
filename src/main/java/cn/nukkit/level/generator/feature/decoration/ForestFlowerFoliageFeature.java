package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ForestFlowerFoliageFeature extends CountGenerateFeature {

    public static final String NAME = "minecraft:forest_first_foliage_feature";

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        int randomX = random.nextInt(15);
        int randomZ = random.nextInt(15);
        int sourceX = (chunkX << 4) + randomX;
        int sourceZ = (chunkZ << 4) + randomZ;

        int flower = random.nextInt(4);

        BlockManager object = new BlockManager(chunk.getLevel());

        int radius = NukkitMath.randomRange(random, 0, 2);
        for (int x = sourceX - radius; x <= sourceX + radius; x++) {
            for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                if(!level.isChunkGenerated( x >> 4, z >> 4)) return;
                if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= radius * radius) {
                    if(random.nextFloat() < 0.3f) {
                        int depth = 0;
                        int height = level.getHeightMap(x, z);
                        BlockState topBlockState = level.getBlockStateAt(x, height, z);
                        while (topBlockState.toBlock() instanceof BlockLeaves || topBlockState == BlockAir.STATE) {
                            topBlockState = level.getBlockStateAt(x, height - (++depth), z);
                        }
                        if(isSupportValid(topBlockState.toBlock(new Position(x, (height - depth), z, level)))) {
                            populateFlower(flower, object, x, (height - depth) + 1, z);
                        }
                    }
                }
            }
        }
        queueObject(chunk, object);
    }

    private void populateFlower(int flower, BlockManager level, int x, int y, int z) {
        switch (flower) {
            case 1 -> level.setBlockStateAt(x, y, z, BlockDandelion.PROPERTIES.getDefaultState());
            case 2 -> level.setBlockStateAt(x, y, z, BlockLilyOfTheValley.PROPERTIES.getDefaultState());
            case 3 -> {
                level.setBlockStateAt(x, y, z, BlockLilac.PROPERTIES.getDefaultState());
                level.setBlockStateAt(x, y+1, z, BlockLilac.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true)));
            }
            case 4 -> {
                level.setBlockStateAt(x, y, z, BlockPeony.PROPERTIES.getDefaultState());
                level.setBlockStateAt(x, y+1, z, BlockPeony.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true)));
            }
            case 0 -> {
                level.setBlockStateAt(x, y, z, BlockRoseBush.PROPERTIES.getDefaultState());
                level.setBlockStateAt(x, y+1, z, BlockRoseBush.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true)));
            }
        }
    }

    @Override
    public int getBase() {
        return -1;
    }

    @Override
    public int getRandom() {
        return 8;
    }

    public boolean isSupportValid(Block support) {
        return support instanceof BlockGrassBlock &&
                Registries.BIOME.get(support.getLevel().getBiomeId(
                        support.getFloorX(),
                        support.getFloorY(),
                        support.getFloorZ())).getTags().contains(BiomeTags.FOREST);
    }

    @Override
    public String name() {
        return NAME;
    }
}
