package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.terra.PNXPlatform;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXProtoChunk;
import cn.nukkit.level.terra.delegate.PNXProtoWorld;
import cn.nukkit.level.terra.delegate.PNXServerWorld;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.world.ServerWorld;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;
import com.dfsek.terra.api.world.chunk.generation.util.GeneratorWrapper;
import com.dfsek.terra.api.world.info.WorldProperties;

import java.util.Collections;
import java.util.Map;

public class PNXChunkGeneratorWrapper extends Generator implements GeneratorWrapper {
    private final ChunkGenerator chunkGenerator;
    private final ConfigPack pack;
    private final BlockState air;
    private final WorldProperties worldProperties;
    private final ServerWorld world;
    private ChunkManager chunkManager;

    public PNXChunkGeneratorWrapper() {
        this(createGenerator(), createConfigPack(), new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR));
    }

    public PNXChunkGeneratorWrapper(Map<String, Object> option) {
        var packName = option.containsKey("present") ? option.get("present").toString() : "default";
        if (packName == null || packName.strip().length() == 0) {
            packName = "default";
        }
        this.air = new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR);
        this.pack = createConfigPack(packName);
        this.chunkGenerator = createGenerator(packName);
        this.world = new PNXServerWorld(this.chunkManager, this.chunkGenerator, this.pack, this.pack.getBiomeProvider());
        this.worldProperties = new WorldProperties() {
            @Override
            public long getSeed() {
                return chunkManager.getSeed();
            }

            @Override
            public int getMaxHeight() {
                return 320;
            }

            @Override
            public int getMinHeight() {
                return -64;
            }

            @Override
            public Object getHandle() {
                return null;
            }
        };
    }

    public PNXChunkGeneratorWrapper(ChunkGenerator chunkGenerator, ConfigPack pack, BlockState air) {
        this.air = air;
        this.pack = pack;
        this.chunkGenerator = chunkGenerator;
        this.world = new PNXServerWorld(this.chunkManager, this.chunkGenerator, this.pack, this.pack.getBiomeProvider());
        this.worldProperties = new WorldProperties() {
            @Override
            public long getSeed() {
                return chunkManager.getSeed();
            }

            @Override
            public int getMaxHeight() {
                return 320;
            }

            @Override
            public int getMinHeight() {
                return -64;
            }

            @Override
            public Object getHandle() {
                return null;
            }
        };
    }

    private static ConfigPack createConfigPack() {
        return PNXPlatform.getInstance().getConfigRegistry().getByID("default").orElseGet(
                () -> PNXPlatform.getInstance().getConfigRegistry().getByID("PNXChunkGeneratorWrapper:default").orElseThrow()
        );
    }

    private static ConfigPack createConfigPack(final String packName) {
        return PNXPlatform.getInstance().getConfigRegistry().getByID(packName).orElseGet(
                () -> PNXPlatform.getInstance().getConfigRegistry().getByID("PNXChunkGeneratorWrapper:" + packName).orElseThrow()
        );
    }

    private static ChunkGenerator createGenerator() {
        var config = createConfigPack();
        return config.getGeneratorProvider().newInstance(config);
    }

    private static ChunkGenerator createGenerator(ConfigPack config) {
        return config.getGeneratorProvider().newInstance(config);
    }

    private static ChunkGenerator createGenerator(final String packName) {
        var config = createConfigPack(packName);
        return config.getGeneratorProvider().newInstance(config);
    }

    @Override
    public int getId() {
        return TYPE_INFINITE;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.chunkManager = level;
        //this.nukkitRandom = random; unused
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        chunkGenerator.generateChunkData(new PNXProtoChunk(chunkManager.getChunk(chunkX, chunkZ)), worldProperties,
                pack.getBiomeProvider(), chunkX, chunkZ);
        var chunk = chunkManager.getChunk(chunkX, chunkZ);
        int minHeight = chunk.isOverWorld() ? -64 : 0;
        int maxHeight = chunk.isOverWorld() ? 320 : 256;
        for (int x = 0; x < 16; x++) {
            for (int y = minHeight; y < maxHeight; y++) {
                for (int z = 0; z < 16; z++) {
                    chunk.setBiome(x, y, z, (Biome) pack.getBiomeProvider().getBiome(chunkX * 16 + x, y, chunkZ * 16 + z, chunkManager.getSeed()).getPlatformBiome().getHandle());
                }
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        var tmp = new PNXProtoWorld(world, chunkManager, chunkGenerator, pack, pack.getBiomeProvider(), chunkX, chunkZ);
        try {
            for (var generationStage : pack.getStages()) {
                generationStage.populate(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 在装饰区块的时候就计算好天光避免重复计算导致内存泄露
        var chunk = chunkManager.getChunk(chunkX, chunkZ);
        chunk.populateSkyLight();
        chunk.setLightPopulated(true);
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public String getName() {
        return "terra";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0.5, 256, 0.5);
    }

    @Override
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    @Override
    public ChunkGenerator getHandle() {
        return chunkGenerator;
    }
}
