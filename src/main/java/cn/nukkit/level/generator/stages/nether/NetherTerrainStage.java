package cn.nukkit.level.generator.stages.nether;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;

public class NetherTerrainStage extends GenerateStage {

    public static final String NAME = "nether_terrain";
    public static final int LAVA_LEVEL = 31;

    private final static BlockState BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();
    private final static BlockState NETHERRACK = BlockNetherrack.PROPERTIES.getDefaultState();
    private final static BlockState BASALT = BlockBasalt.PROPERTIES.getDefaultState();
    private final static BlockState BLACKSTONE = BlockBlackstone.PROPERTIES.getDefaultState();
    private final static BlockState GRAVEL = BlockGravel.PROPERTIES.getDefaultState();
    private final static BlockState SOULSAND = BlockSoulSand.PROPERTIES.getDefaultState();
    private final static BlockState SOULSOIL = BlockSoulSoil.PROPERTIES.getDefaultState();
    private final static BlockState WARPED_WART = BlockWarpedWartBlock.PROPERTIES.getDefaultState();
    private final static BlockState WARPED_NYLIUM = BlockWarpedNylium.PROPERTIES.getDefaultState();
    private final static BlockState NETHERWART_BLOCK = BlockNetherWartBlock.PROPERTIES.getDefaultState();
    private final static BlockState CRIMSON_NYLIUM = BlockCrimsonNylium.PROPERTIES.getDefaultState();
    private final static BlockState LAVA = BlockLava.PROPERTIES.getDefaultState();

    private SimplexNoise surfaceNoise;
    private SimplexNoise patchNoise;
    private SimplexNoise soulsandNoise;
    private SimplexNoise netherStateNoise;
    private SimplexNoise netherwartNoise;

    private final ThreadLocal<NukkitRandom> random = ThreadLocal.withInitial(NukkitRandom::new);

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        Level level = chunk.getLevel();
        NukkitRandom random = this.random.get();
        random.setSeed(level.getSeed());
        if(surfaceNoise == null) surfaceNoise = new SimplexNoise(random.identical(), -6, new float[]{1f, 1f, 1f});
        if(patchNoise == null) patchNoise = new SimplexNoise(random.identical(), -5, new float[]{ 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.013333333333333334f });
        if(soulsandNoise == null) soulsandNoise = new SimplexNoise(random.identical(), -8, new float[]{  1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.013333333333333334f });
        if(netherStateNoise == null) netherStateNoise = new SimplexNoise(random.identical(), -4, new float[]{ 1.0f });
        if(netherwartNoise == null) netherwartNoise = new SimplexNoise(random.identical(), -3, new float[]{ 1.0f, 0.0f, 0.0f, 0.9f });


        try {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    int nx = x + baseX;
                    int nz = z + baseZ;
                    int biomeId = chunk.getBiomeId(x, 0, z);

                    chunk.setBlockState(x, 0, z, BEDROCK);

                    for (int y = 126; y < 127; ++y) {
                        chunk.setBlockState(x, y, z, NETHERRACK);
                    }
                    chunk.setBlockState(x, 127, z, BEDROCK);
                    for (int y = 1; y < 127; ++y) {
                        if (getNoise(baseX | x, y, baseZ | z) > 0) {
                            chunk.setBlockState(x, y, z, NETHERRACK);
                        } else if (y <= LAVA_LEVEL) {
                            chunk.setBlockState(x, y, z, LAVA);
                            chunk.setBlockLight(x, y + 1, z, 15);
                        }
                    }
                    for (int y = 1; y < 127; ++y) {
                        switch (biomeId) {
                            case BiomeID.BASALT_DELTAS -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK) {
                                    if (isCeil(chunk, x, y, z)) {
                                        chunk.setBlockState(x, y, z, BASALT);
                                        continue;
                                    } else if (chunk.getBlockState(x, y + 1, z) == BlockAir.STATE) {
                                        if (netherStateNoise.getValue(nx, y, nz) >= 0 || (y <= 35 && y >= 30 && patchNoise.getValue(nx, y, nz) >= -0.012f)) {
                                            chunk.setBlockState(x, y, z, GRAVEL);
                                            continue;
                                        }
                                    }
                                    if(isTop(chunk, x, y, z) || isCeil(chunk, x, y, z))
                                        chunk.setBlockState(x, y, z, BLACKSTONE);
                                }
                            }
                            case BiomeID.SOULSAND_VALLEY -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK) {
                                    if (isCeil(chunk, x, y, z)) {
                                        chunk.setBlockState(x, y, z, netherStateNoise.getValue(nx, y, nz) >= 0 ? SOULSAND : SOULSOIL);
                                    } else if (isTop(chunk, x, y, z)) {
                                        if (netherStateNoise.getValue(nx, y, nz) >= 0 || (y <= 35 && y >= 30 && patchNoise.getValue(nx, y, nz) >= -0.012f)) {
                                            chunk.setBlockState(x, y, z, SOULSAND);
                                        } else chunk.setBlockState(x, y, z, SOULSOIL);
                                    }
                                }
                            }
                            case BiomeID.WARPED_FOREST -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK) {
                                    if (chunk.getBlockState(x, y + 1, z) == BlockAir.STATE) {
                                        if (y > 31 && netherStateNoise.getValue(nx, y, nz) <= 0f) {
                                            chunk.setBlockState(x, y, z, netherwartNoise.getValue(nx, y, nz) >= 1.17f ? WARPED_WART : WARPED_NYLIUM);
                                        }
                                    }
                                }
                            }
                            case BiomeID.CRIMSON_FOREST -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK) {
                                    if (chunk.getBlockState(x, y + 1, z) == BlockAir.STATE) {
                                        if (y > 31 && netherStateNoise.getValue(nx, y, nz) <= 0.54f) {
                                            chunk.setBlockState(x, y, z, netherwartNoise.getValue(nx, y, nz) >= 1.17f ? NETHERWART_BLOCK : CRIMSON_NYLIUM);
                                        }
                                    }
                                }
                            }
                            case BiomeID.HELL -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK) {
                                    if (isTop(chunk, x, y, z)) {
                                        if (y > 31 && y < 35 && soulsandNoise.getValue(nx, y, nz) >= -0.012f) {
                                            chunk.setBlockState(x, y, z, GRAVEL);
                                            continue;
                                        }
                                        if (y <= 35 && y >= 30 && soulsandNoise.getValue(nx, y, nz) >= -0.012f) {
                                            chunk.setBlockState(x, y, z, SOULSAND);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (int i = 0; i < random.nextBoundedInt(6); i++) {
                        if (chunk.getBlockState(x, 126 - i, z) == NETHERRACK && chunk.getBlockState(x, 125 - i, z) != BlockAir.STATE)
                            chunk.setBlockState(x, 126 - i, z, BEDROCK);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        chunk.recalculateHeightMap();
        chunk.setChunkState(ChunkState.GENERATED);
    }

    private boolean isTop(IChunk chunk, int x, int y, int z) {
        for (int i = 0; i < 5; i++) {
            int yy = y + i;
            if(NukkitMath.clamp(yy, 1, 127) != yy) continue;
            if (chunk.getBlockState(x, yy, z) == BlockAir.STATE) {
                return true;
            }
        }
        return false;
    }

    private boolean isCeil(IChunk chunk, int x, int y, int z) {
        for (int i = 0; i < 5; i++) {
            int yy = y - i;
            if(NukkitMath.clamp(yy, 1, 127) != yy) continue;
            if (chunk.getBlockState(x, yy, z) == BlockAir.STATE) {
                return true;
            }
        }
        return false;
    }

    public float getNoise(int x, int y, int z) {
        //Those "magic numbers" are vanilla numbers without a description.
        float density = surfaceNoise.getValue(x * 0.25f,  y * 0.375f, z * 0.25f);
        density -= 0.9375f;
        density *= NukkitMath.remap(NukkitMath.clamp(y, 104, 128), 104, 108, 1, 0);
        density += 0.9375f;
        density -= 2.5f;
        density *= NukkitMath.remap(NukkitMath.clamp(y, -8, 24), -8, 24, 0, 1);
        density += 2.5f;
        density *= 0.64f;
        density = NukkitMath.clamp(density, -1, 1);
        density = density /2 - density*density*density / 24f;
        return density;
    }

    @Override
    public String name() {
        return NAME;
    }
}
