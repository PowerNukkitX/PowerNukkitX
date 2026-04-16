package cn.nukkit.level.generator.holder;

import cn.nukkit.level.generator.densityfunction.*;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.Getter;

@Getter
public class NormalObjectHolder extends RandomizedObjectHolder {

    private BiomeHolder biomeHolder;
    private TerrainHolder terrainHolder;
    private SurfaceHolder surfaceHolder;
    private SurfaceOverwriteHolder surfaceOverwriteHolder;
    private FeatureHolder featureHolder;

    public NormalObjectHolder(RandomSourceProvider randomSourceProvider) {
        super(randomSourceProvider);
        this.biomeHolder = new BiomeHolder(randomSourceProvider);
        this.terrainHolder = new TerrainHolder(randomSourceProvider);
        this.surfaceHolder = new SurfaceHolder(randomSourceProvider);
        this.surfaceOverwriteHolder = new SurfaceOverwriteHolder(randomSourceProvider);
        this.featureHolder = new FeatureHolder(randomSourceProvider);
    }

    @Getter
    public class TerrainHolder extends RandomizedObjectHolder {

        private NormalNoise surfaceNoise;
        private NormalNoise jagged;
        private DensityFunction densityFunction;
        private DensityFunction continents;
        private DensityFunction erosion;
        private DensityFunction ridges;
        private DensityFunction ridgesFolded;
        private DensityFunction offset;
        private DensityFunction depth;
        private DensityFunction factor;
        private DensityFunction jaggedness;
        private DensityFunction base3d;
        private DensityFunction slopedCheese;
        private NormalNoise barrierNoise;
        private NormalNoise fluidLevelFloodednessNoise;
        private NormalNoise fluidLevelSpreadNoise;
        private NormalNoise lavaNoise;
        private NormalNoise pillar;
        private NormalNoise pillarRareness;
        private NormalNoise pillarThickness;
        private NormalNoise spaghetti2d;
        private NormalNoise spaghetti2dElevation;
        private NormalNoise spaghetti2dModulator;
        private NormalNoise spaghetti2dThickness;
        private NormalNoise spaghetti3dFirst;
        private NormalNoise spaghetti3dSecond;
        private NormalNoise spaghetti3dRarity;
        private NormalNoise spaghetti3dThickness;
        private NormalNoise spaghettiRoughness;
        private NormalNoise spaghettiRoughnessModulator;
        private NormalNoise caveEntrance;
        private NormalNoise caveLayer;
        private NormalNoise caveCheese;
        private NormalNoise noodle;
        private NormalNoise noodleThickness;
        private NormalNoise noodleRidgeA;
        private NormalNoise noodleRidgeB;

        private DensityFunction caveDetector;

        public TerrainHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.jagged = new NormalNoise(randomSourceProvider.identical(), -16, new float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f});
            BiomeHolder noises = NormalObjectHolder.this.getBiomeHolder();

            NormalNoise shiftNoise = noises.getOffsetNoise();
            NormalNoise continentalness = noises.getContinentalNoise();
            NormalNoise erosionNoise = noises.getErosionNoise();
            NormalNoise ridgeNoise = noises.getWeirdnessNoise();
            NormalNoise jaggedNoise = noises.getJaggedNoise();

            continents = DensityContinents.overworldContinents(continentalness, shiftNoise);
            erosion = DensityErosion.overworldErosion(erosionNoise, shiftNoise);
            ridges = DensityRidges.overworldRidges(ridgeNoise, shiftNoise);
            ridgesFolded = DensityRidgesFolded.overworldRidgesFolded(ridges);

            offset = DensityOffset.overworldOffset(continents, erosion, ridgesFolded);
            depth = DensityDepth.overworldDepth(offset);
            factor = DensityFactor.overworldFactor(continents, erosion, ridges, ridgesFolded);
            jaggedness = DensityJaggedness.overworldJaggedness(continents, erosion, ridges, ridgesFolded);
            base3d = DensityBase3dNoise.overworld(randomSourceProvider.identical());
            barrierNoise = new NormalNoise(randomSourceProvider.fork(), -3, new float[]{1f, 1f, 1f});
            fluidLevelFloodednessNoise = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f, 1f, 0f, 1f});
            fluidLevelSpreadNoise = new NormalNoise(randomSourceProvider.fork(), -5, new float[]{1f, 1f, 1f});
            lavaNoise = new NormalNoise(randomSourceProvider.fork(), -1, new float[]{1f, 1f});
            pillar = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f, 1f});
            pillarRareness = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            pillarThickness = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            spaghetti2d = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f});
            spaghetti2dElevation = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            spaghetti2dModulator = new NormalNoise(randomSourceProvider.fork(), -11, new float[]{1f});
            spaghetti2dThickness = new NormalNoise(randomSourceProvider.fork(), -11, new float[]{1f});
            spaghetti3dFirst = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f});
            spaghetti3dSecond = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f});
            spaghetti3dRarity = new NormalNoise(randomSourceProvider.fork(), -11, new float[]{1f});
            spaghetti3dThickness = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            spaghettiRoughness = new NormalNoise(randomSourceProvider.fork(), -5, new float[]{1f});
            spaghettiRoughnessModulator = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            caveEntrance = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{0.4f, 0.5f, 1f});
            caveLayer = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            caveCheese = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{0.5f, 1f, 2f, 1f, 2f, 1f, 0f, 2f, 0f});
            noodle = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            noodleThickness = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            noodleRidgeA = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f});
            noodleRidgeB = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f});

            slopedCheese = DensitySlopedCheese.overworldSlopedCheese(
                    depth, jaggedness, factor, base3d, jaggedNoise
            );
            densityFunction = OverworldCavesDensity.finalDensity(
                    slopedCheese,
                    spaghettiRoughness,
                    spaghettiRoughnessModulator,
                    spaghetti2dThickness,
                    spaghetti2dModulator,
                    spaghetti2d,
                    spaghetti2dElevation,
                    spaghetti3dRarity,
                    spaghetti3dThickness,
                    spaghetti3dFirst,
                    spaghetti3dSecond,
                    caveEntrance,
                    caveLayer,
                    caveCheese,
                    pillar,
                    pillarRareness,
                    pillarThickness,
                    noodle,
                    noodleThickness,
                    noodleRidgeA,
                    noodleRidgeB
            );
            caveDetector = OverworldCavesDensity.createCaveDetector(this);
        }
    }

    @Getter
    public static class SurfaceHolder extends RandomizedObjectHolder {

        private SimplexF simplexF;

        public SurfaceHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.simplexF = new SimplexF(randomSourceProvider.identical(), 1f, 2 / 4f, 1 / 10f);
        }
    }

    @Getter
    public static class SurfaceOverwriteHolder extends RandomizedObjectHolder {

        private NormalNoise surfaceNoise;
        private NormalNoise swampNoise;

        public SurfaceOverwriteHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.swampNoise = new NormalNoise(randomSourceProvider.identical(), -2, new float[]{1f});
        }
    }

    @Getter
    public static class FeatureHolder extends RandomizedObjectHolder {

        private SimplexF randomClayWithDripleavesSnapToFloor;
        private SimplexF dripstoneCluster;
        private SimplexF mossPatchSnapToFloor;
        private SimplexF mossSnapToCeiling;
        private SimplexF sculkPatch;
        private SimplexNoise kelp;

        public FeatureHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.randomClayWithDripleavesSnapToFloor = new SimplexF(randomSourceProvider.identical(), 1f, 2 / 4f, 1 / 15f);
            this.dripstoneCluster = new SimplexF(randomSourceProvider.identical(), 30f, 1 / 99f, 1 / 15f);
            this.mossPatchSnapToFloor = new SimplexF(randomSourceProvider.identical(), 2f, 2 / 4f, 1 / 10f);
            this.mossSnapToCeiling = new SimplexF(randomSourceProvider.identical(), 2f, 2 / 4f, 1 / 30f);
            this.sculkPatch = new SimplexF(randomSourceProvider.identical(), 20f, 1 / 99f, 1 / 100f);
            this.kelp = new SimplexNoise(randomSourceProvider.identical(), -7, new float[]{ 1 });
        }
    }

    @Getter
    public static class BiomeHolder extends RandomizedObjectHolder {

        private final NormalNoise continentalNoise;
        private final NormalNoise temperatureNoise;
        private final NormalNoise humidityNoise;
        private final NormalNoise erosionNoise;
        private final NormalNoise weirdnessNoise;
        private final NormalNoise offsetNoise;
        private final NormalNoise jaggedNoise;

        public BiomeHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            continentalNoise = new NormalNoise(randomSourceProvider.fork(), -9, new float[]{ 1, 1, 2, 2, 2, 1, 1, 1, 1 });
            temperatureNoise = new NormalNoise(randomSourceProvider.fork(), -10 , new float[]{ 1.5f, 0, 1, 0, 0, 0 });
            humidityNoise = new NormalNoise(randomSourceProvider.fork(), -8 , new float[]{ 1, 1, 0, 0, 0, 0 });
            erosionNoise = new NormalNoise(randomSourceProvider.fork(), -9, new float[]{ 1, 1, 0, 1, 1 });
            weirdnessNoise = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{ 1, 2, 1, 0, 0, 0});
            offsetNoise = new NormalNoise(randomSourceProvider.fork(), -3, new float[]{ 1, 1, 1, 0 });
            jaggedNoise = new NormalNoise(randomSourceProvider.fork(), -16, new float[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        }

    }
}
