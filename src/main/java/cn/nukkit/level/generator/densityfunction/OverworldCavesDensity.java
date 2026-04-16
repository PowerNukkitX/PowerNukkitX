package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/tree/data/data/minecraft/worldgen/density_function/overworld/caves">Sources</a>
 */
public final class OverworldCavesDensity {

    private static final DensityFunction Y = new DensityFunction.SimpleFunction() {
        @Override
        public double compute(DensityFunction.FunctionContext context) {
            return context.blockY();
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }
    };

    private OverworldCavesDensity() {
    }

    public static DensityFunction spaghettiRoughnessFunction(
            NormalNoise spaghettiRoughness,
            NormalNoise spaghettiRoughnessModulator
    ) {
        DensityFunction roughness = DensityCommon.noise(spaghettiRoughness, 1.0, 1.0);
        DensityFunction modulator = DensityCommon.mappedNoise(spaghettiRoughnessModulator, 0.0, -0.1);
        return DensityCommon.cacheOnce(
                DensityCommon.mul(
                        modulator,
                        DensityCommon.add(roughness.abs(), DensityCommon.constant(-0.4))
                )
        );
    }

    public static DensityFunction spaghetti2dThicknessModulator(NormalNoise spaghetti2dThickness) {
        return DensityCommon.cacheOnce(
                DensityCommon.mappedNoise(spaghetti2dThickness, 2.0, 1.0, -0.6, -1.3)
        );
    }

    public static DensityFunction entrances(
            DensityFunction spaghettiRoughnessFunction,
            NormalNoise spaghetti3dRarity,
            NormalNoise spaghetti3dThickness,
            NormalNoise spaghetti3dFirst,
            NormalNoise spaghetti3dSecond,
            NormalNoise caveEntrance
    ) {
        DensityFunction rarity = DensityCommon.cacheOnce(DensityCommon.noise(spaghetti3dRarity, 2.0, 1.0));
        DensityFunction thickness = DensityCommon.mappedNoise(spaghetti3dThickness, -0.065, -0.088);
        DensityFunction first = DensityCommon.weirdScaledSampler(
                rarity,
                spaghetti3dFirst,
                DensityCommon.WeirdScaledSampler.RarityValueMapper.TYPE1
        );
        DensityFunction second = DensityCommon.weirdScaledSampler(
                rarity,
                spaghetti3dSecond,
                DensityCommon.WeirdScaledSampler.RarityValueMapper.TYPE1
        );
        DensityFunction spaghetti = DensityCommon.add(DensityCommon.max(first, second), thickness).clamp(-1.0, 1.0);
        DensityFunction entrance = DensityCommon.noise(caveEntrance, 0.75, 0.5);
        DensityFunction entranceGradient = DensityCommon.add(
                DensityCommon.add(entrance, DensityCommon.constant(0.37)),
                DensityCommon.yClampedGradient(-10, 30, 0.3, 0.0)
        );
        return DensityCommon.cacheOnce(
                DensityCommon.min(
                        entranceGradient,
                        DensityCommon.add(spaghettiRoughnessFunction, spaghetti)
                )
        );
    }

    public static DensityFunction pillars(
            NormalNoise pillar,
            NormalNoise pillarRareness,
            NormalNoise pillarThickness
    ) {
        DensityFunction pillarNoise = DensityCommon.noise(pillar, 25.0, 0.3);
        DensityFunction rarity = DensityCommon.mappedNoise(pillarRareness, 0.0, -2.0);
        DensityFunction thickness = DensityCommon.mappedNoise(pillarThickness, 0.0, 1.1);
        DensityFunction combined = DensityCommon.add(
                DensityCommon.mul(pillarNoise, DensityCommon.constant(2.0)),
                rarity
        );
        return DensityCommon.cacheOnce(DensityCommon.mul(combined, thickness.cube()));
    }

    public static DensityFunction spaghetti2d(
            DensityFunction spaghetti2dThicknessModulator,
            NormalNoise spaghetti2dModulator,
            NormalNoise spaghetti2d,
            NormalNoise spaghetti2dElevation
    ) {
        DensityFunction modulator = DensityCommon.noise(spaghetti2dModulator, 2.0, 1.0);
        DensityFunction sampled = DensityCommon.weirdScaledSampler(
                modulator,
                spaghetti2d,
                DensityCommon.WeirdScaledSampler.RarityValueMapper.TYPE2
        );
        DensityFunction elevation = DensityCommon.mappedNoise(spaghetti2dElevation, 1.0, 0.0, -8.0, 8.0);
        DensityFunction elevationGradient = DensityCommon.add(
                DensityCommon.add(elevation, DensityCommon.yClampedGradient(-64, 320, 8.0, -40.0)).abs(),
                spaghetti2dThicknessModulator
        ).cube();
        DensityFunction adjusted = DensityCommon.add(
                sampled,
                DensityCommon.mul(DensityCommon.constant(0.083), spaghetti2dThicknessModulator)
        );
        return DensityCommon.max(adjusted, elevationGradient).clamp(-1.0, 1.0);
    }

    public static DensityFunction noodle(
            NormalNoise noodle,
            NormalNoise noodleThickness,
            NormalNoise noodleRidgeA,
            NormalNoise noodleRidgeB
    ) {
        DensityFunction toggle = yLimitedInterpolatable(
                DensityCommon.noise(noodle, 1.0, 1.0),
                -60,
                320,
                -1.0
        );
        DensityFunction thickness = yLimitedInterpolatable(
                DensityCommon.mappedNoise(noodleThickness, 1.0, 1.0, -0.05, -0.1),
                -60,
                320,
                0.0
        );
        DensityFunction ridgeA = yLimitedInterpolatable(
                DensityCommon.noise(noodleRidgeA, 2.6666666666666665, 2.6666666666666665),
                -60,
                320,
                0.0
        );
        DensityFunction ridgeB = yLimitedInterpolatable(
                DensityCommon.noise(noodleRidgeB, 2.6666666666666665, 2.6666666666666665),
                -60,
                320,
                0.0
        );
        DensityFunction ridges = DensityCommon.mul(
                DensityCommon.constant(1.5),
                DensityCommon.max(ridgeA.abs(), ridgeB.abs())
        );
        return DensityCommon.rangeChoice(
                toggle,
                -1000000.0,
                0.0,
                DensityCommon.constant(64.0),
                DensityCommon.add(thickness, ridges)
        );
    }

    public static DensityFunction finalDensity(
            DensityFunction slopedCheese,
            NormalNoise spaghettiRoughness,
            NormalNoise spaghettiRoughnessModulator,
            NormalNoise spaghetti2dThickness,
            NormalNoise spaghetti2dModulator,
            NormalNoise spaghetti2d,
            NormalNoise spaghetti2dElevation,
            NormalNoise spaghetti3dRarity,
            NormalNoise spaghetti3dThickness,
            NormalNoise spaghetti3dFirst,
            NormalNoise spaghetti3dSecond,
            NormalNoise caveEntrance,
            NormalNoise caveLayer,
            NormalNoise caveCheese,
            NormalNoise pillar,
            NormalNoise pillarRareness,
            NormalNoise pillarThickness,
            NormalNoise noodle,
            NormalNoise noodleThickness,
            NormalNoise noodleRidgeA,
            NormalNoise noodleRidgeB
    ) {
        DensityFunction spaghettiRoughnessFunction = spaghettiRoughnessFunction(spaghettiRoughness, spaghettiRoughnessModulator);
        DensityFunction spaghetti2dThicknessModulator = spaghetti2dThicknessModulator(spaghetti2dThickness);
        DensityFunction spaghetti2dFunction = spaghetti2d(
                spaghetti2dThicknessModulator,
                spaghetti2dModulator,
                spaghetti2d,
                spaghetti2dElevation
        );
        DensityFunction entrances = entrances(
                spaghettiRoughnessFunction,
                spaghetti3dRarity,
                spaghetti3dThickness,
                spaghetti3dFirst,
                spaghetti3dSecond,
                caveEntrance
        );
        DensityFunction pillars = pillars(pillar, pillarRareness, pillarThickness);
        DensityFunction underground = underground(
                slopedCheese,
                spaghetti2dFunction,
                spaghettiRoughnessFunction,
                entrances,
                pillars,
                caveLayer,
                caveCheese
        );
        DensityFunction caves = DensityCommon.rangeChoice(
                slopedCheese,
                -1000000.0,
                1.5625,
                DensityCommon.min(
                        slopedCheese,
                        DensityCommon.mul(DensityCommon.constant(5.0), entrances)
                ),
                underground
        );
        DensityFunction postProcessed = DensityCommon.mul(
                DensityCommon.constant(0.64),
                DensityCommon.interpolated(
                        DensityCommon.blendDensity(
                                DensityCommon.add(
                                        DensityCommon.constant(0.1171875),
                                        DensityCommon.mul(
                                                DensityCommon.yClampedGradient(-64, -40, 0.0, 1.0),
                                                DensityCommon.add(
                                                        DensityCommon.constant(-0.1171875),
                                                        DensityCommon.add(
                                                                DensityCommon.constant(-0.078125),
                                                                DensityCommon.mul(
                                                                        DensityCommon.yClampedGradient(240, 256, 1.0, 0.0),
                                                                        DensityCommon.add(
                                                                                DensityCommon.constant(0.078125),
                                                                                caves
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        ).squeeze();
        return DensityCommon.min(postProcessed, noodle(noodle, noodleThickness, noodleRidgeA, noodleRidgeB));
    }

    private static DensityFunction underground(
            DensityFunction slopedCheese,
            DensityFunction spaghetti2d,
            DensityFunction spaghettiRoughnessFunction,
            DensityFunction entrances,
            DensityFunction pillars,
            NormalNoise caveLayer,
            NormalNoise caveCheese
    ) {
        DensityFunction layerizedCaverns = DensityCommon.mul(
                DensityCommon.constant(4.0),
                DensityCommon.noise(caveLayer, 1.0, 8.0).square()
        );
        DensityFunction caveCheeseFunction = DensityCommon.add(
                DensityCommon.add(
                        DensityCommon.constant(0.27),
                        DensityCommon.noise(caveCheese, 1.0, 0.6666666666666666)
                ).clamp(-1.0, 1.0),
                DensityCommon.add(
                        DensityCommon.constant(1.5),
                        DensityCommon.mul(DensityCommon.constant(-0.64), slopedCheese)
                ).clamp(0.0, 0.5)
        );
        DensityFunction caveDensity = DensityCommon.add(layerizedCaverns, caveCheeseFunction);
        DensityFunction passages = DensityCommon.min(
                DensityCommon.min(caveDensity, entrances),
                DensityCommon.add(spaghetti2d, spaghettiRoughnessFunction)
        );
        DensityFunction pillarFilter = DensityCommon.rangeChoice(
                pillars,
                -1000000.0,
                0.03,
                DensityCommon.constant(-1000000.0),
                pillars
        );
        return DensityCommon.max(passages, pillarFilter);
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction density, int minY, int maxY, double whenOutOfRange) {
        return DensityCommon.interpolated(
                DensityCommon.rangeChoice(
                        Y,
                        minY,
                        maxY + 1.0,
                        density,
                        DensityCommon.constant(whenOutOfRange)
                )
        );
    }

    public static DensityFunction createCaveDetector(NormalObjectHolder.TerrainHolder terrainHolder) {
        DensityFunction spaghettiRoughness = OverworldCavesDensity.spaghettiRoughnessFunction(
                terrainHolder.getSpaghettiRoughness(),
                terrainHolder.getSpaghettiRoughnessModulator()
        );
        DensityFunction spaghetti2dThickness = OverworldCavesDensity.spaghetti2dThicknessModulator(
                terrainHolder.getSpaghetti2dThickness()
        );
        DensityFunction spaghetti2d = OverworldCavesDensity.spaghetti2d(
                spaghetti2dThickness,
                terrainHolder.getSpaghetti2dModulator(),
                terrainHolder.getSpaghetti2d(),
                terrainHolder.getSpaghetti2dElevation()
        );
        DensityFunction entrances = OverworldCavesDensity.entrances(
                spaghettiRoughness,
                terrainHolder.getSpaghetti3dRarity(),
                terrainHolder.getSpaghetti3dThickness(),
                terrainHolder.getSpaghetti3dFirst(),
                terrainHolder.getSpaghetti3dSecond(),
                terrainHolder.getCaveEntrance()
        );
        DensityFunction caveLayer = DensityCommon.mul(
                DensityCommon.constant(4.0),
                DensityCommon.noise(terrainHolder.getCaveLayer(), 1.0, 8.0).square()
        );
        DensityFunction caveCheese = DensityCommon.add(
                DensityCommon.constant(0.27),
                DensityCommon.noise(terrainHolder.getCaveCheese(), 1.0, 0.6666666666666666)
        ).clamp(-1.0, 1.0);
        return DensityCommon.min(
                DensityCommon.min(
                        DensityCommon.add(caveLayer, caveCheese),
                        entrances
                ),
                DensityCommon.add(spaghetti2d, spaghettiRoughness)
        );
    }
}
