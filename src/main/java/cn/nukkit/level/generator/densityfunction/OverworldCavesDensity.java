package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;

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
        DensityFunction roughness = DensityFunctions.noise(spaghettiRoughness, 1.0, 1.0);
        DensityFunction modulator = DensityFunctions.mappedNoise(spaghettiRoughnessModulator, 0.0, -0.1);
        return DensityFunctions.cacheOnce(
                DensityFunctions.mul(
                        modulator,
                        DensityFunctions.add(roughness.abs(), DensityFunctions.constant(-0.4))
                )
        );
    }

    public static DensityFunction spaghetti2dThicknessModulator(NormalNoise spaghetti2dThickness) {
        return DensityFunctions.cacheOnce(
                DensityFunctions.mappedNoise(spaghetti2dThickness, 2.0, 1.0, -0.6, -1.3)
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
        DensityFunction rarity = DensityFunctions.cacheOnce(DensityFunctions.noise(spaghetti3dRarity, 2.0, 1.0));
        DensityFunction thickness = DensityFunctions.mappedNoise(spaghetti3dThickness, -0.065, -0.088);
        DensityFunction first = DensityFunctions.weirdScaledSampler(
                rarity,
                spaghetti3dFirst,
                DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1
        );
        DensityFunction second = DensityFunctions.weirdScaledSampler(
                rarity,
                spaghetti3dSecond,
                DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1
        );
        DensityFunction spaghetti = DensityFunctions.add(DensityFunctions.max(first, second), thickness).clamp(-1.0, 1.0);
        DensityFunction entrance = DensityFunctions.noise(caveEntrance, 0.75, 0.5);
        DensityFunction entranceGradient = DensityFunctions.add(
                DensityFunctions.add(entrance, DensityFunctions.constant(0.37)),
                DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0)
        );
        return DensityFunctions.cacheOnce(
                DensityFunctions.min(
                        entranceGradient,
                        DensityFunctions.add(spaghettiRoughnessFunction, spaghetti)
                )
        );
    }

    public static DensityFunction pillars(
            NormalNoise pillar,
            NormalNoise pillarRareness,
            NormalNoise pillarThickness
    ) {
        DensityFunction pillarNoise = DensityFunctions.noise(pillar, 25.0, 0.3);
        DensityFunction rarity = DensityFunctions.mappedNoise(pillarRareness, 0.0, -2.0);
        DensityFunction thickness = DensityFunctions.mappedNoise(pillarThickness, 0.0, 1.1);
        DensityFunction combined = DensityFunctions.add(
                DensityFunctions.mul(pillarNoise, DensityFunctions.constant(2.0)),
                rarity
        );
        return DensityFunctions.cacheOnce(DensityFunctions.mul(combined, thickness.cube()));
    }

    public static DensityFunction spaghetti2d(
            DensityFunction spaghetti2dThicknessModulator,
            NormalNoise spaghetti2dModulator,
            NormalNoise spaghetti2d,
            NormalNoise spaghetti2dElevation
    ) {
        DensityFunction modulator = DensityFunctions.noise(spaghetti2dModulator, 2.0, 1.0);
        DensityFunction sampled = DensityFunctions.weirdScaledSampler(
                modulator,
                spaghetti2d,
                DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2
        );
        DensityFunction elevation = DensityFunctions.mappedNoise(spaghetti2dElevation, 1.0, 0.0, -8.0, 8.0);
        DensityFunction elevationGradient = DensityFunctions.add(
                DensityFunctions.add(elevation, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs(),
                spaghetti2dThicknessModulator
        ).cube();
        DensityFunction adjusted = DensityFunctions.add(
                sampled,
                DensityFunctions.mul(DensityFunctions.constant(0.083), spaghetti2dThicknessModulator)
        );
        return DensityFunctions.max(adjusted, elevationGradient).clamp(-1.0, 1.0);
    }

    public static DensityFunction noodle(
            NormalNoise noodle,
            NormalNoise noodleThickness,
            NormalNoise noodleRidgeA,
            NormalNoise noodleRidgeB
    ) {
        DensityFunction toggle = yLimitedInterpolatable(
                DensityFunctions.noise(noodle, 1.0, 1.0),
                -60,
                320,
                -1.0
        );
        DensityFunction thickness = yLimitedInterpolatable(
                DensityFunctions.mappedNoise(noodleThickness, 1.0, 1.0, -0.05, -0.1),
                -60,
                320,
                0.0
        );
        DensityFunction ridgeA = yLimitedInterpolatable(
                DensityFunctions.noise(noodleRidgeA, 2.6666666666666665, 2.6666666666666665),
                -60,
                320,
                0.0
        );
        DensityFunction ridgeB = yLimitedInterpolatable(
                DensityFunctions.noise(noodleRidgeB, 2.6666666666666665, 2.6666666666666665),
                -60,
                320,
                0.0
        );
        DensityFunction ridges = DensityFunctions.mul(
                DensityFunctions.constant(1.5),
                DensityFunctions.max(ridgeA.abs(), ridgeB.abs())
        );
        return DensityFunctions.rangeChoice(
                toggle,
                -1000000.0,
                0.0,
                DensityFunctions.constant(64.0),
                DensityFunctions.add(thickness, ridges)
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
        DensityFunction caves = DensityFunctions.rangeChoice(
                slopedCheese,
                -1000000.0,
                1.5625,
                DensityFunctions.min(
                        slopedCheese,
                        DensityFunctions.mul(DensityFunctions.constant(5.0), entrances)
                ),
                underground
        );
        DensityFunction postProcessed = DensityFunctions.mul(
                DensityFunctions.constant(0.64),
                DensityFunctions.interpolated(
                        DensityFunctions.blendDensity(
                                DensityFunctions.add(
                                        DensityFunctions.constant(0.1171875),
                                        DensityFunctions.mul(
                                                DensityFunctions.yClampedGradient(-64, -40, 0.0, 1.0),
                                                DensityFunctions.add(
                                                        DensityFunctions.constant(-0.1171875),
                                                        DensityFunctions.add(
                                                                DensityFunctions.constant(-0.078125),
                                                                DensityFunctions.mul(
                                                                        DensityFunctions.yClampedGradient(240, 256, 1.0, 0.0),
                                                                        DensityFunctions.add(
                                                                                DensityFunctions.constant(0.078125),
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
        return DensityFunctions.min(postProcessed, noodle(noodle, noodleThickness, noodleRidgeA, noodleRidgeB));
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
        DensityFunction layerizedCaverns = DensityFunctions.mul(
                DensityFunctions.constant(4.0),
                DensityFunctions.noise(caveLayer, 1.0, 8.0).square()
        );
        DensityFunction caveCheeseFunction = DensityFunctions.add(
                DensityFunctions.add(
                        DensityFunctions.constant(0.27),
                        DensityFunctions.noise(caveCheese, 1.0, 0.6666666666666666)
                ).clamp(-1.0, 1.0),
                DensityFunctions.add(
                        DensityFunctions.constant(1.5),
                        DensityFunctions.mul(DensityFunctions.constant(-0.64), slopedCheese)
                ).clamp(0.0, 0.5)
        );
        DensityFunction caveDensity = DensityFunctions.add(layerizedCaverns, caveCheeseFunction);
        DensityFunction passages = DensityFunctions.min(
                DensityFunctions.min(caveDensity, entrances),
                DensityFunctions.add(spaghetti2d, spaghettiRoughnessFunction)
        );
        DensityFunction pillarFilter = DensityFunctions.rangeChoice(
                pillars,
                -1000000.0,
                0.03,
                DensityFunctions.constant(-1000000.0),
                pillars
        );
        return DensityFunctions.max(passages, pillarFilter);
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction density, int minY, int maxY, double whenOutOfRange) {
        return DensityFunctions.interpolated(
                DensityFunctions.rangeChoice(
                        Y,
                        minY,
                        maxY + 1.0,
                        density,
                        DensityFunctions.constant(whenOutOfRange)
                )
        );
    }
}
