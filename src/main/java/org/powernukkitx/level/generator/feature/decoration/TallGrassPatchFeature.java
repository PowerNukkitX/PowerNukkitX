package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockSnowLayer;
import org.powernukkitx.block.BlockShortGrass;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class TallGrassPatchFeature extends SurfaceGenerateFeature {

    private static final BlockState STATE = BlockShortGrass.PROPERTIES.getDefaultState();
    private static final BlockState SNOW_LAYER_STATE = BlockSnowLayer.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_tall_grass_feature";

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        int biomeId = context.getChunk().getBiomeId(7, context.getLevel().getHeightMap((context.getChunk().getX() << 4) + 7, (context.getChunk().getZ() << 4) + 7), 7);
        if (Registries.BIOME.getTags(biomeId).contains(BiomeTags.MOOSHROOM_ISLAND)) {
            return;
        }

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int x = random.nextBoundedInt(15);
        int z = random.nextBoundedInt(15);
        int y = context.getChunk().getHeightMap(x, z);
        int worldX = (chunkX << 4) + x;
        int worldZ = (chunkZ << 4) + z;
        Position position = new Position(worldX, y, worldZ, chunk.getLevel());
        while (!isSupportValid(chunk.getBlockState(x, y, z).toBlock(position)) && y > SEA_LEVEL) {
            y--;
            position.setY(y);
        }

        if (y < SEA_LEVEL || !isSupportValid(chunk.getBlockState(x, y, z).toBlock(position))) {
            return;
        }

        BlockManager manager = new BlockManager(chunk.getLevel());
        Block above = manager.getBlockIfCachedOrLoaded(worldX, y + 1, worldZ);
        if (!above.isAir() && !above.getId().equals(BlockID.SNOW_LAYER)) {
            return;
        }

        BlockManager object = new BlockManager(chunk.getLevel());
        place(object, worldX, y + 1, worldZ);
        queueObject(chunk, object);
    }

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        if (manager.getBlockIdAt(x, y, z).equals(BlockID.SNOW_LAYER)) {
            if (manager.getBlockIdAt(x, y, z, 1).equals(BlockID.AIR)) {
                manager.setBlockStateAt(x, y, z, SNOW_LAYER_STATE);
                manager.getLevel().setBlockStateAt(x, y, z, 1, STATE);
            }
            return;
        }
        manager.setBlockStateAt(x, y, z, STATE);
    }

    @Override
    public int getBase() {
        return 10;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }
}
