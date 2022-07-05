package cn.nukkit.level.format.leveldb.datas;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public record LDBChunkData(LDBChunkHeightMap heightMap,
                           LDBChunkBiomeMap biomeMap) {

    public LDBChunkHeightMap getHeightMap() {
        return this.heightMap;
    }

    public LDBChunkBiomeMap getBiomeMap() {
        return this.biomeMap;
    }
}
