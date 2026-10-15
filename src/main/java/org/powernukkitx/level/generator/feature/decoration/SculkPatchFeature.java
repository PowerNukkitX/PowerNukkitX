package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.BlockSculk;
import org.powernukkitx.block.BlockSculkCatalyst;
import org.powernukkitx.block.BlockSculkSensor;
import org.powernukkitx.block.BlockSculkShrieker;
import org.powernukkitx.block.BlockSculkVein;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SculkPatchFeature extends GenerateFeature {

    public static final String NAME = "minecraft:sculk_patch_feature";

    private static final int ORIGIN_ATTEMPTS = 256;
    private static final int MIN_ORIGIN_Y = -63;
    private static final int MAX_ORIGIN_Y = 256;
    private static final int CURSOR_COUNT = 10;
    private static final int CHARGE_AMOUNT = 32;
    private static final int SPREAD_ATTEMPTS = 64;
    private static final int SPREAD_ROUNDS = 1;
    private static final int GROWTH_ROUNDS = 0;
    private static final int WORLD_GEN_MAX_RADIUS_SQUARED = 15 * 15;
    private static final int GROWTH_SPAWN_COST = 50;
    private static final int NO_GROWTH_RADIUS = 1;
    private static final int CHARGE_DECAY_RATE = 5;
    private static final int ADDITIONAL_DECAY_RATE = 10;
    private static final float CATALYST_CHANCE = 0.5f;

    private static final BlockState AIR = BlockAir.STATE;
    private static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    private static final BlockState SCULK = BlockSculk.PROPERTIES.getDefaultState();
    private static final BlockState SCULK_CATALYST = BlockSculkCatalyst.PROPERTIES.getDefaultState();
    private static final BlockState SCULK_SENSOR = BlockSculkSensor.PROPERTIES.getDefaultState();
    private static final BlockState SCULK_SHRIEKER = BlockSculkShrieker.PROPERTIES.getBlockState(
            CommonBlockProperties.CAN_SUMMON.createValue(true));

    private static final Set<String> SCULK_REPLACEABLE = Set.of(
            "minecraft:stone",
            "minecraft:granite",
            "minecraft:diorite",
            "minecraft:andesite",
            "minecraft:tuff",
            "minecraft:deepslate",
            "minecraft:dirt",
            "minecraft:grass_block",
            "minecraft:podzol",
            "minecraft:coarse_dirt",
            "minecraft:mycelium",
            "minecraft:dirt_with_roots",
            "minecraft:moss_block",
            "minecraft:pale_moss_block",
            "minecraft:mud",
            "minecraft:muddy_mangrove_roots",
            "minecraft:hardened_clay",
            "minecraft:white_terracotta",
            "minecraft:orange_terracotta",
            "minecraft:magenta_terracotta",
            "minecraft:light_blue_terracotta",
            "minecraft:yellow_terracotta",
            "minecraft:lime_terracotta",
            "minecraft:pink_terracotta",
            "minecraft:gray_terracotta",
            "minecraft:light_gray_terracotta",
            "minecraft:cyan_terracotta",
            "minecraft:purple_terracotta",
            "minecraft:blue_terracotta",
            "minecraft:brown_terracotta",
            "minecraft:green_terracotta",
            "minecraft:red_terracotta",
            "minecraft:black_terracotta",
            "minecraft:crimson_nylium",
            "minecraft:warped_nylium",
            "minecraft:netherrack",
            "minecraft:basalt",
            "minecraft:blackstone",
            "minecraft:sand",
            "minecraft:red_sand",
            "minecraft:gravel",
            "minecraft:soul_sand",
            "minecraft:soul_soil",
            "minecraft:calcite",
            "minecraft:smooth_basalt",
            "minecraft:clay",
            "minecraft:dripstone_block",
            "minecraft:end_stone",
            "minecraft:red_sandstone",
            "minecraft:sandstone",
            "minecraft:sulfur",
            "minecraft:cinnabar"
    );

    private static final Set<String> SCULK_REPLACEABLE_WORLD_GEN = withWorldGenReplaceables(SCULK_REPLACEABLE);

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int minY = Math.max(level.getMinHeight(), MIN_ORIGIN_Y);
        int maxY = Math.min(level.getMaxHeight() - 1, MAX_ORIGIN_Y);
        if (minY > maxY) {
            return;
        }

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        BlockManager manager = new BlockManager(level);
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        int yBound = maxY - minY + 1;

        for (int attempt = 0; attempt < ORIGIN_ATTEMPTS; attempt++) {
            int localX = random.nextInt(16);
            int y = minY + random.nextInt(yBound);
            int localZ = random.nextInt(16);
            if (chunk.getBiomeId(localX, y, localZ) != BiomeID.DEEP_DARK) {
                continue;
            }

            BlockVector3 origin = new BlockVector3(baseX + localX, y, baseZ + localZ);
            if (canSpreadFrom(manager, origin)) {
                placePatch(manager, origin);
            }
        }

        queueObject(chunk, manager);
    }

    private void placePatch(BlockManager manager, BlockVector3 origin) {
        int totalRounds = SPREAD_ROUNDS + GROWTH_ROUNDS;
        for (int round = 0; round < totalRounds; round++) {
            List<ChargeCursor> cursors = new ArrayList<>(CURSOR_COUNT);
            for (int i = 0; i < CURSOR_COUNT; i++) {
                cursors.add(new ChargeCursor(origin, CHARGE_AMOUNT));
            }

            boolean spreadVeins = round < SPREAD_ROUNDS;
            for (int attempt = 0; attempt < SPREAD_ATTEMPTS && cursors.size() > 0; attempt++) {
                updateCursors(manager, origin, cursors, spreadVeins);
            }
        }

        BlockVector3 below = origin.down();
        if (random.nextFloat() <= CATALYST_CHANCE && isFullSupport(manager, below)) {
            manager.setBlockStateAt(origin, SCULK_CATALYST);
            clearWaterloggedLayer(manager, origin);
        }
    }

    private boolean canSpreadFrom(BlockManager manager, BlockVector3 origin) {
        Block start = getBlock(manager, origin);
        if (isSculkBehaviour(start)) {
            return true;
        }
        if (!start.isAir() && !isSourceWater(start)) {
            return false;
        }

        for (BlockFace face : BlockFace.values()) {
            if (isFullSupport(manager, origin.getSide(face))) {
                return true;
            }
        }
        return false;
    }

    private void updateCursors(BlockManager manager, BlockVector3 origin, List<ChargeCursor> cursors, boolean spreadVeins) {
        Iterator<ChargeCursor> iterator = cursors.iterator();
        while (iterator.hasNext()) {
            ChargeCursor cursor = iterator.next();
            updateCursor(manager, origin, cursor, spreadVeins);
            if (cursor.charge <= 0) {
                iterator.remove();
            }
        }
    }

    private void updateCursor(BlockManager manager, BlockVector3 origin, ChargeCursor cursor, boolean spreadVeins) {
        if (cursor.updateDelay > 0) {
            cursor.updateDelay--;
            return;
        }

        Block current = getBlock(manager, cursor.pos);
        SculkBehaviour behaviour = getBehaviour(current);
        if (spreadVeins && attemptSpreadVein(manager, cursor, current, behaviour) && behaviour != SculkBehaviour.SCULK) {
            current = getBlock(manager, cursor.pos);
            behaviour = getBehaviour(current);
        }

        cursor.charge = attemptUseCharge(manager, origin, cursor, behaviour, spreadVeins);
        if (cursor.charge <= 0) {
            onDischarged(manager, cursor.pos, behaviour);
            return;
        }

        BlockVector3 transferPos = getValidMovementPos(manager, cursor.pos);
        if (transferPos != null) {
            onDischarged(manager, cursor.pos, behaviour);
            cursor.pos = transferPos;

            int dx = cursor.pos.x - origin.x;
            int dy = cursor.pos.y - origin.y;
            int dz = cursor.pos.z - origin.z;
            if (dx * dx + dy * dy + dz * dz >= WORLD_GEN_MAX_RADIUS_SQUARED) {
                cursor.charge = 0;
                return;
            }

            current = getBlock(manager, cursor.pos);
            behaviour = getBehaviour(current);
        }

        if (isSculkBehaviour(current)) {
            cursor.facings = current instanceof BlockSculkVein
                    ? current.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS)
                    : ChargeCursor.SCULK_FACING_DATA;
        }

        cursor.decayDelay = behaviour == SculkBehaviour.DEFAULT ? Math.max(cursor.decayDelay - 1, 0) : 1;
        cursor.updateDelay = 1;
    }

    private boolean attemptSpreadVein(BlockManager manager, ChargeCursor cursor, Block current, SculkBehaviour behaviour) {
        if (behaviour != SculkBehaviour.DEFAULT) {
            return spreadVeins(manager, cursor.pos);
        }
        if (cursor.facings == ChargeCursor.NO_FACING_DATA) {
            return spreadVeinsSamePosition(manager, cursor.pos);
        }
        if (cursor.facings == ChargeCursor.SCULK_FACING_DATA) {
            return spreadVeins(manager, cursor.pos);
        }
        if (cursor.facings != 0) {
            return (current.isAir() || isWater(current)) && regrowVein(manager, cursor.pos, cursor.facings);
        }
        return spreadVeins(manager, cursor.pos);
    }

    private int attemptUseCharge(
            BlockManager manager,
            BlockVector3 origin,
            ChargeCursor cursor,
            SculkBehaviour behaviour,
            boolean spreadVeins
    ) {
        return switch (behaviour) {
            case VEIN -> attemptUseVeinCharge(manager, cursor, spreadVeins);
            case SCULK -> attemptUseSculkCharge(manager, origin, cursor);
            case DEFAULT -> cursor.decayDelay > 0 ? cursor.charge : 0;
        };
    }

    private int attemptUseVeinCharge(BlockManager manager, ChargeCursor cursor, boolean spreadVeins) {
        if (spreadVeins && attemptPlaceSculk(manager, cursor.pos)) {
            return cursor.charge - 1;
        }
        return random.nextInt(CHARGE_DECAY_RATE) == 0 ? (int) Math.floor(cursor.charge * 0.5f) : cursor.charge;
    }

    private int attemptUseSculkCharge(BlockManager manager, BlockVector3 origin, ChargeCursor cursor) {
        int charge = cursor.charge;
        if (charge == 0 || random.nextInt(CHARGE_DECAY_RATE) != 0) {
            return charge;
        }

        boolean closeToOrigin = distanceSquared(cursor.pos, origin) < NO_GROWTH_RADIUS * NO_GROWTH_RADIUS;
        if (!closeToOrigin && canPlaceGrowth(manager, cursor.pos)) {
            if (random.nextInt(GROWTH_SPAWN_COST) < charge) {
                placeGrowth(manager, cursor.pos.up());
            }
            return Math.max(0, charge - GROWTH_SPAWN_COST);
        }

        if (random.nextInt(ADDITIONAL_DECAY_RATE) != 0) {
            return charge;
        }
        return charge - (closeToOrigin ? 1 : getDecayPenalty(cursor.pos, origin, charge));
    }

    private int getDecayPenalty(BlockVector3 pos, BlockVector3 origin, int charge) {
        float distance = (float) Math.sqrt(distanceSquared(pos, origin));
        float outerDistance = distance - NO_GROWTH_RADIUS;
        float distanceFactor = Math.min(1.0f, outerDistance * outerDistance / ((24 - NO_GROWTH_RADIUS) * (24 - NO_GROWTH_RADIUS)));
        return Math.max(1, (int) (charge * distanceFactor * 0.5f));
    }

    private boolean canPlaceGrowth(BlockManager manager, BlockVector3 pos) {
        BlockVector3 above = pos.up();
        Block stateAbove = getBlock(manager, above);
        if (!stateAbove.isAir() && !isWater(stateAbove)) {
            return false;
        }

        int growthCount = 0;
        for (int x = pos.x - 4; x <= pos.x + 4; x++) {
            for (int y = pos.y; y <= pos.y + 2; y++) {
                for (int z = pos.z - 4; z <= pos.z + 4; z++) {
                    String id = manager.getBlockIdAt(x, y, z);
                    if (BlockID.SCULK_SENSOR.equals(id) || BlockID.SCULK_SHRIEKER.equals(id)) {
                        growthCount++;
                        if (growthCount > 2) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void placeGrowth(BlockManager manager, BlockVector3 pos) {
        boolean waterlogged = isWater(getBlock(manager, pos));
        BlockState growth = random.nextInt(11) == 0 ? SCULK_SHRIEKER : SCULK_SENSOR;
        manager.setBlockStateAt(pos, growth);
        if (waterlogged) {
            manager.setBlockStateAt(pos.x, pos.y, pos.z, 1, WATER);
        }
    }

    private boolean attemptPlaceSculk(BlockManager manager, BlockVector3 pos) {
        Block vein = getBlock(manager, pos);
        if (!(vein instanceof BlockSculkVein)) {
            return false;
        }

        int bits = vein.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
        for (BlockFace supportFace : shuffledFaces()) {
            if (!hasFace(bits, supportFace)) {
                continue;
            }

            BlockVector3 supportPos = pos.getSide(supportFace);
            Block support = getBlock(manager, supportPos);
            if (!SCULK_REPLACEABLE_WORLD_GEN.contains(support.getId())) {
                continue;
            }

            manager.setBlockStateAt(supportPos, SCULK);
            spreadVeins(manager, supportPos);
            BlockFace skip = supportFace.getOpposite();

            for (BlockFace face : BlockFace.values()) {
                if (face == skip) {
                    continue;
                }
                BlockVector3 veinPos = supportPos.getSide(face);
                if (getBlock(manager, veinPos) instanceof BlockSculkVein) {
                    dischargeVein(manager, veinPos);
                }
            }
            return true;
        }
        return false;
    }

    private boolean spreadVeinsSamePosition(BlockManager manager, BlockVector3 pos) {
        boolean spread = false;
        for (BlockFace face : BlockFace.values()) {
            spread |= placeVeinFace(manager, pos, pos, face);
        }
        return spread;
    }

    private boolean regrowVein(BlockManager manager, BlockVector3 pos, int facings) {
        int validFacings = 0;
        for (BlockFace face : BlockFace.values()) {
            if (hasFace(facings, face) && canAttachTo(manager, pos, face)) {
                validFacings |= faceBit(face);
            }
        }
        if (validFacings == 0) {
            return false;
        }

        boolean waterlogged = isWater(getBlock(manager, pos));
        manager.setBlockStateAt(pos, veinState(validFacings));
        if (waterlogged) {
            manager.setBlockStateAt(pos.x, pos.y, pos.z, 1, WATER);
        }
        return true;
    }

    private boolean spreadVeins(BlockManager manager, BlockVector3 sourcePos) {
        Block source = getBlock(manager, sourcePos);
        int sourceFacings = source instanceof BlockSculkVein
                ? source.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS)
                : ChargeCursor.NO_FACING_DATA;
        boolean spread = false;

        for (BlockFace startingFace : BlockFace.values()) {
            if (sourceFacings != ChargeCursor.NO_FACING_DATA && !hasFace(sourceFacings, startingFace)) {
                continue;
            }

            for (BlockFace spreadDirection : BlockFace.values()) {
                if (startingFace.getAxis() == spreadDirection.getAxis()) {
                    continue;
                }
                spread |= spreadFromFaceTowardDirection(manager, sourcePos, startingFace, spreadDirection);
            }
        }
        return spread;
    }

    private boolean spreadFromFaceTowardDirection(
            BlockManager manager,
            BlockVector3 sourcePos,
            BlockFace startingFace,
            BlockFace spreadDirection
    ) {
        if (placeVeinFace(manager, sourcePos, sourcePos, spreadDirection)) {
            return true;
        }

        BlockVector3 samePlane = sourcePos.getSide(spreadDirection);
        if (placeVeinFace(manager, sourcePos, samePlane, startingFace)) {
            return true;
        }

        BlockVector3 wrapAround = samePlane.getSide(startingFace);
        return placeVeinFace(manager, sourcePos, wrapAround, spreadDirection.getOpposite());
    }

    private boolean placeVeinFace(BlockManager manager, BlockVector3 sourcePos, BlockVector3 placementPos, BlockFace face) {
        if (!isValidY(manager.getLevel(), placementPos.y) || !canPlaceVeinFace(manager, sourcePos, placementPos, face)) {
            return false;
        }

        Block existing = getBlock(manager, placementPos);
        int bits = existing instanceof BlockSculkVein
                ? existing.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS)
                : 0;
        int bit = faceBit(face);
        if ((bits & bit) != 0) {
            return false;
        }

        boolean waterlogged = isWater(existing) || isWaterlogged(manager, placementPos);
        manager.setBlockStateAt(placementPos, veinState(bits | bit));
        if (waterlogged) {
            manager.setBlockStateAt(placementPos.x, placementPos.y, placementPos.z, 1, WATER);
        }
        return true;
    }

    private boolean canPlaceVeinFace(BlockManager manager, BlockVector3 sourcePos, BlockVector3 placementPos, BlockFace face) {
        Block existing = getBlock(manager, placementPos);
        if (!canReplaceWithVein(existing)) {
            return false;
        }

        BlockVector3 supportPos = placementPos.getSide(face);
        Block support = getBlock(manager, supportPos);
        String supportId = support.getId();
        if (BlockID.SCULK.equals(supportId) || BlockID.SCULK_CATALYST.equals(supportId) || "minecraft:moving_block".equals(supportId)) {
            return false;
        }

        if (manhattanDistance(sourcePos, placementPos) == 2) {
            BlockVector3 blockerPos = sourcePos.getSide(face.getOpposite());
            if (getBlock(manager, blockerPos).isSolid(face)) {
                return false;
            }
        }
        return support.isSolid(face.getOpposite());
    }

    private boolean canReplaceWithVein(Block block) {
        if (block instanceof BlockSculkVein || block.isAir() || isWater(block)) {
            return true;
        }
        if (block instanceof BlockLiquid || BlockID.FIRE.equals(block.getId()) || BlockID.SOUL_FIRE.equals(block.getId())) {
            return false;
        }
        return block.canBeReplaced();
    }

    private BlockVector3 getValidMovementPos(BlockManager manager, BlockVector3 pos) {
        List<BlockVector3> offsets = shuffledMovementOffsets();
        BlockVector3 selected = null;

        for (BlockVector3 offset : offsets) {
            BlockVector3 candidate = pos.add(offset);
            Block state = getBlock(manager, candidate);
            if (!isSculkBehaviour(state) || !isMovementUnobstructed(manager, pos, candidate)) {
                continue;
            }

            selected = candidate;
            if (state instanceof BlockSculkVein && hasSubstrateAccess(manager, candidate, state)) {
                break;
            }
        }
        return selected;
    }

    private boolean hasSubstrateAccess(BlockManager manager, BlockVector3 pos, Block vein) {
        int bits = vein.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
        for (BlockFace face : BlockFace.values()) {
            if (hasFace(bits, face) && SCULK_REPLACEABLE.contains(getBlock(manager, pos.getSide(face)).getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isMovementUnobstructed(BlockManager manager, BlockVector3 from, BlockVector3 to) {
        int dx = to.x - from.x;
        int dy = to.y - from.y;
        int dz = to.z - from.z;
        int manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
        if (manhattan == 1) {
            return true;
        }

        BlockFace first = dx != 0 ? faceForDelta(dx, BlockFace.WEST, BlockFace.EAST)
                : faceForDelta(dy, BlockFace.DOWN, BlockFace.UP);
        BlockFace second = dz != 0 ? faceForDelta(dz, BlockFace.NORTH, BlockFace.SOUTH)
                : faceForDelta(dy, BlockFace.DOWN, BlockFace.UP);
        return isUnobstructed(manager, from, first) || isUnobstructed(manager, from, second);
    }

    private boolean isUnobstructed(BlockManager manager, BlockVector3 from, BlockFace face) {
        Block test = getBlock(manager, from.getSide(face));
        return !test.isSolid(face.getOpposite());
    }

    private List<BlockVector3> shuffledMovementOffsets() {
        List<BlockVector3> offsets = new ArrayList<>(18);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if ((x == 0 || y == 0 || z == 0) && (x != 0 || y != 0 || z != 0)) {
                        offsets.add(new BlockVector3(x, y, z));
                    }
                }
            }
        }

        for (int i = offsets.size() - 1; i > 0; i--) {
            int swapIndex = random.nextInt(i + 1);
            BlockVector3 value = offsets.get(i);
            offsets.set(i, offsets.get(swapIndex));
            offsets.set(swapIndex, value);
        }
        return offsets;
    }

    private BlockFace[] shuffledFaces() {
        BlockFace[] faces = BlockFace.values();
        for (int i = faces.length - 1; i > 0; i--) {
            int swapIndex = random.nextInt(i + 1);
            BlockFace value = faces[i];
            faces[i] = faces[swapIndex];
            faces[swapIndex] = value;
        }
        return faces;
    }

    private void onDischarged(BlockManager manager, BlockVector3 pos, SculkBehaviour behaviour) {
        if (behaviour == SculkBehaviour.VEIN) {
            dischargeVein(manager, pos);
        }
    }

    private void dischargeVein(BlockManager manager, BlockVector3 pos) {
        Block block = getBlock(manager, pos);
        if (!(block instanceof BlockSculkVein)) {
            return;
        }

        int bits = block.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
        for (BlockFace face : BlockFace.values()) {
            if (hasFace(bits, face) && BlockID.SCULK.equals(getBlock(manager, pos.getSide(face)).getId())) {
                bits &= ~faceBit(face);
            }
        }

        if (bits != 0) {
            manager.setBlockStateAt(pos, veinState(bits));
            return;
        }

        if (isWaterlogged(manager, pos)) {
            manager.setBlockStateAt(pos, WATER);
            manager.setBlockStateAt(pos.x, pos.y, pos.z, 1, AIR);
        } else {
            manager.setBlockStateAt(pos, AIR);
        }
    }

    private static Set<String> withWorldGenReplaceables(Set<String> base) {
        Set<String> result = new HashSet<>(base);
        result.add("minecraft:deepslate_bricks");
        result.add("minecraft:deepslate_tiles");
        result.add("minecraft:cobbled_deepslate");
        result.add("minecraft:cracked_deepslate_bricks");
        result.add("minecraft:cracked_deepslate_tiles");
        result.add("minecraft:polished_deepslate");
        return Set.copyOf(result);
    }

    private static BlockState veinState(int bits) {
        return BlockSculkVein.PROPERTIES.getBlockState(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS.createValue(bits));
    }

    private static SculkBehaviour getBehaviour(Block block) {
        if (block instanceof BlockSculkVein) {
            return SculkBehaviour.VEIN;
        }
        if (block instanceof BlockSculk) {
            return SculkBehaviour.SCULK;
        }
        return SculkBehaviour.DEFAULT;
    }

    private static boolean isSculkBehaviour(Block block) {
        return block instanceof BlockSculk || block instanceof BlockSculkVein;
    }

    private static boolean isSourceWater(Block block) {
        return BlockID.WATER.equals(block.getId());
    }

    private static boolean isWater(Block block) {
        return BlockID.WATER.equals(block.getId()) || BlockID.FLOWING_WATER.equals(block.getId());
    }

    private static boolean isWaterlogged(BlockManager manager, BlockVector3 pos) {
        String id = manager.getBlockIdAt(pos.x, pos.y, pos.z, 1);
        return BlockID.WATER.equals(id) || BlockID.FLOWING_WATER.equals(id);
    }

    private static void clearWaterloggedLayer(BlockManager manager, BlockVector3 pos) {
        if (isWaterlogged(manager, pos)) {
            manager.setBlockStateAt(pos.x, pos.y, pos.z, 1, AIR);
        }
    }

    private static boolean isFullSupport(BlockManager manager, BlockVector3 pos) {
        Block block = getBlock(manager, pos);
        return !block.isAir() && !block.canBeReplaced() && block.isFullBlock() && block.isSolid();
    }

    private static boolean canAttachTo(BlockManager manager, BlockVector3 pos, BlockFace face) {
        return getBlock(manager, pos.getSide(face)).isSolid(face.getOpposite());
    }

    private static Block getBlock(BlockManager manager, BlockVector3 pos) {
        return manager.getBlockAt(pos.x, pos.y, pos.z);
    }

    private static boolean isValidY(Level level, int y) {
        return y >= level.getMinHeight() && y < level.getMaxHeight();
    }

    private static int faceBit(BlockFace face) {
        return 1 << face.getDUSWNEIndex();
    }

    private static boolean hasFace(int bits, BlockFace face) {
        return (bits & faceBit(face)) != 0;
    }

    private static int manhattanDistance(BlockVector3 first, BlockVector3 second) {
        return Math.abs(first.x - second.x) + Math.abs(first.y - second.y) + Math.abs(first.z - second.z);
    }

    private static int distanceSquared(BlockVector3 first, BlockVector3 second) {
        int dx = first.x - second.x;
        int dy = first.y - second.y;
        int dz = first.z - second.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private static BlockFace faceForDelta(int delta, BlockFace negative, BlockFace positive) {
        return delta < 0 ? negative : positive;
    }

    @Override
    public String name() {
        return NAME;
    }

    private enum SculkBehaviour {
        DEFAULT,
        VEIN,
        SCULK
    }

    private static final class ChargeCursor {
        private static final int SCULK_FACING_DATA = -2;
        private static final int NO_FACING_DATA = -1;

        private BlockVector3 pos;
        private int charge;
        private int updateDelay;
        private int decayDelay = 1;
        private int facings = NO_FACING_DATA;

        private ChargeCursor(BlockVector3 pos, int charge) {
            this.pos = new BlockVector3(pos.x, pos.y, pos.z);
            this.charge = charge;
        }
    }
}
