package org.powernukkitx.level.format.leveldb;

import org.cloudburstmc.nbt.NbtMap;
import org.powernukkitx.math.BlockVector3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Shares level.dat metadata across dimension providers. It keeps per-dimension runtime and spawn state while applying
 * the selected canonical authority back to level.dat on save.
 *
 * @author Curse
 */
final class WorldMetadata {
    static final String DIMENSION_RUNTIME_STATES_TAG = "PNXDimensionRuntimeStates";
    static final String DIMENSION_SPAWNS_TAG = "PNXDimensionSpawns";

    private final Path path;
    private final LevelDBStorage storage;
    private final TreeMap<Integer, Integer> dimensions = new TreeMap<>();
    private final TreeMap<Integer, RuntimeState> runtimeStates = new TreeMap<>();
    private final TreeMap<Integer, SpawnState> spawnStates = new TreeMap<>();
    private LevelDat levelDat;
    private WorldBiomeSnowState biomeSnowState;
    private Integer runtimeAuthorityDimensionId;

    WorldMetadata(String path, LevelDBStorage storage) {
        this.path = Path.of(path);
        this.storage = storage;
    }

    Path getPath() {
        return this.path;
    }

    synchronized LevelDat getLevelDat() {
        return this.levelDat;
    }

    synchronized WorldBiomeSnowState getBiomeSnowState() {
        return Objects.requireNonNull(this.biomeSnowState);
    }

    synchronized LoadResult getOrLoadLevelDat(LevelDatLoader loader) throws IOException {
        if (this.levelDat != null) {
            return new LoadResult(this.levelDat, false);
        }
        LevelDat loaded = loader.load();
        boolean created = loaded == null;
        this.levelDat = created ? LevelDat.builder().build() : loaded;
        this.biomeSnowState = new WorldBiomeSnowState(this.storage.readBiomeData());
        loadRuntimeStates(this.levelDat.getRawData());
        loadSpawnStates(this.levelDat.getRawData());
        return new LoadResult(this.levelDat, created);
    }

    synchronized boolean registerDimension(int dimensionId) {
        this.dimensions.merge(dimensionId, 1, Integer::sum);
        boolean initialized = false;

        if (!this.runtimeStates.containsKey(dimensionId)) {
            this.runtimeStates.put(dimensionId, RuntimeState.fromLevelDat(Objects.requireNonNull(this.levelDat)));
            initialized = true;
        }
        if (!this.spawnStates.containsKey(dimensionId)) {
            this.spawnStates.put(dimensionId, SpawnState.fromLevelDat(Objects.requireNonNull(this.levelDat)));
            initialized = true;
        }

        if (dimensionId == 0 || this.runtimeAuthorityDimensionId == null || this.runtimeAuthorityDimensionId != 0 && dimensionId < this.runtimeAuthorityDimensionId) {
            this.runtimeAuthorityDimensionId = dimensionId;
        }
        return initialized;
    }

    synchronized void unregisterDimension(int dimensionId) {
        Integer count = this.dimensions.get(dimensionId);
        if (count == null) return;

        if (count <= 1) {
            this.dimensions.remove(dimensionId);
        } else {
            this.dimensions.put(dimensionId, count - 1);
        }
    }

    synchronized boolean isCanonicalAuthority(int dimensionId) {
        return Objects.equals(this.runtimeAuthorityDimensionId, dimensionId);
    }

    synchronized RuntimeState getRuntimeState(int dimensionId) {
        return this.runtimeStates.computeIfAbsent(dimensionId, ignored -> RuntimeState.fromLevelDat(Objects.requireNonNull(this.levelDat))
        );
    }

    synchronized SpawnState getSpawnState(int dimensionId) {
        return this.spawnStates.computeIfAbsent(dimensionId, ignored -> SpawnState.fromLevelDat(Objects.requireNonNull(this.levelDat))
        );
    }

    synchronized void setSpawnState(int dimensionId, int x, int y, int z) {
        SpawnState state = new SpawnState(x, y, z);
        this.spawnStates.put(dimensionId, state);
        if (Objects.equals(this.runtimeAuthorityDimensionId, dimensionId)) {
            state.applyTo(Objects.requireNonNull(this.levelDat));
        }
    }

    synchronized void saveLevelDat(int dimensionId, long time, long currentTick, boolean raining, int rainTime, boolean thundering, int thunderTime, int noSleepNight) {
        LevelDat levelDat = Objects.requireNonNull(this.levelDat);
        this.runtimeStates.put(dimensionId, new RuntimeState(
                time, currentTick, raining, rainTime, thundering, thunderTime, noSleepNight
        ));

        RuntimeState authorityState = this.runtimeStates.get(this.runtimeAuthorityDimensionId);
        if (authorityState != null) authorityState.applyTo(levelDat);

        SpawnState authoritySpawn = this.spawnStates.get(this.runtimeAuthorityDimensionId);
        if (authoritySpawn != null) authoritySpawn.applyTo(levelDat);

        this.storage.writeBiomeData(Objects.requireNonNull(this.biomeSnowState).createNbt());
        LevelDBProvider.writeLevelDat(this.path.toString(), levelDat, createRuntimeStatesNbt(), createSpawnStatesNbt());
    }

    private void loadRuntimeStates(NbtMap rawData) {
        Object value = rawData.get(DIMENSION_RUNTIME_STATES_TAG);
        if (!(value instanceof NbtMap states)) return;

        for (var entry : states.entrySet()) {
            if (!(entry.getValue() instanceof NbtMap state)) continue;

            try {
                this.runtimeStates.put(Integer.parseInt(entry.getKey()), RuntimeState.fromNbt(state, Objects.requireNonNull(this.levelDat)));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void loadSpawnStates(NbtMap rawData) {
        Object value = rawData.get(DIMENSION_SPAWNS_TAG);
        if (!(value instanceof NbtMap states)) return;

        for (var entry : states.entrySet()) {
            if (!(entry.getValue() instanceof NbtMap state)) continue;

            try {
                this.spawnStates.put(Integer.parseInt(entry.getKey()), SpawnState.fromNbt(state, Objects.requireNonNull(this.levelDat)));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private NbtMap createRuntimeStatesNbt() {
        var states = NbtMap.builder();
        this.runtimeStates.forEach((dimensionId, state) -> states.put(Integer.toString(dimensionId), state.toNbt()));
        return states.build();
    }

    private NbtMap createSpawnStatesNbt() {
        var states = NbtMap.builder();
        this.spawnStates.forEach((dimensionId, state) -> states.put(Integer.toString(dimensionId), state.toNbt()));
        return states.build();
    }

    private static long getLong(NbtMap tag, String key, long fallback) {
        Object value = tag.get(key);
        return value instanceof Number number ? number.longValue() : fallback;
    }

    private static int getInt(NbtMap tag, String key, int fallback) {
        Object value = tag.get(key);
        return value instanceof Number number ? number.intValue() : fallback;
    }

    private static boolean getBoolean(NbtMap tag, String key, boolean fallback) {
        Object value = tag.get(key);
        return value instanceof Number number ? number.intValue() != 0 : fallback;
    }

    /**
     * Stores runtime level.dat values for one dimension.
     *
     * @param time value for this API
     * @param currentTick value for this API
     * @param raining value for this API
     * @param rainTime value for this API
     * @param thundering value for this API
     * @param thunderTime value for this API
     * @param noSleepNight value for this API
     *
     * @author Curse
     */
    record RuntimeState(long time, long currentTick, boolean raining, int rainTime, boolean thundering, int thunderTime, int noSleepNight) {
        static RuntimeState fromLevelDat(LevelDat levelDat) {
            return new RuntimeState(
                    levelDat.getTime(),
                    levelDat.getCurrentTick(),
                    levelDat.isRaining(),
                    levelDat.getRainTime(),
                    levelDat.isThundering(),
                    levelDat.getLightningTime(),
                    levelDat.getNoSleepNight()
            );
        }

        static RuntimeState fromNbt(NbtMap tag, LevelDat levelDat) {
            RuntimeState fallback = fromLevelDat(levelDat);
            return new RuntimeState(
                    getLong(tag, "Time", fallback.time),
                    getLong(tag, "currentTick", fallback.currentTick),
                    getBoolean(tag, "raining", fallback.raining),
                    getInt(tag, "rainTime", fallback.rainTime),
                    getBoolean(tag, "thundering", fallback.thundering),
                    getInt(tag, "lightningTime", fallback.thunderTime),
                    getInt(tag, "noSleepNights", fallback.noSleepNight)
            );
        }

        NbtMap toNbt() {
            return NbtMap.builder()
                    .putLong("Time", this.time)
                    .putLong("currentTick", this.currentTick)
                    .putBoolean("raining", this.raining)
                    .putInt("rainTime", this.rainTime)
                    .putBoolean("thundering", this.thundering)
                    .putInt("lightningTime", this.thunderTime)
                    .putInt("noSleepNights", this.noSleepNight)
                    .build();
        }

        void applyTo(LevelDat levelDat) {
            levelDat.setTime(this.time);
            levelDat.setCurrentTick(this.currentTick);
            levelDat.setRaining(this.raining);
            levelDat.setRainTime(this.rainTime);
            levelDat.setThundering(this.thundering);
            levelDat.setLightningTime(this.thunderTime);
            levelDat.setNoSleepNight(this.noSleepNight);
        }
    }

    /**
     * Stores a dimension spawn position.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     *
     * @author Curse
     */
    record SpawnState(int x, int y, int z) {
        static SpawnState fromLevelDat(LevelDat levelDat) {
            BlockVector3 spawn = levelDat.getSpawnPoint();
            return new SpawnState(spawn.getX(), spawn.getY(), spawn.getZ());
        }

        static SpawnState fromNbt(NbtMap tag, LevelDat levelDat) {
            SpawnState fallback = fromLevelDat(levelDat);
            return new SpawnState(
                    getInt(tag, "SpawnX", fallback.x),
                    getInt(tag, "SpawnY", fallback.y),
                    getInt(tag, "SpawnZ", fallback.z)
            );
        }

        NbtMap toNbt() {
            return NbtMap.builder()
                    .putInt("SpawnX", this.x)
                    .putInt("SpawnY", this.y)
                    .putInt("SpawnZ", this.z)
                    .build();
        }

        void applyTo(LevelDat levelDat) {
            levelDat.setSpawnPoint(new BlockVector3(this.x, this.y, this.z));
        }
    }

    /**
     * Describes the result of loading level.dat metadata.
     *
     * @param levelDat value for this API
     * @param created value for this API
     *
     * @author Curse
     */
    record LoadResult(LevelDat levelDat, boolean created) {
    }

    /**
     * Loads level.dat data for the shared world metadata cache.
     *
     * @author Curse
     */
    @FunctionalInterface
    interface LevelDatLoader {
        LevelDat load() throws IOException;
    }
}
