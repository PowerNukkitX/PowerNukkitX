package org.powernukkitx;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockVector3;

final class BlockBreakCollisionGuard {

    private static final int MAX_DENIED_BLOCKS = 16;
    private static final double CHECK_DISTANCE_SQUARED = 16;

    private final int[] deniedBlockX = new int[MAX_DENIED_BLOCKS];
    private final int[] deniedBlockY = new int[MAX_DENIED_BLOCKS];
    private final int[] deniedBlockZ = new int[MAX_DENIED_BLOCKS];
    private final boolean[] deniedBlockCorrected = new boolean[MAX_DENIED_BLOCKS];
    private Level level;
    private boolean correctionRequired;
    private int deniedBlockCount;

    void deny(Level level, BlockVector3 blockPosition) {
        ensureLevel(level);
        int blockX = blockPosition.x;
        int blockY = blockPosition.y;
        int blockZ = blockPosition.z;
        for (int i = 0; i < this.deniedBlockCount; i++) {
            if (this.deniedBlockX[i] == blockX
                    && this.deniedBlockY[i] == blockY
                    && this.deniedBlockZ[i] == blockZ) {
                this.deniedBlockCorrected[i] = false;
                return;
            }
        }

        if (this.deniedBlockCount == MAX_DENIED_BLOCKS) {
            System.arraycopy(this.deniedBlockX, 1, this.deniedBlockX, 0, MAX_DENIED_BLOCKS - 1);
            System.arraycopy(this.deniedBlockY, 1, this.deniedBlockY, 0, MAX_DENIED_BLOCKS - 1);
            System.arraycopy(this.deniedBlockZ, 1, this.deniedBlockZ, 0, MAX_DENIED_BLOCKS - 1);
            System.arraycopy(this.deniedBlockCorrected, 1, this.deniedBlockCorrected, 0, MAX_DENIED_BLOCKS - 1);
            this.deniedBlockCount--;
        }
        this.deniedBlockX[this.deniedBlockCount] = blockX;
        this.deniedBlockY[this.deniedBlockCount] = blockY;
        this.deniedBlockZ[this.deniedBlockCount] = blockZ;
        this.deniedBlockCorrected[this.deniedBlockCount++] = false;
    }

    boolean blocksMovement(Level currentLevel, AxisAlignedBB playerBoundingBox, double dx, double dy, double dz) {
        this.correctionRequired = false;
        if (this.deniedBlockCount == 0) {
            return false;
        }
        if (this.level != currentLevel) {
            clear();
            return false;
        }
        if (dx == 0 && dy == 0 && dz == 0) {
            return false;
        }

        double startX = (playerBoundingBox.getMinX() + playerBoundingBox.getMaxX()) * 0.5;
        double startY = playerBoundingBox.getMinY();
        double startZ = (playerBoundingBox.getMinZ() + playerBoundingBox.getMaxZ()) * 0.5;
        double movementLengthSquared = dx * dx + dy * dy + dz * dz;
        double inverseMovementLengthSquared = 1 / movementLengthSquared;

        for (int i = this.deniedBlockCount - 1; i >= 0; i--) {
            int blockX = this.deniedBlockX[i];
            int blockY = this.deniedBlockY[i];
            int blockZ = this.deniedBlockZ[i];
            if (!isMovementNear(blockX, blockY, blockZ, startX, startY, startZ,
                    dx, dy, dz, inverseMovementLengthSquared)) {
                continue;
            }

            Block block = currentLevel.getBlock(blockX, blockY, blockZ, false);
            AxisAlignedBB[] collisionBoxes = getCollisionBoxes(block);
            if (collisionBoxes == null) {
                removeDenied(i);
            } else if (intersects(playerBoundingBox, dx, dy, dz, collisionBoxes)) {
                this.correctionRequired = !this.deniedBlockCorrected[i];
                this.deniedBlockCorrected[i] = true;
                return true;
            }
        }
        return false;
    }

    boolean isEmpty() {
        return this.deniedBlockCount == 0;
    }

    boolean consumeCorrectionRequired() {
        boolean required = this.correctionRequired;
        this.correctionRequired = false;
        return required;
    }

    void clear() {
        this.deniedBlockCount = 0;
        this.correctionRequired = false;
        this.level = null;
    }

    private void ensureLevel(Level level) {
        if (this.level != level) {
            clear();
            this.level = level;
        }
    }

    private void removeDenied(int index) {
        int moved = this.deniedBlockCount - index - 1;
        if (moved > 0) {
            System.arraycopy(this.deniedBlockX, index + 1, this.deniedBlockX, index, moved);
            System.arraycopy(this.deniedBlockY, index + 1, this.deniedBlockY, index, moved);
            System.arraycopy(this.deniedBlockZ, index + 1, this.deniedBlockZ, index, moved);
            System.arraycopy(this.deniedBlockCorrected, index + 1, this.deniedBlockCorrected, index, moved);
        }
        this.deniedBlockCount--;
    }

    private static AxisAlignedBB[] getCollisionBoxes(Block block) {
        if (block.canPassThrough()) {
            return null;
        }
        AxisAlignedBB[] collisionBoxes = block.getCollisionBoxes();
        return collisionBoxes.length == 0 ? null : collisionBoxes;
    }

    static boolean intersects(AxisAlignedBB playerBoundingBox, double dx, double dy, double dz,
                              AxisAlignedBB[] collisionBoxes) {
        for (AxisAlignedBB collisionBox : collisionBoxes) {
            if (intersectsAtDestination(playerBoundingBox, dx, dy, dz, collisionBox)) {
                return true;
            }
            if (passesCompletelyThrough(playerBoundingBox, dx, dy, dz, collisionBox)) {
                return true;
            }
        }
        return false;
    }

    static boolean passesCompletelyThrough(AxisAlignedBB movingBox, double dx, double dy, double dz,
                                           AxisAlignedBB obstacle) {
        double minX = movingBox.getMinX();
        double minY = movingBox.getMinY();
        double minZ = movingBox.getMinZ();
        double maxX = movingBox.getMaxX();
        double maxY = movingBox.getMaxY();
        double maxZ = movingBox.getMaxZ();
        double destinationMinX = minX + dx;
        double destinationMinY = minY + dy;
        double destinationMinZ = minZ + dz;
        double destinationMaxX = maxX + dx;
        double destinationMaxY = maxY + dy;
        double destinationMaxZ = maxZ + dz;
        double obstacleMinX = obstacle.getMinX();
        double obstacleMinY = obstacle.getMinY();
        double obstacleMinZ = obstacle.getMinZ();
        double obstacleMaxX = obstacle.getMaxX();
        double obstacleMaxY = obstacle.getMaxY();
        double obstacleMaxZ = obstacle.getMaxZ();

        boolean crossesX = (maxX <= obstacleMinX && destinationMinX >= obstacleMaxX)
                || (minX >= obstacleMaxX && destinationMaxX <= obstacleMinX);
        boolean crossesY = (maxY <= obstacleMinY && destinationMinY >= obstacleMaxY)
                || (minY >= obstacleMaxY && destinationMaxY <= obstacleMinY);
        boolean crossesZ = (maxZ <= obstacleMinZ && destinationMinZ >= obstacleMaxZ)
                || (minZ >= obstacleMaxZ && destinationMaxZ <= obstacleMinZ);

        return (crossesX || crossesY || crossesZ) && sweptIntersects(movingBox, dx, dy, dz, obstacle);
    }

    private static boolean isMovementNear(int x, int y, int z, double startX, double startY, double startZ,
                                          double dx, double dy, double dz, double inverseMovementLengthSquared) {
        double blockX = x + 0.5;
        double blockY = y + 0.5;
        double blockZ = z + 0.5;
        double progress = ((blockX - startX) * dx
                + (blockY - startY) * dy
                + (blockZ - startZ) * dz) * inverseMovementLengthSquared;
        progress = Math.max(0, Math.min(1, progress));

        double distanceX = startX + dx * progress - blockX;
        double distanceY = startY + dy * progress - blockY;
        double distanceZ = startZ + dz * progress - blockZ;
        return distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ <= CHECK_DISTANCE_SQUARED;
    }

    static boolean intersectsAtDestination(AxisAlignedBB movingBox, double dx, double dy, double dz, AxisAlignedBB obstacle) {
        double minX = movingBox.getMinX() + dx;
        double minY = movingBox.getMinY() + dy;
        double minZ = movingBox.getMinZ() + dz;
        double maxX = movingBox.getMaxX() + dx;
        double maxY = movingBox.getMaxY() + dy;
        double maxZ = movingBox.getMaxZ() + dz;

        double epsilonX = Math.max(AxisAlignedBB.collisionEpsilon(minX), AxisAlignedBB.collisionEpsilon(maxX));
        double epsilonY = Math.max(AxisAlignedBB.collisionEpsilon(minY), AxisAlignedBB.collisionEpsilon(maxY));
        double epsilonZ = Math.max(AxisAlignedBB.collisionEpsilon(minZ), AxisAlignedBB.collisionEpsilon(maxZ));

        return maxX - epsilonX > obstacle.getMinX()
                && minX + epsilonX < obstacle.getMaxX()
                && maxY - epsilonY > obstacle.getMinY()
                && minY + epsilonY < obstacle.getMaxY()
                && maxZ - epsilonZ > obstacle.getMinZ()
                && minZ + epsilonZ < obstacle.getMaxZ();
    }

    static boolean sweptIntersects(AxisAlignedBB movingBox, double dx, double dy, double dz, AxisAlignedBB obstacle) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return false;
        }

        double epsilonX = Math.max(
                AxisAlignedBB.collisionEpsilon(movingBox.getMinX()),
                AxisAlignedBB.collisionEpsilon(movingBox.getMaxX())
        );
        double epsilonY = Math.max(
                AxisAlignedBB.collisionEpsilon(movingBox.getMinY()),
                AxisAlignedBB.collisionEpsilon(movingBox.getMaxY())
        );
        double epsilonZ = Math.max(
                AxisAlignedBB.collisionEpsilon(movingBox.getMinZ()),
                AxisAlignedBB.collisionEpsilon(movingBox.getMaxZ())
        );

        double minX = movingBox.getMinX() + epsilonX;
        double minY = movingBox.getMinY() + epsilonY;
        double minZ = movingBox.getMinZ() + epsilonZ;
        double maxX = movingBox.getMaxX() - epsilonX;
        double maxY = movingBox.getMaxY() - epsilonY;
        double maxZ = movingBox.getMaxZ() - epsilonZ;

        double entryTime = Double.NEGATIVE_INFINITY;
        double exitTime = Double.POSITIVE_INFINITY;

        if (dx == 0) {
            if (maxX <= obstacle.getMinX() || minX >= obstacle.getMaxX()) {
                return false;
            }
        } else {
            double first = (obstacle.getMinX() - maxX) / dx;
            double second = (obstacle.getMaxX() - minX) / dx;
            entryTime = Math.max(entryTime, Math.min(first, second));
            exitTime = Math.min(exitTime, Math.max(first, second));
        }

        if (dy == 0) {
            if (maxY <= obstacle.getMinY() || minY >= obstacle.getMaxY()) {
                return false;
            }
        } else {
            double first = (obstacle.getMinY() - maxY) / dy;
            double second = (obstacle.getMaxY() - minY) / dy;
            entryTime = Math.max(entryTime, Math.min(first, second));
            exitTime = Math.min(exitTime, Math.max(first, second));
        }

        if (dz == 0) {
            if (maxZ <= obstacle.getMinZ() || minZ >= obstacle.getMaxZ()) {
                return false;
            }
        } else {
            double first = (obstacle.getMinZ() - maxZ) / dz;
            double second = (obstacle.getMaxZ() - minZ) / dz;
            entryTime = Math.max(entryTime, Math.min(first, second));
            exitTime = Math.min(exitTime, Math.max(first, second));
        }

        return entryTime >= 0 && entryTime < 1 && entryTime < exitTime;
    }
}
