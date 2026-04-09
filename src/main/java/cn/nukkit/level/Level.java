package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.GameLoop;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Arrays;

@Slf4j
public class Level {
    public static final Level[] EMPTY_ARRAY = new Level[0];

    public static final int BLOCK_UPDATE_NORMAL = Dimension.BLOCK_UPDATE_NORMAL;
    public static final int BLOCK_UPDATE_RANDOM = Dimension.BLOCK_UPDATE_RANDOM;
    public static final int BLOCK_UPDATE_SCHEDULED = Dimension.BLOCK_UPDATE_SCHEDULED;
    public static final int BLOCK_UPDATE_WEAK = Dimension.BLOCK_UPDATE_WEAK;
    public static final int BLOCK_UPDATE_TOUCH = Dimension.BLOCK_UPDATE_TOUCH;
    public static final int BLOCK_UPDATE_REDSTONE = Dimension.BLOCK_UPDATE_REDSTONE;
    public static final int BLOCK_UPDATE_TICK = Dimension.BLOCK_UPDATE_TICK;
    public static final int BLOCK_UPDATE_MOVED = Dimension.BLOCK_UPDATE_MOVED;

    public static final int TIME_DAY = Dimension.TIME_DAY;
    public static final int TIME_NOON = Dimension.TIME_NOON;
    public static final int TIME_SUNSET = Dimension.TIME_SUNSET;
    public static final int TIME_NIGHT = Dimension.TIME_NIGHT;
    public static final int TIME_MIDNIGHT = Dimension.TIME_MIDNIGHT;
    public static final int TIME_SUNRISE = Dimension.TIME_SUNRISE;
    public static final int TIME_FULL = Dimension.TIME_FULL;

    public static final int DIMENSION_OVERWORLD = Dimension.DIMENSION_OVERWORLD;
    public static final int DIMENSION_NETHER = Dimension.DIMENSION_NETHER;
    public static final int DIMENSION_THE_END = Dimension.DIMENSION_THE_END;

    private static int levelIdCounter = 1;

    private final int id;
    private final Server server;
    private final String name;
    private final String folderPath;
    private final Dimension[] dimensions = new Dimension[3];

    private final Thread baseTickThread;
    @Getter
    private final GameLoop baseTickGameLoop;
    private final Thread subTickThread;
    @Getter
    private final GameLoop subTickGameLoop;

    /**
     * Creates a new world container with tick loops.
     *
     * @param server the owning server
     * @param name the world name
     * @param folderPath the world folder path
     */
    public Level(Server server, String name, String folderPath) {
        this.id = levelIdCounter++;
        this.server = server;
        this.name = name;
        this.folderPath = folderPath;

        final String levelName = this.name;
        baseTickGameLoop = GameLoop.builder()
                .onTick(this::doTick)
                .onStop(this::remove)
                .loopCountPerSec(20)
                .build();
        this.baseTickThread = new Thread() {
            {
                setName(Level.this.getFolderName());
            }

            @Override
            public void run() {
                baseTickGameLoop.startLoop();
            }
        };
        subTickGameLoop = GameLoop.builder()
                .onTick(this::subTick)
                .onStop(() -> log.debug("{} SubTick is closed!", levelName))
                .loopCountPerSec(20)
                .build();
        this.subTickThread = new Thread() {
            {
                setName(Level.this.getFolderName() + " SubTick");
            }

            @Override
            public void run() {
                subTickGameLoop.startLoop();
            }
        };
    }

    /**
     * Returns the internal world id.
     *
     * @return the world id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the owning server.
     *
     * @return the server
     */
    public Server getServer() {
        return server;
    }

    /**
     * Returns the world name.
     *
     * @return the world name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the world folder name.
     *
     * @return the folder name
     */
    public String getFolderName() {
        return name;
    }

    /**
     * Returns the world folder path.
     *
     * @return the folder path
     */
    public String getFolderPath() {
        return folderPath;
    }

    /**
     * Registers a dimension in this world.
     *
     * @param dimension the dimension to add
     */
    public void addDimension(Dimension dimension) {
        int id = dimension.getDimension();
        if (id < 0 || id > 2) {
            throw new IllegalArgumentException("Only dimensions 0 (overworld), 1 (nether), 2 (the_end) are supported");
        }
        dimensions[id] = dimension;
    }

    /**
     * Returns a dimension by id.
     *
     * @param dimensionId the dimension id (0..2)
     * @return the dimension, or null if invalid or not present
     */
    public Dimension getDimension(int dimensionId) {
        if (dimensionId < 0 || dimensionId > 2) {
            return null;
        }
        return dimensions[dimensionId];
    }

    /**
     * Returns the overworld dimension.
     *
     * @return the overworld dimension
     */
    public Dimension getOverworld() {
        return getDimension(DIMENSION_OVERWORLD);
    }

    /**
     * Returns the nether dimension.
     *
     * @return the nether dimension
     */
    public Dimension getNether() {
        return getDimension(DIMENSION_NETHER);
    }

    /**
     * Returns the end dimension.
     *
     * @return the end dimension
     */
    public Dimension getTheEnd() {
        return getDimension(DIMENSION_THE_END);
    }

    /**
     * Returns all loaded dimensions in this world.
     *
     * @return a collection of dimensions
     */
    public Collection<Dimension> getDimensions() {
        return Arrays.stream(dimensions).filter(d -> d != null).toList();
    }

    /**
     * Initializes all dimensions and starts tick loops.
     */
    public void initLevel() {
        for (Dimension dimension : getDimensions()) {
            dimension.initLevel();
        }
        subTickThread.start();
        if (server.getSettings().levelSettings().levelThread()) {
            baseTickThread.start();
        }
    }

    /**
     * Stops tick loops and closes all dimensions.
     */
    public void close() {
        if (baseTickThread.isAlive()) {
            baseTickGameLoop.stop();
            try {
                baseTickThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (baseTickThread.isAlive()) {
                log.warn("Level {} tick thread did not stop in time, interrupting", getFolderName());
                baseTickThread.interrupt();
                try {
                    baseTickThread.join(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (!getDimensions().isEmpty()) {
                    remove();
                    log.warn("Level {} tick thread did not stop gracefully, forcing level unload.", getFolderName());
                }
            }
        } else {
            remove();
        }
    }

    /**
     * Unloads all dimensions in this world.
     *
     * @param force whether to force unload
     * @return true if all dimensions unloaded
     */
    public boolean unload(boolean force) {
        boolean ok = true;
        for (Dimension dimension : getDimensions()) {
            if (!dimension.unload(force)) {
                ok = false;
            }
        }
        if (ok) {
            close();
        }
        return ok;
    }

    /**
     * Removes all dimensions and unregisters this world.
     */
    private void remove() {
        subTickGameLoop.stop();
        for (Dimension dimension : getDimensions()) {
            dimension.close();
        }
        Arrays.fill(dimensions, null);
        server.getLevels().remove(this.id);
    }

    /**
     * Returns whether this world is registered as loaded.
     *
     * @return true if loaded
     */
    public boolean isLoaded() {
        return server.getLevels().containsKey(this.id);
    }

    /**
     * Returns whether the base tick thread is running.
     *
     * @return true if running
     */
    public boolean isThreadRunning() {
        return baseTickThread.isAlive();
    }

    /**
     * Ticks all dimensions for the current tick.
     *
     * @param gameLoop the base game loop
     */
    private void doTick(GameLoop gameLoop) {
        for (Dimension dimension : getDimensions()) {
            dimension.doTick(gameLoop.getTick());
        }
    }

    /**
     * Runs the sub-tick loop for all dimensions.
     *
     * @param gameLoop the sub tick loop
     */
    private void subTick(GameLoop gameLoop) {
        for (Dimension dimension : getDimensions()) {
            dimension.subTick(gameLoop);
        }
    }

    /**
     * Computes a chunk hash from coordinates.
     *
     * @param x the chunk x
     * @param z the chunk z
     * @return the chunk hash
     */
    public static long chunkHash(int x, int z) {
        return Dimension.chunkHash(x, z);
    }

    /**
     * Computes a block hash for a dimension.
     *
     * @param x the block x
     * @param y the block y
     * @param z the block z
     * @param level the dimension
     * @return the block hash
     */
    public static long blockHash(int x, int y, int z, Dimension level) {
        return Dimension.blockHash(x, y, z, level);
    }

    /**
     * Computes a local block hash for a dimension.
     *
     * @param x the block x
     * @param y the block y
     * @param z the block z
     * @param level the dimension
     * @return the local block hash
     */
    public static int localBlockHash(double x, double y, double z, Dimension level) {
        return Dimension.localBlockHash(x, y, z, level);
    }

    /**
     * Computes a local block hash for a layer.
     *
     * @param x the block x
     * @param y the block y
     * @param z the block z
     * @param layer the layer
     * @param level the dimension
     * @return the local block hash
     */
    public static int localBlockHash(int x, int y, int z, int layer, Dimension level) {
        return Dimension.localBlockHash(x, y, z, layer, level);
    }

    /**
     * Expands a chunk hash and block hash into a world position.
     *
     * @param chunkHash the chunk hash
     * @param blockHash the block hash
     * @param level the dimension
     * @return the world position
     */
    public static Vector3 getBlockXYZ(long chunkHash, int blockHash, Dimension level) {
        return Dimension.getBlockXYZ(chunkHash, blockHash, level);
    }

    /**
     * Gets the chunk X from a chunk hash.
     *
     * @param hash the chunk hash
     * @return the chunk x
     */
    public static int getHashX(long hash) {
        return Dimension.getHashX(hash);
    }

    /**
     * Gets the chunk Z from a chunk hash.
     *
     * @param hash the chunk hash
     * @return the chunk z
     */
    public static int getHashZ(long hash) {
        return Dimension.getHashZ(hash);
    }

    /**
     * Saves all dimensions.
     */
    public void save() {
        for(Dimension dim : getDimensions()) {
            dim.save();
        }
    }

    /**
     * Saves all dimensions.
     *
     * @param force whether to force save
     */
    public void save(boolean force) {
        for(Dimension dim : getDimensions()) {
            dim.save(force);
        }
    }
}
