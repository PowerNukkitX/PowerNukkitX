package org.powernukkitx.level.generator.populator.nether;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.NetherBridgePieces;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.object.structures.utils.StructurePiece;
import org.powernukkitx.level.generator.object.structures.utils.StructureStart;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.ChunkVector2;
import org.powernukkitx.utils.random.NukkitRandom;
import org.powernukkitx.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.List;

public class NetherFortressPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "nether_nether_fortress";
    protected static final int MAX_DISTANCE = NetherComplexPlacement.REGION_SIZE_CHUNKS;
    protected static final int MIN_DISTANCE = NetherComplexPlacement.EDGE_EXCLUSION_CHUNKS;
    private static final int START_SEARCH_RADIUS = 8;
    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(NetherComplexPlacement.PLACEMENT_SALT)
            .minDistance(MIN_DISTANCE)
            .maxDistance(MAX_DISTANCE)
            .build()) {
        @Override
        public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, int biome) {
            return NetherComplexPlacement.isNetherComplexStart(levelSeed, chunkX, chunkZ)
                    && !NetherComplexPlacement.shouldGenerateBastion(levelSeed, chunkX, chunkZ);
        }

        @Override
        public ChunkVector2 findNearestGenerationChunk(ChunkVector2 origin, RandomSourceProvider random, BiomePicker<?> biomePicker, int radius) {
            return NetherComplexPlacement.findNearestGenerationChunk(origin, random, biomePicker, radius, false);
        }
    };

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        if (!chunk.isNether()) {
            return;
        }
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        long seed = level.getSeed();
        BlockManager manager = new BlockManager(level);
        BoundingBox chunkBounds = new BoundingBox(chunkX << 4, chunkZ << 4, (chunkX << 4) + 15, (chunkZ << 4) + 15);
        random.setSeed(seed);
        int r1 = random.nextInt();
        int r2 = random.nextInt();
        List<StructureAabbVolumes.DynamicStructure> aabbStructures = new ArrayList<>();

        for (int startChunkX = chunkX - START_SEARCH_RADIUS; startChunkX <= chunkX + START_SEARCH_RADIUS; startChunkX++) {
            for (int startChunkZ = chunkZ - START_SEARCH_RADIUS; startChunkZ <= chunkZ + START_SEARCH_RADIUS; startChunkZ++) {
                if (!PLACEMENT.canGenerate(seed, random, startChunkX, startChunkZ, 0)) {
                    continue;
                }

                final int originChunkX = startChunkX;
                final int originChunkZ = startChunkZ;
                NetherFortressStart start = context.getGenerator().getStructureStartCache().getOrCreate(
                        NetherFortressStart.class,
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

        StructureAabbVolumes.replaceDynamic(chunk, "minecraft:fortress", aabbStructures);
        queueObject(chunk, manager);
    }

    private static NetherFortressStart createStart(Level level, int chunkX, int chunkZ) {
        BlockManager manager = new BlockManager(level);
        NetherFortressStart start = new NetherFortressStart(
                manager,
                chunkX,
                chunkZ,
                NetherComplexPlacement.createPostSelectionRandom(level.getSeed(), chunkX, chunkZ)
        );
        start.generatePieces(manager, chunkX, chunkZ);
        return start;
    }

    public static class NetherFortressStart extends StructureStart {

        public NetherFortressStart(BlockManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        private NetherFortressStart(BlockManager level, int chunkX, int chunkZ, RandomSourceProvider random) {
            super(level, chunkX, chunkZ, random);
        }

        @Override
        public void generatePieces(BlockManager level, int chunkX, int chunkZ) {
            NetherBridgePieces.StartPiece start = new NetherBridgePieces.StartPiece(this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.pieces.add(start);
            start.addChildren(start, this.pieces, this.random);

            List<StructurePiece> pendingChildren = start.pendingChildren;
            while (!pendingChildren.isEmpty()) {
                pendingChildren.remove(this.random.nextBoundedInt(pendingChildren.size()-1))
                        .addChildren(start, this.pieces, this.random);
            }

            this.calculateBoundingBox();
            this.moveInsideHeights(this.random, 48, 70);
        }

        @Override
        public String getType() {
            return "minecraft:fortress";
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
