package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.MineshaftPieces;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.object.structures.utils.StructurePiece;
import org.powernukkitx.level.generator.object.structures.utils.StructureStart;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.BedrockRandom;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;

import java.util.ArrayList;
import java.util.List;

public class MineshaftPopulator extends Populator implements PopulatorStructure {
    public static final String NAME = "normal_mineshaft";

    private static final float PROBABILITY = 0.004f;
    private static final int DISTANCE_BOUND = 80;
    private static final int START_SEARCH_RADIUS = 7;
    private static final int START_CACHE_LIMIT = 8192;
    private static final ThreadLocal<MineshaftPlacementCache> PLACEMENT_CACHE = ThreadLocal.withInitial(MineshaftPlacementCache::new);

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        long seed = level.getSeed();
        BlockManager manager = new BlockManager(level);
        BoundingBox chunkBounds = new BoundingBox(chunkX << 4, chunkZ << 4, (chunkX << 4) + 15, (chunkZ << 4) + 15);
        MineshaftPlacementCache placementCache = PLACEMENT_CACHE.get();
        placementCache.setLevelSeed(seed);
        random.setSeed(seed);
        List<StructureAabbVolumes.DynamicStructure> aabbStructures = new ArrayList<>();

        for (int startChunkX = chunkX - START_SEARCH_RADIUS; startChunkX <= chunkX + START_SEARCH_RADIUS; startChunkX++) {
            for (int startChunkZ = chunkZ - START_SEARCH_RADIUS; startChunkZ <= chunkZ + START_SEARCH_RADIUS; startChunkZ++) {
                if (!placementCache.isStart(startChunkX, startChunkZ)) {
                    continue;
                }

                final int originChunkX = startChunkX;
                final int originChunkZ = startChunkZ;
                MineshaftStart start = context.getGenerator().getStructureStartCache().getOrCreate(
                        MineshaftStart.class,
                        originChunkX,
                        originChunkZ,
                        () -> createStart(level, originChunkX, originChunkZ)
                );
                if (start.isValid() && start.getBoundingBox().intersects(chunkBounds)) {
                    start.postProcessPieces(manager, random, chunkBounds, chunkX, chunkZ);
                    aabbStructures.add(StructureAabbVolumes.DynamicStructure.fromStart(start));
                }
            }
        }

        StructureAabbVolumes.replaceDynamic(chunk, "minecraft:mineshaft", aabbStructures);

        for (Block block : manager.getBlocks()) {
            level.getOrGenerateChunk(block.getChunkX(), block.getChunkZ());
            if (block.isAir()) {
                if (level.getBlock(block).getId() == Block.WATER) {
                    manager.setBlockStateAt(block, BlockWater.PROPERTIES.getDefaultState());
                } else if (block.up().getId() == Block.WATER) {
                    var seaFloor = seaFloorBlockFor(level, block);
                    if (seaFloor != null) {
                        manager.setBlockStateAt(block, seaFloor);
                    }
                }
            }
        }
        queueObject(chunk, manager);
    }

    static boolean isMineshaftStart(long levelSeed, int chunkX, int chunkZ) {
        MineshaftPlacementCache cache = PLACEMENT_CACHE.get();
        cache.setLevelSeed(levelSeed);
        return cache.isStart(chunkX, chunkZ);
    }

    /**
     * Caches deterministic mineshaft placement decisions for a generation worker.
     *
     * @author Curse
     */
    private static final class MineshaftPlacementCache {
        private final BedrockRandom random = new BedrockRandom(0);
        private final Long2ByteOpenHashMap starts = new Long2ByteOpenHashMap();
        private int levelSeed;
        private int r1;
        private int r2;
        private boolean initialized;

        private void setLevelSeed(long seed) {
            int value = (int) seed;
            if (this.initialized && this.levelSeed == value) {
                return;
            }

            this.levelSeed = value;
            this.random.setSeed(value);
            this.r1 = this.random.nextInt();
            this.r2 = this.random.nextInt();
            this.starts.clear();
            this.initialized = true;
        }

        private boolean isStart(int chunkX, int chunkZ) {
            long hash = Level.chunkHash(chunkX, chunkZ);
            byte cached = this.starts.get(hash);
            if (cached != 0) {
                return cached == 2;
            }

            this.random.setSeed(chunkX * this.r1 ^ chunkZ * this.r2 ^ this.levelSeed);
            this.random.nextInt();
            boolean result = this.random.nextFloat() < PROBABILITY
                    && this.random.nextInt(DISTANCE_BOUND) < Math.max(Math.abs(chunkX), Math.abs(chunkZ));

            if (this.starts.size() >= START_CACHE_LIMIT) {
                this.starts.clear();
            }
            this.starts.put(hash, result ? (byte) 2 : (byte) 1);
            return result;
        }
    }

    private static MineshaftStart createStart(Level level, int chunkX, int chunkZ) {
        BlockManager manager = new BlockManager(level);
        MineshaftStart start = new MineshaftStart(manager, chunkX, chunkZ);
        start.generatePieces(manager, chunkX, chunkZ);
        return start;
    }

    /** Resolve the biome's sea-floor block at this position, or null if the biome lacks surface-material data. */
    private static org.powernukkitx.block.BlockState seaFloorBlockFor(Level level, Block block) {
        var biome = Registries.BIOME.get(level.getBiomeId(block.getFloorX(), block.getFloorY(), block.getFloorZ())).second();
        var genData = biome == null ? null : biome.getChunkGenData();
        var surfaceBuilder = genData == null ? null : genData.getSurfaceBuilderData();
        var surfaceMaterial = surfaceBuilder == null ? null : surfaceBuilder.getSurfaceMaterial();
        if (surfaceMaterial == null || surfaceMaterial.getSeaFloorBlock() == null) {
            return null;
        }
        return Registries.BLOCKSTATE.get(surfaceMaterial.getSeaFloorBlock().getRuntimeId());
    }

    @Override
    public String name() {
        return NAME;
    }

    public static class MineshaftStart extends StructureStart {

        public MineshaftStart(BlockManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override
        public void generatePieces(BlockManager level, int chunkX, int chunkZ) {
            var biomePicker = level.getLevel().getBiomePicker();
            int biome = biomePicker instanceof OverworldBiomePicker overworldBiomePicker
                    ? overworldBiomePicker.pickRaw(chunkX << 4, 0, chunkZ << 4).getBiomeId()
                    : biomePicker.pick(chunkX << 4, 0, chunkZ << 4).getBiomeId();
            MineshaftPieces.Type type = isBadlandsBiome(biome) ? MineshaftPieces.Type.MESA : MineshaftPieces.Type.NORMAL;

            MineshaftPieces.MineshaftRoom start = new MineshaftPieces.MineshaftRoom(0, this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2, type);
            this.pieces.add(start);
            start.addChildren(start, this.pieces, this.random);
            this.calculateBoundingBox();

            if (type == MineshaftPieces.Type.MESA) {
                int offset = 64 - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 + 5;
                this.boundingBox.move(0, offset, 0);
                for (StructurePiece piece : this.pieces) {
                    piece.move(0, offset, 0);
                }
            } else {
                this.moveBelowSeaLevel(this.random, 10);
            }
        }

        private static boolean isBadlandsBiome(int biome) {
            return biome >= BiomeID.MESA && biome <= BiomeID.MESA_PLATEAU
                    || biome >= BiomeID.MESA_BRYCE && biome <= BiomeID.MESA_PLATEAU_MUTATED;
        }

        @Override //\\ MineshaftStart::getType(void) // 3
        public String getType() {
            return "minecraft:mineshaft";
        }
    }
}
