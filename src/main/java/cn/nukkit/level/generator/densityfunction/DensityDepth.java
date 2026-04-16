package cn.nukkit.level.generator.densityfunction;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/density_function/overworld/depth.json">Source</a>
 */
public final class DensityDepth {

    private static final int FROM_Y = -64;
    private static final int TO_Y = 320;
    private static final double FROM_VALUE = 1.5;
    private static final double TO_VALUE = -1.5;

    private DensityDepth() {
    }

    public static DensityFunction overworldDepth(DensityFunction offset) {
        return DensityCommon.add(
                DensityCommon.yClampedGradient(FROM_Y, TO_Y, FROM_VALUE, TO_VALUE),
                offset
        );
    }
}
