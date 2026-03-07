package cn.nukkit.entity.ai.sensor;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingLava;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityStrider;
import cn.nukkit.level.Level;


/**
 * Sensor that searches for nearby lava when the Strider is cold.
 *
 * Periodically scans the surrounding area for lava blocks and selects a
 * valid adjacent standing position near the lava. The chosen block is
 * stored in {@link CoreMemoryTypes#NEAREST_BLOCK} so movement executors
 * can route the Strider toward it to warm up.
 */
public class StriderLavaSensor implements ISensor {
    private final int radius;
    private final int period;

    public StriderLavaSensor(int radius, int period) {
        this.radius = radius;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        if (entity.level == null) return;
        if (!(entity instanceof EntityStrider s)) return;

        if (s.isWarm()) {
            entity.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_BLOCK);
            return;
        }

        int tick = entity.getServer().getTick();

        if (s.nextLavaSeekTick != 0 && tick < s.nextLavaSeekTick) return;

        s.nextLavaSeekTick = tick + (20 * 10);

        Block bestStandBlock = findBestStandBlockNearLava(entity.level, entity, radius);

        if (bestStandBlock != null) {
            entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_BLOCK, bestStandBlock);
        } else {
            entity.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_BLOCK);
        }
    }

    private Block findBestStandBlockNearLava(Level level, EntityIntelligent entity, int radius) {
        int cx = entity.getFloorX();
        int cy = entity.getFloorY();
        int cz = entity.getFloorZ();

        double bestD2 = Double.MAX_VALUE;
        Block best = null;

        int r = Math.max(1, radius);
        int yMin = cy - 3;
        int yMax = cy + 3;

        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                int dx = x - cx;
                int dz = z - cz;
                if (dx * dx + dz * dz > r * r) continue;

                for (int y = yMin; y <= yMax; y++) {
                    Block lava = level.getBlock(x, y, z);
                    if (!isLava(lava)) continue;

                    Block candidate = bestAdjacentStandBlock(level, entity, x, y, z);
                    if (candidate == null) continue;

                    double px = candidate.getFloorX() + 0.5;
                    double py = candidate.getFloorY() + 1.0;
                    double pz = candidate.getFloorZ() + 0.5;

                    double ddx = px - entity.x;
                    double ddy = py - entity.y;
                    double ddz = pz - entity.z;
                    double d2 = ddx * ddx + ddy * ddy + ddz * ddz;

                    if (d2 < bestD2) {
                        bestD2 = d2;
                        best = candidate;
                    }
                }
            }
        }

        return best;
    }

    private Block bestAdjacentStandBlock(Level level, EntityIntelligent entity, int lx, int ly, int lz) {
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        Block best = null;
        double bestD2 = Double.MAX_VALUE;

        for (int[] d : dirs) {
            int bx = lx + d[0];
            int bz = lz + d[1];
            int by = ly;

            Block base = level.getBlock(bx, by, bz);
            if (base == null) continue;
            if (base.canPassThrough()) continue;

            Block above = level.getBlock(bx, by + 1, bz);
            if (above == null || !above.canPassThrough()) continue;

            double px = bx + 0.5;
            double py = by + 1.0;
            double pz = bz + 0.5;

            double ddx = px - entity.x;
            double ddy = py - entity.y;
            double ddz = pz - entity.z;
            double d2 = ddx * ddx + ddy * ddy + ddz * ddz;

            if (d2 < bestD2) {
                bestD2 = d2;
                best = base;
            }
        }

        return best;
    }

    private boolean isLava(Block b) {
        if (b == null) return false;
        return (b instanceof BlockFlowingLava);
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
