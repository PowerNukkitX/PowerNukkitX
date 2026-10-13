package org.powernukkitx.level.generator.noise.minecraft.perlin;


import org.powernukkitx.level.generator.noise.minecraft.noise.Noise;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class PerlinNoiseSampler extends Noise {
    private static final double[] GRAD_X = new double[] {
            1, -1, 1, -1,
            1, -1, 1, -1,
            0, 0, 0, 0,
            1, 0, -1, 0
    };

    private static final double[] GRAD_Y = new double[] {
            1, 1, -1, -1,
            0, 0, 0, 0,
            1, -1, 1, -1,
            1, -1, 1, -1
    };

    private static final double[] GRAD_Z = new double[] {
            0, 0, 0, 0,
            1, 1, -1, -1,
            1, 1, -1, -1,
            0, 1, 0, -1
    };

    public PerlinNoiseSampler(RandomSourceProvider rand) {
        super(rand);
    }

    public double sample(double x, double y, double z, double yAmplification, double minY) {
        double offsetX = x + originX;
        double offsetY = y + originY;
        double offsetZ = z + originZ;
        double floorX = Math.floor(offsetX);
        double floorY = Math.floor(offsetY);
        double floorZ = Math.floor(offsetZ);
        double localX = offsetX - floorX;
        double localY = offsetY - floorY;
        double localZ = offsetZ - floorZ;
        double yOffset = 0.0D;
        if (yAmplification != 0.0D) {
            double yClamp = minY >= 0.0D && minY < localY ? minY : localY;
            yOffset = Math.floor(yClamp / yAmplification + 1.0E-7D) * yAmplification;
        }

        return sample((int) floorX, (int) floorY, (int) floorZ, localX, localY - yOffset, localZ, localY);
    }

    private double sample(int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ, double fadeLocalY) {
        byte[] permutation = this.permutations;
        int var0 = sectionX & 0xFF;
        int var1 = (sectionX + 1) & 0xFF;
        int var2 = permutation[var0] & 0xFF;
        int var3 = permutation[var1] & 0xFF;
        int var4 = (var2 + sectionY) & 0xFF;
        int var5 = (var3 + sectionY) & 0xFF;
        int var6 = (var2 + sectionY + 1) & 0xFF;
        int var7 = (var3 + sectionY + 1) & 0xFF;
        int var8 = permutation[var4] & 0xFF;
        int var9 = permutation[var5] & 0xFF;
        int var10 = permutation[var6] & 0xFF;
        int var11 = permutation[var7] & 0xFF;

        int var12 = (var8 + sectionZ) & 0xFF;
        int var13 = (var9 + sectionZ) & 0xFF;
        int var14 = (var10 + sectionZ) & 0xFF;
        int var15 = (var11 + sectionZ) & 0xFF;
        int var16 = (var8 + sectionZ + 1) & 0xFF;
        int var17 = (var9 + sectionZ + 1) & 0xFF;
        int var18 = (var10 + sectionZ + 1) & 0xFF;
        int var19 = (var11 + sectionZ + 1) & 0xFF;
        int var20 = permutation[var12] & 15;
        int var21 = permutation[var13] & 15;
        int var22 = permutation[var14] & 15;
        int var23 = permutation[var15] & 15;
        int var24 = permutation[var16] & 15;
        int var25 = permutation[var17] & 15;
        int var26 = permutation[var18] & 15;
        int var27 = permutation[var19] & 15;
        double xMinusOne = localX - 1.0D;
        double yMinusOne = localY - 1.0D;
        double zMinusOne = localZ - 1.0D;
        double grad000 = GRAD_X[var20] * localX + GRAD_Y[var20] * localY + GRAD_Z[var20] * localZ;
        double grad100 = GRAD_X[var21] * xMinusOne + GRAD_Y[var21] * localY + GRAD_Z[var21] * localZ;
        double grad010 = GRAD_X[var22] * localX + GRAD_Y[var22] * yMinusOne + GRAD_Z[var22] * localZ;
        double grad110 = GRAD_X[var23] * xMinusOne + GRAD_Y[var23] * yMinusOne + GRAD_Z[var23] * localZ;
        double grad001 = GRAD_X[var24] * localX + GRAD_Y[var24] * localY + GRAD_Z[var24] * zMinusOne;
        double grad101 = GRAD_X[var25] * xMinusOne + GRAD_Y[var25] * localY + GRAD_Z[var25] * zMinusOne;
        double grad011 = GRAD_X[var26] * localX + GRAD_Y[var26] * yMinusOne + GRAD_Z[var26] * zMinusOne;
        double grad111 = GRAD_X[var27] * xMinusOne + GRAD_Y[var27] * yMinusOne + GRAD_Z[var27] * zMinusOne;

        double fadeX = localX * localX * localX * (localX * (localX * 6.0D - 15.0D) + 10.0D);
        double fadeY = fadeLocalY * fadeLocalY * fadeLocalY * (fadeLocalY * (fadeLocalY * 6.0D - 15.0D) + 10.0D);
        double fadeZ = localZ * localZ * localZ * (localZ * (localZ * 6.0D - 15.0D) + 10.0D);

        double x00 = grad000 + fadeX * (grad100 - grad000);
        double x10 = grad010 + fadeX * (grad110 - grad010);
        double x01 = grad001 + fadeX * (grad101 - grad001);
        double x11 = grad011 + fadeX * (grad111 - grad011);
        double y0 = x00 + fadeY * (x10 - x00);
        double y1 = x01 + fadeY * (x11 - x01);
        return y0 + fadeZ * (y1 - y0);
    }

}
