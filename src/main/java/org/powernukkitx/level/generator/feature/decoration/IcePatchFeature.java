package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.BlockPackedIce;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.feature.CountGenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.utils.random.RandomSourceProvider;

import static org.powernukkitx.block.BlockID.AIR;
import static org.powernukkitx.block.BlockID.DIRT;
import static org.powernukkitx.block.BlockID.GRASS_BLOCK;
import static org.powernukkitx.block.BlockID.ICE;
import static org.powernukkitx.block.BlockID.PACKED_ICE;
import static org.powernukkitx.block.BlockID.SNOW;
import static org.powernukkitx.block.BlockID.SNOW_LAYER;

public class IcePatchFeature extends CountGenerateFeature {

    public static final String NAME = "minecraft:ice_patch_feature";

    private static final BlockState STATE_PATCH = BlockPackedIce.PROPERTIES.getDefaultState();

    @Override
    public int getBase() {
        return 1;
    }

    @Override
    public int getRandom() {
        return 2;
    }

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();

        int localX = 3 + random.nextInt(10);
        int localZ = 3 + random.nextInt(10);
        int sourceX = (chunk.getX() << 4) + localX;
        int sourceZ = (chunk.getZ() << 4) + localZ;
        int sourceY = chunk.getHeightMap(localX, localZ);

        if (level.getBiomeId(sourceX, sourceY, sourceZ) != BiomeID.ICE_PLAINS_SPIKES) {
            return;
        }

        BlockManager manager = new BlockManager(level);
        while (sourceY > level.getMinHeight() + 1 && manager.getBlockIfCachedOrLoaded(sourceX, sourceY, sourceZ).isAir()) {
            sourceY--;
        }

        int radius = 2 + random.nextInt(3);
        for (int x = sourceX - radius; x <= sourceX + radius; x++) {
            for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                int dx = x - sourceX;
                int dz = z - sourceZ;
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }

                for (int y = sourceY + 1; y >= sourceY - 2; y--) {
                    String id = manager.getBlockIfCachedOrLoaded(x, y, z).getId();
                    if (isReplaceable(id)) {
                        manager.setBlockStateAt(x, y, z, STATE_PATCH);
                    } else if (!id.equals(AIR)) {
                        break;
                    }
                }
            }
        }

        queueObject(chunk, manager);
    }

    private static boolean isReplaceable(String id) {
        return id.equals(SNOW)
                || id.equals(SNOW_LAYER)
                || id.equals(ICE)
                || id.equals(PACKED_ICE)
                || id.equals(DIRT)
                || id.equals(GRASS_BLOCK);
    }


    @Override
    public String name() {
        return NAME;
    }
}
