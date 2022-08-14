package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.dfsek.terra.api.world.biome.Biome;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PNXBiomeProviderDelegate implements BiomeProvider {

    private final BiomeProvider delegate;

    public PNXBiomeProviderDelegate(BiomeProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public Biome getBiome(int x,int y, int z, long seed) {
        return delegate.getBiome(x, y, z, seed);
    }

    @Override
    public Iterable<Biome> getBiomes() {
        return delegate.getBiomes();
    }
}
