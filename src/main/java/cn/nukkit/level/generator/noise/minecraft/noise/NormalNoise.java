package cn.nukkit.level.generator.noise.minecraft.noise;

import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.utils.random.NukkitRandom;

public class NormalNoise {

    private final SimplexNoise first;
    private final SimplexNoise second;
    private final double maxValue;

    public NormalNoise(NukkitRandom random, int firstOctave, float[] amplitudes) {
        this.first = new SimplexNoise(random, firstOctave, amplitudes);
        this.second = new SimplexNoise(random, firstOctave, amplitudes);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for(int i = 0; i < amplitudes.length; i++) {
            double amplitude = amplitudes[i];
            if(amplitude != 0) {
                min = Math.min(min, i);
                max = Math.max(max, i);
            }
        }

        this.maxValue = (this.first.getMax() + this.second.getMax()) / 2;
    }

    public float getValue(double x, double y, double z) {
        double d0 = x * 1.0181268882175227;
        double d1 = y * 1.0181268882175227;
        double d2 = z * 1.0181268882175227;
        return (this.first.getValue(x, y, z) + this.second.getValue(d0, d1, d2)) / 2;
    }

    public double getMax() {
        return maxValue;
    }

}
