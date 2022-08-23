package cn.nukkit.level.generator.populator.impl.structure.mineshaft;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.mineshaft.structure.MineshaftPieces;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.BoundingBox;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructurePiece;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructureStart;
import cn.nukkit.level.generator.populator.type.PopulatorStructure;
import cn.nukkit.level.generator.task.CallbackableChunkGenerationTask;
import cn.nukkit.math.NukkitRandom;

@PowerNukkitXOnly
@Since("1.19.21-r6")
public class PopulatorMineshaft extends PopulatorStructure {

    protected static final int PROBABILITY = 4;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (chunk.getProvider().isOverWorld() && VALID_BIOMES[chunk.getBiomeId(7, 7)]) {
            //\\ MineshaftFeature::isFeatureChunk(BiomeSource const &,Random &,ChunkPos const &,uint)
            long seed = level.getSeed();
            random.setSeed(seed);
            int r1 = random.nextInt();
            int r2 = random.nextInt();
            random.setSeed(chunkX * r1 ^ chunkZ * r2 ^ seed);

            if (random.nextBoundedInt(1000) < PROBABILITY) {
                //\\ MineshaftFeature::createStructureStart(Dimension &,BiomeSource &,Random &,ChunkPos const &)
                MineshaftStart start = new MineshaftStart(level, chunkX, chunkZ);
                start.generatePieces(level, chunkX, chunkZ);

                if (start.isValid()) { //TODO: serialize nbt
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
        }
    }

    protected static boolean[] VALID_BIOMES = new boolean[256];

    static {
        VALID_BIOMES[EnumBiome.OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.PLAINS.id] = true;
        VALID_BIOMES[EnumBiome.DESERT.id] = true;
        VALID_BIOMES[EnumBiome.EXTREME_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.FOREST.id] = true;
        VALID_BIOMES[EnumBiome.TAIGA.id] = true;
        VALID_BIOMES[EnumBiome.SWAMP.id] = true;
        VALID_BIOMES[EnumBiome.RIVER.id] = true;
        VALID_BIOMES[EnumBiome.FROZEN_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.FROZEN_RIVER.id] = true;
        VALID_BIOMES[EnumBiome.ICE_PLAINS.id] = true;
        VALID_BIOMES[13] = true; //SNOWY_MOUNTAINS
        VALID_BIOMES[EnumBiome.MUSHROOM_ISLAND.id] = true;
        VALID_BIOMES[EnumBiome.MUSHROOM_ISLAND_SHORE.id] = true;
        VALID_BIOMES[EnumBiome.BEACH.id] = true;
        VALID_BIOMES[EnumBiome.DESERT_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.FOREST_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.TAIGA_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.EXTREME_HILLS_EDGE.id] = true;
        VALID_BIOMES[EnumBiome.JUNGLE.id] = true;
        VALID_BIOMES[EnumBiome.JUNGLE_HILLS.id] = true;
        VALID_BIOMES[EnumBiome.JUNGLE_EDGE.id] = true;
        VALID_BIOMES[EnumBiome.DEEP_OCEAN.id] = true;
        VALID_BIOMES[EnumBiome.STONE_BEACH.id] = true;
        VALID_BIOMES[EnumBiome.COLD_BEACH.id] = true;
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
        VALID_BIOMES[44] = true; // WARM_OCEAN
        VALID_BIOMES[45] = true; // LUKEWARM_OCEAN
        VALID_BIOMES[46] = true; // COLD_OCEAN
        VALID_BIOMES[47] = true; // DEEP_WARM_OCEAN
        VALID_BIOMES[48] = true; // DEEP_LUKEWARM_OCEAN
        VALID_BIOMES[49] = true; // DEEP_COLD_OCEAN
        VALID_BIOMES[50] = true; // DEEP_FROZEN_OCEAN
        VALID_BIOMES[EnumBiome.SUNFLOWER_PLAINS.id] = true;
        VALID_BIOMES[EnumBiome.DESERT_M.id] = true;
        VALID_BIOMES[EnumBiome.EXTREME_HILLS_M.id] = true;
        VALID_BIOMES[EnumBiome.FLOWER_FOREST.id] = true;
        VALID_BIOMES[EnumBiome.TAIGA_M.id] = true;
        VALID_BIOMES[EnumBiome.SWAMPLAND_M.id] = true;
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
        VALID_BIOMES[168] = true; //BAMBOO_JUNGLE
        VALID_BIOMES[169] = true; //BAMBOO_JUNGLE_HILLS

        MineshaftPieces.init();
    }

    public enum Type {
        NORMAL,
        MESA;

        public static Type byId(int id) {
            Type[] values = values();
            if (id < 0 || id >= values.length) {
                return Type.NORMAL;
            }
            return values[id];
        }
    }

    public static class MineshaftStart extends StructureStart {

        public MineshaftStart(ChunkManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override
        public void generatePieces(ChunkManager level, int chunkX, int chunkZ) {
            BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
            if (chunk != null) {
                int biome = chunk.getBiomeId(7, 7);
                Type type = biome >= EnumBiome.MESA.id && biome <= EnumBiome.MESA_PLATEAU.id || biome >= EnumBiome.MESA_BRYCE.id && biome <= EnumBiome.MESA_PLATEAU_M.id ? Type.MESA : Type.NORMAL;

                MineshaftPieces.MineshaftRoom start = new MineshaftPieces.MineshaftRoom(0, this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2, type);
                this.pieces.add(start);
                start.addChildren(start, this.pieces, this.random);
                this.calculateBoundingBox();

                if (type == Type.MESA) {
                    int offset = 64 - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 + 5;
                    this.boundingBox.move(0, offset, 0);
                    for (StructurePiece piece : this.pieces) {
                        piece.move(0, offset, 0);
                    }
                } else {
                    this.moveBelowSeaLevel(64, this.random, 10);
                }
            }
        }

        @Override //\\ MineshaftStart::getType(void) // 3
        public String getType() {
            return "Mineshaft";
        }
    }

    @Since("1.19.21-r6")
    @Override
    public boolean isAsync() {
        return true;
    }
}
