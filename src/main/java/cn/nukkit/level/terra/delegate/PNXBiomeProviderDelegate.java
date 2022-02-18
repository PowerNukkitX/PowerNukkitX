package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import com.dfsek.terra.api.util.MathUtil;
import com.dfsek.terra.api.world.biome.Biome;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;

import java.util.WeakHashMap;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class PNXBiomeProviderDelegate implements BiomeProvider {
    private final BiomeProvider delegate;
    private final WeakHashMap<Long, Biome> cacheMap;
    private final ChunkManager chunkManager;

    public PNXBiomeProviderDelegate(BiomeProvider delegate,ChunkManager chunkManager) {
        this.delegate = delegate;
        cacheMap = new WeakHashMap<>();
        this.chunkManager = chunkManager;
    }

    @Override
    public Biome getBiome(int x, int z, long seed) {
        final var hash = MathUtil.squash(x, z);
        final var obj = cacheMap.get(hash);
        if (obj != null) {
            return obj;
        }
        final var tmp = delegate.getBiome(x, z, seed);
        var chunk = chunkManager.getChunk(x >> 4,z >> 4);
        if (chunk != null)
            chunk.setBiome(x & 0xF,z & 0xF, (cn.nukkit.level.biome.Biome) tmp.getPlatformBiome().getHandle());
        cacheMap.put(hash, tmp);
        return tmp;
    }

    @Override
    public Iterable<Biome> getBiomes() {
        return null;
    }
}
