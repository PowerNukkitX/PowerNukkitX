package cn.nukkit.level.generator.densityfunction;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/density_function/overworld/ridges_folded.json">Source</a>
 */
public final class DensityRidgesFolded {

    private DensityRidgesFolded() {
    }

    public static DensityFunction overworldRidgesFolded(DensityFunction ridges) {
        return DensityCommon.mul(
                DensityCommon.constant(-3.0),
                DensityCommon.add(
                        DensityCommon.constant(-0.3333333333333333),
                        DensityCommon.add(
                                DensityCommon.constant(-0.6666666666666666),
                                ridges.abs()
                        ).abs()
                )
        );
    }
}
