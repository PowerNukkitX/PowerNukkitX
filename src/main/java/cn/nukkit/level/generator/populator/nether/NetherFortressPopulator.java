package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.NetherBridgePieces;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.generator.object.structures.utils.StructurePiece;
import cn.nukkit.level.generator.object.structures.utils.StructureStart;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.placement.StructurePlacement;
import cn.nukkit.math.ChunkVector2;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.List;

public class NetherFortressPopulator extends Populator {

    public static final String NAME = "nether_nether_fortress";
    protected static final int MAX_DISTANCE = NetherComplexPlacement.REGION_SIZE_CHUNKS;
    protected static final int MIN_DISTANCE = NetherComplexPlacement.EDGE_EXCLUSION_CHUNKS + 1;
    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(0x42415354494F4EL)
            .minDistance(MIN_DISTANCE)
            .maxDistance(MAX_DISTANCE)
            .build()) {
        @Override
        public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, int biome) {
            return NetherComplexPlacement.isNetherComplexStart(levelSeed, chunkX, chunkZ, random)
                    && !NetherComplexPlacement.shouldGenerateBastion(levelSeed, chunkX, chunkZ, random);
        }

        @Override
        public ChunkVector2 findNearestGenerationChunk(ChunkVector2 origin, RandomSourceProvider random, BiomePicker<?> biomePicker, int radius) {
            if (origin == null || random == null || radius < 0) {
                return null;
            }

            long levelSeed = random.getSeed();
            int originX = origin.getX();
            int originZ = origin.getZ();
            int minChunkX = originX - radius;
            int maxChunkX = originX + radius;
            int minChunkZ = originZ - radius;
            int maxChunkZ = originZ + radius;

            int minRegionX = Math.floorDiv(minChunkX, MAX_DISTANCE);
            int maxRegionX = Math.floorDiv(maxChunkX, MAX_DISTANCE);
            int minRegionZ = Math.floorDiv(minChunkZ, MAX_DISTANCE);
            int maxRegionZ = Math.floorDiv(maxChunkZ, MAX_DISTANCE);

            ChunkVector2 nearest = null;
            long nearestDistanceSq = Long.MAX_VALUE;
            for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
                int originRegionX = regionX * MAX_DISTANCE;
                for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                    int originRegionZ = regionZ * MAX_DISTANCE;
                    random.setSeed((levelSeed ^ 0x42415354494F4EL) + Level.chunkHash(regionX, regionZ));
                    int chunkX = originRegionX + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE);
                    int chunkZ = originRegionZ + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE);
                    if (chunkX < minChunkX || chunkX > maxChunkX || chunkZ < minChunkZ || chunkZ > maxChunkZ) {
                        continue;
                    }
                    if (!canGenerate(levelSeed, random, chunkX, chunkZ, 0)) {
                        continue;
                    }

                    long dx = (long) chunkX - originX;
                    long dz = (long) chunkZ - originZ;
                    long distanceSq = dx * dx + dz * dz;
                    if (distanceSq < nearestDistanceSq
                            || (distanceSq == nearestDistanceSq && (nearest == null
                            || chunkX < nearest.getX()
                            || (chunkX == nearest.getX() && chunkZ < nearest.getZ())))) {
                        nearestDistanceSq = distanceSq;
                        nearest = new ChunkVector2(chunkX, chunkZ);
                    }
                }
            }

            random.setSeed(levelSeed);
            return nearest;
        }
    };

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if (!chunk.isNether()) {
            return;
        }
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        long seed = level.getSeed();

        if (PLACEMENT.canGenerate(seed, random, chunkX, chunkZ, 0)) {
            random.setSeed(seed);
            int r1 = random.nextInt();
            int r2 = random.nextInt();
            BlockManager manager = new BlockManager(level);

            NetherFortressStart start = new NetherFortressStart(manager, chunkX, chunkZ);
            start.generatePieces(manager, chunkX, chunkZ);

            if (start.isValid()) {
                BoundingBox boundingBox = start.getBoundingBox();
                for (int cx = boundingBox.x0 >> 4; cx <= boundingBox.x1 >> 4; cx++) {
                    for (int cz = boundingBox.z0 >> 4; cz <= boundingBox.z1 >> 4; cz++) {
                        NukkitRandom rand = new NukkitRandom((long) cx * r1 ^ (long) cz * r2 ^ seed);
                        int x = cx << 4;
                        int z = cz << 4;
                        start.postProcess(manager, rand, new BoundingBox(x, z, x + 15, z + 15), cx, cz);
                    }
                }
                queueObject(chunk, manager);
            }
        }
    }

    public static class NetherFortressStart extends StructureStart {

        public NetherFortressStart(BlockManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
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
            return "Fortress";
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
