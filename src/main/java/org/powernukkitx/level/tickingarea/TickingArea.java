package org.powernukkitx.level.tickingarea;

import org.powernukkitx.Server;
import org.powernukkitx.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class TickingArea {
    protected final UUID uuid;
    protected String name;
    protected String levelName;
    protected final int dimensionId;
    protected final boolean circle;
    protected final int distance;
    protected final boolean preload;
    protected Set<ChunkPos> chunks = new HashSet<>();

    public TickingArea(String name, String levelName, ChunkPos... chunks) {
        this(UUID.randomUUID(), resolveName(name, levelName), levelName, resolveDimensionId(levelName), false, 0, false, chunks);
    }

    public TickingArea(String name, String levelName, boolean circle, int distance, boolean preload, ChunkPos... chunks) {
        this(UUID.randomUUID(), resolveName(name, levelName), levelName, resolveDimensionId(levelName), circle, distance, preload, chunks);
    }

    public TickingArea(UUID uuid, String name, String levelName, int dimensionId, boolean circle, int distance, boolean preload, ChunkPos... chunks) {
        if (uuid == null) throw new IllegalArgumentException("Ticking area UUID cannot be null");
        if (name == null) throw new IllegalArgumentException("Ticking area name cannot be null");
        if (name.length() > 255) throw new IllegalArgumentException("Name cannot be longer than 255 characters");
        if (circle && distance < 0) throw new IllegalArgumentException("Ticking area distance cannot be negative");
        this.uuid = uuid;
        this.name = name;
        this.levelName = levelName;
        this.dimensionId = dimensionId;
        this.circle = circle;
        this.distance = distance;
        this.preload = preload;
        for (ChunkPos chunk : chunks) {
            addChunk(chunk);
        }
    }

    private static String resolveName(String name, String levelName) {
        if (name != null && !name.isEmpty()) {
            if (name.length() > 255) throw new IllegalArgumentException("Name cannot be longer than 255 characters");
            return name;
        }
        Level level = Server.getInstance().getLevelByName(levelName);
        if (level == null) throw new IllegalArgumentException("Ticking area level is not loaded: " + levelName);
        var manager = Server.getInstance().getTickingAreaManager();
        int index = 0;
        String generatedName;
        do {
            generatedName = "Area" + index++;
        } while (manager != null && manager.containTickingArea(level, generatedName));
        return generatedName;
    }

    private static int resolveDimensionId(String levelName) {
        Level level = Server.getInstance().getLevelByName(levelName);
        if (level == null) throw new IllegalArgumentException("Ticking area level is not loaded: " + levelName);
        return level.getDimensionData().getDimensionId();
    }

    public void addChunk(ChunkPos chunk) {
        this.chunks.add(chunk);
    }

    public boolean loadAllChunk() {
        Level level = Server.getInstance().getLevelByName(levelName);
        if (level == null) {
            if (!Server.getInstance().loadLevel(levelName)) return false;
            level = Server.getInstance().getLevelByName(levelName);
        }
        if (level == null || level.getDimensionData().getDimensionId() != dimensionId) return false;
        for (ChunkPos pos : chunks) {
            if (!level.loadChunk(pos.x, pos.z)) return false;
        }
        return true;
    }

    //two entry [0] => min, [1] => max
    public List<ChunkPos> minAndMaxChunkPos() {
        ChunkPos min = new ChunkPos(Integer.MAX_VALUE, Integer.MAX_VALUE);
        ChunkPos max = new ChunkPos(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (ChunkPos pos : chunks) {
            if (pos.x < min.x) min.x = pos.x;
            if (pos.z < min.z) min.z = pos.z;
            if (pos.x > max.x) max.x = pos.x;
            if (pos.z > max.z) max.z = pos.z;
        }
        return List.of(min, max);
    }

    /**
     * Returns the persistent UUID of this ticking area.
     *
     * @return ticking-area UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getLevelName() {
        return levelName;
    }

    /**
     * Returns the dimension ID associated with this ticking area.
     *
     * @return dimension ID
     */
    public int getDimensionId() {
        return dimensionId;
    }

    /**
     * Returns whether this ticking area uses circular bounds.
     *
     * @return whether the area is circular
     */
    public boolean isCircle() {
        return circle;
    }

    /**
     * Returns the configured ticking-area distance.
     *
     * @return ticking-area distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Returns whether chunks in this ticking area should be preloaded.
     *
     * @return whether preloading is enabled
     */
    public boolean isPreload() {
        return preload;
    }

    public Set<ChunkPos> getChunks() {
        return chunks;
    }

    public static class ChunkPos {
        public int x;
        public int z;

        public ChunkPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ChunkPos anotherChunkPos) return anotherChunkPos.x == this.x && anotherChunkPos.z == this.z;
            return false;
        }

        @Override
        public int hashCode() {
            return x ^ (z << 12);
        }
    }
}
