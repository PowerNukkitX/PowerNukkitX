package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockDandelion;
import cn.nukkit.block.BlockPoppy;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.Supportable;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ScatterOverworldFlowerFeature extends CountGenerateFeature implements Supportable {

    public static final String NAME = "minecraft:scatter_overworld_flower_feature";

    private static final BlockState DANDELION = BlockDandelion.PROPERTIES.getDefaultState();
    private static final BlockState POPPY = BlockPoppy.PROPERTIES.getDefaultState();

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

        BlockManager object = new BlockManager(level);
        int radius = NukkitMath.randomRange(random, 2, 3);
        int radiusSquared = radius * radius;

        BlockState state = random.nextBoolean() ? DANDELION : POPPY;

        for (int x = sourceX - radius; x <= sourceX + radius; x++) {
            for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                if (!level.isChunkGenerated(x >> 4, z >> 4)) {
                    continue;
                }
                int dx = x - sourceX;
                int dz = z - sourceZ;
                if (dx * dx + dz * dz > radiusSquared || random.nextFloat() >= 0.2f) {
                    continue;
                }

                int y = level.getHeightMap(x, z);
                BlockState targetState = level.getBlockStateAt(x, y + 1, z);
                if (targetState != BlockAir.STATE) {
                    continue;
                }
                Block support = level.getBlockStateAt(x, y, z).toBlock(new Position(x, y, z, level));
                if (isSupportDirt(support)) {
                    object.setBlockStateAt(x, y + 1, z, state);
                }
            }
        }

        queueObject(chunk, object);
    }


    @Override
    public int getBase() {
        return -7;
    }

    @Override
    public int getRandom() {
        return 8;
    }

    @Override
    public String name() {
        return NAME;
    }
}
