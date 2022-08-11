package cn.nukkit.level.format.powerWorld;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.api.UsedByReflection;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.powerWorld.util.PowerWorldKeyUtil;
import cn.nukkit.level.format.powerWorld.worldData.OpenedDB;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import org.jetbrains.annotations.NotNull;
import org.lmdbjava.Dbi;
import org.lmdbjava.Env;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import static cn.nukkit.level.format.powerWorld.util.PowerWorldSerializer.*;
import static org.lmdbjava.ByteBufferProxy.PROXY_SAFE;
import static org.lmdbjava.DbiFlags.MDB_CREATE;
import static org.lmdbjava.EnvFlags.MDB_WRITEMAP;

@SuppressWarnings("ResultOfMethodCallIgnored")
@PowerNukkitXOnly
@Since("1.19.20-r3")
public class PowerWorld implements LevelProvider {
    protected static final String DB_NAME = "Level";
    public static final int LATEST_WORLD_VERSION = 1;

    protected final Level level;
    private final File dbDir;

    protected OpenedDB db;

    protected int worldVersion;

    public PowerWorld(Level level, String path) throws IOException {
        this.level = level;
        this.dbDir = new File(path);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        this.db = openDB(dbDir);
        init();
    }

    protected void init() {
        var dbi = db.dbi();
        try (var txn = db.env().txnRead()) {
            worldVersion = deserializeWorldVersion(dbi.get(txn, PowerWorldKeyUtil.versionKey()));
        }
    }

    @NotNull
    protected static OpenedDB openDB(File dbDir) throws IOException {
        final Env<ByteBuffer> env = Env.create()
                .setMapSize(17_179_869_184L) // 单个地图限制为16GB
                .setMaxDbs(1)
                .open(dbDir, MDB_WRITEMAP);
        final Dbi<ByteBuffer> dbi = env.openDbi(DB_NAME, MDB_CREATE);
        return new OpenedDB(env, dbi);
    }

    @UsedByReflection
    public static boolean isValid(String path) {
        return false;
    }

    @UsedByReflection
    public static void generate(String path, String name, long seed, Class<? extends Generator> generator, @NotNull Map<String, String> options) throws IOException {
        var dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        var db = openDB(dir);
        var nbt = new CompoundTag();
        nbt.putCompound("GameRules", new CompoundTag())
                .putLong("DayTime", 0)
                .putString("generatorName", Generator.getGeneratorName(generator))
                .putString("generatorOptions", options.getOrDefault("preset", ""))
                .putLong("LastPlayed", System.currentTimeMillis() / 1000)
                .putString("LevelName", name)
                .putBoolean("raining", false)
                .putInt("rainTime", 0)
                .putLong("RandomSeed", seed)
                .putInt("SpawnX", 128)
                .putInt("SpawnY", 70)
                .putInt("SpawnZ", 128)
                .putBoolean("thundering", false)
                .putInt("thunderTime", 0)
                .putLong("Time", 0);
        var dbi = db.dbi();
        try (var txn = db.env().txnWrite()) {
            dbi.put(txn, PowerWorldKeyUtil.versionKey(), serializeWorldVersion(LATEST_WORLD_VERSION));
            dbi.put(txn, PowerWorldKeyUtil.worldDataKey(), serializeNBT(nbt));
        }
        db.env().close();
    }

    public File getDbDir() {
        return dbDir;
    }

    @Override
    public AsyncTask requestChunkTask(int X, int Z) {
        return null;
    }

    @Override
    public String getPath() {
        return dbDir.getPath();
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

    // Time
    @Override
    public long getCurrentTick() {
        return 0;
    }

    @Override
    public void setCurrentTick(long currentTick) {

    }

    //DayTime
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
        if (db != null) {
            var env = db.env();
            if (!env.isClosed()) {
                env.close();
            }
        }
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
