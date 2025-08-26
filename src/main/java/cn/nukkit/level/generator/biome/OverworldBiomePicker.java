package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.utils.random.NukkitRandom;

import static cn.nukkit.level.biome.BiomeID.*;

public class OverworldBiomePicker extends BiomePicker {

    public OverworldBiomePicker(NukkitRandom random) {
        super(random);
    }

    @Override
    public int pick(int x, int z) {
        SimplexF oceanF = new SimplexF(random.identical(), 6f, 2 / 4f, 1 / 2048f);
        SimplexF riverF = new SimplexF(random.identical(), 6f, 2 / 4f, 1 / 1024f);
        SimplexF temperatureF = new SimplexF(random.identical(), 2F, 1F / 8F, 1F / 2048f);
        SimplexF rainfallF = new SimplexF(random.identical(), 2F, 1F / 8F, 1F / 2048f);
        SimplexF hillsF = new SimplexF(random.identical(), 2f, 2 / 4f, 1 / 2048f);
        float noiseOcean = oceanF.noise2D(x, z, true);
        float noiseRiver = riverF.noise2D(x, z, true);
        float temperature = temperatureF.noise2D(x, z, true);
        float rainfall = rainfallF.noise2D(x, z, true);
        float hills = hillsF.noise2D(x, z, true);
        int biome;
        if (noiseOcean < -0.15f) {
            if (noiseOcean < -0.91f) {
                if (noiseOcean < -0.92f) {
                    biome = MUSHROOM_ISLAND;
                } else {
                    biome = MUSHROOM_ISLAND_SHORE;
                }
            } else {
                if (rainfall < 0f) {
                    if (temperature < -0.4f) {
                        biome = FROZEN_OCEAN;
                    } else if (temperature < 0f) {
                        biome = COLD_OCEAN;
                    } else if (temperature < 0.35f) {
                        biome = OCEAN;
                    } else if (temperature < 0.6f) {
                        biome = LUKEWARM_OCEAN;
                    } else {
                        biome = WARM_OCEAN;
                    }
                } else {
                    if (temperature < -0.4f) {
                        biome = DEEP_FROZEN_OCEAN;
                    } else if (temperature < 0f) {
                        biome = DEEP_COLD_OCEAN;
                    } else if (temperature < 0.4f) {
                        biome = DEEP_OCEAN;
                    } else {
                        biome = DEEP_LUKEWARM_OCEAN;
                    }
                }
            }
        } else if (Math.abs(noiseRiver) < 0.04f) {
            if (temperature < -0.3f) {
                biome = FROZEN_RIVER;
            } else {
                biome = RIVER;
            }
        } else {
            if (temperature < -0.379f) {
                //freezing
                if (noiseOcean < -0.12f) {
                    biome = COLD_BEACH;
                } else if (rainfall < 0f) {
                    if (hills < -0.1f) {
                        biome = COLD_TAIGA;
                    } else if (hills < 0.5f) {
                        biome = COLD_TAIGA_HILLS;
                    } else {
                        biome = COLD_TAIGA_MUTATED;
                    }
                } else {
                    if (hills < 0.7f) {
                        biome = ICE_PLAINS;
                    } else {
                        biome = ICE_PLAINS_SPIKES;
                    }
                }
            } else if (noiseOcean < -0.12f) {
                biome = BEACH;
            } else if (temperature < 0f) {
                //cold
                if (hills < 0.2f) {
                    if (rainfall < -0.5f) {
                        biome = EXTREME_HILLS_MUTATED;
                    } else if (rainfall > 0.5f) {
                        biome = EXTREME_HILLS_PLUS_TREES_MUTATED;
                    } else if (rainfall < 0f) {
                        biome = EXTREME_HILLS;
                    } else {
                        biome = EXTREME_HILLS_PLUS_TREES;
                    }
                } else {
                    if (rainfall < -0.6) {
                        biome = MEGA_TAIGA;
                    } else if (rainfall > 0.6) {
                        biome = MEGA_TAIGA_HILLS;
                    } else if (rainfall < 0.2f) {
                        biome = TAIGA;
                    } else {
                        biome = TAIGA_MUTATED;
                    }
                }
            } else if (temperature < 0.5f) {
                //normal
                if (temperature < 0.25f) {
                    if (rainfall < 0f) {
                        if (noiseOcean < 0f) {
                            biome = SUNFLOWER_PLAINS;
                        } else {
                            biome = PLAINS;
                        }
                    } else if (rainfall < 0.25f) {
                        if (noiseOcean < 0f) {
                            biome = FLOWER_FOREST;
                        } else {
                            biome = FOREST;
                        }
                    } else {
                        if (noiseOcean < 0f) {
                            biome = BIRCH_FOREST_MUTATED;
                        } else {
                            biome = BIRCH_FOREST;
                        }
                    }
                } else {
                    if (rainfall < -0.2f) {
                        if (noiseOcean < 0f) {
                            biome = SWAMPLAND_MUTATED;
                        } else {
                            biome = SWAMPLAND;
                        }
                    } else if (rainfall > 0.065f && rainfall <= 0.1f) {
                        if (hills < 0f) {
                            biome = BAMBOO_JUNGLE_HILLS;
                        } else {
                            biome = BAMBOO_JUNGLE;
                        } // TODO: 2022/2/2 修复与原版行为不一致的竹林生成
                    } else if (rainfall > 0.1f) {
                        if (noiseOcean < 0.155f) {
                            biome = JUNGLE_MUTATED;
                        } else {
                            biome = JUNGLE;
                        }
                    } else {
                        if (noiseOcean < 0f) {
                            biome = ROOFED_FOREST_MUTATED;
                        } else {
                            biome = ROOFED_FOREST;
                        }
                    }
                }
            } else {
                //hot
                if (rainfall < 0f) {
                    if (noiseOcean < 0f) {
                        biome = DESERT_MUTATED;
                    } else if (hills < 0f) {
                        biome = DESERT_HILLS;
                    } else {
                        biome = DESERT;
                    }
                } else if (rainfall > 0.4f) {
                    if (noiseOcean < 0.155f) {
                        if (hills < 0f) {
                            biome = SAVANNA_PLATEAU_MUTATED;
                        } else {
                            biome = SAVANNA_MUTATED;
                        }
                    } else {
                        if (hills < 0f) {
                            biome = SAVANNA_PLATEAU;
                        } else {
                            biome = SAVANNA;
                        }
                    }
                } else {
                    if (noiseOcean < 0f) {
                        if (hills < 0f) {
                            biome = MESA_PLATEAU_STONE;
                        } else {
                            biome = MESA_PLATEAU_STONE_MUTATED;
                        }
                    } else if (hills < 0f) {
                        if (noiseOcean < 0.2f) {
                            biome = MESA_PLATEAU_MUTATED;
                        } else {
                            biome = MESA_PLATEAU;
                        }
                    } else {
                        if (noiseOcean < 0.1f) {
                            biome = MESA_BRYCE;
                        } else {
                            biome = MESA;
                        }
                    }
                }
            }
        }
        return biome;
    }

}
