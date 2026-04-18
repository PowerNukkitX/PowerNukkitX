package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.level.Level;
import cn.nukkit.utils.random.RandomSourceProvider;

public final class NetherComplexPlacement {

    public static final int REGION_SIZE_CHUNKS = 30;
    public static final int EDGE_EXCLUSION_CHUNKS = 4;
    private static final int CANDIDATE_OFFSET_MAX = REGION_SIZE_CHUNKS - EDGE_EXCLUSION_CHUNKS - 1;
    private static final long PLACEMENT_SALT = 0x42415354494F4EL;
    private static final long SELECTOR_SALT = 0x4E45544845524CL;

    private NetherComplexPlacement() {
    }

    public static boolean isNetherComplexStart(long levelSeed, int chunkX, int chunkZ, RandomSourceProvider random) {
        int regionX = Math.floorDiv(chunkX, REGION_SIZE_CHUNKS);
        int regionZ = Math.floorDiv(chunkZ, REGION_SIZE_CHUNKS);
        int originX = regionX * REGION_SIZE_CHUNKS;
        int originZ = regionZ * REGION_SIZE_CHUNKS;

        random.setSeed((levelSeed ^ PLACEMENT_SALT) + Level.chunkHash(regionX, regionZ));
        int candidateX = originX + random.nextBoundedInt(CANDIDATE_OFFSET_MAX);
        int candidateZ = originZ + random.nextBoundedInt(CANDIDATE_OFFSET_MAX);
        return candidateX == chunkX && candidateZ == chunkZ;
    }

    public static boolean shouldGenerateBastion(long levelSeed, int chunkX, int chunkZ, RandomSourceProvider random) {
        int regionX = Math.floorDiv(chunkX, REGION_SIZE_CHUNKS);
        int regionZ = Math.floorDiv(chunkZ, REGION_SIZE_CHUNKS);
        random.setSeed((levelSeed ^ SELECTOR_SALT) + Level.chunkHash(regionX, regionZ));
        return random.nextBoundedInt(2) != 0;
    }
}
