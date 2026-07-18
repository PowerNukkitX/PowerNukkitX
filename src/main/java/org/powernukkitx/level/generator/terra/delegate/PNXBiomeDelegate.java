package org.powernukkitx.level.generator.terra.delegate;

import com.dfsek.terra.api.world.biome.PlatformBiome;

public record PNXBiomeDelegate(int innerBiome) implements PlatformBiome {
    @Override
    public Integer getHandle() {
        return innerBiome;
    }
}
