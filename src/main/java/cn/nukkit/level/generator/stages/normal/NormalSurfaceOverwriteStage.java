package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.UnsafeChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.Arrays;

import static cn.nukkit.level.biome.BiomeID.*;
import static cn.nukkit.block.BlockID.FLOWING_WATER;
import static cn.nukkit.block.BlockID.WATER;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;


/**
 * @implNote <a href="https://minecraft.wiki/w/World_generation#Overworld_2">...</a>
 */
public class NormalSurfaceOverwriteStage extends GenerateStage {

    public static final String NAME = "normal_surface_overwrite";

    protected static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    protected static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();
    protected static final BlockState COARSE_DIRT = BlockCoarseDirt.PROPERTIES.getDefaultState();
    protected static final BlockState GRASS = BlockGrassBlock.PROPERTIES.getDefaultState();
    protected static final BlockState HARDENED_CLAY = BlockHardenedClay.PROPERTIES.getDefaultState();
    protected static final BlockState ORANGE_TERRACOTTA = BlockOrangeTerracotta.PROPERTIES.getDefaultState();
    protected static final BlockState WHITE_TERRACOTTA = BlockWhiteTerracotta.PROPERTIES.getDefaultState();
    protected static final BlockState YELLOW_TERRACOTTA = BlockYellowTerracotta.PROPERTIES.getDefaultState();
    protected static final BlockState BROWN_TERRACOTTA = BlockBrownTerracotta.PROPERTIES.getDefaultState();
    protected static final BlockState RED_TERRACOTTA = BlockRedTerracotta.PROPERTIES.getDefaultState();
    protected static final BlockState LIGHT_GRAY_TERRACOTTA = BlockLightGrayTerracotta.PROPERTIES.getDefaultState();
    protected static final BlockState SNOW_LAYER = BlockSnowLayer.PROPERTIES.getDefaultState();
    protected static final BlockState SNOW_BLOCK = BlockSnow.PROPERTIES.getDefaultState();
    protected static final BlockState ICE = BlockIce.PROPERTIES.getDefaultState();
    protected static final BlockState PACKED_ICE = BlockPackedIce.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        NormalObjectHolder.SurfaceOverwriteHolder holder = ((NormalObjectHolder) level.getGeneratorObjectHolder()).getSurfaceOverwriteHolder();
        chunk.batchProcess(unsafeChunk -> {
            for(int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int lx = x + (unsafeChunk.getX() << 4);
                    int lz = z + (unsafeChunk.getZ() << 4);
                    int y = unsafeChunk.getHeightMap(x, z);
                    int biomeId = unsafeChunk.getBiomeId(x, y, z);
                    switch (biomeId) {
                        case MESA,
                             MESA_BRYCE,
                             MESA_PLATEAU_STONE -> {
                            applyClayBandsDepth(unsafeChunk, holder, level, x, z, lx, lz, y);
                        }
                    }
                    switch (biomeId) {
                        case SWAMPLAND -> {
                            if(holder.getSwampNoise().getValue(lx, y, lz) > 0)
                                if(y == SEA_LEVEL) unsafeChunk.setBlockState(x, y, z, WATER, 0);
                        }
                        case MANGROVE_SWAMP -> {
                            if(holder.getSwampNoise().getValue(lx, y, lz) > 0)
                                if(y >= SEA_LEVEL -2 && y <= SEA_LEVEL) unsafeChunk.setBlockState(x, y, z, WATER, 0);
                        }
                        case MESA_PLATEAU_STONE -> {
                            if(y > 97) {
                                float noise = holder.getSurfaceNoise().getValue(lx * 0.25f, y, lz * 0.25f);
                                if (isInRange(noise)) {
                                    unsafeChunk.setBlockState(x, y, z, COARSE_DIRT, 0);
                                } else unsafeChunk.setBlockState(x, y, z, GRASS, 0);
                            }
                        }
                        case ICE_PLAINS -> {
                            Block support = unsafeChunk.getBlockState(x, y, z).toBlock();
                            if(support.isSolid()) {
                                BlockState state = unsafeChunk.getBlockState(x, y+1, z);
                                if(state.equals(BlockAir.STATE)) {
                                    unsafeChunk.setBlockState(x, y+1, z, SNOW_LAYER, 0);
                                } else {
                                    unsafeChunk.getAndSetBlockState(x, y+1, z, SNOW_LAYER, 1);
                                }
                            }
                        }
                        case FROZEN_OCEAN,
                             DEEP_FROZEN_OCEAN,
                             LEGACY_FROZEN_OCEAN -> {
                            frozenOceanExtension(unsafeChunk, holder, level, x, z, lx, lz, y);
                        }
                        case MESA_BRYCE -> {
                            erodedBadlandsExtension(unsafeChunk, holder, level, x, z, lx, lz, y);
                        }
                    }
                }
            }
        });
    }

    private void frozenOceanExtension(
            UnsafeChunk chunk,
            NormalObjectHolder.SurfaceOverwriteHolder holder,
            Level level,
            int localX,
            int localZ,
            int worldX,
            int worldZ,
            int height
    ) {
        double iceberg = Math.min(
                Math.abs(holder.getIcebergSurfaceNoise().getValue(worldX, 0.0, worldZ) * 8.25),
                holder.getIcebergPillarNoise().getValue(worldX * 1.28, 0.0, worldZ * 1.28) * 15.0
        );
        if (iceberg <= 1.8) {
            return;
        }

        double icebergRoof = Math.abs(holder.getIcebergPillarRoofNoise().getValue(worldX * 1.17, 0.0, worldZ * 1.17) * 1.5);
        double top = Math.min(iceberg * iceberg * 1.2, Math.ceil(icebergRoof * 40.0) + 14.0);
        if (top <= 2.0) {
            return;
        }

        double extensionBottom = SEA_LEVEL - top - 7.0;
        double extensionTop = top + SEA_LEVEL;

        NukkitRandom random = new NukkitRandom(level.getSeed()
                ^ Level.chunkHash(chunk.getX(), chunk.getZ())
                ^ ((long) worldX)
                ^ ((long) worldZ));

        int maxSnowDepth = 2 + random.nextBoundedInt(4);
        int minSnowHeight = SEA_LEVEL + 18 + random.nextBoundedInt(10);
        int snowDepth = 0;

        for (int y = Math.max(height, (int) extensionTop + 1); y >= level.getMinHeight(); y--) {
            BlockState state = chunk.getBlockState(localX, y, localZ);
            boolean place = (state == BlockAir.STATE && y < (int) extensionTop && random.nextDouble() > 0.01)
                    || (state.toBlock() instanceof BlockFlowingWater && y > (int) extensionBottom && y <= SEA_LEVEL && random.nextDouble() > 0.15);
            if (!place) {
                continue;
            }

            if (snowDepth <= maxSnowDepth && y > minSnowHeight) {
                chunk.setBlockState(localX, y, localZ, SNOW_BLOCK, 0);
                snowDepth++;
            } else {
                chunk.setBlockState(localX, y, localZ, PACKED_ICE, 0);
            }
        }
    }

    private void erodedBadlandsExtension(
            UnsafeChunk chunk,
            NormalObjectHolder.SurfaceOverwriteHolder holder,
            Level level,
            int localX,
            int localZ,
            int worldX,
            int worldZ,
            int height
    ) {
        double pillarBuffer = Math.min(
                Math.abs(holder.getBadlandsSurfaceNoise().getValue(worldX, 0.0, worldZ) * 8.25),
                holder.getBadlandsPillarNoise().getValue(worldX * 0.2, 0.0, worldZ * 0.2) * 15.0
        );
        if (pillarBuffer <= 0.0) {
            return;
        }

        double pillarFloor = Math.abs(holder.getBadlandsPillarRoofNoise().getValue(worldX * 0.75, 0.0, worldZ * 0.75) * 1.5);
        double extensionTop = 64.0 + Math.min(pillarBuffer * pillarBuffer * 2.5, Math.ceil(pillarFloor * 50.0) + 24.0);
        int startY = (int) Math.floor(extensionTop);
        if (height > startY) {
            return;
        }

        for (int y = startY; y >= level.getMinHeight(); y--) {
            BlockState state = chunk.getBlockState(localX, y, localZ);
            if (isWaterState(state)) {
                return;
            }
            if (state.toBlock().isSolid()) {
                break;
            }
        }

        for (int y = startY; y >= level.getMinHeight(); y--) {
            BlockState state = chunk.getBlockState(localX, y, localZ);
            if (state != BlockAir.STATE) {
                break;
            }
            chunk.setBlockState(localX, y, localZ, getClayBand(holder, level, worldX, y, worldZ), 0);
        }
    }

    private void applyClayBandsDepth(
            UnsafeChunk chunk,
            NormalObjectHolder.SurfaceOverwriteHolder holder,
            Level level,
            int localX,
            int localZ,
            int worldX,
            int worldZ,
            int surfaceY
    ) {
        if (surfaceY >= 256) {
            chunk.setBlockState(localX, surfaceY, localZ, ORANGE_TERRACOTTA, 0);
        } else if (surfaceY >= 74) {
            chunk.setBlockState(localX, surfaceY, localZ, getClayBand(holder, level, worldX, surfaceY, worldZ), 0);
        }

        for (int y = 74; y > 63; y--) {
            if (chunk.getBlockState(localX, y, localZ) == BlockAir.STATE) {
                continue;
            }
            if (surfaceY > 63 && surfaceY < 74) {
                chunk.setBlockState(localX, y, localZ, ORANGE_TERRACOTTA, 0);
            } else {
                chunk.setBlockState(localX, y, localZ, getClayBand(holder, level, worldX, y, worldZ), 0);
            }
        }

        // Continue the terracotta bands below the surface, not only at the top.
        int depth = 4 + (int) Math.floor(Math.abs(holder.getSurfaceNoise().getValue(worldX * 0.25f, 0.0, worldZ * 0.25f)) * 3.0);
        int minY = Math.max(level.getMinHeight(), surfaceY - depth);
        for (int y = surfaceY - 1; y >= minY; y--) {
            BlockState state = chunk.getBlockState(localX, y, localZ);
            if (state == BlockAir.STATE || isWaterState(state)) {
                break;
            }
            if (!state.toBlock().isSolid()) {
                break;
            }
            chunk.setBlockState(localX, y, localZ, getClayBand(holder, level, worldX, y, worldZ), 0);
        }
    }

    private boolean isWaterState(BlockState state) {
        return state.toBlock() instanceof BlockFlowingWater;
    }

    private boolean isInRange(float noise) {
        return (noise >= -0.909 && noise <= -0.5454) ||
                (noise >= -0.1818 && noise <= 0.1818) ||
                (noise >= 0.5454 && noise <= 0.909);
    }

    public BlockState getClayBand(NormalObjectHolder.SurfaceOverwriteHolder holder, Level level, int worldX, int y, int worldZ) {
        BlockState[] bands = holder.getClayBandsCache();
        if (bands[0] == null) {
            synchronized (bands) {
                if (bands[0] == null) {
                    generateBands(level.getSeed(), bands);
                }
            }
        }
        int offset = Math.round(holder.getClayBandsOffsetNoise().getValue(worldX, 0.0, worldZ) * 4.0f);
        return bands[(y + offset + bands.length) % bands.length];
    }

    private static void generateBands(long seed, BlockState[] bands) {
        NukkitRandom random = new NukkitRandom(seed ^ "clay_bands".hashCode());
        Arrays.fill(bands, HARDENED_CLAY);

        for (int i = 0; i < bands.length; i++) {
            i += random.nextBoundedInt(5) + 1;
            if (i < bands.length) {
                bands[i] = ORANGE_TERRACOTTA;
            }
        }

        makeBands(random, bands, 1, YELLOW_TERRACOTTA);
        makeBands(random, bands, 2, BROWN_TERRACOTTA);
        makeBands(random, bands, 1, RED_TERRACOTTA);

        int whiteBandCount = random.nextInt(9, 15);
        int count = 0;
        for (int start = 0; count < whiteBandCount && start < bands.length; start += random.nextBoundedInt(16) + 4) {
            bands[start] = WHITE_TERRACOTTA;
            if (start - 1 > 0 && random.nextBoolean()) {
                bands[start - 1] = LIGHT_GRAY_TERRACOTTA;
            }
            if (start + 1 < bands.length && random.nextBoolean()) {
                bands[start + 1] = LIGHT_GRAY_TERRACOTTA;
            }
            count++;
        }
    }

    private static void makeBands(NukkitRandom random, BlockState[] bands, int baseWidth, BlockState state) {
        int bandCount = random.nextInt(6, 15);
        for (int i = 0; i < bandCount; i++) {
            int width = baseWidth + random.nextBoundedInt(3);
            int start = random.nextBoundedInt(bands.length - 1);
            for (int p = 0; start + p < bands.length && p < width; p++) {
                bands[start + p] = state;
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
