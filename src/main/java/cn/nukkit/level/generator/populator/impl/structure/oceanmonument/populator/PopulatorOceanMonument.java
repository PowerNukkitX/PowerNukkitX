package cn.nukkit.level.generator.populator.impl.structure.oceanmonument.populator;

import cn.nukkit.Server;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.oceanmonument.structure.OceanMonumentPieces;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.BoundingBox;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructureStart;
import cn.nukkit.level.generator.populator.type.PopulatorStructure;
import cn.nukkit.level.generator.task.CallbackableChunkGenerationTask;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class PopulatorOceanMonument extends PopulatorStructure {

    protected static final int SPACING = 32;
    protected static final int SEPARATION = 5;

    protected final Map<Long, Set<Long>> waitingChunks = Maps.newConcurrentMap();

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (DEEP_OCEAN_BIOMES[chunk.getBiomeId(7, 7)]) {
            //\\ OceanMonumentFeature::isFeatureChunk(BiomeSource const &,Random &,ChunkPos const &,uint)
            int cX = (chunkX < 0 ? chunkX - SPACING + 1 : chunkX) / SPACING;
            int cZ = (chunkZ < 0 ? chunkZ - SPACING + 1 : chunkZ) / SPACING;
            random.setSeed(cX * 0x4f9939f508L + cZ * 0x1ef1565bd5L + level.getSeed() + 0x9e7f71L);

            if (chunkX == cX * SPACING + (random.nextBoundedInt(SPACING - SEPARATION) + random.nextBoundedInt(SPACING - SEPARATION)) / 2
                    && chunkZ == cZ * SPACING + (random.nextBoundedInt(SPACING - SEPARATION) + random.nextBoundedInt(SPACING - SEPARATION)) / 2) {
                int startX = (chunkX << 4) + 9;
                int startZ = (chunkZ << 4) + 9;

                Set<BaseFullChunk> chunks = Sets.newHashSet();
                Set<Long> indexes = Sets.newConcurrentHashSet();

                for (int ckX = (startX - 29) >> 4; ckX <= (startX + 29) >> 4; ckX++) {
                    for (int ckZ = (startZ - 29) >> 4; ckZ <= (startZ + 29) >> 4; ckZ++) {
                        BaseFullChunk ck = level.getChunk(ckX, ckZ);
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
                    Level world = chunk.getProvider().getLevel();
                    this.waitingChunks.put(Level.chunkHash(chunkX, chunkZ), indexes);
                    for (BaseFullChunk ck : chunks) {
                        Server.getInstance().getScheduler().scheduleAsyncTask(null, new CallbackableChunkGenerationTask<>(world, ck, this,
                                feature -> feature.generateChunkCallback(level, startX, startZ, chunk, ck.getX(), ck.getZ())));
                    }
                    return;
                }

                this.place(level, startX, startZ, chunk);
            }
        }
    }

    public void place(ChunkManager level, int startX, int startZ, FullChunk chunk) {
        int chunkX = startX >> 4;
        int chunkZ = startZ >> 4;

        int minX = startX - 29;
        int minZ = startZ - 29;
        int maxX = startX + 29;
        int maxZ = startZ + 29;
        int minChunkX = minX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkX = maxX >> 4;
        int maxChunkZ = maxZ >> 4;

        int x0 = startX - 16;
        int z0 = startZ - 16;
        int x1 = startX + 16;
        int z1 = startZ + 16;
        int chunkX0 = x0 >> 4;
        int chunkZ0 = z0 >> 4;
        int chunkX1 = x1 >> 4;
        int chunkZ1 = z1 >> 4;

        for (int ckX = minChunkX; ckX < maxChunkX; ckX++) {
            int baseX = ckX << 4;
            for (int ckZ = minChunkZ; ckZ < maxChunkZ; ckZ++) {
                int baseZ = ckZ << 4;
                BaseFullChunk ck = level.getChunk(ckX, ckZ);
                if (ck == null) {
                    ck = chunk.getProvider().getChunk(ckX, ckZ, true);
                }

                if (ckX >= chunkX0 && ckX <= chunkX1 || ckZ >= chunkZ0 && ckZ <= chunkZ1) {
                    for (int cx = 0; cx < 16; cx++) {
                        int x = baseX + cx;
                        boolean insideX = x >= x0 && x <= x1;
                        for (int cz = 0; cz < 16; cz++) {
                            int z = baseZ + cz;
                            boolean[] biomes;
                            if (insideX && z >= z0 && z <= z1) {
                                biomes = DEEP_OCEAN_BIOMES;
                            } else {
                                biomes = WATER_BIOMES;
                            }
                            if (!biomes[ck.getBiomeId(cx, cz)]) {
                                return;
                            }
                        }
                    }
                } else {
                    for (int cx = 0; cx < 16; cx++) {
                        int x = baseX + cx;
                        if (x >= minX && x <= maxX) {
                            for (int cz = 0; cz < 16; cz++) {
                                int z = baseZ + cz;
                                if (z >= minZ && z <= maxZ && !WATER_BIOMES[ck.getBiomeId(cx, cz)]) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        //\\ OceanMonumentFeature::createStructureStart(Dimension &,BiomeSource &,Random &,ChunkPos const &)
        OceanMonumentStart start = new OceanMonumentStart(level, chunkX, chunkZ);
        start.generatePieces(level, chunkX, chunkZ);

        if (start.isValid()) { //TODO: serialize nbt
            long seed = level.getSeed();
            NukkitRandom random = new NukkitRandom(seed);
            int r1 = random.nextInt();
            int r2 = random.nextInt();

            BoundingBox boundingBox = start.getBoundingBox();
            for (int cx = boundingBox.x0 >> 4; cx <= boundingBox.x1 >> 4; cx++) {
                for (int cz = boundingBox.z0 >> 4; cz <= boundingBox.z1 >> 4; cz++) {
                    NukkitRandom rand = new NukkitRandom(cx * r1 ^ cz * r2 ^ seed);
                    int x = cx << 4;
                    int z = cz << 4;
                    BaseFullChunk ck = level.getChunk(cx, cz);
                    if (ck == null) {
                        ck = chunk.getProvider().getChunk(cx, cz, true);
                    }

                    if (ck.isGenerated()) {
                        start.postProcess(level, rand, new BoundingBox(x, z, x + 15, z + 15), cx, cz);
                    } else {
                        int f_cx = cx;
                        int f_cz = cz;
                        Server.getInstance().getScheduler().scheduleAsyncTask(null, new CallbackableChunkGenerationTask<>(
                                chunk.getProvider().getLevel(), ck, start,
                                structure -> structure.postProcess(level, rand, new BoundingBox(x, z, x + 15, z + 15), f_cx, f_cz)));
                    }
                }
            }
        }
    }

    public synchronized void generateChunkCallback(ChunkManager level, int startX, int startZ, FullChunk chunk, int chunkX, int chunkZ) {
        Set<Long> indexes = this.waitingChunks.get(Level.chunkHash(startX >> 4, startZ >> 4));
        indexes.remove(Level.chunkHash(chunkX, chunkZ));
        if (indexes.isEmpty()) {
            this.place(level, startX, startZ, chunk);
        }
    }

    protected static boolean[] DEEP_OCEAN_BIOMES = new boolean[256];
    protected static boolean[] WATER_BIOMES = new boolean[256];

    static {
        // Oceans
        WATER_BIOMES[EnumBiome.OCEAN.id]/* = DEEP_OCEAN_BIOMES[EnumBiome.OCEAN.id]*/ = true;//debug
        WATER_BIOMES[EnumBiome.FROZEN_OCEAN.id]/* = DEEP_OCEAN_BIOMES[EnumBiome.FROZEN_OCEAN.id]*/ = true;//debug

        WATER_BIOMES[EnumBiome.DEEP_OCEAN.id] = DEEP_OCEAN_BIOMES[EnumBiome.DEEP_OCEAN.id] = true;
        WATER_BIOMES[EnumBiome.DEEP_WARM_OCEAN.id] = DEEP_OCEAN_BIOMES[EnumBiome.DEEP_WARM_OCEAN.id] = true;
        WATER_BIOMES[EnumBiome.LUKEWARM_OCEAN.id] = DEEP_OCEAN_BIOMES[EnumBiome.LUKEWARM_OCEAN.id] = true;
        WATER_BIOMES[EnumBiome.DEEP_COLD_OCEAN.id] = DEEP_OCEAN_BIOMES[EnumBiome.DEEP_COLD_OCEAN.id] = true;
        WATER_BIOMES[EnumBiome.DEEP_FROZEN_OCEAN.id] = DEEP_OCEAN_BIOMES[EnumBiome.DEEP_FROZEN_OCEAN.id] = true;
        // Rivers
        WATER_BIOMES[EnumBiome.RIVER.id] = true;
        WATER_BIOMES[EnumBiome.FROZEN_RIVER.id] = true;

        OceanMonumentPieces.init();
    }

    public static class OceanMonumentStart extends StructureStart {

        private boolean isCreated;

        public OceanMonumentStart(ChunkManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override //\\ OceanMonumentStart::createMonument(Dimension &,Random &,int,int)
        public void generatePieces(ChunkManager level, int chunkX, int chunkZ) {
            this.pieces.add(new OceanMonumentPieces.MonumentBuilding(this.random, chunkX * 16 - 29, chunkZ * 16 - 29, BlockFace.Plane.HORIZONTAL.random(this.random)));
            this.calculateBoundingBox();

            this.isCreated = true;
        }

        @Override //\\ OceanMonumentStart::postProcess(BlockSource *,Random &,BoundingBox const &)
        public void postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
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

    @Since("1.19.20-r6")
    @Override
    public boolean isAsync() {
        return true;
    }
}
