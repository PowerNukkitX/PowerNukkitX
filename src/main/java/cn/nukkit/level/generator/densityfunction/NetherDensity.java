package cn.nukkit.level.generator.densityfunction;

/**
 * @author Buddelbubi
 * @since 2026/04/15
 * @implNote <a href="https://github.com/misode/mcmeta/tree/data/data/minecraft/worldgen">Sources</a>
 */
public final class NetherDensity {

    private static final double BASE_3D_AMPLITUDE = 0.82;

    private NetherDensity() {
    }

    public static DensityFunction finalDensity(DensityFunction base3dNoise) {
        DensityFunction base3d = DensityCommon.mul(base3dNoise, DensityCommon.constant(BASE_3D_AMPLITUDE));
        DensityFunction density = DensityCommon.add(base3d, DensityCommon.constant(-0.9375));
        density = DensityCommon.mul(density, DensityCommon.yClampedGradient(104, 128, 1.0, 0.0));
        density = DensityCommon.add(density, DensityCommon.constant(0.9375));
        density = DensityCommon.add(density, DensityCommon.constant(-2.5));
        density = DensityCommon.mul(density, DensityCommon.yClampedGradient(-8, 24, 0.0, 1.0));
        density = DensityCommon.add(density, DensityCommon.constant(2.5));
        density = DensityCommon.blendDensity(density);
        density = DensityCommon.mul(density, DensityCommon.constant(0.64));
        return density.squeeze();
    }
}
