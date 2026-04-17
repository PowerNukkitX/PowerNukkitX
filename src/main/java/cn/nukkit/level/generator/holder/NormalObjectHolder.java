package cn.nukkit.level.generator.holder;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.densityfunction.*;
import cn.nukkit.level.generator.material.MaterialFiller;
import cn.nukkit.level.generator.material.Aquifer;
import cn.nukkit.level.generator.material.MultiMaterial;
import cn.nukkit.level.generator.material.OreVeinifier;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
        private NormalNoise veinToggleNoise;
        private NormalNoise veinANoise;
        private NormalNoise veinBNoise;
        private NormalNoise oreGapNoise;

        private DensityFunction caveDetector;
        private DensityFunction veinToggle;
        private DensityFunction veinRidged;
        private DensityFunction veinGap;
        private DensityFunction preliminarySurfaceDensity;
        private DensityFunction preliminarySurfaceUpperBound;
        private DensityFunction fullNoiseValue;
        private MultiMaterial multiMaterial;
        private final ThreadLocal<Aquifer> aquifer = new ThreadLocal<>();

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
            veinToggleNoise = new NormalNoise(randomSourceProvider.fork(), -8, new float[]{1f});
            veinANoise = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f});
            veinBNoise = new NormalNoise(randomSourceProvider.fork(), -7, new float[]{1f});
            oreGapNoise = new NormalNoise(randomSourceProvider.fork(), -5, new float[]{1f});

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
            preliminarySurfaceDensity = OverworldCavesDensity.preliminarySurfaceLevel(offset, factor);
            preliminarySurfaceUpperBound = OverworldCavesDensity.preliminarySurfaceLevelUpperBound(offset, factor);
            BlockState stone = BlockStone.PROPERTIES.getDefaultState();
            veinToggle = DensityOreVeins.overworldVeinToggle(veinToggleNoise);
            veinRidged = DensityOreVeins.overworldVeinRidged(veinANoise, veinBNoise);
            veinGap = DensityOreVeins.overworldVeinGap(oreGapNoise);
            fullNoiseValue = DensityCommon.cacheAllInCell(densityFunction);
            OreVeinifier oreVeinifier = new OreVeinifier(veinToggle, veinRidged, veinGap, randomSourceProvider.nextLong());
            List<MaterialFiller> builder = new ArrayList<>();
            builder.add(context -> {
                Aquifer currentAquifer = aquifer.get();
                return currentAquifer == null ? null : currentAquifer.computeSubstance(context, fullNoiseValue.compute(context));
            });
            builder.add(oreVeinifier::calculate);
            builder.add(context -> fullNoiseValue.compute(context) > 0.0d ? stone : null);
            multiMaterial = new MultiMaterial(builder.toArray(new MaterialFiller[0]));
            caveDetector = OverworldCavesDensity.createCaveDetector(this);
        }

        public void beginAquifer(IChunk chunk, Level level, DensityCommon.ChunkCache chunkCache, int minY, int yBlockSize, int seaLevel) {
            aquifer.set(
                    new Aquifer(
                            chunk,
                            level,
                            chunkCache,
                            DensityCommon.noise(barrierNoise, 1.0, 0.5),
                            DensityCommon.noise(fluidLevelFloodednessNoise, 1.0, 0.67),
                            DensityCommon.noise(fluidLevelSpreadNoise, 1.0, 0.7142857142857143),
                            DensityCommon.noise(lavaNoise, 1.0, 1.0),
                            erosion,
                            depth,
                            preliminarySurfaceDensity,
                            preliminarySurfaceUpperBound,
                            -64,
                            8,
                            minY,
                            yBlockSize,
                            Aquifer.overworldFluidPicker(seaLevel)
                    )
            );
        }

        public void endAquifer() {
            aquifer.remove();
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
        private NormalNoise clayBandsOffsetNoise;
        private NormalNoise badlandsPillarNoise;
        private NormalNoise badlandsPillarRoofNoise;
        private NormalNoise badlandsSurfaceNoise;
        private NormalNoise icebergPillarNoise;
        private NormalNoise icebergPillarRoofNoise;
        private NormalNoise icebergSurfaceNoise;
        private cn.nukkit.block.BlockState[] clayBandsCache;

        public SurfaceOverwriteHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.swampNoise = new NormalNoise(randomSourceProvider.identical(), -2, new float[]{1f});
            this.clayBandsOffsetNoise = new NormalNoise(randomSourceProvider.identical(), -8, new float[]{1f});
            this.badlandsPillarNoise = new NormalNoise(randomSourceProvider.identical(), -2, new float[]{1f, 1f, 1f});
            this.badlandsPillarRoofNoise = new NormalNoise(randomSourceProvider.identical(), -8, new float[]{1f});
            this.badlandsSurfaceNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.icebergPillarNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f, 1f});
            this.icebergPillarRoofNoise = new NormalNoise(randomSourceProvider.identical(), -3, new float[]{1f});
            this.icebergSurfaceNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.clayBandsCache = new cn.nukkit.block.BlockState[192];
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
