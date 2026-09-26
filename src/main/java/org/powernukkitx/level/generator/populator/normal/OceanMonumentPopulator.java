package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.OceanMonumentPieces;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.object.structures.utils.StructureStart;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.OceanMonumentPlacement;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.random.NukkitRandom;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.List;

public class OceanMonumentPopulator extends Populator implements PopulatorStructure {
    public static final String NAME = "normal_ocean_monument";

    public static final StructurePlacement PLACEMENT = new OceanMonumentPlacement(StructurePlacement.PlacementSettings.builder()
            .salt(10387313L)
            .minDistance(5)
            .maxDistance(32)
            .isBiomeValid(biome -> switch (biome) {
                case BiomeID.DEEP_OCEAN,
                     BiomeID.DEEP_WARM_OCEAN,
                     BiomeID.DEEP_LUKEWARM_OCEAN,
                     BiomeID.DEEP_COLD_OCEAN,
                     BiomeID.DEEP_FROZEN_OCEAN -> true;
                default -> false;
            })
            .build());
    private static final int START_SEARCH_RADIUS = 2;

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
        Xoroshiro128 placementRandom = new Xoroshiro128(seed);
        NukkitRandom chunkRandom = new NukkitRandom(seed);
        int r1 = chunkRandom.nextInt();
        int r2 = chunkRandom.nextInt();
        List<StructureAabbVolumes.DynamicStructure> aabbStructures = new ArrayList<>();

        for (int startChunkX = chunkX - START_SEARCH_RADIUS; startChunkX <= chunkX + START_SEARCH_RADIUS; startChunkX++) {
            for (int startChunkZ = chunkZ - START_SEARCH_RADIUS; startChunkZ <= chunkZ + START_SEARCH_RADIUS; startChunkZ++) {
                if (!PLACEMENT.canGenerate(seed, placementRandom, startChunkX, startChunkZ, level.getBiomePicker())) {
                    continue;
                }

                final int originChunkX = startChunkX;
                final int originChunkZ = startChunkZ;
                OceanMonumentStart start = context.getGenerator().getStructureStartCache().getOrCreate(
                        OceanMonumentStart.class,
                        originChunkX,
                        originChunkZ,
                        () -> createStart(level, originChunkX, originChunkZ)
                );
                if (start.isValid() && start.getBoundingBox().intersects(chunkBounds)) {
                    NukkitRandom postProcessRandom = new NukkitRandom((long) chunkX * r1 ^ (long) chunkZ * r2 ^ seed);
                    start.postProcessPieces(manager, postProcessRandom, chunkBounds, chunkX, chunkZ);
                    aabbStructures.add(StructureAabbVolumes.DynamicStructure.fromStart(start));
                }
            }
        }

        StructureAabbVolumes.replaceDynamic(chunk, "minecraft:monument", aabbStructures);
        queueObject(chunk, manager);
    }

    private static OceanMonumentStart createStart(Level level, int chunkX, int chunkZ) {
        BlockManager manager = new BlockManager(level);
        OceanMonumentStart start = new OceanMonumentStart(manager, chunkX, chunkZ);
        start.generatePieces(manager, chunkX, chunkZ);
        return start;
    }

    @Override
    public String name() {
        return NAME;
    }

    public static class OceanMonumentStart extends StructureStart {

        private boolean isCreated;

        public OceanMonumentStart(BlockManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override //\\ OceanMonumentStart::createMonument(Dimension &,Random &,int,int)
        public void generatePieces(BlockManager level, int chunkX, int chunkZ) {
            this.pieces.add(new OceanMonumentPieces.MonumentBuilding(
                    this.random,
                    (chunkX << 4) - 21,
                    (chunkZ << 4) - 21,
                    BlockFace.Plane.HORIZONTAL.random(this.random)
            ));
            this.calculateBoundingBox();

            this.isCreated = true;
        }

        @Override //\\ OceanMonumentStart::postProcess(BlockSource *,Random &,BoundingBox const &)
        public void postProcess(BlockManager level, RandomSourceProvider random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.ensureCreated(level);
            super.postProcess(level, random, boundingBox, chunkX, chunkZ);
        }

        @Override
        public void postProcessPieces(BlockManager level, RandomSourceProvider random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.ensureCreated(level);
            super.postProcessPieces(level, random, boundingBox, chunkX, chunkZ);
        }

        private void ensureCreated(BlockManager level) {
            if (!this.isCreated) {
                this.pieces.clear();
                this.generatePieces(level, this.getChunkX(), this.getChunkZ());
            }
        }

        @Override //\\ OceanMonumentStart::getType(void) // 4
        public String getType() {
            return "minecraft:monument";
        }
    }
}
