package cn.nukkit.level.generator.object;

import cn.nukkit.block.*;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ObjectCherryTree extends TreeGenerator {
    protected BlockState LOG_Y_AXIS;
    protected BlockState LOG_X_AXIS;
    protected BlockState LOG_Z_AXIS;
    protected BlockState LEAVES;

    public ObjectCherryTree() {
        var logY = new BlockCherryLog();
        logY.setPillarAxis(BlockFace.Axis.Y);
        this.LOG_Y_AXIS = logY.getBlockState();
        var logX = new BlockCherryLog();
        logX.setPillarAxis(BlockFace.Axis.X);
        this.LOG_X_AXIS = logX.getBlockState();
        var logZ = new BlockCherryLog();
        logZ.setPillarAxis(BlockFace.Axis.Z);
        this.LOG_Z_AXIS = logZ.getBlockState();
        this.LEAVES = new BlockCherryLeaves().getBlockState();
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 pos) {
        final int x = pos.getFloorX();
        final int y = pos.getFloorY();
        final int z = pos.getFloorZ();

        final var isBigTree = rand.nextBoolean();
        if (isBigTree) {
            var ok = generateBigTree(level, rand, x, y, z);
            if (ok) return true;
        }
        return generateSmallTree(level, rand, x, y, z);
    }

    protected boolean generateBigTree(BlockManager level, @NotNull RandomSourceProvider rand, final int x, final int y, final int z) {
        final int mainTrunkHeight = (rand.nextBoolean() ? 1 : 0) + 10;

        if (!canPlaceObject(level, mainTrunkHeight, x, y, z)) return false;

        var growOnXAxis = rand.nextBoolean();
        int xMultiplier = growOnXAxis ? 1 : 0;
        int zMultiplier = growOnXAxis ? 0 : 1;

        final int leftSideTrunkLength = rand.nextInt(2, 4);
        final int leftSideTrunkHeight = rand.nextInt(3, 5);
        final int leftSideTrunkStartY = rand.nextInt(4, 5);

        if (!canPlaceObject(level, leftSideTrunkHeight, x - leftSideTrunkLength * xMultiplier,
                y + leftSideTrunkStartY, z - leftSideTrunkLength * zMultiplier)) {
            growOnXAxis = !growOnXAxis;
            xMultiplier = growOnXAxis ? 1 : 0;
            zMultiplier = growOnXAxis ? 0 : 1;
            if (!canPlaceObject(level, leftSideTrunkHeight, x - leftSideTrunkLength * xMultiplier,
                    y + leftSideTrunkStartY, z - leftSideTrunkLength * zMultiplier)) {
                return false;
            }
        }

        final int rightSideTrunkLength = rand.nextInt(2, 4);
        final int rightSideTrunkHeight = rand.nextInt(3, 5);
        final int rightSideTrunkStartY = rand.nextInt(4, 5);

        if (!canPlaceObject(level, rightSideTrunkHeight, x + rightSideTrunkLength * xMultiplier,
                y + rightSideTrunkStartY, z + rightSideTrunkLength * zMultiplier)) return false;

        this.setDirtAt(level, new BlockVector3(x, y - 1, z));

        // Generate main trunk
        for (int yy = 0; yy < mainTrunkHeight; ++yy) {
            level.setBlockStateAt(x, y + yy, z, LOG_Y_AXIS);
        }
        // generate side trunks
        final var sideBlockState = growOnXAxis ? LOG_X_AXIS : LOG_Z_AXIS;
        // generate left-side trunk
        for (int xx = 1; xx <= leftSideTrunkLength; ++xx) {
            if (this.canGrowInto(level.getBlockIdAt(x - xx * xMultiplier, y + leftSideTrunkStartY, z - xx * zMultiplier)))
                level.setBlockStateAt(x - xx * xMultiplier, y + leftSideTrunkStartY, z - xx * zMultiplier, sideBlockState);
        }
        for (int yy = 1; yy < leftSideTrunkHeight; ++yy) {
            if (this.canGrowInto(level.getBlockIdAt(x - leftSideTrunkLength * xMultiplier,
                    y + leftSideTrunkStartY + yy, z - leftSideTrunkLength * zMultiplier)))
                level.setBlockStateAt(x - leftSideTrunkLength * xMultiplier, y + leftSideTrunkStartY + yy,
                        z - leftSideTrunkLength * zMultiplier, LOG_Y_AXIS);
        }
        // We just generated this above
        //       |
        // |     |     |
        // └-----|-----┘
        //       |
        // However, when start y == 4, minecraft generate trunk like this:
        //       |
        // └-┐   |   ┌-┘
        //   └---|---┘
        //       |
        if (leftSideTrunkStartY == 4) {
            var tmpX = x - leftSideTrunkLength * xMultiplier;
            var tmpY = y + leftSideTrunkStartY;
            var tmpZ = z - leftSideTrunkLength * zMultiplier;
            level.setBlockStateAt(tmpX, tmpY, tmpZ, BlockAir.PROPERTIES.getDefaultState());
            tmpX += xMultiplier;
            tmpY += 1;
            tmpZ += zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                level.setBlockStateAt(tmpX, tmpY, tmpZ, LOG_Y_AXIS);
            }
            tmpX -= xMultiplier;
            tmpZ -= zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                level.setBlockStateAt(tmpX, tmpY, tmpZ, sideBlockState);
            }
        }
        // generate right-side trunk
        for (int xx = 1; xx <= rightSideTrunkLength; ++xx) {
            if (this.canGrowInto(level.getBlockIdAt(x + xx * xMultiplier, y + rightSideTrunkStartY, z + xx * zMultiplier)))
                level.setBlockStateAt(x + xx * xMultiplier, y + rightSideTrunkStartY, z + xx * zMultiplier, sideBlockState);
        }
        for (int yy = 1; yy < rightSideTrunkHeight; ++yy) {
            if (this.canGrowInto(level.getBlockIdAt(x + rightSideTrunkLength * xMultiplier,
                    y + rightSideTrunkStartY + yy, z + rightSideTrunkLength * zMultiplier)))
                level.setBlockStateAt(x + rightSideTrunkLength * xMultiplier, y + rightSideTrunkStartY + yy,
                        z + rightSideTrunkLength * zMultiplier, LOG_Y_AXIS);
        }
        if (rightSideTrunkStartY == 4) {
            var tmpX = x + rightSideTrunkLength * xMultiplier;
            var tmpY = y + rightSideTrunkStartY;
            var tmpZ = z + rightSideTrunkLength * zMultiplier;
            level.setBlockStateAt(tmpX, tmpY, tmpZ, BlockAir.PROPERTIES.getDefaultState());
            tmpX -= xMultiplier;
            tmpY += 1;
            tmpZ -= zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                level.setBlockStateAt(tmpX, tmpY, tmpZ, LOG_Y_AXIS);
            }
            tmpX += xMultiplier;
            tmpZ += zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                level.setBlockStateAt(tmpX, tmpY, tmpZ, sideBlockState);
            }
        }
        // generate main trunk leaves
        generateLeaves(level, rand, x, y + mainTrunkHeight + 1, z);
        // generate left-side trunk leaves
        generateLeaves(level, rand, x - leftSideTrunkLength * xMultiplier,
                y + leftSideTrunkStartY + leftSideTrunkHeight + 1, z - leftSideTrunkLength * zMultiplier);
        // generate right-side trunk leaves
        generateLeaves(level, rand, x + rightSideTrunkLength * xMultiplier,
                y + rightSideTrunkStartY + rightSideTrunkHeight + 1, z + rightSideTrunkLength * zMultiplier);
        return true;
    }

    protected boolean generateSmallTree(BlockManager level, @NotNull RandomSourceProvider rand, final int x, final int y, final int z) {
        final int mainTrunkHeight = (rand.nextBoolean() ? 1 : 0) + 4;
        final int sideTrunkHeight = rand.nextInt(3, 5);

        if (!canPlaceObject(level, mainTrunkHeight + 1, x, y, z)) return false;

        var growDirection = rand.nextInt(0, 3);
        int xMultiplier = 0;
        int zMultiplier = 0;
        var canPlace = false;
        for (int i = 0; i < 4; i++) {
            growDirection = (growDirection + 1) % 4;
            xMultiplier = switch (growDirection) {
                case 0 -> -1;
                case 1 -> 1;
                default -> 0;
            };
            zMultiplier = switch (growDirection) {
                case 2 -> -1;
                case 3 -> 1;
                default -> 0;
            };
            if (canPlaceObject(level, sideTrunkHeight, x + xMultiplier * sideTrunkHeight, y,
                    z + zMultiplier * sideTrunkHeight)) {
                canPlace = true;
                break;
            }
        }
        if (!canPlace) {
            return false;
        }

        final var sideBlockState = xMultiplier == 0 ? LOG_Z_AXIS : LOG_X_AXIS;
        // Generate main trunk
        for (int yy = 0; yy < mainTrunkHeight; ++yy) {
            if (this.canGrowInto(level.getBlockIdAt(x, y + yy, z)))
                level.setBlockStateAt(x, y + yy, z, LOG_Y_AXIS);
        }
        // Generate side trunk
        // (└)-┐      <- if side trunk is 4 or more blocks high, do not place the last block
        //     └-┐    <- side trunk
        //       └-┐
        //         |  <- main trunk
        //         |
        for (int yy = 1; yy <= sideTrunkHeight; ++yy) {
            var tmpX = x + yy * xMultiplier;
            var tmpY = y + mainTrunkHeight + yy - 2;
            var tmpZ = z + yy * zMultiplier;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                level.setBlockStateAt(tmpX, tmpY, tmpZ, sideBlockState);
            }
            // if side trunk is 4 or 5 blocks high, do not place the last block
            if (yy == sideTrunkHeight - 1 && sideTrunkHeight > 3) {
                continue;
            }
            tmpY += 1;
            if (this.canGrowInto(level.getBlockIdAt(tmpX, tmpY, tmpZ))) {
                level.setBlockStateAt(tmpX, tmpY, tmpZ, LOG_Y_AXIS);
            }
        }

        // generate leaves
        generateLeaves(level, rand, x + sideTrunkHeight * xMultiplier, y + mainTrunkHeight + sideTrunkHeight,
                z + sideTrunkHeight * zMultiplier);

        return true;
    }

    static final int LEAVES_RADIUS = 4;

    public void generateLeaves(BlockManager level, RandomSourceProvider rand, final int x, final int y, final int z) {
        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -LEAVES_RADIUS; dx <= LEAVES_RADIUS; dx++) {
                for (int dz = -LEAVES_RADIUS; dz <= LEAVES_RADIUS; dz++) {
                    var currentRadius = LEAVES_RADIUS - (Math.max(1, Math.abs(dy)));
                    if (dx * dx + dz * dz > currentRadius * currentRadius) continue;
                    var block = level.getBlockAt(x + dx, y + dy, z + dz);
                    var blockId = block.getId();
                    if (Objects.equals(blockId, BlockID.AIR) ||
                            block instanceof BlockLeaves ||
                            Objects.equals(blockId, BlockID.AZALEA_LEAVES_FLOWERED)) {
                        level.setBlockStateAt(x + dx, y + dy, z + dz, LEAVES);
                    }
                    if (dy == -2 && rand.nextInt(0, 2) == 0) {
                        block = level.getBlockAt(x + dx, y + dy - 1, z + dz);
                        blockId = block.getId();
                        if (Objects.equals(blockId, BlockID.AIR) ||
                                block instanceof BlockLeaves ||
                                Objects.equals(blockId, BlockID.AZALEA_LEAVES_FLOWERED)) {
                            level.setBlockStateAt(x + dx, y + dy - 1, z + dz, LEAVES);
                        }
                    }
                }
            }
        }
    }

    public boolean canPlaceObject(BlockManager level, int treeHeight, int x, int y, int z) {
        int radiusToCheck = 0;
        for (int yy = 0; yy < treeHeight + 3; ++yy) {
            if (yy == 1 || yy == treeHeight) {
                ++radiusToCheck;
            }
            for (int xx = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int zz = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.canGrowInto(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}

