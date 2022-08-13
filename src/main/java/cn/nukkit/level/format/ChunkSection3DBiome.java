package cn.nukkit.level.format;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.20-r3")
public interface ChunkSection3DBiome {
    /**
     * 获取子区块中某个特定位置的生物群系id
     * @param x [0, 16)
     * @param y [0, 16)
     * @param z [0, 16)
     * @return 特定位置的生物群系id
     */
    int getBiomeId(int x, int y, int z);

    /**
     * 设置子区块中某个特定位置的生物群系id
     * @param x [0, 16)
     * @param y [0, 16)
     * @param z [0, 16)
     * @param id 生物群系id
     */
    void setBiomeId(int x, int y, int z, byte id);
}
