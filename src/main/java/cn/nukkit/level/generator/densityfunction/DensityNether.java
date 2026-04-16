package cn.nukkit.level.generator.densityfunction;

/**
 * @author Buddelbubi
 * @since 2026/04/15
 * @implNote <a href="https://github.com/misode/mcmeta/tree/data/data/minecraft/worldgen">Sources</a>
 */
public final class DensityNether {


    private DensityNether() {
    }

    public static DensityFunction finalDensity(DensityFunction base3dNoise) {
        DensityFunction density = DensityCommon.add(DensityCommon.constant(-0.9375), base3dNoise);
        density = DensityCommon.mul(DensityCommon.yClampedGradient(104, 128, 1.0, 0.0), density);
        density = DensityCommon.add(DensityCommon.constant(0.9375), density);
        density = DensityCommon.add(DensityCommon.constant(-2.5), density);
        density = DensityCommon.mul(DensityCommon.yClampedGradient(-8, 24, 0.0, 1.0), density);
        density = DensityCommon.add(DensityCommon.constant(2.5), density);
        density = DensityCommon.blendDensity(density);
        density = DensityCommon.interpolated(density);
        density = DensityCommon.mul(DensityCommon.constant(0.64), density);
        return density.squeeze();
    }
}
