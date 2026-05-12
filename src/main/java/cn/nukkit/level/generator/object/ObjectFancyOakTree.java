package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockOakLeaves;
import cn.nukkit.block.BlockOakLog;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/05/11
 */
public class ObjectFancyOakTree extends TreeGenerator {

    private static final double TRUNK_SCALE = 0.618;
    private static final double CLUSTER_DENSITY = 1.382;
    private static final double BRANCH_SLOPE = 0.381;
    private static final double BRANCH_LENGTH = 0.328;
    private static final int FOLIAGE_HEIGHT = 4;
    private static final int FOLIAGE_RADIUS = 2;
    private static final int FOLIAGE_OFFSET = 4;

    private final BlockState oakLeaves = BlockOakLeaves.PROPERTIES.getDefaultState();

    private final int baseHeight;
    private final int heightRandA;
    private final int heightRandB;

    public ObjectFancyOakTree() {
        this(3, 11, 0);
    }

    public ObjectFancyOakTree(int baseHeight, int heightRandA, int heightRandB) {
        this.baseHeight = baseHeight;
        this.heightRandA = heightRandA;
        this.heightRandB = heightRandB;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        BlockVector3 origin = position.asBlockVector3();
        int treeHeight = this.baseHeight + rand.nextInt(this.heightRandA + 1) + rand.nextInt(this.heightRandB + 1);
        int height = treeHeight + 2;

        if (origin.y < level.getMinHeight() + 1 || origin.y + height + 1 >= level.getMaxHeight()) {
            return false;
        }

        String ground = level.getBlockIdIfCachedOrLoaded(origin.x, origin.y - 1, origin.z);
        if (!ground.equals(Block.GRASS_BLOCK) && !ground.equals(Block.DIRT)) {
            return false;
        }

        int trunkHeight = MathHelper.floor(height * TRUNK_SCALE);
        int clustersPerY = Math.min(1, MathHelper.floor(CLUSTER_DENSITY + Math.pow((double) height / 13.0, 2.0)));
        int trunkTop = origin.y + trunkHeight;
        int relativeY = height - 5;
        List<FoliageCoords> foliageCoords = new ArrayList<>();
        foliageCoords.add(new FoliageCoords(origin.up(relativeY), trunkTop));

        for (; relativeY >= 0; relativeY--) {
            float treeShape = treeShape(height, relativeY);
            if (treeShape < 0.0F) {
                continue;
            }

            for (int i = 0; i < clustersPerY; i++) {
                double radius = treeShape * (rand.nextFloat() + BRANCH_LENGTH);
                double angle = rand.nextFloat() * 2.0F * Math.PI;
                double x = radius * Math.sin(angle) + 0.5;
                double z = radius * Math.cos(angle) + 0.5;
                BlockVector3 checkStart = origin.add(MathHelper.floor(x), relativeY - 1, MathHelper.floor(z));
                BlockVector3 checkEnd = checkStart.up(5);

                if (this.makeLimb(level, checkStart, checkEnd, false)) {
                    int dx = origin.x - checkStart.x;
                    int dz = origin.z - checkStart.z;
                    double branchHeight = checkStart.y - Math.sqrt(dx * dx + dz * dz) * BRANCH_SLOPE;
                    int branchTop = branchHeight > trunkTop ? trunkTop : (int) branchHeight;
                    BlockVector3 checkBranchBase = new BlockVector3(origin.x, branchTop, origin.z);
                    if (this.makeLimb(level, checkBranchBase, checkStart, false)) {
                        foliageCoords.add(new FoliageCoords(checkStart, checkBranchBase.y));
                    }
                }
            }
        }

        this.setDirtAt(level, origin.down());
        this.makeLimb(level, origin, origin.up(trunkHeight), true);
        this.makeBranches(level, height, origin, foliageCoords);

        for (FoliageCoords foliageCoord : foliageCoords) {
            if (this.trimBranches(height, foliageCoord.branchBase - origin.y)) {
                this.createFoliage(level, foliageCoord.pos);
            }
        }

        return true;
    }

    private boolean makeLimb(BlockManager level, BlockVector3 startPos, BlockVector3 endPos, boolean doPlace) {
        if (!doPlace && startPos.equals(endPos)) {
            return true;
        }

        BlockVector3 delta = endPos.subtract(startPos);
        int steps = this.getSteps(delta);
        if (steps == 0) {
            return true;
        }

        float dx = (float) delta.x / steps;
        float dy = (float) delta.y / steps;
        float dz = (float) delta.z / steps;

        for (int i = 0; i <= steps; i++) {
            BlockVector3 blockPos = startPos.add(
                    MathHelper.floor(0.5F + i * dx),
                    MathHelper.floor(0.5F + i * dy),
                    MathHelper.floor(0.5F + i * dz)
            );

            if (doPlace) {
                this.placeLogAt(level, blockPos, this.getLogAxis(startPos, blockPos));
            } else if (!this.isFree(level, blockPos)) {
                return false;
            }
        }

        return true;
    }

    private int getSteps(BlockVector3 pos) {
        int absX = MathHelper.abs(pos.x);
        int absY = MathHelper.abs(pos.y);
        int absZ = MathHelper.abs(pos.z);
        return Math.max(absX, Math.max(absY, absZ));
    }

    private BlockFace.Axis getLogAxis(BlockVector3 startPos, BlockVector3 blockPos) {
        BlockFace.Axis axis = BlockFace.Axis.Y;
        int xdiff = Math.abs(blockPos.x - startPos.x);
        int zdiff = Math.abs(blockPos.z - startPos.z);
        int maxdiff = Math.max(xdiff, zdiff);
        if (maxdiff > 0) {
            axis = xdiff == maxdiff ? BlockFace.Axis.X : BlockFace.Axis.Z;
        }

        return axis;
    }

    private boolean trimBranches(int height, int localY) {
        return localY >= height * 0.2;
    }

    private void makeBranches(BlockManager level, int height, BlockVector3 origin, List<FoliageCoords> foliageCoords) {
        for (FoliageCoords endCoord : foliageCoords) {
            int branchBase = endCoord.branchBase;
            BlockVector3 baseCoord = new BlockVector3(origin.x, branchBase, origin.z);
            if (!baseCoord.equals(endCoord.pos) && this.trimBranches(height, branchBase - origin.y)) {
                this.makeLimb(level, baseCoord, endCoord.pos, true);
            }
        }
    }

    private void createFoliage(BlockManager level, BlockVector3 foliagePos) {
        for (int yo = FOLIAGE_OFFSET; yo >= FOLIAGE_OFFSET - FOLIAGE_HEIGHT; yo--) {
            int currentRadius = FOLIAGE_RADIUS + (yo != FOLIAGE_OFFSET && yo != FOLIAGE_OFFSET - FOLIAGE_HEIGHT ? 1 : 0);
            this.placeLeavesRow(level, foliagePos, currentRadius, yo);
        }
    }

    private void placeLeavesRow(BlockManager level, BlockVector3 center, int radius, int yOffset) {
        int y = center.y + yOffset;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (!this.shouldSkipLocation(dx, dz, radius)) {
                    this.placeLeafAt(level, center.x + dx, y, center.z + dz);
                }
            }
        }
    }

    private boolean shouldSkipLocation(int dx, int dz, int currentRadius) {
        return square(dx + 0.5F) + square(dz + 0.5F) > currentRadius * currentRadius;
    }

    private static float treeShape(int height, int y) {
        if (y < height * 0.3F) {
            return -1.0F;
        }

        float radius = height / 2.0F;
        float adjacent = radius - y;
        float distance = MathHelper.sqrt(radius * radius - adjacent * adjacent);
        if (adjacent == 0.0F) {
            distance = radius;
        } else if (Math.abs(adjacent) >= radius) {
            return 0.0F;
        }

        return distance * 0.5F;
    }

    private static float square(float value) {
        return value * value;
    }

    private boolean isFree(BlockManager level, BlockVector3 pos) {
        return this.canGrowInto(level.getBlockIdIfCachedOrLoaded(pos.x, pos.y, pos.z));
    }

    private void placeLogAt(BlockManager level, BlockVector3 pos, BlockFace.Axis axis) {
        if (this.isFree(level, pos)) {
            level.setBlockStateAt(pos, BlockOakLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis));
        }
    }

    private void placeLeafAt(BlockManager level, int x, int y, int z) {
        Block block = level.getBlockIfCachedOrLoaded(x, y, z);
        if (block.isAir() || block instanceof BlockLeaves) {
            level.setBlockStateAt(x, y, z, this.oakLeaves);
        }
    }

    private static class FoliageCoords {
        private final BlockVector3 pos;
        private final int branchBase;

        private FoliageCoords(BlockVector3 pos, int branchBase) {
            this.pos = pos;
            this.branchBase = branchBase;
        }
    }
}
