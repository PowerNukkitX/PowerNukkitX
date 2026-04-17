package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.NukkitRandom;

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
        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int lx = x + (chunk.getX() << 4);
                int lz = z + (chunk.getZ() << 4);
                int y = chunk.getHeightMap(x, z);
                int biomeId = chunk.getBiomeId(x, y, z);
                switch (biomeId) {
                    case MESA,
                         MESA_BRYCE,
                         MESA_PLATEAU_STONE -> {
                        if(y >= 256) {
                            chunk.setBlockState(x, y, z, ORANGE_TERRACOTTA);
                        } else if(y >= 74) {
                            float noise = holder.getSurfaceNoise().getValue(lx * 0.25f, y, lz * 0.25f);
                            if (isInRange(noise)) {
                                chunk.setBlockState(x, y, z, HARDENED_CLAY);
                            } else chunk.setBlockState(x, y, z, getClayBand(y));
                        }
                        for(int i = 74; i > 63; i--) {
                            if(chunk.getBlockState(x, i, z) != BlockAir.STATE) {
                                if(y > 63 && y < 74 ) {
                                    chunk.setBlockState(x, i, z, ORANGE_TERRACOTTA);
                                } else chunk.setBlockState(x, i, z, getClayBand(i));
                            }
                        }
                    }
                }
                switch (biomeId) {
                    case SWAMPLAND -> {
                        if(holder.getSwampNoise().getValue(lx, y, lz) > 0)
                            if(y == SEA_LEVEL) chunk.setBlockState(x, y, z, WATER);
                    }
                    case MANGROVE_SWAMP -> {
                        if(holder.getSwampNoise().getValue(lx, y, lz) > 0)
                            if(y >= SEA_LEVEL -2 && y <= SEA_LEVEL) chunk.setBlockState(x, y, z, WATER);
                    }
                    case MESA_PLATEAU_STONE -> {
                        if(y > 97) {
                            float noise = holder.getSurfaceNoise().getValue(lx * 0.25f, y, lz * 0.25f);
                            if (isInRange(noise)) {
                                chunk.setBlockState(x, y, z, COARSE_DIRT);
                            } else chunk.setBlockState(x, y, z, GRASS);
                        }
                    }
                    case ICE_PLAINS -> {
                        Block support = chunk.getBlockState(x, y, z).toBlock();
                        if(support.isSolid()) {
                            BlockState state = chunk.getBlockState(x, y+1, z);
                            if(state.equals(BlockAir.STATE)) {
                                chunk.setBlockState(x, y+1, z, SNOW_LAYER);
                            } else {
                                chunk.getAndSetBlockState(x, y+1, z, SNOW_LAYER, 1);
                            }
                        }
                    }
                    case FROZEN_OCEAN,
                         DEEP_FROZEN_OCEAN,
                         LEGACY_FROZEN_OCEAN -> {
                        frozenOceanExtension(chunk, holder, level, x, z, lx, lz, y);
                    }
                }
            }
        }
    }

    private void frozenOceanExtension(
            IChunk chunk,
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
                chunk.setBlockState(localX, y, localZ, SNOW_BLOCK);
                snowDepth++;
            } else {
                chunk.setBlockState(localX, y, localZ, PACKED_ICE);
            }
        }
    }

    private static final BlockState[] CLAYBAND = new BlockState[] {
            WHITE_TERRACOTTA,
            ORANGE_TERRACOTTA,
            HARDENED_CLAY,
            YELLOW_TERRACOTTA,
            BROWN_TERRACOTTA,
            RED_TERRACOTTA,
            LIGHT_GRAY_TERRACOTTA
    };

    private boolean isInRange(float noise) {
        return (noise >= -0.909 && noise <= -0.5454) ||
                (noise >= -0.1818 && noise <= 0.1818) ||
                (noise >= 0.5454 && noise <= 0.909);
    }

    public BlockState getClayBand(int y) {
        return CLAYBAND[ Math.abs((y * 1103515245 + 12345) >> 16) % CLAYBAND.length];
    }

    @Override
    public String name() {
        return NAME;
    }
}
