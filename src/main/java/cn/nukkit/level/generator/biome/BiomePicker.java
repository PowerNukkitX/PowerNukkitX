package cn.nukkit.level.generator.biome;

import cn.nukkit.utils.random.NukkitRandom;

public abstract class BiomePicker {

    protected final NukkitRandom random;

    protected BiomePicker(NukkitRandom random) {
        this.random = random;
    }

    public abstract int pick(int x, int z);

}
