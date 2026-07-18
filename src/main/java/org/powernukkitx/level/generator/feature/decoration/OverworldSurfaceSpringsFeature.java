package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLava;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.NukkitMath;

import java.util.Set;

/**
 * @author Buddelbubi
 * @since 2026/05/10
 * @see <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/placed_feature/spring_lava.json">Lava</a>
 * @see <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/placed_feature/spring_water.json">Water</a>
 */
public class OverworldSurfaceSpringsFeature extends GenerateFeature {

    public static final String NAME = "minecraft:overworld_surface_springs_feature";

    private static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    private static final BlockState LAVA = BlockLava.PROPERTIES.getDefaultState();

    private static final Set<String> VALID_BLOCKS = Set.of(
            BlockID.STONE,
            BlockID.GRANITE,
            BlockID.DIORITE,
            BlockID.ANDESITE,
            BlockID.DEEPSLATE,
            BlockID.TUFF,
            BlockID.CALCITE,
            BlockID.DIRT,
            BlockID.GRASS_BLOCK,
            BlockID.POWDER_SNOW
    );

    private static final BlockFace[] SPRING_NEIGHBORS = {
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.EAST
    };

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());
        BlockManager manager = new BlockManager(level);

        int minY = level.getMinHeight();
        placeSprings(manager, chunkX, chunkZ, 25, minY, Math.min(192, level.getMaxHeight()), WATER);
        placeSprings(manager, chunkX, chunkZ, 20, minY, Math.max(minY, level.getMaxHeight() - 8), LAVA);

        queueObject(chunk, manager);
    }

    private void placeSprings(BlockManager manager, int chunkX, int chunkZ, int count, int minY, int maxY, BlockState fluid) {
        if (maxY < minY) {
            return;
        }

        int sourceX = chunkX << 4;
        int sourceZ = chunkZ << 4;

        for (int i = 0; i < count; i++) {
            int x = sourceX + random.nextInt(14) + 1;
            int y = fluid == LAVA ? nextVeryBiasedToBottomY(minY, maxY) : nextUniformY(minY, maxY);
            int z = sourceZ + random.nextInt(14) + 1;

            if (canPlaceSpring(manager, x, y, z)) {
                manager.setBlockStateAt(x, y, z, fluid);
                manager.addHook(() -> manager.getLevel().scheduleUpdate(manager.getLevel().getBlock(x, y, z), 1));
            }
        }
    }

    private int nextUniformY(int minY, int maxY) {
        return minY + random.nextInt(maxY - minY + 1);
    }

    private int nextVeryBiasedToBottomY(int minY, int maxY) {
        if (maxY - minY - 8 + 1 <= 0) {
            return minY;
        }

        int upperInclusive = NukkitMath.randomRange(random, minY + 8, maxY);
        int biasedUpperInclusive = NukkitMath.randomRange(random, minY, upperInclusive - 1);
        return NukkitMath.randomRange(random, minY, biasedUpperInclusive - 1 + 8);
    }

    private boolean canPlaceSpring(BlockManager manager, int x, int y, int z) {
        if (!isValidBlock(manager, x, y + 1, z, VALID_BLOCKS) || !isValidBlock(manager, x, y - 1, z, VALID_BLOCKS)) {
            return false;
        }

        Block current = manager.getBlockIfCachedOrLoaded(x, y, z);
        if (!current.isAir() && !VALID_BLOCKS.contains(current.getId())) {
            return false;
        }

        int rockCount = 0;
        int holeCount = 0;
        for (BlockFace face : SPRING_NEIGHBORS) {
            Block block = manager.getBlockIfCachedOrLoaded(
                    x + face.getXOffset(),
                    y + face.getYOffset(),
                    z + face.getZOffset()
            );
            if (VALID_BLOCKS.contains(block.getId())) {
                rockCount++;
            }
            if (block.isAir()) {
                holeCount++;
            }
        }

        return rockCount == 4 && holeCount == 1;
    }

    private boolean isValidBlock(BlockManager manager, int x, int y, int z, Set<String> validBlocks) {
        return validBlocks.contains(manager.getBlockIfCachedOrLoaded(x, y, z).getId());
    }

    @Override
    public String name() {
        return NAME;
    }
}
