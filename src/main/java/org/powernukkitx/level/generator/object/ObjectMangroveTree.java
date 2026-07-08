package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.*;
import org.powernukkitx.block.*;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.RandomSourceProvider;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/05/14
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/configured_feature/mangrove.json">Source</a>
 */
public class ObjectMangroveTree extends TreeGenerator {

    private static final BlockState LOG = BlockMangroveLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y));
    private static final BlockState ROOTS = BlockMangroveRoots.PROPERTIES.getDefaultState();
    private static final BlockState MANGROVE_LEAVES = BlockMangroveLeaves.PROPERTIES.getDefaultState();
    private static final BlockState MOSS_CARPET = BlockMossCarpet.PROPERTIES.getDefaultState();
    private static final BlockState STILL_WATER = BlockWater.PROPERTIES.getDefaultState();
    private static final int ROOT_WIDTH_LIMIT = 8;
    private static final int ROOT_LENGTH_LIMIT = 15;
    private static final float ROOT_RANDOM_SKEW_CHANCE = 0.2F;
    private static final float BRANCH_PER_LOG_PROBABILITY = 0.5F;
    private static final int LEAF_RADIUS = 3;
    private static final int LEAF_HEIGHT = 2;
    private static final int LEAF_PLACEMENT_ATTEMPTS = 70;
    private static final float VINE_PROBABILITY = 0.125F;
    private static final float PROPAGULE_PROBABILITY = 0.14F;

    @Setter
    private boolean withBeenest = false;
    private final boolean tall;

    public ObjectMangroveTree() {
        this(RandomSourceProvider.create().nextFloat() > 0.15f);
    }

    public ObjectMangroveTree(boolean tall) {
        this.tall = tall;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        MangroveProperties mangroveProperties = this.tall
                ? new MangroveProperties(4, 1, 9, 1, 6, 0, 1, 3, 7)
                : new MangroveProperties(2, 1, 4, 1, 4, 0, 1, 1, 3);

        int originX = position.getFloorX();
        int originY = position.getFloorY();
        int originZ = position.getFloorZ();
        int trunkOffsetY = NukkitMath.randomRange(rand, mangroveProperties.rootOffsetMin(), mangroveProperties.rootOffsetMax());
        BlockVector3 trunkOrigin = new BlockVector3(originX, originY + trunkOffsetY, originZ);
        int treeHeight = mangroveProperties.baseHeight() + rand.nextInt(mangroveProperties.heightRandA() + 1) + rand.nextInt(mangroveProperties.heightRandB() + 1);

        if (!placeRoots(level, rand, new BlockVector3(originX, originY, originZ), trunkOrigin)) {
            return false;
        }

        List<BlockVector3> foliageAttachments = placeTrunk(level, rand, treeHeight, trunkOrigin, mangroveProperties);
        for (BlockVector3 attachment : foliageAttachments) {
            createRandomSpreadFoliage(level, rand, attachment);
        }

        placeLeafVines(level, rand, foliageAttachments);
        placePropagules(level, rand, foliageAttachments);
        placeBeeNest(level, rand, foliageAttachments);
        return true;
    }

    private List<BlockVector3> placeTrunk(BlockManager level, RandomSourceProvider random, int treeHeight, BlockVector3 origin, MangroveProperties mangroveProperties) {
        List<BlockVector3> attachments = new ArrayList<>();

        for (int heightPos = 0; heightPos < treeHeight; heightPos++) {
            int currentHeight = origin.y + heightPos;
            BlockVector3 logPos = new BlockVector3(origin.x, currentHeight, origin.z);
            if (placeLog(level, logPos)
                    && heightPos < treeHeight - 1
                    && random.nextFloat() < BRANCH_PER_LOG_PROBABILITY) {
                BlockFace branchDir = randomHorizontal(random);
                int branchLen = NukkitMath.randomRange(random, mangroveProperties.extraBranchLengthMin(), mangroveProperties.extraBranchLengthMax());
                int branchPos = Math.max(0, branchLen - NukkitMath.randomRange(random, mangroveProperties.extraBranchLengthMin(), mangroveProperties.extraBranchLengthMax()) - 1);
                int branchSteps = NukkitMath.randomRange(random, mangroveProperties.extraBranchStepsMin(), mangroveProperties.extraBranchStepsMax());
                placeBranch(level, treeHeight, attachments, logPos, currentHeight, branchDir, branchPos, branchSteps);
            }

            if (heightPos == treeHeight - 1) {
                attachments.add(new BlockVector3(origin.x, currentHeight + 1, origin.z));
            }
        }

        return attachments;
    }

    private void placeBranch(BlockManager level, int treeHeight, List<BlockVector3> attachments, BlockVector3 logPos,
                             int currentHeight, BlockFace branchDir, int branchPos, int branchSteps) {
        int heightAlongBranch = currentHeight + branchPos;
        int logX = logPos.x;
        int logZ = logPos.z;
        int branchPlacementIndex = branchPos;

        while (branchPlacementIndex < treeHeight && branchSteps > 0) {
            if (branchPlacementIndex >= 1) {
                int placementHeight = currentHeight + branchPlacementIndex;
                logX += branchDir.getXOffset();
                logZ += branchDir.getZOffset();
                BlockVector3 branchLogPos = new BlockVector3(logX, placementHeight, logZ);
                heightAlongBranch = placementHeight;
                if (placeLog(level, branchLogPos)) {
                    heightAlongBranch = placementHeight + 1;
                }
                attachments.add(branchLogPos);
            }

            branchPlacementIndex++;
            branchSteps--;
        }

        if (heightAlongBranch - currentHeight > 1) {
            BlockVector3 foliagePos = new BlockVector3(logX, heightAlongBranch, logZ);
            attachments.add(foliagePos);
            attachments.add(foliagePos.down(2));
        }
    }

    private boolean placeRoots(BlockManager level, RandomSourceProvider random, BlockVector3 origin, BlockVector3 trunkOrigin) {
        List<BlockVector3> rootPositions = new ArrayList<>();
        BlockVector3 columnPos = origin.clone();

        while (columnPos.y < trunkOrigin.y) {
            if (!canPlaceRoot(level.getBlockIfCachedOrLoaded(columnPos.asVector3()))) {
                return false;
            }
            columnPos = columnPos.up();
        }

        rootPositions.add(trunkOrigin.down());
        for (BlockFace direction : BlockFace.Plane.HORIZONTAL) {
            BlockVector3 pos = trunkOrigin.getSide(direction);
            List<BlockVector3> positionsInDirection = new ArrayList<>();
            if (!simulateRoots(level, random, pos, direction, trunkOrigin, positionsInDirection, 0)) {
                return false;
            }

            rootPositions.addAll(positionsInDirection);
            rootPositions.add(pos);
        }

        for (BlockVector3 rootPos : rootPositions) {
            placeRoot(level, random, rootPos);
        }
        return true;
    }

    private boolean simulateRoots(BlockManager level, RandomSourceProvider random, BlockVector3 rootPos, BlockFace dir,
                                  BlockVector3 rootOrigin, List<BlockVector3> rootPositions, int layer) {
        if (layer == ROOT_LENGTH_LIMIT || rootPositions.size() > ROOT_LENGTH_LIMIT) {
            return false;
        }

        for (BlockVector3 pos : potentialRootPositions(rootPos, dir, random, rootOrigin)) {
            if (canPlaceRoot(level.getBlockIfCachedOrLoaded(pos.asVector3()))) {
                rootPositions.add(pos);
                if (!simulateRoots(level, random, pos, dir, rootOrigin, rootPositions, layer + 1)) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<BlockVector3> potentialRootPositions(BlockVector3 pos, BlockFace previousDir, RandomSourceProvider random, BlockVector3 rootOrigin) {
        BlockVector3 below = pos.down();
        BlockVector3 nextTo = pos.getSide(previousDir);
        int width = Math.abs(pos.x - rootOrigin.x) + Math.abs(pos.y - rootOrigin.y) + Math.abs(pos.z - rootOrigin.z);

        if (width > ROOT_WIDTH_LIMIT - 3 && width <= ROOT_WIDTH_LIMIT) {
            return random.nextFloat() < ROOT_RANDOM_SKEW_CHANCE ? List.of(below, nextTo.down()) : List.of(below);
        } else if (width > ROOT_WIDTH_LIMIT) {
            return List.of(below);
        } else if (random.nextFloat() < ROOT_RANDOM_SKEW_CHANCE) {
            return List.of(below);
        }
        return random.nextBoolean() ? List.of(nextTo) : List.of(below);
    }

    private void placeRoot(BlockManager level, RandomSourceProvider random, BlockVector3 pos) {
        Block previous = level.getBlockIfCachedOrLoaded(pos.asVector3());
        BlockState state = BlockID.MUD.equals(previous.getId()) || BlockID.MUDDY_MANGROVE_ROOTS.equals(previous.getId())
                ? BlockMuddyMangroveRoots.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y))
                : ROOTS;
        placeWithWaterlogging(level, pos.asVector3(), state, previous);

        if (random.nextFloat() < 0.5F) {
            BlockVector3 above = pos.up();
            if (level.getBlockIfCachedOrLoaded(above.asVector3()).isAir()) {
                level.setBlockStateAt(above, MOSS_CARPET);
            }
        }
    }

    private void createRandomSpreadFoliage(BlockManager level, RandomSourceProvider random, BlockVector3 origin) {
        for (int i = 0; i < LEAF_PLACEMENT_ATTEMPTS; i++) {
            int x = origin.x + random.nextInt(LEAF_RADIUS) - random.nextInt(LEAF_RADIUS);
            int y = origin.y + random.nextInt(LEAF_HEIGHT) - random.nextInt(LEAF_HEIGHT);
            int z = origin.z + random.nextInt(LEAF_RADIUS) - random.nextInt(LEAF_RADIUS);
            placeLeaf(level, new BlockVector3(x, y, z));
        }
    }

    private void placeLeafVines(BlockManager level, RandomSourceProvider random, List<BlockVector3> foliageAttachments) {
        Set<BlockVector3> leaves = collectPlacedLeaves(level, foliageAttachments);
        for (BlockVector3 leafPos : leaves) {
            maybePlaceVine(level, random, leafPos.west(), BlockFace.EAST);
            maybePlaceVine(level, random, leafPos.east(), BlockFace.WEST);
            maybePlaceVine(level, random, leafPos.north(), BlockFace.SOUTH);
            maybePlaceVine(level, random, leafPos.south(), BlockFace.NORTH);
        }
    }

    private void maybePlaceVine(BlockManager level, RandomSourceProvider random, BlockVector3 pos, BlockFace attachedTo) {
        if (random.nextFloat() < VINE_PROBABILITY && level.getBlockIfCachedOrLoaded(pos.asVector3()).isAir()) {
            addHangingVine(level, pos, attachedTo);
        }
    }

    private void addHangingVine(BlockManager level, BlockVector3 pos, BlockFace attachedTo) {
        placeVine(level, pos, attachedTo);
        BlockVector3 vinePos = pos.down();
        int maxLength = 4;

        while (level.getBlockIfCachedOrLoaded(vinePos.asVector3()).isAir() && maxLength > 0) {
            placeVine(level, vinePos, attachedTo);
            vinePos = vinePos.down();
            maxLength--;
        }
    }

    private void placePropagules(BlockManager level, RandomSourceProvider random, List<BlockVector3> foliageAttachments) {
        Set<BlockVector3> blacklist = new HashSet<>();
        List<BlockVector3> leaves = new ArrayList<>(collectPlacedLeaves(level, foliageAttachments));
        Collections.shuffle(leaves, new java.util.Random(random.nextLong()));

        for (BlockVector3 leafPos : leaves) {
            BlockVector3 placementPos = leafPos.down();
            if (!blacklist.contains(placementPos)
                    && random.nextFloat() < PROPAGULE_PROBABILITY
                    && hasRequiredEmptyBlocks(level, leafPos, BlockFace.DOWN, 2)) {
                for (int x = placementPos.x - 1; x <= placementPos.x + 1; x++) {
                    for (int y = placementPos.y; y <= placementPos.y; y++) {
                        for (int z = placementPos.z - 1; z <= placementPos.z + 1; z++) {
                            blacklist.add(new BlockVector3(x, y, z));
                        }
                    }
                }

                BlockState propagule = BlockMangrovePropagule.PROPERTIES.getBlockState(
                        CommonBlockProperties.HANGING.createValue(true),
                        CommonBlockProperties.PROPAGULE_STAGE.createValue(NukkitMath.randomRange(random, 0, 4))
                );
                level.setBlockStateAt(placementPos, propagule);
            }
        }
    }

    private boolean hasRequiredEmptyBlocks(BlockManager level, BlockVector3 leafPos, BlockFace direction, int count) {
        for (int i = 1; i <= count; i++) {
            if (!level.getBlockIfCachedOrLoaded(leafPos.getSide(direction, i).asVector3()).isAir()) {
                return false;
            }
        }
        return true;
    }

    private void placeBeeNest(BlockManager level, RandomSourceProvider random, List<BlockVector3> foliageAttachments) {
        if (!withBeenest || foliageAttachments.isEmpty()) {
            return;
        }

        BlockVector3 attachment = foliageAttachments.get(random.nextInt(foliageAttachments.size()));
        BlockFace face = randomHorizontal(random);
        BlockVector3 nestPos = attachment.down().getSide(face);
        if (!level.getBlockIfCachedOrLoaded(nestPos.asVector3()).isAir()) {
            return;
        }

        level.setBlockStateAt(nestPos, BlockBeeNest.PROPERTIES.getBlockState(
                CommonBlockProperties.DIRECTION.createValue(face.getOpposite().getHorizontalIndex()),
                CommonBlockProperties.HONEY_LEVEL.createValue(NukkitMath.randomRange(random, 0, 3))
        ));
        if (level.getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            Registries.ENTITY.provideEntity(EntityID.BEE, level.getLevel().getChunk(nestPos.x >> 4, nestPos.z >> 4),
                    Entity.getDefaultNBT(new Vector3(nestPos.x, nestPos.y - 1, nestPos.z)));
            Registries.ENTITY.provideEntity(EntityID.BEE, level.getLevel().getChunk(nestPos.x >> 4, nestPos.z >> 4),
                    Entity.getDefaultNBT(new Vector3(nestPos.x, nestPos.y - 1, nestPos.z)));
        }
    }

    private Set<BlockVector3> collectPlacedLeaves(BlockManager level, List<BlockVector3> foliageAttachments) {
        Set<BlockVector3> leaves = new HashSet<>();
        for (BlockVector3 origin : foliageAttachments) {
            for (int x = origin.x - LEAF_RADIUS + 1; x <= origin.x + LEAF_RADIUS - 1; x++) {
                for (int y = origin.y - LEAF_HEIGHT + 1; y <= origin.y + LEAF_HEIGHT - 1; y++) {
                    for (int z = origin.z - LEAF_RADIUS + 1; z <= origin.z + LEAF_RADIUS - 1; z++) {
                        BlockVector3 pos = new BlockVector3(x, y, z);
                        if (BlockID.MANGROVE_LEAVES.equals(level.getBlockIfCachedOrLoaded(pos.asVector3()).getId())) {
                            leaves.add(pos);
                        }
                    }
                }
            }
        }
        return leaves;
    }

    private boolean placeLog(BlockManager level, BlockVector3 pos) {
        Block previous = level.getBlockIfCachedOrLoaded(pos.asVector3());
        if (!canPlaceLogInto(previous)) {
            return false;
        }
        placeWithWaterlogging(level, pos.asVector3(), LOG, previous);
        return true;
    }

    private void placeLeaf(BlockManager level, BlockVector3 pos) {
        Block previous = level.getBlockIfCachedOrLoaded(pos.asVector3());
        if (canPlaceLeafInto(previous.getId())) {
            placeWithWaterlogging(level, pos.asVector3(), MANGROVE_LEAVES, previous);
        }
    }

    private void placeVine(BlockManager level, BlockVector3 pos, BlockFace attachedTo) {
        level.setBlockStateAt(pos, BlockVine.PROPERTIES.getBlockState(
                CommonBlockProperties.VINE_DIRECTION_BITS.createValue(getVineMeta(attachedTo))
        ));
    }

    private static int getVineMeta(BlockFace attachedTo) {
        return switch (attachedTo) {
            case SOUTH -> 1;
            case WEST -> 2;
            case NORTH -> 4;
            case EAST -> 8;
            default -> 0;
        };
    }

    private static BlockFace randomHorizontal(RandomSourceProvider random) {
        return BlockFace.getHorizontals()[random.nextBoundedInt(BlockFace.getHorizontals().length - 1)];
    }

    private static boolean canPlaceLogInto(Block block) {
        String id = block.getId();
        return block.isAir()
                || BlockID.WATER.equals(id)
                || BlockID.FLOWING_WATER.equals(id)
                || BlockID.MANGROVE_LEAVES.equals(id)
                || BlockID.VINE.equals(id)
                || BlockID.MANGROVE_PROPAGULE.equals(id);
    }

    private static boolean canPlaceLeafInto(String id) {
        return BlockID.AIR.equals(id)
                || BlockID.WATER.equals(id)
                || BlockID.FLOWING_WATER.equals(id)
                || BlockID.VINE.equals(id)
                || BlockID.MANGROVE_PROPAGULE.equals(id);
    }

    private static boolean canPlaceRoot(Block block) {
        String id = block.getId();
        return block.isAir()
                || BlockID.WATER.equals(id)
                || BlockID.FLOWING_WATER.equals(id)
                || BlockID.MUD.equals(id)
                || BlockID.MANGROVE_ROOTS.equals(id)
                || BlockID.MUDDY_MANGROVE_ROOTS.equals(id)
                || BlockID.MANGROVE_LEAVES.equals(id)
                || BlockID.MANGROVE_PROPAGULE.equals(id)
                || BlockID.VINE.equals(id);
    }

    private static void placeWithWaterlogging(BlockManager level, Vector3 pos, BlockState state, Block previousBlock) {
        level.setBlockStateAt(pos, state);
        String id = previousBlock.getId();
        if (BlockID.WATER.equals(id) || BlockID.FLOWING_WATER.equals(id)) {
            level.setBlockStateAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), 1, STILL_WATER);
        }
    }

    private record MangroveProperties(
            int baseHeight,
            int heightRandA,
            int heightRandB,
            int extraBranchStepsMin,
            int extraBranchStepsMax,
            int extraBranchLengthMin,
            int extraBranchLengthMax,
            int rootOffsetMin,
            int rootOffsetMax
    ) {}
}
