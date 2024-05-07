package cn.nukkit.level.format;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.Pair;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface LevelProvider {
    byte ORDER_YZX = 0;
    byte ORDER_ZXY = 1;

    DimensionData getDimensionData();

    Pair<byte[], Integer> requestChunkData(int x, int z);

    String getPath();

    IChunk getLoadedChunk(int x, int z);

    IChunk getLoadedChunk(long hash);

    IChunk getChunk(int x, int z);

    IChunk getChunk(int x, int z, boolean create);

    IChunk getEmptyChunk(int x, int z);

    void saveChunks();

    void saveChunk(int x, int z);

    void saveChunk(int x, int z, IChunk chunk);

    void unloadChunks();

    boolean loadChunk(int x, int z);

    boolean loadChunk(int x, int z, boolean create);

    boolean unloadChunk(int x, int z);

    boolean unloadChunk(int x, int z, boolean safe);

    boolean isChunkGenerated(int x, int z);

    boolean isChunkPopulated(int x, int z);

    boolean isChunkLoaded(int x, int z);

    boolean isChunkLoaded(long hash);

    void setChunk(int chunkX, int chunkZ, IChunk chunk);

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

    Map<Long, IChunk> getLoadedChunks();

    Level getLevel();

    void close();

    void saveLevelData();

    void updateLevelName(String name);

    GameRules getGamerules();

    void setGameRules(GameRules rules);

    default int getMaximumLayer() {
        return 1;//two layer 0,1
    }

    default boolean isOverWorld() {
        return this.getDimensionData().getDimensionId() == 0;
    }

    default boolean isNether() {
        return this.getDimensionData().getDimensionId() == 1;
    }

    default boolean isTheEnd() {
        return this.getDimensionData().getDimensionId() == 2;
    }
}
