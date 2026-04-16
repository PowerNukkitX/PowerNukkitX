package cn.nukkit.level.generator.stages.nether;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.level.generator.holder.NetherObjectHolder;
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
        NetherObjectHolder.TerrainHolder noises = ((NetherObjectHolder) level.getGeneratorObjectHolder()).getTerrainHolder();
        DensityFunction densityFunction = noises.getDensityFunction();
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
                        if (densityFunction.compute(new DensityFunction.SinglePointContext(baseX | x, y, baseZ | z)) > 0) {
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
                                    } else if (chunk.getBlockState(x, y + 1, z) == BlockAir.STATE
                                            && (noises.getNetherStateNoise().getValue(nx, y, nz) >= 0
                                            || y <= 35 && y >= 30 && noises.getPatchNoise().getValue(nx, y, nz) >= -0.012f)) {
                                        chunk.setBlockState(x, y, z, GRAVEL);
                                        continue;
                                    }
                                    if (isTop(chunk, x, y, z) || isCeil(chunk, x, y, z))
                                        chunk.setBlockState(x, y, z, BLACKSTONE);
                                }
                            }
                            case BiomeID.SOULSAND_VALLEY -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK) {
                                    if (isCeil(chunk, x, y, z)) {
                                        chunk.setBlockState(x, y, z, noises.getNetherStateNoise().getValue(nx, y, nz) >= 0 ? SOULSAND : SOULSOIL);
                                    } else if (isTop(chunk, x, y, z)) {
                                        if (noises.getNetherStateNoise().getValue(nx, y, nz) >= 0 || (y <= 35 && y >= 30 && noises.getPatchNoise().getValue(nx, y, nz) >= -0.012f)) {
                                            chunk.setBlockState(x, y, z, SOULSAND);
                                        } else chunk.setBlockState(x, y, z, SOULSOIL);
                                    }
                                }
                            }
                            case BiomeID.WARPED_FOREST -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK
                                        && chunk.getBlockState(x, y + 1, z) == BlockAir.STATE
                                        && y > 31
                                        && noises.getNetherStateNoise().getValue(nx, y, nz) <= 0f) {
                                    chunk.setBlockState(x, y, z, noises.getNetherwartNoise().getValue(nx, y, nz) >= 1.17f ? WARPED_WART : WARPED_NYLIUM);
                                }
                            }
                            case BiomeID.CRIMSON_FOREST -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK
                                        && chunk.getBlockState(x, y + 1, z) == BlockAir.STATE
                                        && y > 31
                                        && noises.getNetherStateNoise().getValue(nx, y, nz) <= 0.54f) {
                                    chunk.setBlockState(x, y, z, noises.getNetherwartNoise().getValue(nx, y, nz) >= 1.17f ? NETHERWART_BLOCK : CRIMSON_NYLIUM);
                                }
                            }
                            case BiomeID.HELL -> {
                                if (chunk.getBlockState(x, y, z) == NETHERRACK && isTop(chunk, x, y, z)) {
                                    if (y > 31 && y < 35 && noises.getSoulsandNoise().getValue(nx, y, nz) >= -0.012f) {
                                        chunk.setBlockState(x, y, z, GRAVEL);
                                        continue;
                                    }
                                    if (y <= 35 && y >= 30 && noises.getSoulsandNoise().getValue(nx, y, nz) >= -0.012f) {
                                        chunk.setBlockState(x, y, z, SOULSAND);
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
    }

    private boolean isTop(IChunk chunk, int x, int y, int z) {
        for (int i = 0; i < 5; i++) {
            int yy = y + i;
            if (NukkitMath.clamp(yy, 1, 127) != yy) continue;
            if (chunk.getBlockState(x, yy, z) == BlockAir.STATE) {
                return true;
            }
        }
        return false;
    }

    private boolean isCeil(IChunk chunk, int x, int y, int z) {
        for (int i = 0; i < 5; i++) {
            int yy = y - i;
            if (NukkitMath.clamp(yy, 1, 127) != yy) continue;
            if (chunk.getBlockState(x, yy, z) == BlockAir.STATE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String name() {
        return NAME;
    }
}
