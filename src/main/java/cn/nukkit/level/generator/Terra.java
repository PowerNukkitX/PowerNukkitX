package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.terra.PNXPlatform;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXProtoChunk;
import cn.nukkit.level.terra.delegate.PNXProtoWorld;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.world.chunk.generation.ChunkGenerator;
import com.dfsek.terra.api.world.chunk.generation.util.GeneratorWrapper;
import com.dfsek.terra.api.world.info.WorldProperties;

import java.util.Collections;
import java.util.Map;

public class Terra extends Generator implements GeneratorWrapper {
    private ChunkGenerator delegate;
    private ConfigPack pack;
    private final BlockState air;

    private ChunkManager chunkManager = null;
    private NukkitRandom nukkitRandom = null;

    public Terra() {
        this(createGenerator(), createConfigPack(), new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR));
    }

    public Terra(Map<String, Object> ignore) {
        this.air = new PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState.AIR);
        try {
            this.delegate = createGenerator();
            this.pack = createConfigPack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ConfigPack createConfigPack() {
        return PNXPlatform.getInstance().getConfigRegistry().getByID("DEFAULT").orElseGet(
                () -> PNXPlatform.getInstance().getConfigRegistry().getByID("Terra:DEFAULT").orElseThrow()
        );
    }

    private static ChunkGenerator createGenerator() {
        var config = createConfigPack();
        return config.getGeneratorProvider().newInstance(config);
    }

    public Terra(ChunkGenerator delegate, ConfigPack pack, BlockState air) {
        this.delegate = delegate;
        this.pack = pack;
        this.air = air;
    }

    public void setDelegate(ChunkGenerator delegate) {
        this.delegate = delegate;
    }

    public void setPack(ConfigPack pack) {
        this.pack = pack;
        setDelegate(pack.getGeneratorProvider().newInstance(pack));
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
        delegate.generateChunkData(new PNXProtoChunk(chunkManager.getChunk(chunkX, chunkZ)), new WorldProperties() {
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
        }, pack.getBiomeProvider().caching(), chunkX, chunkZ);
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        var tmp = new PNXProtoWorld(chunkManager, delegate, pack);
        for (var generationStage : pack.getStages()) {
            try {
                generationStage.populate(tmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public String getName() {
        return "Terra";
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
        return delegate;
    }
}
