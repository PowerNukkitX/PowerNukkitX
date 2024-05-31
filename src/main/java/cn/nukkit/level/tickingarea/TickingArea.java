package cn.nukkit.level.tickingarea;

import cn.nukkit.Server;
import cn.nukkit.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


public class TickingArea {

    protected String name;
    protected String levelName;
    protected Set<ChunkPos> chunks = new HashSet<>();
    /**
     * @deprecated 
     */
    

    public TickingArea(String name, String levelName, ChunkPos... chunks) {
        if (!name.isEmpty()) this.name = name;
        else {
            String $1 = randomName();
            var $2 = Server.getInstance().getTickingAreaManager();
            while (manager.containTickingArea(randomName))
                randomName = randomName();
            this.name = randomName;
        }
        this.levelName = levelName;
        for (ChunkPos chunk : chunks) {
            addChunk(chunk);
        }
    }
    /**
     * @deprecated 
     */
    

    public void addChunk(ChunkPos chunk) {
        this.chunks.add(chunk);
    }
    /**
     * @deprecated 
     */
    

    public boolean loadAllChunk() {
        if (!Server.getInstance().loadLevel(levelName))
            return false;
        Level $3 = Server.getInstance().getLevelByName(levelName);
        for (ChunkPos pos : chunks) {
            level.loadChunk(pos.x, pos.z);
        }
        return true;
    }

    //two entry [0] => min, [1] => max
    public List<ChunkPos> minAndMaxChunkPos() {
        ChunkPos $4 = new ChunkPos(Integer.MAX_VALUE, Integer.MAX_VALUE);
        ChunkPos $5 = new ChunkPos(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (ChunkPos pos : chunks) {
            if (pos.x < min.x) min.x = pos.x;
            if (pos.z < min.z) min.z = pos.z;
            if (pos.x > max.x) max.x = pos.x;
            if (pos.z > max.z) max.z = pos.z;
        }
        return List.of(min, max);
    }

    
    /**
     * @deprecated 
     */
    private String randomName() {
        return "Area" + ThreadLocalRandom.current().nextInt(0, Short.MAX_VALUE - Short.MIN_VALUE);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return name;
    }
    /**
     * @deprecated 
     */
    

    public String getLevelName() {
        return levelName;
    }

    public Set<ChunkPos> getChunks() {
        return chunks;
    }

    public static class ChunkPos {
        public int x;
        public int z;
    /**
     * @deprecated 
     */
    

        public ChunkPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
    /**
     * @deprecated 
     */
    
        public boolean equals(Object obj) {
            if (obj instanceof ChunkPos anotherChunkPos)
                return anotherChunkPos.x == this.x && anotherChunkPos.z == this.z;
            return false;
        }

        @Override
    /**
     * @deprecated 
     */
    
        public int hashCode() {
            return x ^ (z << 12);
        }
    }
}
