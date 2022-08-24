package cn.nukkit.level.generator.populator.impl.structure.stronghold.populator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.stronghold.structure.StrongholdPieces;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.BoundingBox;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructurePiece;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructureStart;
import cn.nukkit.level.generator.populator.type.PopulatorStructure;
import cn.nukkit.level.generator.task.CallbackableChunkGenerationTask;
import cn.nukkit.math.NukkitRandom;
import com.google.common.collect.Lists;

import java.util.List;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class PopulatorStronghold extends PopulatorStructure {

    protected static final int DISTANCE = 32;
    protected static final int COUNT = 128;
    protected static final int SPREAD = 3;

    protected boolean isSpotSelected = false;
    protected long[] strongholdPos = new long[COUNT];
    private final List<StrongholdStart> discoveredStarts = Lists.newArrayList();

    private final Object lock = new Object(); //\\ (char *)this + 456

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        //\\ StrongholdFeature::isFeatureChunk(BiomeSource const &,Random &,ChunkPos const &,uint)
        if (VALID_BIOMES[chunk.getBiomeId(7, 7)]) {
            //\\ StrongholdFeature *v28 = (StrongholdFeature *)((char *)this + 456); // [rsp+60h] [rbp+8h]
            synchronized (this.lock) { //\\ _Mtx_lock((char *)this + 456);
                if (!this.isSpotSelected) {
                    //\\ StrongholdFeature::generatePositions(Random &,BiomeSource const &,uint)
                    int count = 0;
                    for (StructureStart start : this.discoveredStarts) {
                        if (count < COUNT) {
                            this.strongholdPos[count++] = Level.chunkHash(start.getChunkX(), start.getChunkZ());
                        }
                    }

                    NukkitRandom rand = new NukkitRandom(level.getSeed());
                    double angle = rand.nextDouble() * Math.PI * 2.0;
                    int spread = SPREAD;

                    if (count < COUNT) {
                        int spreadCount = 0;
                        int nextCount = 0;

                        for (int i = 0; i < COUNT; ++i) {
                            double radius = (double) (4 * DISTANCE + DISTANCE * nextCount * 6) + (rand.nextDouble() - 0.5) * (double) DISTANCE * 2.5;
                            int cx = (int) Math.round(Math.cos(angle) * radius);
                            int cz = (int) Math.round(Math.sin(angle) * radius);

                            if (i >= count) {
                                this.strongholdPos[i] = Level.chunkHash(cx, cz);
//                                Server.getInstance().getLogger().info("Stronghold found at " + cx * 16 + "," + cz * 16);
                            }

                            angle += Math.PI * 2.0 / (double) spread;
                            ++spreadCount;
                            if (spreadCount == spread) {
                                ++nextCount;
                                spreadCount = 0;
                                spread += 2 * spread / (nextCount + 1);
                                spread = Math.min(spread, COUNT - i);
                                angle += rand.nextDouble() * Math.PI * 2.0;
                            }
                        }
                    }

                    this.isSpotSelected = true;
                }
            } //\\ _Mtx_unlock((char *)this + 456)

            long hash = Level.chunkHash(chunkX, chunkZ);
            for (long chunkPos : strongholdPos) {
                if (hash == chunkPos) {
                    //\\ StrongholdFeature::createStructureStart(Dimension &,BiomeSource &,Random &,ChunkPos const &)
                    StrongholdStart start = new StrongholdStart(level, chunkX, chunkZ);
                    start.generatePieces(level, chunkX, chunkZ);

                    if (start.isValid()) { //TODO: serialize nbt
                        long seed = level.getSeed();
                        random.setSeed(seed);
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

                    break;
                }
            }
        }
    }

    protected static boolean[] VALID_BIOMES = new boolean[256];

    static {
        VALID_BIOMES[EnumBiome.PLAINS.id] = true;
        VALID_BIOMES[EnumBiome.DESERT.id] = true;
        VALID_BIOMES[EnumBiome.EXTREME_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.FOREST.id] = true;
        VALID_BIOMES[EnumBiome.TAIGA.id] = true;
        VALID_BIOMES[EnumBiome.ICE_PLAINS.id] = true;
        VALID_BIOMES[13] = true; //SNOWY_MOUNTAINS
        VALID_BIOMES[EnumBiome.MUSHROOM_ISLAND.id] = true;
        VALID_BIOMES[EnumBiome.MUSHROOM_ISLAND_SHORE.id] = true;
        VALID_BIOMES[EnumBiome.DESERT_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.FOREST_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.TAIGA_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.EXTREME_HILLS_EDGE.id] = true;
        VALID_BIOMES[EnumBiome.JUNGLE.id] = true;
        VALID_BIOMES[EnumBiome.JUNGLE_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.JUNGLE_EDGE.id] = true;
        VALID_BIOMES[EnumBiome.STONE_BEACH.id] = true;
        VALID_BIOMES[EnumBiome.BIRCH_FOREST.id] = true;
        VALID_BIOMES[EnumBiome.BIRCH_FOREST_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.ROOFED_FOREST.id] = true;
        VALID_BIOMES[EnumBiome.COLD_TAIGA.id] = true;
        VALID_BIOMES[EnumBiome.COLD_TAIGA_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.MEGA_TAIGA.id] = true;
        VALID_BIOMES[EnumBiome.MEGA_TAIGA_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.EXTREME_HILLS_PLUS.id] = true;
        VALID_BIOMES[EnumBiome.SAVANNA.id] = true;
        VALID_BIOMES[EnumBiome.SAVANNA_PLATEAU.id] = true;
        VALID_BIOMES[EnumBiome.MESA.id] = true;
        VALID_BIOMES[EnumBiome.MESA_PLATEAU_F.id] = true;
        VALID_BIOMES[EnumBiome.MESA_PLATEAU.id] = true;
        VALID_BIOMES[EnumBiome.SUNFLOWER_PLAINS.id] = true;
        VALID_BIOMES[EnumBiome.DESERT_M.id] = true;
        VALID_BIOMES[EnumBiome.EXTREME_HILLS_M.id] = true;
        VALID_BIOMES[EnumBiome.FLOWER_FOREST.id] = true;
        VALID_BIOMES[EnumBiome.TAIGA_M.id] = true;
        VALID_BIOMES[EnumBiome.ICE_PLAINS_SPIKES.id] = true;
        VALID_BIOMES[EnumBiome.JUNGLE_M.id] = true;
        VALID_BIOMES[EnumBiome.JUNGLE_EDGE_M.id] = true;
        VALID_BIOMES[EnumBiome.BIRCH_FOREST_M.id] = true;
        VALID_BIOMES[EnumBiome.BIRCH_FOREST_HILLS_M.id] = true;
        VALID_BIOMES[EnumBiome.ROOFED_FOREST_M.id] = true;
        VALID_BIOMES[EnumBiome.COLD_TAIGA_M.id] = true;
        VALID_BIOMES[EnumBiome.MEGA_SPRUCE_TAIGA.id] = true;
        VALID_BIOMES[161] = true; //GIANT_SPRUCE_TAIGA_HILLS
        VALID_BIOMES[EnumBiome.EXTREME_HILLS_PLUS_M.id] = true;
        VALID_BIOMES[EnumBiome.SAVANNA_M.id] = true;
        VALID_BIOMES[EnumBiome.SAVANNA_PLATEAU_M.id] = true;
        VALID_BIOMES[EnumBiome.MESA_BRYCE.id] = true;
        VALID_BIOMES[EnumBiome.MESA_PLATEAU_F_M.id] = true;
        VALID_BIOMES[EnumBiome.MESA_PLATEAU_M.id] = true;
        VALID_BIOMES[EnumBiome.BAMBOO_JUNGLE.id] = true; //BAMBOO_JUNGLE
        VALID_BIOMES[EnumBiome.BAMBOO_JUNGLE_HILLS.id] = true; //BAMBOO_JUNGLE_HILLS

        StrongholdPieces.init();
    }

    public class StrongholdStart extends StructureStart {

        public StrongholdStart(ChunkManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override
        public void generatePieces(ChunkManager level, int chunkX, int chunkZ) {
            synchronized (StrongholdPieces.getLock()) {
                int count = 0;
                long seed = level.getSeed();
                StrongholdPieces.StartPiece start;

                do {
                    this.pieces.clear();
                    this.boundingBox = BoundingBox.getUnknownBox();
                    this.random.setSeed(seed + count++);
                    this.random.setSeed(chunkX * this.random.nextInt() ^ chunkZ * this.random.nextInt() ^ level.getSeed());
                    StrongholdPieces.resetPieces();

                    start = new StrongholdPieces.StartPiece(this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
                    this.pieces.add(start);
                    start.addChildren(start, this.pieces, this.random);

                    List<StructurePiece> children = start.pendingChildren;
                    while (!children.isEmpty()) {
                        children.remove(this.random.nextBoundedInt(children.size()))
                                .addChildren(start, this.pieces, this.random);
                    }

                    this.calculateBoundingBox();
                    this.moveBelowSeaLevel(64, this.random, 10);
                } while (this.pieces.isEmpty() || start.portalRoomPiece == null);

                PopulatorStronghold.this.discoveredStarts.add(this);
            }
        }

        @Override //\\ StrongholdStart::getType(void) // 5
        public String getType() {
            return "Stronghold";
        }
    }

    @Since("1.19.21-r2")
    @Override
    public boolean isAsync() {
        return true;
    }
}
