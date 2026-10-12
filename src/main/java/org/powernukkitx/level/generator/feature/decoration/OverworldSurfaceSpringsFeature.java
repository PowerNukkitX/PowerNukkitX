package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLava;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;

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
            BlockID.DEEPSLATE,
            BlockID.TUFF,
            BlockID.CALCITE,
            BlockID.SNOW,
            BlockID.POWDER_SNOW,
            BlockID.PACKED_ICE
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
        placeSprings(manager, chunk, 25, minY, WATER);
        placeSprings(manager, chunk, 20, minY, LAVA);

        queueObject(chunk, manager);
    }

    private void placeSprings(BlockManager manager, IChunk chunk, int count, int minY, BlockState fluid) {
        int sourceX = chunk.getX() << 4;
        int sourceZ = chunk.getZ() << 4;

        for (int i = 0; i < count; i++) {
            int localX = random.nextInt(16);
            int localZ = random.nextInt(16);
            int x = sourceX + localX;
            int y = fluid == LAVA ? nextLavaY(minY) : nextWaterY(minY);
            int z = sourceZ + localZ;

            if (fluid == LAVA && !isLavaBiomeAllowed(chunk, localX, y, localZ)) {
                continue;
            }

            if (canPlaceSpring(manager, x, y, z)) {
                manager.setBlockStateAt(x, y, z, fluid);
                manager.addHook(() -> manager.getLevel().scheduleUpdate(manager.getLevel().getBlock(x, y, z), 1));
            }
        }
    }

    private int nextWaterY(int minY) {
        int range = 192 - minY;
        return range <= 0 ? minY : minY + random.nextInt(range);
    }

    private int nextLavaY(int minY) {
        int range = 174 - minY;
        if (range <= 0) {
            return minY + 8;
        }

        double bias = random.nextDouble() * random.nextDouble() * random.nextDouble();
        return minY + 8 + (int) (bias * range);
    }

    private boolean isLavaBiomeAllowed(IChunk chunk, int x, int y, int z) {
        int biomeId = chunk.getBiomeId(x, y, z);
        if (biomeId == BiomeID.DEEP_DARK) {
            return false;
        }

        boolean frozen = Registries.BIOME.containsTag(BiomeTags.FROZEN, biomeId);
        boolean ocean = Registries.BIOME.containsTag(BiomeTags.OCEAN, biomeId);
        boolean icePlains = Registries.BIOME.containsTag(BiomeTags.ICE_PLAINS, biomeId);
        boolean mutated = Registries.BIOME.containsTag(BiomeTags.MUTATED, biomeId);
        return !(frozen && ocean || icePlains && mutated);
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
