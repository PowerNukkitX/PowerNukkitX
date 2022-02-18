package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.terra.PNXPlatform;
import cn.nukkit.level.terra.delegate.PNXBiomeProviderDelegate;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXProtoChunk;
import cn.nukkit.level.terra.delegate.PNXProtoWorld;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;
import com.dfsek.terra.api.world.chunk.generation.util.GeneratorWrapper;
import com.dfsek.terra.api.world.info.WorldProperties;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;

public class PNXChunkGeneratorWrapper extends Generator implements GeneratorWrapper {
    private WeakReference<ChunkGenerator> delegate;
    private ConfigPack pack;
    private final BlockState air;
    @NotNull
    private WeakReference<PNXBiomeProviderDelegate> biomeProvider = new WeakReference<>(null);

    private ChunkManager chunkManager = null;
    private NukkitRandom nukkitRandom = null;

    private final WorldProperties worldProperties;

    public PNXChunkGeneratorWrapper() {
        this(createGenerator(), createConfigPack(), new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR));
    }

    public PNXChunkGeneratorWrapper(Map<String, Object> option) {
        var packName = option.containsKey("present") ? option.get("present").toString() : "default";
        if (packName == null || packName.strip().length() == 0) {
            packName = "default";
        }
        this.air = new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR);
        try {
            this.delegate = new WeakReference<>(createGenerator(packName));
            this.pack = createConfigPack(packName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        worldProperties = new WorldProperties() {
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

    public PNXChunkGeneratorWrapper(ChunkGenerator delegate, ConfigPack pack, BlockState air) {
        this.delegate = new WeakReference<>(delegate);
        this.pack = pack;
        this.air = air;
        worldProperties = new WorldProperties() {
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

    public void setDelegate(ChunkGenerator delegate) {
        this.delegate = new WeakReference<>(delegate);
    }

    public void setPack(ConfigPack pack) {
        this.pack = pack;
        setDelegate(pack.getGeneratorProvider().newInstance(pack));
    }

    private ChunkGenerator getChunkGeneratorDelegate() {
        final var gen = delegate.get();
        if (gen != null) {
            return gen;
        }
        final var newGen = createGenerator(this.pack);
        delegate = new WeakReference<>(newGen);
        return newGen;
    }

    private BiomeProvider getBiomeProviderDelegate() {
        final var provider = biomeProvider.get();
        if(provider != null) {
            return provider;
        }
        final var newProvider = pack.getBiomeProvider();
        biomeProvider = new WeakReference<>(new PNXBiomeProviderDelegate(newProvider,chunkManager));
        return newProvider;
    }

    @Override
    public int getId() {
        return TYPE_INFINITE;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.chunkManager = level;
        this.nukkitRandom = random;
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        getChunkGeneratorDelegate().generateChunkData(new PNXProtoChunk(chunkManager.getChunk(chunkX, chunkZ)), worldProperties,
                getBiomeProviderDelegate(), chunkX, chunkZ);
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        var tmp = new PNXProtoWorld(chunkManager, getChunkGeneratorDelegate(), pack, getBiomeProviderDelegate(), chunkX, chunkZ);
        for (var generationStage : pack.getStages()) {
            try {
                generationStage.populate(tmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 释放掉旧的区块生成器代理以释放内存
        delegate.clear();
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
        return getChunkGeneratorDelegate();
    }
}
