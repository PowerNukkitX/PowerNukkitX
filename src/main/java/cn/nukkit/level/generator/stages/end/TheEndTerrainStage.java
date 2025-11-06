package cn.nukkit.level.generator.stages.end;

import cn.nukkit.block.BlockEndStone;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.noise.d.NoiseGeneratorOctavesD;
import cn.nukkit.level.generator.noise.d.NoiseGeneratorSimplexD;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;


public class TheEndTerrainStage extends GenerateStage {

    public static final String NAME = "the_end_terrain";

    private static final BlockState STATE_END_STONE = BlockEndStone.PROPERTIES.getDefaultState();
    private static final double coordinateScale = 684.412;
    private static final double detailNoiseScaleX = 80;
    private static final double detailNoiseScaleZ = 80;
    double[] detailNoise;
    private NoiseGeneratorOctavesD roughnessNoiseOctaves;
    private NoiseGeneratorOctavesD roughnessNoiseOctaves2;
    private NoiseGeneratorOctavesD detailNoiseOctaves;
    private NoiseGeneratorSimplexD islandNoise;

    private NukkitRandom random;

    @Override
    public void apply(ChunkGenerateContext context) {
        Level level = context.getLevel();
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        if(random == null) random = new NukkitRandom(level.getSeed());
        if(roughnessNoiseOctaves == null) roughnessNoiseOctaves = new NoiseGeneratorOctavesD(random.identical(), 16);
        if(roughnessNoiseOctaves2 == null) roughnessNoiseOctaves2 = new NoiseGeneratorOctavesD(random.identical(), 16);
        if(detailNoise == null) detailNoiseOctaves = new NoiseGeneratorOctavesD(random.identical(), 8);
        if(islandNoise == null) islandNoise = new NoiseGeneratorSimplexD(random.identical());

        int densityX = chunkX << 1;
        int densityZ = chunkZ << 1;
        double[][][] density = new double[3][3][33];
        double[] detailNoise = detailNoiseOctaves.generateNoiseOctaves(densityX, 0, densityZ, 3, 33,
                3, (coordinateScale * 2) / detailNoiseScaleX, 4.277575000000001,
                (coordinateScale * 2) / detailNoiseScaleZ);
        double[] roughnessNoise = roughnessNoiseOctaves.generateNoiseOctaves(densityX, 0,
                densityZ, 3, 33, 3, coordinateScale * 2, coordinateScale, coordinateScale * 2);
        double[] roughnessNoise2 = roughnessNoiseOctaves2.generateNoiseOctaves(densityX, 0,
                densityZ, 3, 33, 3, coordinateScale * 2, coordinateScale, coordinateScale * 2);

        int index = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float noiseHeight = getIslandHeight(chunkX, chunkZ, i, j, islandNoise);
                for (int k = 0; k < 33; k++) {
                    double noiseR = roughnessNoise[index] / 512d;
                    double noiseR2 = roughnessNoise2[index] / 512d;
                    double noiseD = (detailNoise[index] / 10d + 1d) / 2d;
                    // linear interpolation
                    double dens = noiseD < 0 ? noiseR : noiseD > 1 ? noiseR2 : noiseR + (noiseR2 - noiseR) * noiseD;
                    dens = (dens - 8d) + (double) noiseHeight;
                    index++;
                    double lowering;
                    if (k < 8) {
                        lowering = (float) (8 - k) / 7;
                        dens = dens * (1d - lowering) + lowering * -30d;
                    } else if (k > 33 / 2 - 2) {
                        lowering = (float) (k - ((33 / 2) - 2)) / 64d;
                        lowering = NukkitMath.clamp(lowering, 0, 1);
                        dens = dens * (1d - lowering) + lowering * -3000d;
                    }
                    density[i][j][k] = dens;
                }
            }
        }

        for (int i = 0; i < 3 - 1; i++) {
            for (int j = 0; j < 3 - 1; j++) {
                for (int k = 0; k < 33 - 1; k++) {
                    double d1 = density[i][j][k];
                    double d2 = density[i + 1][j][k];
                    double d3 = density[i][j + 1][k];
                    double d4 = density[i + 1][j + 1][k];
                    double d5 = (density[i][j][k + 1] - d1) / 4;
                    double d6 = (density[i + 1][j][k + 1] - d2) / 4;
                    double d7 = (density[i][j + 1][k + 1] - d3) / 4;
                    double d8 = (density[i + 1][j + 1][k + 1] - d4) / 4;

                    for (int l = 0; l < 4; l++) {
                        double d9 = d1;
                        double d10 = d3;
                        for (int m = 0; m < 8; m++) {
                            double dens = d9;
                            for (int n = 0; n < 8; n++) {
                                // any density > 0 is ground, any density <= 0 is air.
                                if (dens > 0) {
                                    chunk.setBlockState(m + (i << 3), l + (k << 2), n + (j << 3), STATE_END_STONE);
                                }
                                // interpolation along z
                                dens += (d10 - d9) / 8;
                            }
                            // interpolation along x
                            d9 += (d2 - d1) / 8;
                            // interpolate along z
                            d10 += (d4 - d3) / 8;
                        }
                        // interpolation along y
                        d1 += d5;
                        d3 += d7;
                        d2 += d6;
                        d4 += d8;
                    }
                }
            }
        }
        chunk.recalculateHeightMap();
        chunk.setChunkState(ChunkState.GENERATED);
    }

    public static float getIslandHeight(int chunkX, int chunkZ, int x, int z, NoiseGeneratorSimplexD islandNoise) {
        float x1 = (float) (chunkX * 2 + x);
        float z1 = (float) (chunkZ * 2 + z);
        float islandHeight1 = NukkitMath.clamp(100f - MathHelper.sqrt((x1 * x1) + (z1 * z1)) * 8f, -100f, 80f);

        for (int i = -12; i <= 12; i++) {
            for (int j = -12; j <= 12; j++) {
                long x2 = chunkX + i;
                long z2 = chunkZ + j;
                if ((x2 * x2) + (z2 * z2) > 4096L
                        && islandNoise.getValue((double) x2, (double) z2) < (double) -0.9f) {
                    x1 = (float) (x - i * 2);
                    z1 = (float) (z - j * 2);
                    float islandHeight2 = 100f - MathHelper.sqrt((x1 * x1) + (z1 * z1))
                            * ((Math.abs((float) x2) * 3439f + Math.abs((float) z2) * 147f) % 13f + 9f);
                    islandHeight2 = NukkitMath.clamp(islandHeight2, -100f, 80f);
                    islandHeight1 = Math.max(islandHeight1, islandHeight2);
                }
            }
        }

        return islandHeight1;
    }


    @Override
    public String name() {
        return NAME;
    }
}
