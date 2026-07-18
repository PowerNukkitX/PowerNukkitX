package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockIce;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.tags.BlockTags;

/**
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/06/23
 */
public abstract class LakeFeature extends GenerateFeature {

    protected static final BlockState AIR = BlockAir.STATE;
    protected static final BlockState ICE = BlockIce.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());
        BlockManager manager = new BlockManager(level);
        int minY = level.getMinHeight() + 5;
        int maxY = Math.max(minY, chunk.getHeightMap(random.nextInt(16), random.nextInt(16)));

        for (int i = 0; i < placementAttempts(); i++) {
            int x = (chunkX << 4) + random.nextInt(16);
            int y = minY + random.nextInt(maxY - minY + 1);
            int z = (chunkZ << 4) + random.nextInt(16);
            if (place(manager, x, y, z)) {
                queueObject(chunk, manager);
                return;
            }
        }
    }

    protected int placementAttempts() {
        return 1;
    }

    protected abstract BlockState fluid();

    protected BlockState barrier() {
        return AIR;
    }

    protected boolean canPlaceFeature(BlockManager manager, int x, int y, int z) {
        return true;
    }

    protected boolean canReplaceWithAirOrFluid(BlockManager manager, int x, int y, int z) {
        Block block = manager.getBlockIfCachedOrLoaded(x, y, z);
        return !block.hasTag(BlockTags.NOT_FEATURE_REPLACEABLE);
    }

    protected boolean canReplaceWithBarrier(BlockManager manager, int x, int y, int z) {
        return canReplaceWithAirOrFluid(manager, x, y, z);
    }

    protected boolean place(BlockManager manager, int originX, int originY, int originZ) {
        if (originY <= manager.getMinHeight() + 4) {
            return false;
        }

        originX -= 8;
        originY -= 4;
        originZ -= 8;

        boolean[] grid = new boolean[16 * 16 * 8];
        int spots = random.nextInt(4) + 4;

        for (int i = 0; i < spots; i++) {
            double xr = random.nextDouble() * 6.0 + 3.0;
            double yr = random.nextDouble() * 4.0 + 2.0;
            double zr = random.nextDouble() * 6.0 + 3.0;
            double xp = random.nextDouble() * (16.0 - xr - 2.0) + 1.0 + xr / 2.0;
            double yp = random.nextDouble() * (8.0 - yr - 4.0) + 2.0 + yr / 2.0;
            double zp = random.nextDouble() * (16.0 - zr - 2.0) + 1.0 + zr / 2.0;

            for (int xx = 1; xx < 15; xx++) {
                for (int zz = 1; zz < 15; zz++) {
                    for (int yy = 1; yy < 7; yy++) {
                        double xd = (xx - xp) / (xr / 2.0);
                        double yd = (yy - yp) / (yr / 2.0);
                        double zd = (zz - zp) / (zr / 2.0);
                        if (xd * xd + yd * yd + zd * zd < 1.0) {
                            grid[index(xx, yy, zz)] = true;
                        }
                    }
                }
            }
        }

        BlockState fluid = fluid();
        if (!validateBoundary(manager, grid, originX, originY, originZ, fluid)) {
            return false;
        }

        carveLake(manager, grid, originX, originY, originZ, fluid);
        placeBarrier(manager, grid, originX, originY, originZ);
        return true;
    }

    private boolean validateBoundary(BlockManager manager, boolean[] grid, int originX, int originY, int originZ, BlockState fluid) {
        for (int xx = 0; xx < 16; xx++) {
            for (int zz = 0; zz < 16; zz++) {
                for (int yy = 0; yy < 8; yy++) {
                    if (!isBoundary(grid, xx, yy, zz)) {
                        continue;
                    }

                    int x = originX + xx;
                    int y = originY + yy;
                    int z = originZ + zz;
                    Block block = manager.getBlockIfCachedOrLoaded(x, y, z);
                    if (yy >= 4 && block instanceof BlockLiquid) {
                        return false;
                    }
                    if (yy < 4 && !block.isSolid() && !block.getBlockState().equals(fluid)) {
                        return false;
                    }
                    if (!canPlaceFeature(manager, x, y, z)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void carveLake(BlockManager manager, boolean[] grid, int originX, int originY, int originZ, BlockState fluid) {
        Level level = manager.getLevel();
        for (int xx = 0; xx < 16; xx++) {
            for (int zz = 0; zz < 16; zz++) {
                for (int yy = 0; yy < 8; yy++) {
                    if (!grid[index(xx, yy, zz)]) {
                        continue;
                    }

                    int x = originX + xx;
                    int y = originY + yy;
                    int z = originZ + zz;
                    if (!canReplaceWithAirOrFluid(manager, x, y, z)) {
                        continue;
                    }

                    BlockState state = yy >= 4 ? AIR : fluid;
                    manager.setBlockStateAt(x, y, z, state);
                    if (state == AIR) {
                        manager.addHook(() -> level.scheduleUpdate(level.getBlock(x, y, z), 0));
                    } else if (state.toBlock() instanceof BlockLiquid) {
                        manager.addHook(() -> level.scheduleUpdate(level.getBlock(x, y, z), 1));
                    }
                }
            }
        }
    }

    private void placeBarrier(BlockManager manager, boolean[] grid, int originX, int originY, int originZ) {
        BlockState barrier = barrier();
        if (barrier == AIR || barrier.toBlock().isAir()) {
            return;
        }

        for (int xx = 0; xx < 16; xx++) {
            for (int zz = 0; zz < 16; zz++) {
                for (int yy = 0; yy < 8; yy++) {
                    if (!isBoundary(grid, xx, yy, zz) || yy >= 4 && random.nextInt(2) == 0) {
                        continue;
                    }

                    int x = originX + xx;
                    int y = originY + yy;
                    int z = originZ + zz;
                    Block block = manager.getBlockIfCachedOrLoaded(x, y, z);
                    if (block.isSolid() && canReplaceWithBarrier(manager, x, y, z)) {
                        manager.setBlockStateAt(x, y, z, barrier);
                    }
                }
            }
        }
    }

    private boolean isBoundary(boolean[] grid, int xx, int yy, int zz) {
        return !grid[index(xx, yy, zz)]
                && (xx < 15 && grid[index(xx + 1, yy, zz)]
                || xx > 0 && grid[index(xx - 1, yy, zz)]
                || zz < 15 && grid[index(xx, yy, zz + 1)]
                || zz > 0 && grid[index(xx, yy, zz - 1)]
                || yy < 7 && grid[index(xx, yy + 1, zz)]
                || yy > 0 && grid[index(xx, yy - 1, zz)]);
    }

    private int index(int x, int y, int z) {
        return (x * 16 + z) * 8 + y;
    }

}
