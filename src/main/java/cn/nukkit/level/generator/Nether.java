package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.level.ChunkPrePopulateEvent;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.biome.impl.nether.NetherBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.noise.nukkit.OpenSimplex2S;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorGroundFire;
import cn.nukkit.level.generator.populator.impl.PopulatorLava;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.impl.nether.PopulatorGlowStone;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.*;

public class Nether extends Generator {
    private static final double BIOME_AMPLIFICATION = 512;
    private final List<Populator> populators = new ArrayList<>();
    private final List<Populator> generationPopulators = new ArrayList<>();
    private ChunkManager level;
    /**
     * @var Random
     */
    private NukkitRandom nukkitRandom;
    private Random random;
    private final double lavaHeight = 32;
    private final double bedrockDepth = 5;
    private final SimplexF[] noiseGen = new SimplexF[3];
    private OpenSimplex2S biomeGen;
    private long localSeed1;
    private long localSeed2;

    public Nether() {
        this(new HashMap<>());
    }

    public Nether(Map<String, Object> options) {
        //Nothing here. Just used for future update.
    }

    @Override
    public int getId() {
        return Generator.TYPE_NETHER;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_NETHER;
    }

    @Override
    public String getName() {
        return "nether";
    }

    @Override
    public Map<String, Object> getSettings() {
        return new HashMap<>();
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public NukkitRandom getRandom() {
        return this.nukkitRandom;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;
        this.random = new Random();
        this.nukkitRandom.setSeed(this.level.getSeed());

        for (int i = 0; i < noiseGen.length; i++) {
            noiseGen[i] = new SimplexF(nukkitRandom, 4, 1 / 4f, 1 / 64f);
        }

        this.biomeGen = new OpenSimplex2S(random.getSeed());

        this.nukkitRandom.setSeed(this.level.getSeed());
        this.localSeed1 = this.random.nextLong();
        this.localSeed2 = this.random.nextLong();

        PopulatorOre ores = new PopulatorOre(Block.NETHERRACK, new OreType[]{
                new OreType(Block.get(BlockID.QUARTZ_ORE), 20, 16, 0, 128),
                new OreType(Block.get(BlockID.SOUL_SAND), 5, 64, 0, 128),
                new OreType(Block.get(BlockID.GRAVEL), 5, 64, 0, 128),
                new OreType(Block.get(BlockID.FLOWING_LAVA), 1, 16, 0, (int) this.lavaHeight),
        });
        this.populators.add(ores);

        PopulatorGroundFire groundFire = new PopulatorGroundFire();
        groundFire.setBaseAmount(1);
        groundFire.setRandomAmount(1);
        this.populators.add(groundFire);

        PopulatorLava lava = new PopulatorLava();
        lava.setBaseAmount(1);
        lava.setRandomAmount(2);
        this.populators.add(lava);
        this.populators.add(new PopulatorGlowStone());
        PopulatorOre ore = new PopulatorOre(Block.NETHERRACK, new OreType[]{
                new OreType(Block.get(BlockID.QUARTZ_ORE), 20, 16, 0, 128, NETHERRACK),
                new OreType(Block.get(BlockID.SOUL_SAND), 1, 64, 30, 35, NETHERRACK),
                new OreType(Block.get(BlockID.FLOWING_LAVA), 32, 1, 0, 32, NETHERRACK),
                new OreType(Block.get(BlockID.MAGMA), 32, 16, 26, 37, NETHERRACK),
                new OreType(Block.get(BlockID.NETHER_GOLD_ORE), 5, 16, 10, 117, NETHERRACK),
                new OreType(Block.get(BlockID.ANCIENT_DERBRIS), 2, 2, 8, 119, NETHERRACK),
                new OreType(Block.get(BlockID.ANCIENT_DERBRIS), 1, 3, 8, 22, NETHERRACK),
        });
        this.populators.add(ore);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        this.nukkitRandom.setSeed(chunkX * localSeed1 ^ chunkZ * localSeed2 ^ this.level.getSeed());

        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                NetherBiome biome = (NetherBiome) pickBiomeExperimental(baseX + x, baseZ + z).biome;
                chunk.setBiomeId(x, z, biome.getId());

                chunk.setBlockId(x, 0, z, Block.BEDROCK);
                for (int i = 0; i < nukkitRandom.nextBoundedInt(6); i++) {
                    chunk.setBlockId(x, 126 - i, z, biome.getMiddleBlock());
                }
                for (int y = 126; y < 127; ++y) {
                    chunk.setBlockId(x, y, z, biome.getMiddleBlock());
                }
                chunk.setBlockId(x, 127, z, Block.BEDROCK);
                for (int y = 1; y < 127; ++y) {
                    if (getNoise(baseX | x, y, baseZ | z) > 0) {
                        chunk.setBlockId(x, y, z, biome.getMiddleBlock());
                    } else if (y <= this.lavaHeight) {
                        chunk.setBlockId(x, y, z, Block.FLOWING_LAVA);
                        chunk.setBlockLight(x, y + 1, z, 15);
                    }
                }
                for (int y = 1; y < 127; ++y) {
                    if (getNoise(baseX | x, y, baseZ | z) > 0) {
                        if (chunk.getBlockId(x, y + 1, z) == 0) chunk.setBlockId(x, y, z, biome.getCoverBlock());
                    }
                }
            }
        }
        for (Populator populator : this.generationPopulators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        this.nukkitRandom.setSeed(0xdeadbeef ^ ((long) chunkX << 8) ^ chunkZ ^ this.level.getSeed());
        @SuppressWarnings("deprecation")
        Biome biome = EnumBiome.getBiome(chunk.getBiomeId(7, 7));
        var event = new ChunkPrePopulateEvent(chunk, this.populators, biome.getPopulators());
        Server.getInstance().getPluginManager().callEvent(event);
        for (Populator populator : event.getTerrainPopulators()) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }
        biome.populateChunk(this.level, event.getBiomePopulators(), chunkX, chunkZ, this.nukkitRandom);
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, 64, 0);
    }

    public float getNoise(int x, int y, int z) {
        float val = 0f;
        for (int i = 0; i < noiseGen.length; i++) {
            val += noiseGen[i].noise3D(x >> i, y, z >> i, true);
        }
        return val;
    }

    public EnumBiome pickBiome(int x, int z) {
        double value = biomeGen.noise2(x / BIOME_AMPLIFICATION, z / BIOME_AMPLIFICATION);
        if (value >= .6) {
            return EnumBiome.BASALT_DELTAS;
        } else if (value >= .2) {
            return EnumBiome.WARPED_FOREST;
        } else if (value >= -.2) {
            return EnumBiome.HELL;
        } else if (value >= -.6) {
            return EnumBiome.CRIMSON_FOREST;
        } else {
            return EnumBiome.SOUL_SAND_VALLEY;
        }
    }

    public EnumBiome pickBiomeExperimental(int x, int z) {
        double value = biomeGen.noise2(x / BIOME_AMPLIFICATION, z / BIOME_AMPLIFICATION);
        double secondaryValue = biomeGen.noise3_XZBeforeY(x / (BIOME_AMPLIFICATION * 2d), 0, z / (BIOME_AMPLIFICATION * 2d));
        if (value >= 1 / 3f) {
            return secondaryValue >= 0 ? EnumBiome.WARPED_FOREST : EnumBiome.CRIMSON_FOREST;
        } else if (value >= -1 / 3f) {
            return EnumBiome.HELL;
        } else {
            return secondaryValue >= 0 ? EnumBiome.BASALT_DELTAS : EnumBiome.SOUL_SAND_VALLEY;
        }
    }

    @Since("1.19.21-r2")
    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }
}
