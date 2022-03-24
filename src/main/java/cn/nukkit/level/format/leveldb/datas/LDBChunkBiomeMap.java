package cn.nukkit.level.format.leveldb.datas;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.leveldb.palette.IntPalette;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public final class LDBChunkBiomeMap {
    private final Int2ObjectOpenHashMap<LDBSubChunkBiomeMap> subChunkBiomes = new Int2ObjectOpenHashMap<>();

    /**
     * Retrieve a sub chunk biome map if one exists. Otherwise return null
     * @param subChunk sub chunk index
     * @return the biome map at that index if one exists
     */
    public LDBSubChunkBiomeMap getSubChunk(int subChunk) {
        this.subChunkBiomes.computeIfAbsent(subChunk, ignored -> new LDBSubChunkBiomeMap(new IntPalette()));
        return this.subChunkBiomes.get(subChunk);
    }

    /**
     * Set a sub chunk biome map at an index.
     * @param subChunk sub chunk index
     * @param biomeMap the biome map at that index
     */
    public void setSubChunk(int subChunk, LDBSubChunkBiomeMap biomeMap) {
        this.subChunkBiomes.put(subChunk, biomeMap);
    }

    public List<LDBSubChunkBiomeMap> getSubChunks() {
        return new ArrayList<>(this.subChunkBiomes.values());
    }

    public LDBSubChunkBiomeMap getTopSubChunkBiomeMap() {
        return subChunkBiomes.get(0);
    }
}
