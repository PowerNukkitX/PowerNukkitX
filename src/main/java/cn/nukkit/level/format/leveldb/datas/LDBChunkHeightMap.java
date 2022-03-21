package cn.nukkit.level.format.leveldb.datas;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public final class LDBChunkHeightMap {
    private final int[] heightMap = new int[256];

    /**
     * 获取最高的方块y坐标
     * @param x x
     * @param z z
     * @return 高度
     */
    public int getHighestBlockAt(int x, int z) {
        return this.heightMap[getChunkPosIndex(x & 0xF, z & 0xF)];
    }

    /**
     * 设置最高的方块y坐标
     * @param x x
     * @param z z
     * @param newHeight 新高度
     */
    public void setHighestBlockAt(int x, int z, int newHeight) {
        this.heightMap[getChunkPosIndex(x & 0xF, z & 0xF)] = newHeight;
    }

    public int[] array() {
        return this.heightMap;
    }

    private static int getChunkPosIndex(int x, int z) {
        return z << 4 | x;
    }
}
