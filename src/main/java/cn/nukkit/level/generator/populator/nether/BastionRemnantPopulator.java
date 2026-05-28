package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.bastion.BastionStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.placement.StructurePlacement;
import cn.nukkit.math.ChunkVector2;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class BastionRemnantPopulator extends Populator {

    public static final String NAME = "nether_bastion_remnant";
    protected static final int MAX_DISTANCE = NetherComplexPlacement.REGION_SIZE_CHUNKS;
    protected static final int MIN_DISTANCE = NetherComplexPlacement.EDGE_EXCLUSION_CHUNKS + 1;
    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(0x42415354494F4EL)
            .minDistance(MIN_DISTANCE)
            .maxDistance(MAX_DISTANCE)
            .build()) {
        @Override
        public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, int biome) {
            return biome != BiomeID.BASALT_DELTAS
                    && NetherComplexPlacement.isNetherComplexStart(levelSeed, chunkX, chunkZ, random)
                    && NetherComplexPlacement.shouldGenerateBastion(levelSeed, chunkX, chunkZ, random);
        }

        @Override
        public ChunkVector2 findNearestGenerationChunk(ChunkVector2 origin, RandomSourceProvider random, BiomePicker<?> biomePicker, int radius) {
            if (origin == null || random == null || biomePicker == null || radius < 0) {
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

                    int sampleX = (chunkX << 4) + 7;
                    int sampleZ = (chunkZ << 4) + 7;
                    int biome = biomePicker.pick(sampleX, 33, sampleZ).getBiomeId();
                    if (!canGenerate(levelSeed, random, chunkX, chunkZ, biome)) {
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

    private static final BastionStructure BASTION = new BastionStructure();

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
        int biome = chunk.getBiomeId(7, 33, 7);

        if (!PLACEMENT.canGenerate(seed, random, chunkX, chunkZ, biome)) {
            return;
        }

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = 33;
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, originY, originZ));
        BASTION.place(helper, random.fork());
    }

    @Override
    public String name() {
        return NAME;
    }
}
