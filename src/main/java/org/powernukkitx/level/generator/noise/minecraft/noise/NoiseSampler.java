package org.powernukkitx.level.generator.noise.minecraft.noise;

import org.powernukkitx.level.generator.noise.minecraft.utils.LCG;

public interface NoiseSampler {
    LCG SKIP_262 = LCG.JAVA.combine(262);

    double sample(double x, double y, double d, double e);
}