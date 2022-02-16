package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.dfsek.terra.api.util.MathUtil;
import com.dfsek.terra.api.world.biome.Biome;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.lang.ref.WeakReference;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class PNXBiomeProviderDelegate implements BiomeProvider {
    private final BiomeProvider delegate;
    private final Long2ObjectOpenHashMap<WeakReference<Biome>> cacheMap;

    public PNXBiomeProviderDelegate(BiomeProvider delegate) {
        this.delegate = delegate;
        cacheMap = new Long2ObjectOpenHashMap<>();
    }

    @Override
    public Biome getBiome(int x, int z, long seed) {
        final var hash = MathUtil.squash(x, z);
        if (cacheMap.containsKey(hash)) {
            final var ref = cacheMap.get(hash);
            final var obj = ref.get();
            if (obj != null) {
                return obj;
            }
        }
        final var tmp = delegate.getBiome(x, z, seed);
        cacheMap.put(hash, new WeakReference<>(tmp));
        return tmp;
    }

    @Override
    public Iterable<Biome> getBiomes() {
        return null;
    }

    public void optimize() {
        final var it = cacheMap.long2ObjectEntrySet().fastIterator();
        Long2ObjectMap.Entry<WeakReference<Biome>> obj;
        while (it.hasNext()) {
            obj = it.next();
            if (obj.getValue().get() == null) {
                it.remove();
            }
        }
        cacheMap.trim();
    }
}
