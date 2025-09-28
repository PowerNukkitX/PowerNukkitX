package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.OceanMonumentPieces;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.generator.object.structures.utils.StructureStart;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class OceanMonumentPopulator extends Populator {

    public static final String NAME = "normal_ocean_monument";


    protected static final int SPACING = 32;
    protected static final int SEPARATION = 5;
    protected static boolean[] DEEP_OCEAN_BIOMES = new boolean[256];
    protected static boolean[] WATER_BIOMES = new boolean[256];

    static {
        // Oceans
        WATER_BIOMES[BiomeID.OCEAN] = true;
        WATER_BIOMES[BiomeID.FROZEN_OCEAN] = true;
        WATER_BIOMES[BiomeID.LUKEWARM_OCEAN] = true;
        WATER_BIOMES[BiomeID.COLD_OCEAN] = true;
        WATER_BIOMES[BiomeID.WARM_OCEAN] = true;

        //Deep Oceans
        WATER_BIOMES[BiomeID.DEEP_OCEAN] = DEEP_OCEAN_BIOMES[BiomeID.DEEP_OCEAN] = true;
        WATER_BIOMES[BiomeID.DEEP_WARM_OCEAN] = DEEP_OCEAN_BIOMES[BiomeID.DEEP_WARM_OCEAN] = true;
        WATER_BIOMES[BiomeID.DEEP_LUKEWARM_OCEAN] = DEEP_OCEAN_BIOMES[BiomeID.DEEP_LUKEWARM_OCEAN] = true;
        WATER_BIOMES[BiomeID.DEEP_COLD_OCEAN] = DEEP_OCEAN_BIOMES[BiomeID.DEEP_COLD_OCEAN] = true;
        WATER_BIOMES[BiomeID.DEEP_FROZEN_OCEAN] = DEEP_OCEAN_BIOMES[BiomeID.DEEP_FROZEN_OCEAN] = true;

        // Rivers
        WATER_BIOMES[BiomeID.RIVER] = true;
        WATER_BIOMES[BiomeID.FROZEN_RIVER] = true;
    }

    protected final Map<Long, Set<Long>> waitingChunks = Maps.newConcurrentMap();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level l = chunk.getLevel();
        if(Registries.BIOME.get(chunk.getBiomeId(7, chunk.getHeightMap(7,7), 7)).getTags().contains(BiomeTags.OCEAN)) {
            BlockManager level = new BlockManager(l);
            int cX = (chunkX < 0 ? chunkX - SPACING + 1 : chunkX) / SPACING;
            int cZ = (chunkZ < 0 ? chunkZ - SPACING + 1 : chunkZ) / SPACING;
            random.setSeed(cX * 0x4f9939f508L + cZ * 0x1ef1565bd5L + level.getSeed() + 0x9e7f71L);

            if (chunkX == cX * SPACING + (random.nextBoundedInt(SPACING - SEPARATION) + random.nextBoundedInt(SPACING - SEPARATION)) / 2
                    && chunkZ == cZ * SPACING + (random.nextBoundedInt(SPACING - SEPARATION) + random.nextBoundedInt(SPACING - SEPARATION)) / 2) {
                int startX = (chunkX << 4) + 9;
                int startZ = (chunkZ << 4) + 9;

                Set<IChunk> chunks = Sets.newHashSet();
                Set<Long> indexes = Sets.newConcurrentHashSet();

                for (int ckX = (startX - 29) >> 4; ckX <= (startX + 29) >> 4; ckX++) {
                    for (int ckZ = (startZ - 29) >> 4; ckZ <= (startZ + 29) >> 4; ckZ++) {
                        IChunk ck = level.getChunk(ckX, ckZ);
                        if (ck == null) {
                            ck = chunk.getProvider().getChunk(ckX, ckZ, true);
                        }
                        if (!ck.isGenerated()) {
                            chunks.add(ck);
                            indexes.add(Level.chunkHash(ck.getX(), ck.getZ()));
                        }
                    }
                }

                if (!chunks.isEmpty()) {
                    this.waitingChunks.put(Level.chunkHash(chunkX, chunkZ), indexes);
                    for (IChunk ck : chunks) {
                        if(ck.isGenerated())
                        level.getLevel().syncGenerateChunk(ck.getX(), ck.getZ());
                        generateChunkCallback(level, startX, startZ, chunk, ck.getX(), ck.getZ());
                    }
                    queueObject(chunk, level);
                    return;
                }

                this.place(level, startX, startZ, chunk);
                queueObject(chunk, level);
            }
        }
    }


    public void place(BlockManager level, int startX, int startZ, IChunk chunk) {
        int chunkX = startX >> 4;
        int chunkZ = startZ >> 4;

        //\\ OceanMonumentFeature::createStructureStart(Dimension &,BiomeSource &,Random &,ChunkPos const &)
        OceanMonumentStart start = new OceanMonumentStart(level, chunkX, chunkZ);
        start.generatePieces(level, chunkX, chunkZ);

        if (start.isValid()) {
            long seed = level.getSeed();
            NukkitRandom random = new NukkitRandom(seed);
            int r1 = random.nextInt();
            int r2 = random.nextInt();

            BoundingBox boundingBox = start.getBoundingBox();
            for (int cx = boundingBox.x0 >> 4; cx <= boundingBox.x1 >> 4; cx++) {
                for (int cz = boundingBox.z0 >> 4; cz <= boundingBox.z1 >> 4; cz++) {
                    NukkitRandom rand = new NukkitRandom((long) cx * r1 ^ (long) cz * r2 ^ seed);
                    int x = cx << 4;
                    int z = cz << 4;
                    start.postProcess(level, rand, new BoundingBox(x, z, x + 15, z + 15), cx, cz);
                }
            }
        }
    }

    public synchronized void generateChunkCallback(BlockManager level, int startX, int startZ, IChunk chunk, int chunkX, int chunkZ) {
        Set<Long> indexes = this.waitingChunks.get(Level.chunkHash(startX >> 4, startZ >> 4));
        indexes.remove(Level.chunkHash(chunkX, chunkZ));
        if (indexes.isEmpty()) {
            this.place(level, startX, startZ, chunk);
        }
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
            this.pieces.add(new OceanMonumentPieces.MonumentBuilding(this.random, chunkX * 16 - 29, chunkZ * 16 - 29, BlockFace.Plane.HORIZONTAL.random(this.random)));
            this.calculateBoundingBox();

            this.isCreated = true;
        }

        @Override //\\ OceanMonumentStart::postProcess(BlockSource *,Random &,BoundingBox const &)
        public void postProcess(BlockManager level, RandomSourceProvider random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (!this.isCreated) {
                this.pieces.clear();
                this.generatePieces(level, chunkX, chunkZ);
            }

            super.postProcess(level, random, boundingBox, chunkX, chunkZ);
        }

        @Override //\\ OceanMonumentStart::getType(void) // 4
        public String getType() {
            return "Monument";
        }
    }
}
