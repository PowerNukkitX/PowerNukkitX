package cn.nukkit.level.generator.object;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.GameRule;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.Setter;

public class ObjectMangroveTree extends TreeGenerator {

    private static final BlockState LOG = BlockMangroveLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y));
    private static final BlockState ROOTS = BlockMangroveRoots.PROPERTIES.getDefaultState();
    private static final BlockState MUDDY_ROOTS = BlockMuddyMangroveRoots.PROPERTIES.getDefaultState();
    private static final BlockState MANGROVE_LEAVES = BlockMangroveLeaves.PROPERTIES.getDefaultState();
    private static final BlockState MOSS_CARPET = BlockMossCarpet.PROPERTIES.getDefaultState();
    private static final BlockState PROPAGULE = BlockMangrovePropagule.PROPERTIES.getBlockState(
            CommonBlockProperties.HANGING.createValue(true),
            CommonBlockProperties.PROPAGULE_STAGE.createValue(4)
    );

    private static final double GOLDEN_ANGLE = 2.39996;

    @Setter
    private boolean withBeenest = false;

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        int trunkHeight = 4 + rand.nextInt(4);
        int trunkBase = 2 + rand.nextInt(5);

        for (int i = 0; i < trunkHeight; i++) {
            Vector3 p = position.add(0, trunkBase + i, 0);
            level.setBlockStateAt(p, LOG);
        }

        int roots = 3 + rand.nextInt(2);
        int rootMaxLength = 30;
        double rootDroop = 0.06;
        double rootVerticalDirection = -0.4 - rand.nextFloat() * 0.3;
        double rootAngle = rand.nextFloat() * 2 * Math.PI;

        for (int r = 0; r < roots; r++) {
            double dx = Math.sin(rootAngle);
            double dy = rootVerticalDirection;
            double dz = Math.cos(rootAngle);

            double mag = Math.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= mag;
            dy /= mag;
            dz /= mag;

            for (double i = 0; i <= rootMaxLength; i += 0.5) {
                int cx = (int) Math.round(position.x + i * dx);
                int cy = (int) Math.round(position.y + trunkBase + i * dy);
                int cz = (int) Math.round(position.z + i * dz);

                Block currentBlock = level.getBlockIfCachedOrLoaded(cx, cy, cz);

                if (currentBlock.getId().equals(Block.STONE)) {
                    break;
                } else if (currentBlock.getId().equals(Block.MUD)) {
                    level.setBlockStateAt(currentBlock, MUDDY_ROOTS);
                    maybePlaceMossCarpet(level, currentBlock, rand);
                } else {
                    level.setBlockStateAt(currentBlock, ROOTS);
                    maybePlaceMossCarpet(level, currentBlock, rand);
                }

                dy -= rootDroop;
                mag = Math.sqrt(dx * dx + dy * dy + dz * dz);
                dx /= mag;
                dy /= mag;
                dz /= mag;
            }

            rootAngle += GOLDEN_ANGLE;
        }

        int sideBranchInterval = 3 + rand.nextInt(2);
        int sideBranchMinHeight = 2 + rand.nextInt(3);
        int sideBranchLengthMin = 3;
        int sideBranchLengthVariation = 2;

        double branchAngle = rand.nextFloat() * 2 * Math.PI;
        for (int i = 0; i < trunkHeight; i++) {
            if (i > sideBranchMinHeight && i % sideBranchInterval == 0) {
                double dx = Math.sin(branchAngle);
                double dy = 0.5;
                double dz = Math.cos(branchAngle);

                double mag = Math.sqrt(dx * dx + dy * dy + dz * dz);
                dx /= mag;
                dy /= mag;
                dz /= mag;

                Vector3 origin = position.add(0, trunkBase + i, 0);
                int branchLength = sideBranchLengthMin + rand.nextInt(sideBranchLengthVariation + 1);

                for (int l = 1; l <= branchLength; l++) {
                    int bx = (int) Math.round(origin.x + l * dx);
                    int by = (int) Math.round(origin.y + l * dy);
                    int bz = (int) Math.round(origin.z + l * dz);

                    level.setBlockStateAt(new Vector3(bx, by, bz), LOG);
                }

                branchAngle += GOLDEN_ANGLE;
            }
        }

        int topBranches = 10 + rand.nextInt(3);
        branchAngle = rand.nextFloat() * 2 * Math.PI;
        for (int b = 1; b <= topBranches; b++) {
            double t = (double) b / topBranches;
            double ti = 1 - t;

            double dx = Math.sin(branchAngle) * t;
            double dy = 0.4;
            double dz = Math.cos(branchAngle) * t;

            double mag = Math.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= mag;
            dy /= mag;
            dz /= mag;

            Vector3 origin = position.add(0, trunkBase + trunkHeight, 0);
            int branchLength = (int) (7 * ti + 4 * t);

            for (int l = 0; l <= branchLength; l++) {
                int bx = (int) Math.round(origin.x + l * dx);
                int by = (int) Math.round(origin.y + l * dy);
                int bz = (int) Math.round(origin.z + l * dz);

                placeLeafCluster(level, new Vector3(bx, by, bz), rand);
                level.setBlockStateAt(new Vector3(bx, by, bz), LOG);

                if (withBeenest) {
                    withBeenest = false;
                    int faceIdx = rand.nextInt(BlockFace.getHorizontals().length - 1);
                    BlockFace face = BlockFace.getHorizontals()[faceIdx];
                    Block target = level.getBlockIfCachedOrLoaded(bx, by - 1, bz).getSide(face);
                    level.setBlockStateAt(target, BlockBeeNest.PROPERTIES.getBlockState(
                            CommonBlockProperties.DIRECTION.createValue(faceIdx),
                            CommonBlockProperties.HONEY_LEVEL.createValue(rand.nextInt(0, 4)))
                    );
                    if (level.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
                        Registries.ENTITY.provideEntity(EntityID.BEE, level.getLevel().getChunk(bx >> 4, bz >> 4),
                                Entity.getDefaultNBT(new Vector3(bx, by - 2, bz)));
                        Registries.ENTITY.provideEntity(EntityID.BEE, level.getLevel().getChunk(bx >> 4, bz >> 4),
                                Entity.getDefaultNBT(new Vector3(bx, by - 2, bz)));
                    }
                }
            }

            branchAngle += GOLDEN_ANGLE;
        }
        return true;
    }

    public void placeLeafCluster(BlockManager level, Vector3 pos, RandomSourceProvider random) {
        level.setBlockStateAt(pos, MANGROVE_LEAVES);

        if (random.nextInt(15) == 0) {
            Vector3 p = pos.add(0, -1, 0);
            if (level.getBlockIfCachedOrLoaded(p).isAir()) {
                level.setBlockStateAt(p, PROPAGULE);
            }
        }

        if (random.nextInt(10) < 7) {
            int[][] offsets = {
                    {0,0,1}, {0,1,0}, {0,1,1},
                    {1,0,0}, {1,0,1}, {1,1,0}, {1,1,1}
            };

            for (int[] o : offsets) {
                Vector3 p = pos.add(o[0], o[1], o[2]);
                if (level.getBlockIfCachedOrLoaded(p).isAir()) {
                    level.setBlockStateAt(p, MANGROVE_LEAVES);
                }
            }
        }

        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (random.nextInt(5) == 0) {
                Vector3 vinePos = pos.getSide(face);
                if (level.getBlockIfCachedOrLoaded(vinePos).isAir()) {
                    addVine(level, vinePos, face);
                    addHangingVine(level, vinePos, face, random);
                }
            }
        }
    }

    private void maybePlaceMossCarpet(BlockManager level, Block rootBlock, RandomSourceProvider rand) {
        if (rand.nextInt(6) == 0) {
            Vector3 above = rootBlock.add(0, 1, 0);
            if (level.getBlockIfCachedOrLoaded(above).isAir()) {
                level.setBlockStateAt(above, MOSS_CARPET);
            }
        }
    }

    private void addVine(BlockManager level, Vector3 pos, BlockFace face) {
        int meta = switch (face) {
            case NORTH -> 1;
            case EAST -> 2;
            case SOUTH -> 4;
            case WEST -> 8;
            default -> 0;
        };
        if (meta == 0) return;

        BlockState vineState = BlockVine.PROPERTIES.getBlockState(
                CommonBlockProperties.VINE_DIRECTION_BITS.createValue(meta)
        );
        level.setBlockStateAt(pos, vineState);
    }

    private void addHangingVine(BlockManager level, Vector3 pos, BlockFace face, RandomSourceProvider rand) {
        Vector3 down = pos.clone();
        int length = 1 + rand.nextInt(3);
        for (int i = 0; i < length; i++) {
            down = down.add(0, -1, 0);
            if (level.getBlockIfCachedOrLoaded(down).isAir()) {
                addVine(level, down, face);
            } else {
                break;
            }
        }
    }
}
