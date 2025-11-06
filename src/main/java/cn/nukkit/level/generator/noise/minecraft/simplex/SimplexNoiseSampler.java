package cn.nukkit.level.generator.noise.minecraft.simplex;

import cn.nukkit.level.generator.noise.minecraft.noise.Noise;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.noise.minecraft.utils.MathHelper.floor;
import static cn.nukkit.level.generator.noise.minecraft.utils.MathHelper.grad;

public class SimplexNoiseSampler extends Noise {

    private static final double SQRT_3 = Math.sqrt(3.0D);
    private static final double SKEW_FACTOR_2D = 0.5D * (SQRT_3 - 1.0D); // also known as F2 // 0.3660254037844386D
    private static final double UNSKEW_FACTOR_2D = (3.0D - SQRT_3) / 6.0D; // also known as G2 // 0.21132486540518713D
    private static final double F3 = 0.3333333333333333D;
    private static final double G3 = 0.16666666666666666D;

    public SimplexNoiseSampler(RandomSourceProvider rand) {
        super(rand);
    }

    public double sample2D(double x, double y) {
        double hairyFactor = (x + y) * SKEW_FACTOR_2D;
        // in minecraft those are the temperatures
        int hairyX = floor(x + hairyFactor);
        int hairyZ = floor(y + hairyFactor);
        double mixedHairyXz = (double)(hairyX + hairyZ) * UNSKEW_FACTOR_2D;
        double diffXToXz = (double)hairyX - mixedHairyXz;
        double diffZToXz = (double)hairyZ - mixedHairyXz;
        double x0 = x - diffXToXz;
        double y0 = y - diffZToXz;
        byte offsetSecondCornerX;
        byte offsetSecondCornerZ;
        if(x0 > y0) { // lower triangle, XY order: (0,0)->(1,0)->(1,1)
            offsetSecondCornerX = 1;
            offsetSecondCornerZ = 0;
        } else { // upper triangle, YX order: (0,0)->(0,1)->(1,1)
            offsetSecondCornerX = 0;
            offsetSecondCornerZ = 1;
        }

        double x1 = x0 - (double)offsetSecondCornerX + UNSKEW_FACTOR_2D;
        double y1 = y0 - (double)offsetSecondCornerZ + UNSKEW_FACTOR_2D;
        double x3 = x0 - 1.0D + 2.0D * UNSKEW_FACTOR_2D;
        double y3 = y0 - 1.0D + 2.0D * UNSKEW_FACTOR_2D;
        int ii = hairyX & 255;
        int jj = hairyZ & 255;
        int gi0 = this.lookup(ii + this.lookup(jj)) % 12;
        int gi1 = this.lookup(ii + offsetSecondCornerX + this.lookup(jj + offsetSecondCornerZ)) % 12;
        int gi2 = this.lookup(ii + 1 + this.lookup(jj + 1)) % 12;
        double t0 = this.cornerNoise3d(gi0, x0, y0, 0.0D, 0.5D);
        double t1 = this.cornerNoise3d(gi1, x1, y1, 0.0D, 0.5D);
        double t2 = this.cornerNoise3d(gi2, x3, y3, 0.0D, 0.5D);
        return 70.0D * (t0 + t1 + t2);
    }

    public double sample3D(double x, double y, double z) {
        double skewFactor = (x + y + z) * F3; // F3 is 1/3
        // Skew the input space to determine which simplex cell we're in
        int i = floor(x + skewFactor);
        int j = floor(y + skewFactor);
        int k = floor(z + skewFactor);
        double unskewFactor = (double)(i + j + k) * G3; // G3 is 1/6
        double x0 = (double)i - unskewFactor;
        double y0 = (double)j - unskewFactor;
        double z0 = (double)k - unskewFactor;
        x0 = x - x0;
        y0 = y - y0;
        z0 = z - z0;
        byte i1, j1, k1;
        byte i2, j2, k2;
        if(x0 >= y0) {
            if(y0 >= z0) { // X Y Z order
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if(x0 >= z0) { // X Z Y order
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else { // Z X Y order
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else if(y0 < z0) { // Z Y X order
            i1 = 0;
            j1 = 0;
            k1 = 1;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else if(x0 < z0) { // Y Z X order
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else { // Y X Z order
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 1;
            j2 = 1;
            k2 = 0;
        }

        double x1 = x0 - (double)i1 + G3;
        double y1 = y0 - (double)j1 + G3;
        double z1 = z0 - (double)k1 + G3;
        double x2 = x0 - (double)i2 + F3;
        double y2 = y0 - (double)j2 + F3;
        double z2 = z0 - (double)k2 + F3;
        double x3 = x0 - 1.0D + 0.5D;
        double y3 = y0 - 1.0D + 0.5D;
        double z3 = z0 - 1.0D + 0.5D;
        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;
        int gi0 = this.lookup(ii + this.lookup(jj + this.lookup(kk))) % 12;
        int gi1 = this.lookup(ii + i1 + this.lookup(jj + j1 + this.lookup(kk + k1))) % 12;
        int gi2 = this.lookup(ii + i2 + this.lookup(jj + j2 + this.lookup(kk + k2))) % 12;
        int gi3 = this.lookup(ii + 1 + this.lookup(jj + 1 + this.lookup(kk + 1))) % 12;
        double t0 = this.cornerNoise3d(gi0, x0, y0, z0, 0.6D);
        double t1 = this.cornerNoise3d(gi1, x1, y1, z1, 0.6D);
        double t2 = this.cornerNoise3d(gi2, x2, y2, z2, 0.6D);
        double t3 = this.cornerNoise3d(gi3, x3, y3, z3, 0.6D);
        return 32.0D * (t0 + t1 + t2 + t3);
    }

    private double cornerNoise3d(int hash, double x, double y, double z, double max) {
        double contribution = max - x * x - y * y - z * z;
        double result;
        if(contribution < 0.0D) {
            result = 0.0D;
        } else {
            contribution *= contribution;
            result = contribution * contribution * grad(hash, x, y, z);
        }

        return result;
    }

}