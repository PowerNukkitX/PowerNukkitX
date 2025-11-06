package cn.nukkit.level.generator.noise.minecraft.noise;

import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.utils.random.NukkitRandom;

public class NormalNoise {

    private final float valueFactor;
    private final SimplexNoise first;
    private final SimplexNoise second;
    private final double maxValue;

    private final int firstOctave;
    private final float[] amplitudes;

    public NormalNoise(NukkitRandom random, int firstOctave, float[] amplitudes) {
        this.firstOctave = firstOctave;
        this.amplitudes = amplitudes;
        this.first = new SimplexNoise(random, this.firstOctave, this.amplitudes);
        this.second = new SimplexNoise(random, this.firstOctave, this.amplitudes);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for(int i = 0; i < this.amplitudes.length; i++) {
            double amplitude = this.amplitudes[i];
            if(amplitude != 0) {
                min = Math.min(min, i);
                max = Math.max(max, i);
            }
        }

        this.valueFactor = (float) ((1/6f) / (0.1 * (1.0 + 1.0 / ((max-min) + 1))));
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
