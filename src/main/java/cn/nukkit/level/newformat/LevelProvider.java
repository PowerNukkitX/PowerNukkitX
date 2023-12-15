package cn.nukkit.level.newformat;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface LevelProvider {
    byte ORDER_YZX = 0;
    byte ORDER_ZXY = 1;

    DimensionData getDimensionData();

    AsyncTask requestChunkTask(int X, int Z);

    String getPath();

    String getGenerator();

    Map<String, Object> getGeneratorOptions();

    Chunk getLoadedChunk(int X, int Z);

    Chunk getLoadedChunk(long hash);

    Chunk getChunk(int X, int Z);

    Chunk getChunk(int X, int Z, boolean create);

    Chunk getEmptyChunk(int x, int z);

    void saveChunks();

    void saveChunk(int X, int Z);

    void saveChunk(int X, int Z, Chunk chunk);

    void unloadChunks();

    boolean loadChunk(int X, int Z);

    boolean loadChunk(int X, int Z, boolean create);

    boolean unloadChunk(int X, int Z);

    boolean unloadChunk(int X, int Z, boolean safe);

    boolean isChunkGenerated(int X, int Z);

    boolean isChunkPopulated(int X, int Z);

    boolean isChunkLoaded(int X, int Z);

    boolean isChunkLoaded(long hash);

    void setChunk(int chunkX, int chunkZ, Chunk chunk);

    String getName();

    boolean isRaining();

    void setRaining(boolean raining);

    int getRainTime();

    void setRainTime(int rainTime);

    boolean isThundering();

    void setThundering(boolean thundering);

    int getThunderTime();

    void setThunderTime(int thunderTime);

    long getCurrentTick();

    void setCurrentTick(long currentTick);

    long getTime();

    void setTime(long value);

    long getSeed();

    void setSeed(long value);

    Vector3 getSpawn();

    void setSpawn(Vector3 pos);

    Map<Long, Chunk> getLoadedChunks();

    void doGarbageCollection();

    default void doGarbageCollection(long time) {
    }

    Level getLevel();

    void close();

    void saveLevelData();

    void updateLevelName(String name);

    GameRules getGamerules();

    void setGameRules(GameRules rules);

    @PowerNukkitOnly
    default int getMaximumLayer() {
        return 1;//two layer 0,1
    }

    default int getDimension() {
        return this.getDimensionData().getDimensionId();
    }

    default boolean isOverWorld() {
        return getDimension() == 0;
    }

    default boolean isNether() {
        return getDimension() == 1;
    }

    default boolean isTheEnd() {
        return getDimension() == 2;
    }
}
