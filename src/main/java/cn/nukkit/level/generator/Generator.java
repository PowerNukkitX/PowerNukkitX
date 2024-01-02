package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.utils.random.NukkitRandomSource;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.random.RandomSource;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Generator implements BlockID {
    protected ChunkManager chunkManager;
    protected RandomSource random;
    protected Level level;
    protected final Map<String, Object> options;

    public Generator(Map<String, Object> options) {
        this.options = options;
    }

    public Level getLevel() {
        return level;
    }

    public RandomSource getRandom() {
        return random;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public Map<String, Object> getSettings() {
        return this.options;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setChunkManager(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    public void setRandom(NukkitRandomSource random) {
        this.random = random;
    }

    public abstract String getName();

    public abstract DimensionData getDimensionData();

    public abstract void generateChunk(int chunkX, int chunkZ);

    public abstract void populateChunk(int chunkX, int chunkZ);

    /**
     * 处理需要计算的异步地形生成任务<br/>
     * 有特殊需求的地形生成器可以覆写此方法并提供自己的逻辑<br/>
     * 默认采用Server类的fjp线程池
     *
     * @param task 地形生成任务
     */

    public void handleAsyncChunkPopTask(AsyncTask task) {
        Server.getInstance().computeThreadPool.submit(task);
    }

    /**
     * 处理需要计算的异步结构生成任务<br/>
     * 有特殊需求的地形生成器可以覆写此方法并提供自己的逻辑<br/>
     * 默认采用Server类的fjp线程池
     *
     * @param task 结构生成任务
     */
    public void handleAsyncStructureGenTask(AsyncTask task) {
        Server.getInstance().computeThreadPool.submit(task);
    }
}
