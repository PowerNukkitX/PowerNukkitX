package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.bastion.BastionStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class BastionRemnantPopulator extends Populator {

    public static final String NAME = "nether_bastion_remnant";

    public static final int REGION_SIZE_CHUNKS = 30;
    public static final int EDGE_EXCLUSION_CHUNKS = 4;
    private static final int CANDIDATE_OFFSET_MAX = REGION_SIZE_CHUNKS - EDGE_EXCLUSION_CHUNKS - 1;
    private static final long PLACEMENT_SALT = 0x42415354494F4EL;
    private static final long SELECTOR_SALT = 0x4E45544845524CL;

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

        if (!isNetherComplexStart(seed, chunkX, chunkZ, random)) {
            return;
        }
        if (!shouldGenerateBastion(seed, chunkX, chunkZ, random)) {
            return;
        }

        int biome = chunk.getBiomeId(7, 33, 7);
        if (biome == BiomeID.BASALT_DELTAS) {
            return;
        }

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = 33;
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, originY, originZ));
        BASTION.place(helper, random.fork());
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

    @Override
    public String name() {
        return NAME;
    }
}
