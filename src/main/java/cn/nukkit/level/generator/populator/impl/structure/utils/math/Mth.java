package cn.nukkit.level.generator.populator.impl.structure.utils.math;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.NukkitRandom;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public final class Mth {

    public static int nextInt(NukkitRandom random, int origin, int bound) {
        return origin >= bound ? origin : random.nextBoundedInt(bound - origin + 1) + origin;
    }

    private Mth() {

    }
}
