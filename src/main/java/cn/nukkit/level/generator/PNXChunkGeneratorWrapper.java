package cn.nukkit.level.generator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
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
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;
import com.dfsek.terra.api.world.chunk.generation.util.GeneratorWrapper;
import com.dfsek.terra.api.world.info.WorldProperties;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PNXChunkGeneratorWrapper extends Generator implements GeneratorWrapper {
    private final BiomeProvider biomeProvider;
    @Getter
    private final ConfigPack configPack;
    private final BlockState air;
    private final WorldProperties worldProperties;
    private final ChunkGenerator chunkGenerator;

    private ServerWorld world;
    private ChunkManager chunkManager;
    private DimensionData dimensionData;
    private NukkitRandom nukkitRandom;

    public PNXChunkGeneratorWrapper() {
        this(createConfigPack(), new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR));
    }

    public PNXChunkGeneratorWrapper(Map<String, Object> option) {
        String packName = "default";
        this.dimensionData = DimensionEnum.getDataFromId(0);
        if (option.containsKey("preset")) {
            var opts = option.get("preset").toString().split(":");
            if (opts.length >= 1) {
                packName = opts[0];
                if (opts.length == 2) {
                    this.dimensionData = DimensionEnum.valueOf(opts[1].toUpperCase()).getDimensionData();
                }
            }
        }
        this.air = new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR);
        this.configPack = createConfigPack(packName);
        this.chunkGenerator = createGenerator(this.configPack);
        this.biomeProvider = this.configPack.getBiomeProvider();
        this.worldProperties = new WorldProperties() {
            @Override
            public long getSeed() {
                return chunkManager.getSeed();
            }

            @Override
            public int getMaxHeight() {
                return dimensionData.getMaxHeight();
            }

            @Override
            public int getMinHeight() {
                return dimensionData.getMinHeight();
            }

            @Override
            public Object getHandle() {
                return null;
            }
        };
    }

    public PNXChunkGeneratorWrapper(ConfigPack configPack, BlockState air) {
        this.air = air;
        this.configPack = configPack;
        this.chunkGenerator = createGenerator(this.configPack);
        this.dimensionData = DimensionEnum.getDataFromId(0);
        this.biomeProvider = this.configPack.getBiomeProvider();
        this.worldProperties = new WorldProperties() {
            @Override
            public long getSeed() {
                return chunkManager.getSeed();
            }

            @Override
            public int getMaxHeight() {
                return dimensionData.getMaxHeight();
            }

            @Override
            public int getMinHeight() {
                return dimensionData.getMinHeight();
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
        return getDimensionData().getDimensionId();
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.chunkManager = level;
        this.world = new PNXServerWorld(this);
        this.nukkitRandom = random;
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        var chunk = chunkManager.getChunk(chunkX, chunkZ);
        chunkGenerator.generateChunkData(new PNXProtoChunk(chunk), worldProperties,
                biomeProvider, chunkX, chunkZ);
        var level = this.getLevel();
        int minHeight = level.getMinHeight();
        int maxHeight = level.getMaxHeight();
        for (int x = 0; x < 16; x++) {
            for (int y = minHeight; y < maxHeight; y++) {
                for (int z = 0; z < 16; z++) {
                    chunk.setBiome(x, y, z, (Biome) biomeProvider.getBiome(chunkX * 16 + x, y, chunkZ * 16 + z, chunkManager.getSeed()).getPlatformBiome().getHandle());
                }
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        var tmp = new PNXProtoWorld(world, chunkX, chunkZ);
        try {
            //todo: ConcurrentModificationException
            for (var generationStage : configPack.getStages()) {
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

    @Since("1.19.21-r2")
    @Override
    public boolean shouldGenerateStructures() {
        return true;
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

    public BiomeProvider getBiomeProvider() {
        return biomeProvider;
    }

    @Override
    public DimensionData getDimensionData() {
        return dimensionData;
    }
}
