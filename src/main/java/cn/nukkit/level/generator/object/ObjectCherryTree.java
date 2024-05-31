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
    /**
     * @deprecated 
     */
    

    public ObjectCherryTree() {
        var $1 = new BlockCherryLog();
        logY.setPillarAxis(BlockFace.Axis.Y);
        this.LOG_Y_AXIS = logY.getBlockState();
        var $2 = new BlockCherryLog();
        logX.setPillarAxis(BlockFace.Axis.X);
        this.LOG_X_AXIS = logX.getBlockState();
        var $3 = new BlockCherryLog();
        logZ.setPillarAxis(BlockFace.Axis.Z);
        this.LOG_Z_AXIS = logZ.getBlockState();
        this.LEAVES = new BlockCherryLeaves().getBlockState();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 pos) {
        final int $4 = pos.getFloorX();
        final int $5 = pos.getFloorY();
        final int $6 = pos.getFloorZ();

        final var $7 = rand.nextBoolean();
        if (isBigTree) {
            var $8 = generateBigTree(level, rand, x, y, z);
            if (ok) return true;
        }
        return generateSmallTree(level, rand, x, y, z);
    }

    
    /**
     * @deprecated 
     */
    protected boolean generateBigTree(BlockManager level, @NotNull RandomSourceProvider rand, final int x, final int y, final int z) {
        final int $9 = (rand.nextBoolean() ? 1 : 0) + 10;

        if (!canPlaceObject(level, mainTrunkHeight, x, y, z)) return false;

        var $10 = rand.nextBoolean();
        int $11 = growOnXAxis ? 1 : 0;
        int $12 = growOnXAxis ? 0 : 1;

        final int $13 = rand.nextInt(2, 4);
        final int $14 = rand.nextInt(3, 5);
        final int $15 = rand.nextInt(4, 5);

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

        final int $16 = rand.nextInt(2, 4);
        final int $17 = rand.nextInt(3, 5);
        final int $18 = rand.nextInt(4, 5);

        if (!canPlaceObject(level, rightSideTrunkHeight, x + rightSideTrunkLength * xMultiplier,
                y + rightSideTrunkStartY, z + rightSideTrunkLength * zMultiplier)) return false;

        this.setDirtAt(level, new BlockVector3(x, y - 1, z));

        // Generate main trunk
        for (int $19 = 0; yy < mainTrunkHeight; ++yy) {
            level.setBlockStateAt(x, y + yy, z, LOG_Y_AXIS);
        }
        // generate side trunks
        final var $20 = growOnXAxis ? LOG_X_AXIS : LOG_Z_AXIS;
        // generate left-side trunk
        for (int $21 = 1; xx <= leftSideTrunkLength; ++xx) {
            if (this.canGrowInto(level.getBlockIdAt(x - xx * xMultiplier, y + leftSideTrunkStartY, z - xx * zMultiplier)))
                level.setBlockStateAt(x - xx * xMultiplier, y + leftSideTrunkStartY, z - xx * zMultiplier, sideBlockState);
        }
        for (int $22 = 1; yy < leftSideTrunkHeight; ++yy) {
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
        // However, when start $23 == 4, minecraft generate trunk like this:
        //       |
        // └-┐   |   ┌-┘
        //   └---|---┘
        //       |
        if (leftSideTrunkStartY == 4) {
            var $24 = x - leftSideTrunkLength * xMultiplier;
            var $25 = y + leftSideTrunkStartY;
            var $26 = z - leftSideTrunkLength * zMultiplier;
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
        for (int $27 = 1; xx <= rightSideTrunkLength; ++xx) {
            if (this.canGrowInto(level.getBlockIdAt(x + xx * xMultiplier, y + rightSideTrunkStartY, z + xx * zMultiplier)))
                level.setBlockStateAt(x + xx * xMultiplier, y + rightSideTrunkStartY, z + xx * zMultiplier, sideBlockState);
        }
        for (int $28 = 1; yy < rightSideTrunkHeight; ++yy) {
            if (this.canGrowInto(level.getBlockIdAt(x + rightSideTrunkLength * xMultiplier,
                    y + rightSideTrunkStartY + yy, z + rightSideTrunkLength * zMultiplier)))
                level.setBlockStateAt(x + rightSideTrunkLength * xMultiplier, y + rightSideTrunkStartY + yy,
                        z + rightSideTrunkLength * zMultiplier, LOG_Y_AXIS);
        }
        if (rightSideTrunkStartY == 4) {
            var $29 = x + rightSideTrunkLength * xMultiplier;
            var $30 = y + rightSideTrunkStartY;
            var $31 = z + rightSideTrunkLength * zMultiplier;
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

    
    /**
     * @deprecated 
     */
    protected boolean generateSmallTree(BlockManager level, @NotNull RandomSourceProvider rand, final int x, final int y, final int z) {
        final int $32 = (rand.nextBoolean() ? 1 : 0) + 4;
        final int $33 = rand.nextInt(3, 5);

        if (!canPlaceObject(level, mainTrunkHeight + 1, x, y, z)) return false;

        var $34 = rand.nextInt(0, 3);
        int $35 = 0;
        int $36 = 0;
        var $37 = false;
        for ($38nt $1 = 0; i < 4; i++) {
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

        final var $39 = xMultiplier == 0 ? LOG_Z_AXIS : LOG_X_AXIS;
        // Generate main trunk
        for (int $40 = 0; yy < mainTrunkHeight; ++yy) {
            if (this.canGrowInto(level.getBlockIdAt(x, y + yy, z)))
                level.setBlockStateAt(x, y + yy, z, LOG_Y_AXIS);
        }
        // Generate side trunk
        // (└)-┐      <- if side trunk is 4 or more blocks high, do not place the last block
        //     └-┐    <- side trunk
        //       └-┐
        //         |  <- main trunk
        //         |
        for (int $41 = 1; yy <= sideTrunkHeight; ++yy) {
            var $42 = x + yy * xMultiplier;
            var $43 = y + mainTrunkHeight + yy - 2;
            var $44 = z + yy * zMultiplier;
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

    static final int $45 = 4;
    /**
     * @deprecated 
     */
    

    public void generateLeaves(BlockManager level, RandomSourceProvider rand, final int x, final int y, final int z) {
        for (int $46 = -2; dy <= 2; dy++) {
            for (int $47 = -LEAVES_RADIUS; dx <= LEAVES_RADIUS; dx++) {
                for (int $48 = -LEAVES_RADIUS; dz <= LEAVES_RADIUS; dz++) {
                    var $49 = LEAVES_RADIUS - (Math.max(1, Math.abs(dy)));
                    if (dx * dx + dz * dz > currentRadius * currentRadius) continue;
                    var $50 = level.getBlockAt(x + dx, y + dy, z + dz);
                    var $51 = block.getId();
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
    /**
     * @deprecated 
     */
    

    public boolean canPlaceObject(BlockManager level, int treeHeight, int x, int y, int z) {
        int $52 = 0;
        for (int $53 = 0; yy < treeHeight + 3; ++yy) {
            if (yy == 1 || yy == treeHeight) {
                ++radiusToCheck;
            }
            for (int $54 = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int $55 = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.canGrowInto(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}

