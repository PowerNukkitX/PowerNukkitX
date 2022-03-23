package cn.nukkit.level.format.leveldb;

import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Map;

public final class LevelDB implements LevelProvider {
    @Override
    public AsyncTask requestChunkTask(int X, int Z) {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getGenerator() {
        return null;
    }

    @Override
    public Map<String, Object> getGeneratorOptions() {
        return null;
    }

    @Override
    public BaseFullChunk getLoadedChunk(int X, int Z) {
        return null;
    }

    @Override
    public BaseFullChunk getLoadedChunk(long hash) {
        return null;
    }

    @Override
    public BaseFullChunk getChunk(int X, int Z) {
        return null;
    }

    @Override
    public BaseFullChunk getChunk(int X, int Z, boolean create) {
        return null;
    }

    @Override
    public BaseFullChunk getEmptyChunk(int x, int z) {
        return null;
    }

    @Override
    public void saveChunks() {

    }

    @Override
    public void saveChunk(int X, int Z) {

    }

    @Override
    public void saveChunk(int X, int Z, FullChunk chunk) {

    }

    @Override
    public void unloadChunks() {

    }

    @Override
    public boolean loadChunk(int X, int Z) {
        return false;
    }

    @Override
    public boolean loadChunk(int X, int Z, boolean create) {
        return false;
    }

    @Override
    public boolean unloadChunk(int X, int Z) {
        return false;
    }

    @Override
    public boolean unloadChunk(int X, int Z, boolean safe) {
        return false;
    }

    @Override
    public boolean isChunkGenerated(int X, int Z) {
        return false;
    }

    @Override
    public boolean isChunkPopulated(int X, int Z) {
        return false;
    }

    @Override
    public boolean isChunkLoaded(int X, int Z) {
        return false;
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        return false;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isRaining() {
        return false;
    }

    @Override
    public void setRaining(boolean raining) {

    }

    @Override
    public int getRainTime() {
        return 0;
    }

    @Override
    public void setRainTime(int rainTime) {

    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public void setThundering(boolean thundering) {

    }

    @Override
    public int getThunderTime() {
        return 0;
    }

    @Override
    public void setThunderTime(int thunderTime) {

    }

    @Override
    public long getCurrentTick() {
        return 0;
    }

    @Override
    public void setCurrentTick(long currentTick) {

    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public void setTime(long value) {

    }

    @Override
    public long getSeed() {
        return 0;
    }

    @Override
    public void setSeed(long value) {

    }

    @Override
    public Vector3 getSpawn() {
        return null;
    }

    @Override
    public void setSpawn(Vector3 pos) {

    }

    @Override
    public Map<Long, ? extends FullChunk> getLoadedChunks() {
        return null;
    }

    @Override
    public void doGarbageCollection() {

    }

    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void saveLevelData() {

    }

    @Override
    public void updateLevelName(String name) {

    }

    @Override
    public GameRules getGamerules() {
        return null;
    }

    @Override
    public void setGameRules(GameRules rules) {

    }
}
