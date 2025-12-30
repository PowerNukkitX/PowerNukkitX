package cn.nukkit.level.generator.noise.minecraft.perlin;


import cn.nukkit.level.generator.noise.minecraft.noise.Noise;
import cn.nukkit.level.generator.noise.minecraft.utils.Triplet;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.noise.minecraft.utils.MathHelper.floor;
import static cn.nukkit.level.generator.noise.minecraft.utils.MathHelper.grad;
import static cn.nukkit.level.generator.noise.minecraft.utils.MathHelper.lerp3;
import static cn.nukkit.level.generator.noise.minecraft.utils.MathHelper.smoothStep;

public class PerlinNoiseSampler extends Noise {

    public PerlinNoiseSampler(RandomSourceProvider rand) {
        super(rand);
    }

    public double sample(double x, double y, double z, double yAmplification, double minY) {
        Triplet<int[], double[], double[]> args = getArgs(x, y, z, yAmplification, minY);
        int[] section = args.getFirst();
        double[] local = args.getSecond();
        double[] fading = args.getThird();
        double[] perms = samplePermutations(section, local);
        return lerp3(fading[0], fading[1], fading[2], perms[0], perms[1], perms[2], perms[3], perms[4], perms[5], perms[6], perms[7]);
    }

    public Triplet<int[], double[], double[]> getArgs(double x, double y, double z, double yAmplification, double minY) {
        double offsetX = x + this.originX;
        double offsetY = y + this.originY;
        double offsetZ = z + this.originZ;
        // this could be done with modf
        int sectionX = floor(offsetX);
        int sectionY = floor(offsetY);
        int sectionZ = floor(offsetZ);
        double localX = offsetX - (double)sectionX;
        double localY = offsetY - (double)sectionY;
        double localZ = offsetZ - (double)sectionZ;

        double fadeLocalX = smoothStep(localX);
        double fadeLocalY = smoothStep(localY);
        double fadeLocalZ = smoothStep(localZ);

        // this is useful for 1.16+
        if(yAmplification != 0.0D) {
            double yFloor = Math.min(minY, localY);
            localY -= (double)floor(yFloor / yAmplification) * yAmplification;
        }
        return new Triplet<>(new int[] {sectionX, sectionY, sectionZ}, new double[] {localX, localY, localZ}, new double[] {fadeLocalX, fadeLocalY, fadeLocalZ});
    }


    public double[] samplePermutations(int[] section, double[] local) {
        int pXY = this.lookup(section[0]) + section[1];
        int pX1Y = this.lookup(section[0] + 1) + section[1];

        int ppXYZ = this.lookup(pXY) + section[2];
        int ppX1YZ = this.lookup(pX1Y) + section[2];

        int ppXY1Z = this.lookup(pXY + 1) + section[2];
        int ppX1Y1Z = this.lookup(pX1Y + 1) + section[2];

        double x1 = grad(this.lookup(ppXYZ), local[0], local[1], local[2]);
        double x2 = grad(this.lookup(ppX1YZ), local[0] - 1.0D, local[1], local[2]);
        double x3 = grad(this.lookup(ppXY1Z), local[0], local[1] - 1.0D, local[2]);
        double x4 = grad(this.lookup(ppX1Y1Z), local[0] - 1.0D, local[1] - 1.0D, local[2]);
        double x5 = grad(this.lookup(ppXYZ + 1), local[0], local[1], local[2] - 1.0D);
        double x6 = grad(this.lookup(ppX1YZ + 1), local[0] - 1.0D, local[1], local[2] - 1.0D);
        double x7 = grad(this.lookup(ppXY1Z + 1), local[0], local[1] - 1.0D, local[2] - 1.0D);
        double x8 = grad(this.lookup(ppX1Y1Z + 1), local[0] - 1.0D, local[1] - 1.0D, local[2] - 1.0D);

        return new double[] {x1, x2, x3, x4, x5, x6, x7, x8};
    }

}