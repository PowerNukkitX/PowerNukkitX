package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.Biome;
import com.dfsek.terra.api.world.biome.PlatformBiome;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public record PNXBiomeDelegate(Biome innerBiome) implements PlatformBiome {
    @Override
    public Biome getHandle() {
        return innerBiome;
    }
}
