package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.biome.result.BiomeResult;
import cn.nukkit.utils.random.NukkitRandom;

public abstract class BiomePicker<E extends BiomeResult> {

    protected final NukkitRandom random;

    protected BiomePicker(NukkitRandom random) {
        this.random = random;
    }

    public abstract E pick(int x, int y, int z);

}
