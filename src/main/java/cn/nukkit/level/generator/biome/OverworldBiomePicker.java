package cn.nukkit.level.generator.biome;

import cn.nukkit.level.Level;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.utils.random.NukkitRandom;
import lombok.Getter;

import static cn.nukkit.level.biome.BiomeID.*;
import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

@Getter
public class OverworldBiomePicker extends BiomePicker<OverworldBiomeResult> {

    public static final int CONTINENT_MUSHROOM = 0;
    public static final int CONTINENT_DEEP_OCEAN = 1;
    public static final int CONTINENT_OCEAN = 2;
    public static final int CONTINENT_COAST = 3;
    public static final int CONTINENT_NEAR_INLAND = 4;
    public static final int CONTINENT_MID_INLAND = 5;
    public static final int CONTINENT_FAR_INLAND = 6;

    private final Level level;

    public OverworldBiomePicker(Level level) {
        super(new NukkitRandom(level.getSeed()));
        this.level = level;
    }

    @Override
    public OverworldBiomeResult pick(int x, int y, int z) {

        NormalObjectHolder.TerrainHolder density = ((NormalObjectHolder) level.getGeneratorObjectHolder()).getTerrainHolder();
        NormalObjectHolder.BiomeHolder noises = ((NormalObjectHolder) level.getGeneratorObjectHolder()).getBiomeHolder();

        var point = new DensityFunction.SinglePointContext(x, y, z);
        //Those values are 2D
        float continental = (float) density.getContinents().compute(point);
        float temperature = noises.getTemperatureNoise().getValue(x, SEA_LEVEL, z);
        float humidity = noises.getHumidityNoise().getValue(x, SEA_LEVEL, z);
        float erosion = (float) density.getErosion().compute(point);
        float weirdness = (float) density.getRidges().compute(point);
        float pv = -3 * (-(1/3f) + Math.abs(-(2/3f) + Math.abs(weirdness)));

        int continentalLevel = continental < -1.05f ? 0 : (continental < -0.455f ? 1 : (continental < -0.19 ? 2 : (continental < -0.11 ? 3 : (continental < 0.03 ? 4 : (continental < 0.3 ? 5 : 6)))));
        int temperatureLevel = temperature < -0.45f ? 0 : (temperature < -0.15f ? 1 : (temperature < 0.3f ? 2 : (temperature < 0.55f ? 3 : 4)));
        int humidityLevel = humidity < -0.35f ? 0 : (humidity < -0.1f ? 1 : (humidity < 0.1f ? 2 : (humidity < 0.3f ? 3 : 4)));
        int erosionLevel = erosion < -0.78f ? 0 : (erosion < -0.375f ? 1 : (erosion < -0.2225f ? 2 : (erosion < 0.05f ? 3 : (erosion < 0.45f ? 4 : (erosion < 0.55f ? 5 : 6)))));
        int biome = switch (continentalLevel){
            case CONTINENT_MUSHROOM -> MUSHROOM_ISLAND;
            case CONTINENT_OCEAN,
                 CONTINENT_DEEP_OCEAN -> getNonInlandBiome(temperatureLevel, continentalLevel);
            default -> getInlandBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        };

        return new OverworldBiomeResult(biome, continental, temperature, humidity, erosion, weirdness, pv).correct(y - level.getHeightMap(x, z));
    }

    protected int getNonInlandBiome(int temperatureLevel, int continentalLevel) {
        return switch (temperatureLevel) {
            case 0 -> continentalLevel == CONTINENT_OCEAN ? FROZEN_OCEAN : DEEP_FROZEN_OCEAN;
            case 1 -> continentalLevel == CONTINENT_OCEAN ? COLD_OCEAN : DEEP_COLD_OCEAN;
            case 2 -> continentalLevel == CONTINENT_OCEAN ? OCEAN : DEEP_OCEAN;
            case 3 -> continentalLevel == CONTINENT_OCEAN ? LUKEWARM_OCEAN : DEEP_LUKEWARM_OCEAN;
            default -> WARM_OCEAN;
        };
    }

    protected int getInlandBiome(int temperatureLevel, int humidityLevel, int continentalLevel, int erosionLevel, float weirdness) {
        if (weirdness < -0.93333334f) {
            return pickMidSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < -0.7666667f) {
            return pickHighSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < -0.56666666f) {
            return pickPeaksBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < -0.4f) {
            return pickHighSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < -0.26666668f) {
            return pickMidSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < -0.05f) {
            return pickLowSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < 0.05f) {
            return pickValleysBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < 0.26666668f) {
            return pickLowSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < 0.4f) {
            return pickMidSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < 0.56666666f) {
            return pickHighSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < 0.7666667f) {
            return pickPeaksBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else if (weirdness < 0.93333334f) {
            return pickHighSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        } else {
            return pickMidSliceBiome(temperatureLevel, humidityLevel, continentalLevel, erosionLevel, weirdness);
        }
    }

    private int pickPeaksBiome(int temperatureLevel, int humidityLevel, int continentalLevel, int erosionLevel, float weirdness) {
        boolean weird = weirdness >= 0f;
        int middleBiome = getMiddleBiome(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrBadlandsIfHot = getMiddleBiomeOrBadlandsIfHot(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrBadlandsIfHotOrSlopeIfCold = getMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(temperatureLevel, humidityLevel, weird);
        int plateauBiome = getPlateauBiome(temperatureLevel, humidityLevel, weird);
        int shatteredBiome = getShatteredBiome(temperatureLevel, humidityLevel, weird);
        int shatteredBiomeOrWindsweptSavanna = maybePickWindsweptSavannaBiome(temperatureLevel, humidityLevel, weird, shatteredBiome);
        int peakBiome = getPeakBiome(temperatureLevel, humidityLevel, weird);

        if (isCoastToFar(continentalLevel) && erosionLevel == 0) return peakBiome;
        if (isCoastToNear(continentalLevel) && erosionLevel == 1) return middleBiomeOrBadlandsIfHotOrSlopeIfCold;
        if (isMidToFar(continentalLevel) && erosionLevel == 1) return peakBiome;
        if (isCoastToNear(continentalLevel) && (erosionLevel == 2 || erosionLevel == 3)) return middleBiome;
        if (isMidToFar(continentalLevel) && erosionLevel == 2) return plateauBiome;
        if (isMid(continentalLevel) && erosionLevel == 3) return middleBiomeOrBadlandsIfHot;
        if (isFar(continentalLevel) && erosionLevel == 3) return plateauBiome;
        if (isCoastToFar(continentalLevel) && erosionLevel == 4) return middleBiome;
        if (isCoastToNear(continentalLevel) && erosionLevel == 5) return shatteredBiomeOrWindsweptSavanna;
        if (isMidToFar(continentalLevel) && erosionLevel == 5) return shatteredBiome;
        return middleBiome;
    }

    private int pickHighSliceBiome(int temperatureLevel, int humidityLevel, int continentalLevel, int erosionLevel, float weirdness) {
        boolean weird = weirdness >= 0f;
        int middleBiome = getMiddleBiome(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrBadlandsIfHot = getMiddleBiomeOrBadlandsIfHot(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrBadlandsIfHotOrSlopeIfCold = getMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(temperatureLevel, humidityLevel, weird);
        int plateauBiome = getPlateauBiome(temperatureLevel, humidityLevel, weird);
        int shatteredBiome = getShatteredBiome(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrWindsweptSavanna = maybePickWindsweptSavannaBiome(temperatureLevel, humidityLevel, weird, middleBiome);
        int slopeBiome = getSlopeBiome(temperatureLevel, humidityLevel, weird);
        int peakBiome = getPeakBiome(temperatureLevel, humidityLevel, weird);

        if (isCoast(continentalLevel) && (erosionLevel == 0 || erosionLevel == 1)) return middleBiome;
        if (isNear(continentalLevel) && erosionLevel == 0) return slopeBiome;
        if (isMidToFar(continentalLevel) && erosionLevel == 0) return peakBiome;
        if (isNear(continentalLevel) && erosionLevel == 1) return middleBiomeOrBadlandsIfHotOrSlopeIfCold;
        if (isMidToFar(continentalLevel) && erosionLevel == 1) return slopeBiome;
        if (isCoastToNear(continentalLevel) && (erosionLevel == 2 || erosionLevel == 3)) return middleBiome;
        if (isMidToFar(continentalLevel) && erosionLevel == 2) return plateauBiome;
        if (isMid(continentalLevel) && erosionLevel == 3) return middleBiomeOrBadlandsIfHot;
        if (isFar(continentalLevel) && erosionLevel == 3) return plateauBiome;
        if (isCoastToFar(continentalLevel) && erosionLevel == 4) return middleBiome;
        if (isCoastToNear(continentalLevel) && erosionLevel == 5) return middleBiomeOrWindsweptSavanna;
        if (isMidToFar(continentalLevel) && erosionLevel == 5) return shatteredBiome;
        return middleBiome;
    }

    private int pickMidSliceBiome(int temperatureLevel, int humidityLevel, int continentalLevel, int erosionLevel, float weirdness) {
        boolean weird = weirdness >= 0f;
        if (isCoast(continentalLevel) && erosionLevel <= 2) return STONE_BEACH;
        if ((temperatureLevel == 1 || temperatureLevel == 2) && isNearToFar(continentalLevel) && erosionLevel == 6) return SWAMPLAND;
        if ((temperatureLevel == 3 || temperatureLevel == 4) && isNearToFar(continentalLevel) && erosionLevel == 6) return MANGROVE_SWAMP;

        int middleBiome = getMiddleBiome(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrBadlandsIfHot = getMiddleBiomeOrBadlandsIfHot(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrBadlandsIfHotOrSlopeIfCold = getMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(temperatureLevel, humidityLevel, weird);
        int shatteredBiome = getShatteredBiome(temperatureLevel, humidityLevel, weird);
        int plateauBiome = getPlateauBiome(temperatureLevel, humidityLevel, weird);
        int beachBiome = getBeachBiome(temperatureLevel);
        int middleBiomeOrWindsweptSavanna = maybePickWindsweptSavannaBiome(temperatureLevel, humidityLevel, weird, middleBiome);
        int shatteredCoastBiome = pickShatteredCoastBiome(temperatureLevel, humidityLevel, weird);
        int slopeBiome = getSlopeBiome(temperatureLevel, humidityLevel, weird);

        if (isNearToFar(continentalLevel) && erosionLevel == 0) return slopeBiome;
        if (isNearToMid(continentalLevel) && erosionLevel == 1) return middleBiomeOrBadlandsIfHotOrSlopeIfCold;
        if (isFar(continentalLevel) && erosionLevel == 1) return temperatureLevel == 0 ? slopeBiome : plateauBiome;
        if (isNear(continentalLevel) && erosionLevel == 2) return middleBiome;
        if (isMid(continentalLevel) && erosionLevel == 2) return middleBiomeOrBadlandsIfHot;
        if (isFar(continentalLevel) && erosionLevel == 2) return plateauBiome;
        if (isCoastToNear(continentalLevel) && erosionLevel == 3) return middleBiome;
        if (isMidToFar(continentalLevel) && erosionLevel == 3) return middleBiomeOrBadlandsIfHot;
        if (erosionLevel == 4) {
            if (!weird) {
                if (isCoast(continentalLevel)) return beachBiome;
                if (isNearToFar(continentalLevel)) return middleBiome;
            } else if (isCoastToFar(continentalLevel)) {
                return middleBiome;
            }
        }
        if (isCoast(continentalLevel) && erosionLevel == 5) return shatteredCoastBiome;
        if (isNear(continentalLevel) && erosionLevel == 5) return middleBiomeOrWindsweptSavanna;
        if (isMidToFar(continentalLevel) && erosionLevel == 5) return shatteredBiome;
        if (isCoast(continentalLevel) && erosionLevel == 6) return weird ? middleBiome : beachBiome;
        if (temperatureLevel == 0 && isNearToFar(continentalLevel) && erosionLevel == 6) return middleBiome;
        return middleBiome;
    }

    private int pickLowSliceBiome(int temperatureLevel, int humidityLevel, int continentalLevel, int erosionLevel, float weirdness) {
        boolean weird = weirdness >= 0f;
        if (isCoast(continentalLevel) && erosionLevel <= 2) return STONE_BEACH;
        if ((temperatureLevel == 1 || temperatureLevel == 2) && isNearToFar(continentalLevel) && erosionLevel == 6) return SWAMPLAND;
        if ((temperatureLevel == 3 || temperatureLevel == 4) && isNearToFar(continentalLevel) && erosionLevel == 6) return MANGROVE_SWAMP;

        int middleBiome = getMiddleBiome(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrBadlandsIfHot = getMiddleBiomeOrBadlandsIfHot(temperatureLevel, humidityLevel, weird);
        int middleBiomeOrBadlandsIfHotOrSlopeIfCold = getMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(temperatureLevel, humidityLevel, weird);
        int beachBiome = getBeachBiome(temperatureLevel);
        int middleBiomeOrWindsweptSavanna = maybePickWindsweptSavannaBiome(temperatureLevel, humidityLevel, weird, middleBiome);
        int shatteredCoastBiome = pickShatteredCoastBiome(temperatureLevel, humidityLevel, weird);

        if (isNear(continentalLevel) && (erosionLevel == 0 || erosionLevel == 1)) return middleBiomeOrBadlandsIfHot;
        if (isMidToFar(continentalLevel) && (erosionLevel == 0 || erosionLevel == 1)) return middleBiomeOrBadlandsIfHotOrSlopeIfCold;
        if (isNear(continentalLevel) && (erosionLevel == 2 || erosionLevel == 3)) return middleBiome;
        if (isMidToFar(continentalLevel) && (erosionLevel == 2 || erosionLevel == 3)) return middleBiomeOrBadlandsIfHot;
        if (isCoast(continentalLevel) && (erosionLevel == 3 || erosionLevel == 4)) return beachBiome;
        if (isNearToFar(continentalLevel) && erosionLevel == 4) return middleBiome;
        if (isCoast(continentalLevel) && erosionLevel == 5) return shatteredCoastBiome;
        if (isNear(continentalLevel) && erosionLevel == 5) return middleBiomeOrWindsweptSavanna;
        if (isMidToFar(continentalLevel) && erosionLevel == 5) return middleBiome;
        if (isCoast(continentalLevel) && erosionLevel == 6) return beachBiome;
        if (temperatureLevel == 0 && isNearToFar(continentalLevel) && erosionLevel == 6) return middleBiome;
        return middleBiome;
    }

    private int pickValleysBiome(int temperatureLevel, int humidityLevel, int continentalLevel, int erosionLevel, float weirdness) {
        boolean weird = weirdness >= 0f;
        boolean frozen = temperatureLevel == 0;

        if (isCoast(continentalLevel) && (erosionLevel == 0 || erosionLevel == 1)) {
            if (frozen) return weird ? FROZEN_RIVER : STONE_BEACH;
            return weird ? RIVER : STONE_BEACH;
        }
        if (isNear(continentalLevel) && (erosionLevel == 0 || erosionLevel == 1)) return frozen ? FROZEN_RIVER : RIVER;
        if (isCoastToFar(continentalLevel) && erosionLevel >= 2 && erosionLevel <= 5) return frozen ? FROZEN_RIVER : RIVER;
        if (isCoast(continentalLevel) && erosionLevel == 6) return frozen ? FROZEN_RIVER : RIVER;
        if ((temperatureLevel == 1 || temperatureLevel == 2) && isNearToFar(continentalLevel) && erosionLevel == 6) return SWAMPLAND;
        if ((temperatureLevel == 3 || temperatureLevel == 4) && isNearToFar(continentalLevel) && erosionLevel == 6) return MANGROVE_SWAMP;
        if (frozen && isNearToFar(continentalLevel) && erosionLevel == 6) return FROZEN_RIVER;
        if (isMidToFar(continentalLevel) && (erosionLevel == 0 || erosionLevel == 1)) {
            return getMiddleBiomeOrBadlandsIfHot(temperatureLevel, humidityLevel, weird);
        }
        return frozen ? FROZEN_RIVER : RIVER;
    }

    private int getMiddleBiomeOrBadlandsIfHot(int temperatureLevel, int humidityLevel, boolean weird) {
        return temperatureLevel == 4 ? getBadlandBiome(humidityLevel, weird) : getMiddleBiome(temperatureLevel, humidityLevel, weird);
    }

    private int getMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(int temperatureLevel, int humidityLevel, boolean weird) {
        if (temperatureLevel == 0) {
            return getSlopeBiome(temperatureLevel, humidityLevel, weird);
        }
        return getMiddleBiomeOrBadlandsIfHot(temperatureLevel, humidityLevel, weird);
    }

    private int maybePickWindsweptSavannaBiome(int temperatureLevel, int humidityLevel, boolean weird, int underlyingBiome) {
        return weird && temperatureLevel > 1 && humidityLevel < 4 ? SAVANNA_MUTATED : underlyingBiome;
    }

    private int pickShatteredCoastBiome(int temperatureLevel, int humidityLevel, boolean weird) {
        int beachOrMiddleBiome = weird ? getMiddleBiome(temperatureLevel, humidityLevel, true) : getBeachBiome(temperatureLevel);
        return maybePickWindsweptSavannaBiome(temperatureLevel, humidityLevel, weird, beachOrMiddleBiome);
    }

    private int getPeakBiome(int temperatureLevel, int humidityLevel, boolean weird) {
        if (temperatureLevel <= 2) {
            return weird ? FROZEN_PEAKS : JAGGED_PEAKS;
        }
        if (temperatureLevel == 3) {
            return STONY_PEAKS;
        }
        return getBadlandBiome(humidityLevel, weird);
    }

    private int getSlopeBiome(int temperatureLevel, int humidityLevel, boolean weird) {
        if (temperatureLevel >= 3) {
            return getPlateauBiome(temperatureLevel, humidityLevel, weird);
        }
        return humidityLevel <= 1 ? SNOWY_SLOPES : GROVE;
    }

    private boolean isCoast(int continentalLevel) {
        return continentalLevel == CONTINENT_COAST;
    }

    private boolean isNear(int continentalLevel) {
        return continentalLevel == CONTINENT_NEAR_INLAND;
    }

    private boolean isMid(int continentalLevel) {
        return continentalLevel == CONTINENT_MID_INLAND;
    }

    private boolean isFar(int continentalLevel) {
        return continentalLevel == CONTINENT_FAR_INLAND;
    }

    private boolean isCoastToNear(int continentalLevel) {
        return continentalLevel >= CONTINENT_COAST && continentalLevel <= CONTINENT_NEAR_INLAND;
    }

    private boolean isCoastToFar(int continentalLevel) {
        return continentalLevel >= CONTINENT_COAST && continentalLevel <= CONTINENT_FAR_INLAND;
    }

    private boolean isNearToMid(int continentalLevel) {
        return continentalLevel >= CONTINENT_NEAR_INLAND && continentalLevel <= CONTINENT_MID_INLAND;
    }

    private boolean isNearToFar(int continentalLevel) {
        return continentalLevel >= CONTINENT_NEAR_INLAND && continentalLevel <= CONTINENT_FAR_INLAND;
    }

    private boolean isMidToFar(int continentalLevel) {
        return continentalLevel >= CONTINENT_MID_INLAND && continentalLevel <= CONTINENT_FAR_INLAND;
    }

    protected int getBeachBiome(int temperatureLevel) {
        return switch (temperatureLevel) {
            case 0 -> COLD_BEACH;
            case 1, 2, 3 -> BEACH;
            default -> DESERT;
        };
    }

    protected int getBadlandBiome(int humidityLevel, boolean weird) {
        return switch (humidityLevel) {
            case 0, 1 -> weird ? MESA_BRYCE : MESA;
            case 2 -> MESA;
            default -> MESA_PLATEAU_STONE;
        };
    }

    protected int getMiddleBiome(int temperatureLevel, int humidityLevel, boolean weird) {
        return switch (temperatureLevel) {
            case 0 -> switch (humidityLevel) {
                case 0 -> weird ? ICE_PLAINS_SPIKES : ICE_PLAINS;
                case 1 -> ICE_PLAINS;
                case 2 -> weird ? COLD_TAIGA : ICE_PLAINS;
                case 3 -> COLD_TAIGA;
                default -> TAIGA;
            };
            case 1 -> switch (humidityLevel) {
                case 0, 1 -> PLAINS;
                case 2 -> FOREST;
                case 3 -> TAIGA;
                default -> weird ? MEGA_TAIGA : REDWOOD_TAIGA_MUTATED;
            };
            case 2 -> switch (humidityLevel) {
                case 0 -> weird ? SUNFLOWER_PLAINS : FLOWER_FOREST;
                case 1 -> PLAINS;
                case 2 -> FOREST;
                case 3 -> weird ? BIRCH_FOREST_MUTATED : BIRCH_FOREST;
                default -> ROOFED_FOREST;
            };
            case 3 -> switch (humidityLevel) {
                case 0, 1 -> SAVANNA;
                case 2 -> weird ? PLAINS : FOREST;
                case 3 -> weird ? JUNGLE_EDGE : JUNGLE;
                default -> weird ? BAMBOO_JUNGLE : JUNGLE;
            };
            default -> DESERT;
        };
    }

    protected int getPlateauBiome(int temperatureLevel, int humidityLevel, boolean weird) {
        return switch (temperatureLevel) {
            case 0 -> switch (humidityLevel) {
                case 0 -> weird ? ICE_PLAINS_SPIKES : ICE_PLAINS;
                case 1, 2 -> ICE_PLAINS;
                default -> COLD_TAIGA;
            };
            case 1 -> switch (humidityLevel) {
                case 0 -> weird ? CHERRY_GROVE : MEADOW;
                case 1 -> MEADOW;
                case 2 -> weird ? MEADOW : FOREST;
                case 3 -> weird ? MEADOW : TAIGA;
                default -> weird ? MEGA_TAIGA : REDWOOD_TAIGA_MUTATED;
            };
            case 2 -> switch (humidityLevel) {
                case 0, 1 -> weird ? CHERRY_GROVE : MEADOW;
                case 2 -> weird ? FOREST : MEADOW;
                case 3 -> weird ? BIRCH_FOREST : MEADOW;
                default -> PALE_GARDEN;
            };
            case 3 -> switch (humidityLevel) {
                case 0, 1 -> SAVANNA_PLATEAU;
                case 2, 3 -> FOREST;
                default -> JUNGLE;
            };
            default -> switch (humidityLevel) {
                case 0, 1 -> weird ? MESA_BRYCE : MESA;
                case 2 -> MESA;
                default -> MESA_PLATEAU_STONE;
            };
        };
    }

    protected int getShatteredBiome(int temperatureLevel, int humidityLevel, boolean weird) {
        return switch (temperatureLevel) {
            case 0, 1 -> switch (humidityLevel) {
                case 0, 1 -> EXTREME_HILLS_MUTATED;
                case 2 -> EXTREME_HILLS;
                default -> EXTREME_HILLS_PLUS_TREES;
            };
            case 2 -> switch (humidityLevel) {
                case 0, 1, 2 -> EXTREME_HILLS;
                default -> EXTREME_HILLS_PLUS_TREES;
            };
            case 3 -> switch (humidityLevel) {
                case 0, 1 -> SAVANNA;
                case 2 -> weird ? PLAINS : FOREST;
                case 3 -> weird ? JUNGLE_EDGE : JUNGLE;
                default -> weird ? BAMBOO_JUNGLE : JUNGLE;
            };
            default -> DESERT;
        };
    }

}
