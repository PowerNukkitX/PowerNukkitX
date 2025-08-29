package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.noise.f.PerlinF;
import cn.nukkit.level.generator.noise.f.vanilla.NormalNoise;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.NukkitRandom;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import static cn.nukkit.level.biome.BiomeID.*;

public class OverworldBiomePicker extends BiomePicker {

    public static final int CONTINENT_MUSHROOM = 0;
    public static final int CONTINENT_DEEP_OCEAN = 1;
    public static final int CONTINENT_OCEAN = 2;
    public static final int CONTINENT_COAST = 3;
    public static final int CONTINENT_NEAR_INLAND = 4;
    public static final int CONTINENT_MID_INLAND = 5;
    public static final int CONTINENT_FAR_INLAND = 6;

    private final NormalNoise continentalNoise;
    private final NormalNoise temperatureNoise;
    private final NormalNoise humidityNoise;
    private final NormalNoise erosionNoise;
    private final NormalNoise weirdnessNoise;

    public OverworldBiomePicker(NukkitRandom random) {
        super(random);
        continentalNoise = new NormalNoise((NukkitRandom) random.fork(), -9, new float[]{ 1, 1, 2, 2, 2, 1, 1, 1, 1 });
        temperatureNoise = new NormalNoise((NukkitRandom) random.fork(), -10 , new float[]{ 1.5f, 0, 1, 0, 0, 0 });
        humidityNoise = new NormalNoise((NukkitRandom) random.fork(), -8 , new float[]{ 1, 1, 0, 0, 0, 0 });
        erosionNoise = new NormalNoise((NukkitRandom) random.fork(), -9 , new float[]{ 1, 1, 0, 1, 1 });
        weirdnessNoise = new NormalNoise((NukkitRandom) random.fork(), -7 , new float[]{ 1, 2, 1, 0, 0, 0});
    }

    @Override
    public int pick(int x, int y, int z) {

        float continental = NukkitMath.remap(continentalNoise.getValue(x, y, z), -2, 2.4051785f, -1.2f, 1);
        float temperature = NukkitMath.remapNormalized(temperatureNoise.getValue(x, y, z), -1.900178f, 1.8860668f);
        float humidity = NukkitMath.remapNormalized(humidityNoise.getValue(x, y, z), -1.3698664f, 1.413946f);
        float erosion = NukkitMath.remapNormalized(erosionNoise.getValue(x, y, z), -1.8172232f, 1.8637897f);
        float weirdness = NukkitMath.remapNormalized(weirdnessNoise.getValue(x, y, z), -2.2341442f, 2.194725f);
        float pv = NukkitMath.remapNormalized(1f-Math.abs(3*Math.abs(weirdness))-2f, -4, -1);

        int continentalLevel = continental < -1.05f ? 0 : (continental < -0.455f ? 1 : (continental < -0.19 ? 2 : (continental < -0.11 ? 3 : (continental < 0.03 ? 4 : (continental < 0.3 ? 5 : 6)))));
        int temperatureLevel = temperature < -0.45f ? 0 : (temperature < -0.35f ? 1 : (temperature < 0.2f ? 2 : (temperature < 0.55f ? 3 : 4)));
        int humidityLevel = humidity < -0.35f ? 0 : (humidity < -0.1f ? 1 : (humidity < 0.1f ? 2 : (humidity < 0.3f ? 3 : 4)));
        int erosionLevel = erosion < -0.78f ? 0 : (erosion < -0.375f ? 1 : (erosion < -0.2225f ? 2 : (erosion < 0.05f ? 3 : (erosion < 0.45f ? 4 : (erosion < 0.55f ? 5 : 6)))));
        int pvLevel = pv < -0.85f ? 0 : (pv < -0.2f ? 1 : (pv < 0.2f ? 2 : (pv < 0.7f ? 3 : 4)));
        boolean weird = weirdness > 0;

        int biome = switch (continentalLevel){
            case CONTINENT_MUSHROOM -> MUSHROOM_ISLAND;
            case CONTINENT_OCEAN,
                 CONTINENT_DEEP_OCEAN -> getNonInlandBiome(temperatureLevel, continentalLevel);
            default -> getInlandBiome(pvLevel, erosionLevel, humidityLevel, temperatureLevel, weird, continentalLevel);
        };

        return biome;
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

    protected int getInlandBiome(int pvLevel, int erosionLevel, int humidityLevel, int temperatureLevel, boolean weird, int contnentalLevel) {
        return switch (pvLevel) {
            case 0 -> switch (contnentalLevel) {
                case CONTINENT_COAST -> temperatureLevel == 0 ? FROZEN_RIVER : RIVER;
                case CONTINENT_NEAR_INLAND -> switch (erosionLevel) {
                    default -> temperatureLevel == 0 ? FROZEN_RIVER : RIVER;
                    case 6 -> switch (temperatureLevel) {
                        case 0 -> FROZEN_RIVER;
                        case 1, 2 -> SWAMPLAND;
                        default -> MANGROVE_SWAMP;
                    };
                };
                default -> switch (erosionLevel) {
                    case 0, 1 -> temperatureLevel == 4 ? getBadlandBiome(humidityLevel, weird) : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 2, 3, 4, 5 -> temperatureLevel == 0 ? FROZEN_RIVER : RIVER;
                    default -> switch (temperatureLevel) {
                        case 0 -> FROZEN_RIVER;
                        case 1, 2 -> SWAMPLAND;
                        default -> MANGROVE_SWAMP;
                    };
                };
            };
            case 1 -> switch (contnentalLevel) {
                case CONTINENT_COAST -> switch (erosionLevel) {
                    case 0, 1, 2 -> STONE_BEACH;
                    case 5 -> weird && temperatureLevel < 5 && temperatureLevel > 1 && humidityLevel < 4 ? SAVANNA_MUTATED : (weird ? getMiddleBiome(temperatureLevel, humidityLevel, true) : getBeachBiome(temperatureLevel));
                    default -> getBeachBiome(temperatureLevel);
                };
                case CONTINENT_NEAR_INLAND -> switch (erosionLevel) {
                    case 0, 1 -> temperatureLevel == 4 ? getBadlandBiome(humidityLevel, weird) : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 2, 3, 4 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 5 -> weird && temperatureLevel < 5 && temperatureLevel > 1 && humidityLevel < 4 ? SAVANNA_MUTATED : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    default -> switch (temperatureLevel) {
                        case 0 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        case 1, 2 -> SWAMPLAND;
                        default -> MANGROVE_SWAMP;
                    };
                };
                default -> switch (erosionLevel) {
                    case 0, 1 -> switch (temperatureLevel) {
                        case 0 -> switch (humidityLevel) {
                            case 0, 1 -> SNOWY_SLOPES;
                            default -> GROVE;
                        };
                        case 1, 2, 3 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        default -> getBadlandBiome(humidityLevel, weird);
                    };
                    case 2, 3 -> temperatureLevel == 4 ? getBadlandBiome(humidityLevel, weird) : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 4, 5 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    default -> switch (temperatureLevel) {
                        case 0 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        case 1, 2 -> SWAMPLAND;
                        default -> MANGROVE_SWAMP;
                    };
                };
            };
            case 2 -> switch (contnentalLevel) {
                case CONTINENT_COAST -> switch (erosionLevel) {
                    case 0, 1, 2 -> STONE_BEACH;
                    case 3 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 5 -> weird ? temperatureLevel < 5 && temperatureLevel > 1 && humidityLevel < 4 ? SAVANNA_MUTATED : getMiddleBiome(temperatureLevel, humidityLevel, weird) : getBeachBiome(temperatureLevel);
                    default -> weird ? getMiddleBiome(temperatureLevel, humidityLevel, weird) : getBeachBiome(temperatureLevel);
                };
                case CONTINENT_NEAR_INLAND -> switch (erosionLevel) {
                    case 0 -> switch (temperatureLevel) {
                        case 3,4 -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                        default -> switch (humidityLevel) {
                            case 0, 1 -> SNOWY_SLOPES;
                            default -> GROVE;
                        };
                    };
                    case 1 -> switch (temperatureLevel) {
                        case 0 -> switch (humidityLevel) {
                            case 0, 1 -> SNOWY_SLOPES;
                            default -> GROVE;
                        };
                        case 4 -> getBadlandBiome(humidityLevel, weird);
                        default -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    };
                    case 2, 3, 4 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 5 -> weird && temperatureLevel < 5 && temperatureLevel > 1 && humidityLevel < 4 ? SAVANNA_MUTATED : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    default -> switch (temperatureLevel) {
                        case 0 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        case 1, 2 -> SWAMPLAND;
                        default -> MANGROVE_SWAMP;
                    };
                };
                case CONTINENT_MID_INLAND -> switch (erosionLevel) {
                    case 0 -> switch (temperatureLevel) {
                        case 3,4 -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                        default -> switch (humidityLevel) {
                            case 0, 1 -> SNOWY_SLOPES;
                            default -> GROVE;
                        };
                    };
                    case 1 -> switch (temperatureLevel) {
                        case 0 -> switch (humidityLevel) {
                            case 0, 1 -> SNOWY_SLOPES;
                            default -> GROVE;
                        };
                        case 4 -> getBadlandBiome(humidityLevel, weird);
                        default -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    };
                    case 2, 3 -> temperatureLevel == 4 ? getBadlandBiome(humidityLevel, weird) : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 4 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 5 -> getShatteredBiome(temperatureLevel, humidityLevel, weird);
                    default -> switch (temperatureLevel) {
                        case 0 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        case 1, 2 -> SWAMPLAND;
                        default -> MANGROVE_SWAMP;
                    };
                };
                default -> switch (erosionLevel) {
                    case 0 -> switch (temperatureLevel) {
                        case 3,4 -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                        default -> switch (humidityLevel) {
                            case 0, 1 -> SNOWY_SLOPES;
                            default -> GROVE;
                        };
                    };
                    case 1 -> switch (temperatureLevel) {
                        case 0 -> switch (humidityLevel) {
                            case 0, 1 -> SNOWY_SLOPES;
                            default -> GROVE;
                        };
                        default -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                    };
                    case 2 -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                    case 3 -> temperatureLevel == 4 ? getBadlandBiome(humidityLevel, weird) : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 4 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case 5 -> getShatteredBiome(temperatureLevel, humidityLevel, weird);
                    default -> switch (temperatureLevel) {
                        case 0 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        case 1, 2 -> SWAMPLAND;
                        default -> MANGROVE_SWAMP;
                    };
                };
            };
            case 3 -> switch (erosionLevel) {
                case 5 -> switch (contnentalLevel) {
                    case CONTINENT_COAST,
                         CONTINENT_NEAR_INLAND -> weird && temperatureLevel < 5 && temperatureLevel > 1 && humidityLevel < 4 ? SAVANNA_MUTATED : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    default -> getShatteredBiome(temperatureLevel, humidityLevel, weird);
                };
                case 6 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                default -> switch (contnentalLevel) {
                    case CONTINENT_COAST -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    case CONTINENT_NEAR_INLAND -> switch (erosionLevel) {
                        case 0 -> switch (temperatureLevel) {
                            case 3, 4 -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                            default -> switch (humidityLevel) {
                                case 0, 1 -> SNOWY_SLOPES;
                                default -> GROVE;
                            };
                        };
                        case 1 -> switch (temperatureLevel) {
                            case 0 -> switch (humidityLevel) {
                                case 0, 1 -> SNOWY_SLOPES;
                                default -> GROVE;
                            };
                            case 4 -> getBadlandBiome(humidityLevel, weird);
                            default -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        };
                        default -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    };
                    default -> switch (erosionLevel) {
                        case 0 -> switch (temperatureLevel) {
                            case 0, 1, 2 -> weird ? FROZEN_PEAKS : JAGGED_PEAKS;
                            case 3 -> STONY_PEAKS;
                            default -> getBadlandBiome(humidityLevel, weird);
                        };
                        case 1 -> switch (temperatureLevel) {
                            case 3, 4 -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                            default -> humidityLevel > 1 ? GROVE : SNOWY_SLOPES;
                        };
                        case 2, 3 -> switch (contnentalLevel) {
                            case CONTINENT_MID_INLAND -> erosionLevel == 2 ? getPlateauBiome(temperatureLevel, humidityLevel, weird) : (temperatureLevel == 4 ? getBadlandBiome(humidityLevel, weird) : getMiddleBiome(temperatureLevel, humidityLevel, weird));
                            default -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                        };
                        default -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                    };
                };
            };
            default -> switch (erosionLevel) {
                case 6 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                default -> switch (contnentalLevel) {
                    case CONTINENT_COAST,
                         CONTINENT_NEAR_INLAND -> switch (erosionLevel) {
                        case 0 -> switch (temperatureLevel) {
                            case 3 -> STONY_PEAKS;
                            case 4 -> getBadlandBiome(humidityLevel, weird);
                            default -> weird ? FROZEN_PEAKS : JAGGED_PEAKS;
                        };
                        case 1 -> switch (temperatureLevel) {
                            case 0 -> humidityLevel < 2 ? SNOWY_SLOPES : GROVE;
                            case 4 -> getBadlandBiome(humidityLevel, weird);
                            default -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        };
                        case 2, 3, 4 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        default -> weird && temperatureLevel > 1 && temperatureLevel < 5 && humidityLevel < 4 ? SAVANNA_MUTATED : getShatteredBiome(temperatureLevel, humidityLevel, weird);
                    };
                    default -> switch (erosionLevel) {
                        case 0, 1 -> switch (temperatureLevel) {
                            case 0, 1, 2 -> weird ? FROZEN_PEAKS : JAGGED_PEAKS;
                            case 3 -> STONY_PEAKS;
                            default -> getBadlandBiome(humidityLevel, weird);
                        };
                        case 2, 3 -> switch (contnentalLevel) {
                            case CONTINENT_MID_INLAND -> switch (erosionLevel) {
                                case 2 -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                                default -> temperatureLevel == 4 ? getBadlandBiome(humidityLevel, weird) : getMiddleBiome(temperatureLevel, humidityLevel, weird);
                            };
                            default -> getPlateauBiome(temperatureLevel, humidityLevel, weird);
                        };
                        case 4 -> getMiddleBiome(temperatureLevel, humidityLevel, weird);
                        default -> getShatteredBiome(temperatureLevel, humidityLevel, weird);
                    };
                };
            };
        };
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
