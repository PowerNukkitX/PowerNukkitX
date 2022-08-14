package cn.nukkit.level.format;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface Chunk extends FullChunk {
    boolean isSectionEmpty(float fY);

    ChunkSection getSection(float fY);

    boolean setSection(float fY, ChunkSection section);

    ChunkSection[] getSections();

    /**
     * 最大高度，请注意此高度不能放置方块，-1之后才能
     * @return 最大高度
     */
    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    int getMaxHeight();

    /**
     * 最低高度，此高度可以放置方块
     * @return 最低高度
     */
    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    int getMinHeight();

    @PowerNukkitXOnly
    @Since("1.19.20-r3")
    default boolean isChunkSection3DBiomeSupported() {
        return false;
    }

    class Entry {
        public final int chunkX;
        public final int chunkZ;

        public Entry(int chunkX, int chunkZ) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }
    }
}
