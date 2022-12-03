package cn.nukkit.level.terra;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXProtoChunk;
import cn.nukkit.level.terra.delegate.PNXProtoWorld;
import cn.nukkit.level.terra.delegate.PNXServerWorld;
import cn.nukkit.math.Vector3;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;
import com.dfsek.terra.api.world.chunk.generation.util.GeneratorWrapper;
import com.dfsek.terra.api.world.info.WorldProperties;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Terra生成器平台实现类<br/>
 * 请注意我们通常不直接使用此类而是使用{@link cn.nukkit.level.generator.TerraGeneratorWrapper}<br/>
 * 其每个实例都会持有一个相同的{@link TerraGenerator}实例<br/>
 * 这样做的原因是因为nk底层会在每个线程新建一个生成器实例并行化生成区块时，而Terra本身就是并行的<br/>
 * <br/>
 * Terra generator platform implementation class<br/>
 * Please note that we usually do not use this class directly but use {@link cn.nukkit.level.generator.TerraGeneratorWrapper}<br/>
 * Each of its instances will hold an identical instance of {@link TerraGenerator}<br/>
 * The reason for this is because the bottom layer of nk will create a new generator instance for each thread to generate blocks in parallel, and Terra itself is parallel
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TerraGenerator implements GeneratorWrapper {
    private final BiomeProvider biomeProvider;
    @Getter
    private final ConfigPack configPack;
    private final BlockState air;
    private final WorldProperties worldProperties;
    private final ChunkGenerator chunkGenerator;
    private final Level level;
    private DimensionData dimensionData;

    public TerraGenerator(Level level) {
        this(createConfigPack(), new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR), level);
    }

    public TerraGenerator(Map<String, Object> option, Level level) {
        this.level = level;
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
                return level.getSeed();
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

    public TerraGenerator(ConfigPack configPack, BlockState air, Level level) {
        this.level = level;
        this.air = air;
        this.configPack = configPack;
        this.chunkGenerator = createGenerator(this.configPack);
        this.dimensionData = DimensionEnum.getDataFromId(0);
        this.biomeProvider = this.configPack.getBiomeProvider();
        this.worldProperties = new WorldProperties() {
            @Override
            public long getSeed() {
                return level.getSeed();
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
        return PNXPlatform.getInstance().getConfigRegistry().getByID("default").orElseGet(() -> PNXPlatform.getInstance().getConfigRegistry().getByID("PNXChunkGeneratorWrapper:default").orElseThrow());
    }

    private static ConfigPack createConfigPack(final String packName) {
        return PNXPlatform.getInstance().getConfigRegistry().getByID(packName).orElseGet(() -> PNXPlatform.getInstance().getConfigRegistry().getByID("PNXChunkGeneratorWrapper:" + packName).orElseThrow());
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

    public void generateChunk(int chunkX, int chunkZ, ChunkManager chunkManager) {
        var chunk = chunkManager.getChunk(chunkX, chunkZ);
        chunkGenerator.generateChunkData(new PNXProtoChunk(chunk), worldProperties, biomeProvider, chunkX, chunkZ);
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

    public void populateChunk(int chunkX, int chunkZ, ChunkManager chunkManager) {
        var tmp = new PNXProtoWorld(new PNXServerWorld(this, chunkManager), chunkX, chunkZ);
        try {
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

    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    public String getName() {
        return "terra";
    }

    public Vector3 getSpawn() {
        return new Vector3(0.5, 256, 0.5);
    }

    @Override
    public ChunkGenerator getHandle() {
        return chunkGenerator;
    }

    public BiomeProvider getBiomeProvider() {
        return biomeProvider;
    }

    public DimensionData getDimensionData() {
        return dimensionData;
    }

    public Level getLevel() {return level; }
}
