package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.terra.TerraGenerator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Map;

/**
 * 用于nk底层的Terra生成器代理类<br/>
 * 这样做的具体原因详见{@link cn.nukkit.level.terra.TerraGenerator}<br/>
 * <br/>
 * Terra generator wrapper class for nk bottom layer<br/>
 * The specific reasons for this are detailed in {@link cn.nukkit.level.terra.TerraGenerator}
 */
@Since("1.19.50-r2")
@PowerNukkitXOnly
public class TerraGeneratorWrapper extends Generator {

    //此代理类实例共享的Terra生成器实例
    protected static TerraGenerator INSTANCE;
    protected static Map<String, Object> OPTION;

    public TerraGeneratorWrapper(Map<String, Object> option) {
        TerraGeneratorWrapper.OPTION = option;
    }

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom random) {
        synchronized (TerraGeneratorWrapper.class) {
            if (INSTANCE == null) INSTANCE = new TerraGenerator(OPTION, getLevel());
        }
    }

    @Override
    public int getId() {
        return INSTANCE.getDimensionData().getDimensionId();
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        INSTANCE.generateChunk(chunkX, chunkZ, getChunkManager());
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        INSTANCE.populateChunk(chunkX, chunkZ, getChunkManager());
    }

    @Override
    public Map<String, Object> getSettings() {
        return INSTANCE.getSettings();
    }

    @Override
    public String getName() {
        return INSTANCE.getName();
    }

    @Override
    public Vector3 getSpawn() {
        return INSTANCE.getSpawn();
    }

    @Override
    public DimensionData getDimensionData() {
        return INSTANCE.getDimensionData();
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }

    /**
     * Terra底层自带fjp线程池进行多核并行区块生成，所以说这边我们不需要使用fjp
     */
    @Since("1.19.50-r2")
    @Override
    public void handleAsyncChunkPopTask(AsyncTask task) {
        Server.getInstance().getScheduler().scheduleAsyncTask(null, task);
    }
}
