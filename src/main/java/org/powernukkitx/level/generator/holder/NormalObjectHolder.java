package org.powernukkitx.level.generator.holder;

import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockStone;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.densityfunction.*;
import org.powernukkitx.level.generator.material.MaterialFiller;
import org.powernukkitx.level.generator.material.Aquifer;
import org.powernukkitx.level.generator.material.MultiMaterial;
import org.powernukkitx.level.generator.material.OreVeinifier;
import org.powernukkitx.level.generator.noise.f.SimplexF;
import org.powernukkitx.level.generator.noise.minecraft.noise.NormalNoise;
import org.powernukkitx.level.generator.noise.minecraft.simplex.SimplexNoise;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;
import org.powernukkitx.utils.random.Xoroshiro128PositionalFactory;
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
    /**
     * World factory: {@code Xoroshiro128.ofSeed(worldSeed).forkPositional()}. Every noise is derived from it with
     * {@code hashOf("minecraft:<name>")}, independent of the creation order.
     */
    private final Xoroshiro128PositionalFactory noiseFactory;

    /**
     * @param randomSourceProvider must return the world seed: {@link RandomSourceProvider#getSeed()} is used as the
     *                             seed of the positional factory.
     */
    public NormalObjectHolder(RandomSourceProvider randomSourceProvider) {
        super(randomSourceProvider);
        this.noiseFactory = createNoiseFactory(randomSourceProvider);
        this.biomeHolder = new BiomeHolder(randomSourceProvider, noiseFactory);
        this.terrainHolder = new TerrainHolder(randomSourceProvider);
        this.surfaceHolder = new SurfaceHolder(randomSourceProvider, noiseFactory);
        this.surfaceOverwriteHolder = new SurfaceOverwriteHolder(randomSourceProvider, noiseFactory);
        this.featureHolder = new FeatureHolder(randomSourceProvider);
    }

    /**
     * Builds the world factory ({@code Xoroshiro(seed).forkPositional()}).
     * Only the seed of the provider is used, so a {@link Xoroshiro128} created with
     * {@link Xoroshiro128#fromState} (seed 0) gives the factory of seed 0.
     */
    public static Xoroshiro128PositionalFactory createNoiseFactory(RandomSourceProvider randomSourceProvider) {
        return Xoroshiro128.ofSeed(randomSourceProvider.getSeed()).forkPositional();
    }

    @Getter
    public class TerrainHolder extends RandomizedObjectHolder {

        private NormalNoise surfaceNoise;
        private NormalNoise jagged;
        private DensityFunction densityFunction;
        private DensityFunction continents;
        private DensityFunction temperature;
        private DensityFunction humidity;
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
        private DensityFunction terrainDensity;
        private OreVeinifier oreVeinifier;
        private BlockState terrainStone;
        private MultiMaterial multiMaterial;
        private final ThreadLocal<Aquifer> aquifer = new ThreadLocal<>();

        public TerrainHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            final Xoroshiro128PositionalFactory noiseFactory = NormalObjectHolder.this.noiseFactory;
            this.surfaceNoise = NormalNoise.create(noiseFactory, "minecraft:surface", -6, new float[]{1f, 1f, 1f});
            this.jagged = NormalNoise.create(noiseFactory, "minecraft:jagged", -16, new float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f});
            BiomeHolder noises = NormalObjectHolder.this.getBiomeHolder();

            NormalNoise shiftNoise = noises.getOffsetNoise();
            NormalNoise continentalness = noises.getContinentalNoise();
            NormalNoise temperatureNoise = noises.getTemperatureNoise();
            NormalNoise humidityNoise = noises.getHumidityNoise();
            NormalNoise erosionNoise = noises.getErosionNoise();
            NormalNoise ridgeNoise = noises.getWeirdnessNoise();
            NormalNoise jaggedNoise = noises.getJaggedNoise();

            DensityFunction shiftX = DensityCommon.flatCache(DensityCommon.shiftA(shiftNoise));
            DensityFunction shiftZ = DensityCommon.flatCache(DensityCommon.shiftB(shiftNoise));
            continents = DensityContinents.overworldContinents(continentalness, shiftX, shiftZ);
            temperature = DensityCommon.flatCache(new DensityCommon.ShiftedNoise(
                    shiftX,
                    DensityCommon.zero(),
                    shiftZ,
                    0.25,
                    0.0,
                    new DensityFunction.NoiseHolder(temperatureNoise)
            ));
            humidity = DensityCommon.flatCache(new DensityCommon.ShiftedNoise(
                    shiftX,
                    DensityCommon.zero(),
                    shiftZ,
                    0.25,
                    0.0,
                    new DensityFunction.NoiseHolder(humidityNoise)
            ));
            erosion = DensityErosion.overworldErosion(erosionNoise, shiftX, shiftZ);
            ridges = DensityRidges.overworldRidges(ridgeNoise, shiftX, shiftZ);
            ridgesFolded = DensityRidgesFolded.overworldRidgesFolded(ridges);

            offset = DensityOffset.overworldOffset(continents, erosion, ridgesFolded);
            depth = DensityDepth.overworldDepth(offset);
            factor = DensityFactor.overworldFactor(continents, erosion, ridges, ridgesFolded);
            jaggedness = DensityJaggedness.overworldJaggedness(continents, erosion, ridges, ridgesFolded);
            base3d = DensityBase3dNoise.overworld(noiseFactory);
            barrierNoise = NormalNoise.create(noiseFactory, "minecraft:aquifer_barrier", -3, new float[]{1f});
            fluidLevelFloodednessNoise = NormalNoise.create(noiseFactory, "minecraft:aquifer_fluid_level_floodedness", -7, new float[]{1f});
            fluidLevelSpreadNoise = NormalNoise.create(noiseFactory, "minecraft:aquifer_fluid_level_spread", -5, new float[]{1f});
            lavaNoise = NormalNoise.create(noiseFactory, "minecraft:aquifer_lava", -1, new float[]{1f});
            pillar = NormalNoise.create(noiseFactory, "minecraft:pillar", -7, new float[]{1f, 1f});
            pillarRareness = NormalNoise.create(noiseFactory, "minecraft:pillar_rareness", -8, new float[]{1f});
            pillarThickness = NormalNoise.create(noiseFactory, "minecraft:pillar_thickness", -8, new float[]{1f});
            spaghetti2d = NormalNoise.create(noiseFactory, "minecraft:spaghetti_2d", -7, new float[]{1f});
            spaghetti2dElevation = NormalNoise.create(noiseFactory, "minecraft:spaghetti_2d_elevation", -8, new float[]{1f});
            spaghetti2dModulator = NormalNoise.create(noiseFactory, "minecraft:spaghetti_2d_modulator", -11, new float[]{1f});
            spaghetti2dThickness = NormalNoise.create(noiseFactory, "minecraft:spaghetti_2d_thickness", -11, new float[]{1f});
            spaghetti3dFirst = NormalNoise.create(noiseFactory, "minecraft:spaghetti_3d_1", -7, new float[]{1f});
            spaghetti3dSecond = NormalNoise.create(noiseFactory, "minecraft:spaghetti_3d_2", -7, new float[]{1f});
            spaghetti3dRarity = NormalNoise.create(noiseFactory, "minecraft:spaghetti_3d_rarity", -11, new float[]{1f});
            spaghetti3dThickness = NormalNoise.create(noiseFactory, "minecraft:spaghetti_3d_thickness", -8, new float[]{1f});
            spaghettiRoughness = NormalNoise.create(noiseFactory, "minecraft:spaghetti_roughness", -5, new float[]{1f});
            spaghettiRoughnessModulator = NormalNoise.create(noiseFactory, "minecraft:spaghetti_roughness_modulator", -8, new float[]{1f});
            caveEntrance = NormalNoise.create(noiseFactory, "minecraft:cave_entrance", -7, new float[]{0.4f, 0.5f, 1f});
            caveLayer = NormalNoise.create(noiseFactory, "minecraft:cave_layer", -8, new float[]{1f});
            caveCheese = NormalNoise.create(noiseFactory, "minecraft:cave_cheese", -8, new float[]{0.5f, 1f, 2f, 1f, 2f, 1f, 0f, 2f, 0f});
            noodle = NormalNoise.create(noiseFactory, "minecraft:noodle", -8, new float[]{1f});
            noodleThickness = NormalNoise.create(noiseFactory, "minecraft:noodle_thickness", -8, new float[]{1f});
            noodleRidgeA = NormalNoise.create(noiseFactory, "minecraft:noodle_ridge_a", -7, new float[]{1f});
            noodleRidgeB = NormalNoise.create(noiseFactory, "minecraft:noodle_ridge_b", -7, new float[]{1f});
            veinToggleNoise = NormalNoise.create(noiseFactory, "minecraft:ore_veininess", -8, new float[]{1f});
            veinANoise = NormalNoise.create(noiseFactory, "minecraft:ore_vein_a", -7, new float[]{1f});
            veinBNoise = NormalNoise.create(noiseFactory, "minecraft:ore_vein_b", -7, new float[]{1f});
            oreGapNoise = NormalNoise.create(noiseFactory, "minecraft:ore_gap", -5, new float[]{1f});

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
            terrainStone = BlockStone.PROPERTIES.getDefaultState();
            veinToggle = DensityOreVeins.overworldVeinToggle(veinToggleNoise);
            veinRidged = DensityOreVeins.overworldVeinRidged(veinANoise, veinBNoise);
            veinGap = DensityOreVeins.overworldVeinGap(oreGapNoise);
            oreVeinifier = new OreVeinifier(veinToggle, veinRidged, veinGap, randomSourceProvider.nextLong());
            List<MaterialFiller> builder = new ArrayList<>();
            terrainDensity = DensityCommon.cacheAllInCell(densityFunction);
            builder.add(context -> {
                Aquifer currentAquifer = aquifer.get();
                return currentAquifer == null ? null : currentAquifer.computeSubstance(context, terrainDensity.compute(context));
            });
            builder.add(oreVeinifier::calculate);
            builder.add(context -> terrainDensity.compute(context) > 0.0d ? terrainStone : null);
            multiMaterial = new MultiMaterial(builder.toArray(new MaterialFiller[0]));
        }

        /**
         * Resolves the terrain material for a density sample.
         *
         * @param context density-function context
         * @param currentAquifer aquifer used for fluid material resolution
         * @return resolved block state, or {@code null} when the position remains empty
         */
        public BlockState calculateTerrainMaterial(DensityFunction.FunctionContext context, Aquifer currentAquifer) {
            final double density = terrainDensity.compute(context);
            BlockState state = currentAquifer == null ? null : currentAquifer.computeSubstance(context, density);
            if (state != null) return state;

            state = oreVeinifier.calculate(context);
            if (state != null) return state;

            return density > 0.0d ? terrainStone : null;
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

        private NormalNoise noise;

        public SurfaceHolder(RandomSourceProvider randomSourceProvider) {
            this(randomSourceProvider, createNoiseFactory(randomSourceProvider));
        }

        public SurfaceHolder(RandomSourceProvider randomSourceProvider, Xoroshiro128PositionalFactory noiseFactory) {
            super(randomSourceProvider);
            this.noise = NormalNoise.create(noiseFactory, "minecraft:surface", -6, new float[] {1f, 1f, 1f});
        }
    }

    @Getter
    public static class SurfaceOverwriteHolder extends RandomizedObjectHolder {

        private NormalNoise surfaceNoise;
        private NormalNoise surfaceSecondaryNoise;
        private NormalNoise swampNoise;
        private NormalNoise clayBandsOffsetNoise;
        private NormalNoise badlandsPillarNoise;
        private NormalNoise badlandsPillarRoofNoise;
        private NormalNoise badlandsSurfaceNoise;
        private NormalNoise icebergPillarNoise;
        private NormalNoise icebergPillarRoofNoise;
        private NormalNoise icebergSurfaceNoise;
        private org.powernukkitx.block.BlockState[] clayBandsCache;

        public SurfaceOverwriteHolder(RandomSourceProvider randomSourceProvider) {
            this(randomSourceProvider, createNoiseFactory(randomSourceProvider));
        }

        public SurfaceOverwriteHolder(RandomSourceProvider randomSourceProvider, Xoroshiro128PositionalFactory noiseFactory) {
            super(randomSourceProvider);
            this.surfaceNoise = NormalNoise.create(noiseFactory, "minecraft:surface", -6, new float[]{1f, 1f, 1f});
            this.surfaceSecondaryNoise = NormalNoise.create(noiseFactory, "minecraft:surface_secondary", -6, new float[]{1f, 1f, 0f, 1f});
            this.swampNoise = NormalNoise.create(noiseFactory, "minecraft:surface_swamp", -2, new float[]{1f});
            this.clayBandsOffsetNoise = NormalNoise.create(noiseFactory, "minecraft:clay_bands_offset", -8, new float[]{1f});
            this.badlandsPillarNoise = NormalNoise.create(noiseFactory, "minecraft:badlands_pillar", -2, new float[]{1f, 1f, 1f, 1f});
            this.badlandsPillarRoofNoise = NormalNoise.create(noiseFactory, "minecraft:badlands_pillar_roof", -8, new float[]{1f});
            this.badlandsSurfaceNoise = NormalNoise.create(noiseFactory, "minecraft:badlands_surface", -6, new float[]{1f, 1f, 1f});
            this.icebergPillarNoise = NormalNoise.create(noiseFactory, "minecraft:iceberg_pillar", -6, new float[]{1f, 1f, 1f, 1f});
            this.icebergPillarRoofNoise = NormalNoise.create(noiseFactory, "minecraft:iceberg_pillar_roof", -3, new float[]{1f});
            this.icebergSurfaceNoise = NormalNoise.create(noiseFactory, "minecraft:iceberg_surface", -6, new float[]{1f, 1f, 1f});
            this.clayBandsCache = new org.powernukkitx.block.BlockState[192];
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
        private SimplexNoise sulfurCaveGradient;

        public FeatureHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.randomClayWithDripleavesSnapToFloor = new SimplexF(randomSourceProvider.identical(), 1f, 2 / 4f, 1 / 15f);
            this.dripstoneCluster = new SimplexF(randomSourceProvider.identical(), 30f, 1 / 99f, 1 / 15f);
            this.mossPatchSnapToFloor = new SimplexF(randomSourceProvider.identical(), 2f, 2 / 4f, 1 / 10f);
            this.mossSnapToCeiling = new SimplexF(randomSourceProvider.identical(), 2f, 2 / 4f, 1 / 30f);
            this.sculkPatch = new SimplexF(randomSourceProvider.identical(), 20f, 1 / 99f, 1 / 100f);
            this.kelp = new SimplexNoise(randomSourceProvider.identical(), -7, new float[]{ 1 });
            this.sulfurCaveGradient = new SimplexNoise(randomSourceProvider.fork(), -5, new float[]{1f, 0f, 1f});
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
            this(randomSourceProvider, createNoiseFactory(randomSourceProvider));
        }

        public BiomeHolder(RandomSourceProvider randomSourceProvider, Xoroshiro128PositionalFactory noiseFactory) {
            super(randomSourceProvider);
            continentalNoise = NormalNoise.create(noiseFactory, "minecraft:continentalness", -9, new float[]{1, 1, 2, 2, 2, 1, 1, 1, 1});
            temperatureNoise = NormalNoise.create(noiseFactory, "minecraft:temperature", -10, new float[]{1.5f, 0, 1, 0, 0, 0});
            humidityNoise = NormalNoise.create(noiseFactory, "minecraft:vegetation", -8, new float[]{1, 1, 0, 0, 0, 0});
            erosionNoise = NormalNoise.create(noiseFactory, "minecraft:erosion", -9, new float[]{1, 1, 0, 1, 1});
            weirdnessNoise = NormalNoise.create(noiseFactory, "minecraft:ridge", -7, new float[]{1, 2, 1, 0, 0, 0});
            offsetNoise = NormalNoise.create(noiseFactory, "minecraft:offset", -3, new float[]{1, 1, 1, 0});
            jaggedNoise = NormalNoise.create(noiseFactory, "minecraft:jagged", -16, new float[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        }

    }
}
