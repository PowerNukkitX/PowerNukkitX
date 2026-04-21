package cn.nukkit.level.generator.stages.nether;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.UnsafeChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.densityfunction.DensityCommon;
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
        final IChunk chunk = context.getChunk();
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();
        final int baseX = chunkX << 4;
        final int baseZ = chunkZ << 4;
        final Level level = chunk.getLevel();
        final NukkitRandom random = this.random.get();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        final NetherObjectHolder.TerrainHolder noises = ((NetherObjectHolder) level.getGeneratorObjectHolder()).getTerrainHolder();
        final DensityFunction densityFunction = noises.getDensityFunction();
        final DensityCommon.ChunkCache chunkCache = DensityCommon.chunkCache(chunk);
        chunkCache.clear();
        final DensityCommon.CellFunctionContext functionContext = new DensityCommon.CellFunctionContext(chunkCache);
        final int minDensityY = 1;
        final int maxDensityY = 126;
        final int cellMinY = Math.floorDiv(minDensityY, 8) * 8;
        final int cellMaxY = Math.floorDiv(maxDensityY, 8) * 8;

        try {
            chunk.batchProcess(unsafeChunk -> {
                for (int cellX = 0; cellX < 16; cellX += 4) {
                    for (int cellZ = 0; cellZ < 16; cellZ += 4) {
                        for (int cellY = cellMaxY; cellY >= cellMinY; cellY -= 8) {
                            for (int localX = 0; localX < 4; localX++) {
                                final int x = cellX + localX;
                                final int worldX = baseX + x;
                                for (int localZ = 0; localZ < 4; localZ++) {
                                    final int z = cellZ + localZ;
                                    final int worldZ = baseZ + z;
                                    for (int localY = 7; localY >= 0; localY--) {
                                        final int y = cellY + localY;
                                        if (y < minDensityY || y > maxDensityY) {
                                            continue;
                                        }
                                        if (densityFunction.compute(functionContext.set(worldX, y, worldZ)) > 0) {
                                            unsafeChunk.setBlockState(x, y, z, NETHERRACK, 0);
                                        } else if (y <= LAVA_LEVEL) {
                                            unsafeChunk.setBlockState(x, y, z, LAVA, 0);
                                            unsafeChunk.setBlockLight(x, y + 1, z, 15);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        final int nx = x + baseX;
                        final int nz = z + baseZ;
                        final int biomeId = unsafeChunk.getBiomeId(x, 0, z);

                        unsafeChunk.setBlockState(x, 0, z, BEDROCK, 0);
                        unsafeChunk.setBlockState(x, 126, z, NETHERRACK, 0);
                        unsafeChunk.setBlockState(x, 127, z, BEDROCK, 0);

                        for (int y = 1; y < 127; ++y) {
                            switch (biomeId) {
                                case BiomeID.BASALT_DELTAS -> {
                                    if (unsafeChunk.getBlockState(x, y, z) == NETHERRACK) {
                                        if (isCeil(unsafeChunk, x, y, z)) {
                                            unsafeChunk.setBlockState(x, y, z, BASALT, 0);
                                            continue;
                                        } else if (unsafeChunk.getBlockState(x, y + 1, z) == BlockAir.STATE
                                                && (noises.getNetherStateNoise().getValue(nx, y, nz) >= 0
                                                || y <= 35 && y >= 30 && noises.getPatchNoise().getValue(nx, y, nz) >= -0.012f)) {
                                            unsafeChunk.setBlockState(x, y, z, GRAVEL, 0);
                                            continue;
                                        }
                                        if (isTop(unsafeChunk, x, y, z) || isCeil(unsafeChunk, x, y, z)) {
                                            unsafeChunk.setBlockState(x, y, z, BLACKSTONE, 0);
                                        }
                                    }
                                }
                                case BiomeID.SOULSAND_VALLEY -> {
                                    if (unsafeChunk.getBlockState(x, y, z) == NETHERRACK) {
                                        if (isCeil(unsafeChunk, x, y, z)) {
                                            unsafeChunk.setBlockState(x, y, z, noises.getNetherStateNoise().getValue(nx, y, nz) >= 0 ? SOULSAND : SOULSOIL, 0);
                                        } else if (isTop(unsafeChunk, x, y, z)) {
                                            if (noises.getNetherStateNoise().getValue(nx, y, nz) >= 0 || (y <= 35 && y >= 30 && noises.getPatchNoise().getValue(nx, y, nz) >= -0.012f)) {
                                                unsafeChunk.setBlockState(x, y, z, SOULSAND, 0);
                                            } else {
                                                unsafeChunk.setBlockState(x, y, z, SOULSOIL, 0);
                                            }
                                        }
                                    }
                                }
                                case BiomeID.WARPED_FOREST -> {
                                    if (unsafeChunk.getBlockState(x, y, z) == NETHERRACK
                                            && unsafeChunk.getBlockState(x, y + 1, z) == BlockAir.STATE
                                            && y > 31
                                            && noises.getNetherStateNoise().getValue(nx, y, nz) <= 0f) {
                                        unsafeChunk.setBlockState(x, y, z, noises.getNetherwartNoise().getValue(nx, y, nz) >= 1.17f ? WARPED_WART : WARPED_NYLIUM, 0);
                                    }
                                }
                                case BiomeID.CRIMSON_FOREST -> {
                                    if (unsafeChunk.getBlockState(x, y, z) == NETHERRACK
                                            && unsafeChunk.getBlockState(x, y + 1, z) == BlockAir.STATE
                                            && y > 31
                                            && noises.getNetherStateNoise().getValue(nx, y, nz) <= 0.54f) {
                                        unsafeChunk.setBlockState(x, y, z, noises.getNetherwartNoise().getValue(nx, y, nz) >= 1.17f ? NETHERWART_BLOCK : CRIMSON_NYLIUM, 0);
                                    }
                                }
                                case BiomeID.HELL -> {
                                    if (unsafeChunk.getBlockState(x, y, z) == NETHERRACK && isTop(unsafeChunk, x, y, z)) {
                                        if (y > 31 && y < 35 && noises.getSoulsandNoise().getValue(nx, y, nz) >= -0.012f) {
                                            unsafeChunk.setBlockState(x, y, z, GRAVEL, 0);
                                            continue;
                                        }
                                        if (y <= 35 && y >= 30 && noises.getSoulsandNoise().getValue(nx, y, nz) >= -0.012f) {
                                            unsafeChunk.setBlockState(x, y, z, SOULSAND, 0);
                                        }
                                    }
                                }
                            }
                        }

                        for (int i = 0; i < random.nextBoundedInt(6); i++) {
                            if (unsafeChunk.getBlockState(x, 126 - i, z) == NETHERRACK
                                    && unsafeChunk.getBlockState(x, 125 - i, z) != BlockAir.STATE) {
                                unsafeChunk.setBlockState(x, 126 - i, z, BEDROCK, 0);
                            }
                        }
                    }
                }
            });
        } finally {
            DensityCommon.releaseChunkCache(chunk);
        }
        chunk.recalculateHeightMap();
    }

    private boolean isTop(UnsafeChunk chunk, int x, int y, int z) {
        for (int i = 0; i < 5; i++) {
            int yy = y + i;
            if (NukkitMath.clamp(yy, 1, 127) != yy) continue;
            if (chunk.getBlockState(x, yy, z) == BlockAir.STATE) {
                return true;
            }
        }
        return false;
    }

    private boolean isCeil(UnsafeChunk chunk, int x, int y, int z) {
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
