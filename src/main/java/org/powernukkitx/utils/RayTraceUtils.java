package org.powernukkitx.utils;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;

public final class RayTraceUtils {

    public record BlockRayTraceResult(int x, int y, int z, BlockFace face, Vector3 hitPos) {}

    /**
     * Shoots a ray between two points and returns the first solid block it hits,
     * along with the exact entry face. Returns null if nothing is hit.
     */
    public static BlockRayTraceResult raytraceBlocks(Level level, Vector3 start, Vector3 end) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;

        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1e-6) return null;

        int voxelX = (int) Math.floor(start.x);
        int voxelY = (int) Math.floor(start.y);
        int voxelZ = (int) Math.floor(start.z);

        int stepX = Double.compare(dx, 0);
        int stepY = Double.compare(dy, 0);
        int stepZ = Double.compare(dz, 0);

        double tMaxX = intbound(start.x, dx);
        double tMaxY = intbound(start.y, dy);
        double tMaxZ = intbound(start.z, dz);

        double tDeltaX = stepX != 0 ? Math.abs(1 / dx) : Double.POSITIVE_INFINITY;
        double tDeltaY = stepY != 0 ? Math.abs(1 / dy) : Double.POSITIVE_INFINITY;
        double tDeltaZ = stepZ != 0 ? Math.abs(1 / dz) : Double.POSITIVE_INFINITY;

        BlockFace lastFace = null;
        double t = 0;
        int maxSteps = 256;

        while (t <= 1.0 && maxSteps-- > 0) {
            Block block = level.getBlock(voxelX, voxelY, voxelZ);
            if (block != null && !block.isAir() && block.getBoundingBox() != null) {
                Vector3 hitPos = new Vector3(
                    start.x + t * dx,
                    start.y + t * dy,
                    start.z + t * dz
                );
                return new BlockRayTraceResult(voxelX, voxelY, voxelZ, lastFace, hitPos);
            }

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    voxelX += stepX;
                    t = tMaxX;
                    tMaxX += tDeltaX;
                    lastFace = stepX > 0 ? BlockFace.WEST : BlockFace.EAST;
                } else {
                    voxelZ += stepZ;
                    t = tMaxZ;
                    tMaxZ += tDeltaZ;
                    lastFace = stepZ > 0 ? BlockFace.NORTH : BlockFace.SOUTH;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    voxelY += stepY;
                    t = tMaxY;
                    tMaxY += tDeltaY;
                    lastFace = stepY > 0 ? BlockFace.DOWN : BlockFace.UP;
                } else {
                    voxelZ += stepZ;
                    t = tMaxZ;
                    tMaxZ += tDeltaZ;
                    lastFace = stepZ > 0 ? BlockFace.NORTH : BlockFace.SOUTH;
                }
            }
        }

        return null;
    }

    private static double intbound(double s, double ds) {
        if (ds == 0) return Double.POSITIVE_INFINITY;
        if (ds < 0) return intbound(-s, -ds);
        double frac = mod(s, 1);
        return (1 - frac) / ds;
    }

    private static double mod(double value, double modulus) {
        return (value % modulus + modulus) % modulus;
    }
}
