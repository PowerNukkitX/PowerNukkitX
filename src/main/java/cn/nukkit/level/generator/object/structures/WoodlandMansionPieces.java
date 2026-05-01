package cn.nukkit.level.generator.object.structures;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockCobblestone;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockMobSpawner;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.enums.LeverDirection;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.block.property.enums.TorchFacingDirection;
import cn.nukkit.block.BlockWallBase;
import cn.nukkit.block.BlockStructureBlock;
import cn.nukkit.block.BlockStructureVoid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.structure.AbstractStructure;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.Rotation;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.RandomSourceProvider;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.LEVER_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.WEIRDO_DIRECTION;

public final class WoodlandMansionPieces {
    private static final MansionChestLoot MANSION_CHEST_LOOT = new MansionChestLoot();

    private WoodlandMansionPieces() {
    }

    public static List<WoodlandMansionPiece> generate(BlockVector3 origin, Rotation rotation, RandomSourceProvider random) {
        List<WoodlandMansionPiece> pieces = new ArrayList<>();
        generateMansion(origin, rotation, pieces, random);
        return pieces;
    }

    public static PostPlacement place(BlockManager manager, BlockVector3 origin, Rotation rotation, RandomSourceProvider random) {
        List<WoodlandMansionPiece> pieces = generate(origin, rotation, random);
        List<BlockVector3> wallPositions = new ArrayList<>();
        for (WoodlandMansionPiece piece : pieces) {
            piece.place(manager);
        }
        for (var block : manager.getBlocks()) {
            if (block instanceof BlockWallBase) {
                wallPositions.add(block.asBlockVector3());
            }
        }
        afterPlaceFoundationFill(manager, pieces);
        if (!wallPositions.isEmpty()) {
            manager.addHook(() -> refreshWallConnections(manager.getLevel(), wallPositions));
        }

        List<BlockVector3> chests = new ArrayList<>();
        List<BlockVector3> markerBlocks = new ArrayList<>();
        List<BlockVector3> spiderSpawnerPositions = new ArrayList<>();
        for (var block : manager.getBlocks()) {
            if (block instanceof BlockChest) {
                chests.add(block.asBlockVector3());
            } else if (block instanceof BlockMobSpawner) {
                spiderSpawnerPositions.add(block.asBlockVector3());
            } else if (block instanceof BlockStructureBlock) {
                markerBlocks.add(block.asBlockVector3());
                manager.unsetBlockStateAt(block);
                manager.setBlockStateAt(block, BlockAir.STATE);
            }
        }
        List<MobSpawn> mobSpawns = collectMobSpawnsFromTemplates(pieces, markerBlocks, random);
        return new PostPlacement(chests, mobSpawns, spiderSpawnerPositions);
    }

    private static void refreshWallConnections(Level level, List<BlockVector3> wallPositions) {
        for (BlockVector3 pos : wallPositions) {
            Block block = level.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (block instanceof BlockWallBase wall) {
                if (wall.autoConfigureState()) {
                    level.setBlock(wall, wall, true, true);
                }
            }
        }
    }

    private static void afterPlaceFoundationFill(BlockManager manager, List<WoodlandMansionPiece> pieces) {
        if (pieces.isEmpty()) {
            return;
        }
        BoundingBox bounds = calculateBounds(pieces);
        int yStart = bounds.y0;
        int minY = manager.getLevel().getMinHeight();
        BlockState cobblestone = BlockCobblestone.PROPERTIES.getDefaultState();

        for (int x = bounds.x0; x <= bounds.x1; x++) {
            for (int z = bounds.z0; z <= bounds.z1; z++) {
                if (!isInsideAnyPiece(pieces, x, yStart, z)) {
                    continue;
                }
                if (manager.getBlockIfCachedOrLoaded(x, yStart, z).isAir()) {
                    continue;
                }
                for (int y = yStart - 1; y > minY; y--) {
                    String blockId = manager.getBlockIdAt(x, y, z);
                    if (!isAirOrLiquid(blockId)) {
                        break;
                    }
                    manager.setBlockStateAt(x, y, z, cobblestone);
                }
            }
        }
    }

    private static BoundingBox calculateBounds(List<WoodlandMansionPiece> pieces) {
        BoundingBox first = pieces.get(0).boundingBox();
        int minX = first.x0;
        int minY = first.y0;
        int minZ = first.z0;
        int maxX = first.x1;
        int maxY = first.y1;
        int maxZ = first.z1;
        for (int i = 1; i < pieces.size(); i++) {
            BoundingBox bb = pieces.get(i).boundingBox();
            minX = Math.min(minX, bb.x0);
            minY = Math.min(minY, bb.y0);
            minZ = Math.min(minZ, bb.z0);
            maxX = Math.max(maxX, bb.x1);
            maxY = Math.max(maxY, bb.y1);
            maxZ = Math.max(maxZ, bb.z1);
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static boolean isInsideAnyPiece(List<WoodlandMansionPiece> pieces, int x, int y, int z) {
        BlockVector3 pos = new BlockVector3(x, y, z);
        for (WoodlandMansionPiece piece : pieces) {
            if (piece.boundingBox().isInside(pos)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAirOrLiquid(String blockId) {
        return blockId.equals(BlockAir.STATE.getIdentifier())
                || blockId.equals(BlockID.WATER)
                || blockId.equals(BlockID.FLOWING_WATER)
                || blockId.equals(BlockID.LAVA)
                || blockId.equals(BlockID.FLOWING_LAVA);
    }

    public static void populatePlacedData(
            Level level,
            List<BlockVector3> chestPositions,
            List<MobSpawn> mobSpawns,
            List<BlockVector3> spiderSpawnerPositions,
            RandomSourceProvider random
    ) {
        for (BlockVector3 chestPos : chestPositions) {
            if (level.getBlock(chestPos.getX(), chestPos.getY(), chestPos.getZ()) instanceof BlockChest chest) {
                MANSION_CHEST_LOOT.create(chest.getOrCreateBlockEntity().getInventory(), random);
            }
        }

        int spiderNetworkId = Registries.ENTITY.getEntityNetworkId(EntityID.SPIDER);
        for (BlockVector3 spawnerPos : spiderSpawnerPositions) {
            if (level.getBlock(spawnerPos.getX(), spawnerPos.getY(), spawnerPos.getZ()) instanceof BlockMobSpawner spawner) {
                spawner.getOrCreateBlockEntity().setSpawnEntityType(spiderNetworkId);
            }
        }

        for (MobSpawn spawn : mobSpawns) {
            BlockVector3 pos = spawn.position();
            Entity entity = Entity.createEntity(spawn.entityId(), new Position(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level));
            if (entity != null) {
                entity.setPersistent(true);
                entity.spawnToAll();
            }
        }
    }

    private static List<MobSpawn> collectMobSpawnsFromTemplates(List<WoodlandMansionPiece> pieces, List<BlockVector3> markers, RandomSourceProvider random) {
        List<MobSpawn> result = new ArrayList<>();
        List<BlockVector3> availableMarkers = new ArrayList<>(markers);

        for (WoodlandMansionPiece piece : pieces) {
            MobSpawnPlan plan = getMobSpawnPlan(piece.templateName());
            if (plan == null) {
                continue;
            }
            List<BlockVector3> pieceMarkers = collectMarkersInPiece(piece, availableMarkers);
            int index = 0;

            for (int i = 0; i < plan.evokerCount(); i++) {
                BlockVector3 pos = index < pieceMarkers.size() ? pieceMarkers.get(index++) : fallbackSpawnPos(piece, random, i);
                result.add(new MobSpawn(EntityID.EVOCATION_ILLAGER, pos));
            }
            for (int i = 0; i < plan.vindicatorCount(); i++) {
                BlockVector3 pos = index < pieceMarkers.size() ? pieceMarkers.get(index++) : fallbackSpawnPos(piece, random, i + 7);
                result.add(new MobSpawn(EntityID.VINDICATOR, pos));
            }
            int allayCount = 0;
            if (plan.allayMaxCount() > 0 && random.nextBoolean()) {
                allayCount = 1 + random.nextInt(plan.allayMaxCount());
            }
            for (int i = 0; i < allayCount; i++) {
                BlockVector3 pos = index < pieceMarkers.size() ? pieceMarkers.get(index++) : fallbackSpawnPos(piece, random, i + 17);
                result.add(new MobSpawn(EntityID.ALLAY, pos));
            }
        }

        return result;
    }

    private static List<BlockVector3> collectMarkersInPiece(WoodlandMansionPiece piece, List<BlockVector3> availableMarkers) {
        List<BlockVector3> inPiece = new ArrayList<>();
        for (int i = availableMarkers.size() - 1; i >= 0; i--) {
            BlockVector3 marker = availableMarkers.get(i);
            if (piece.boundingBox().isInside(marker)) {
                inPiece.add(marker);
                availableMarkers.remove(i);
            }
        }
        return inPiece;
    }

    private static BlockVector3 fallbackSpawnPos(WoodlandMansionPiece piece, RandomSourceProvider random, int salt) {
        BoundingBox bb = piece.boundingBox();
        int x = bb.x0 + Math.floorMod(salt + random.nextInt(3), Math.max(1, bb.getXSpan()));
        int y = bb.y0 + 1;
        int z = bb.z0 + Math.floorMod(salt * 3 + random.nextInt(3), Math.max(1, bb.getZSpan()));
        return new BlockVector3(x, y, z);
    }

    private static MobSpawnPlan getMobSpawnPlan(String templateName) {
        return switch (templateName) {
            case "1x2_a1", "1x2_a3", "1x2_a8", "1x2_a9", "1x2_b1", "1x2_b2", "1x2_b3", "2x2_a2" -> new MobSpawnPlan(0, 1, 0);
            case "1x2_a2", "1x2_d3", "2x2_b1", "2x2_b2", "2x2_b4" -> new MobSpawnPlan(1, 2, 0);
            case "2x2_a1" -> new MobSpawnPlan(0, 1, 3);
            default -> null;
        };
    }

    private record MobSpawn(String entityId, BlockVector3 position) {
    }

    private record MobSpawnPlan(int evokerCount, int vindicatorCount, int allayMaxCount) {
    }

    public static void generateMansion(
            BlockVector3 origin,
            Rotation rotation,
            List<WoodlandMansionPiece> pieces,
            RandomSourceProvider random
    ) {
        MansionGrid grid = new MansionGrid(random);
        MansionPiecePlacer placer = new MansionPiecePlacer(random);
        placer.createMansion(origin, rotation, pieces, grid);
    }

    private static BlockFace rotateFace(BlockFace face, Rotation rotation) {
        if (!face.getAxis().isHorizontal()) {
            return face;
        }
        return switch (rotation) {
            case ROTATE_90 -> face.rotateY();
            case ROTATE_180 -> face.getOpposite();
            case ROTATE_270 -> face.rotateYCCW();
            default -> face;
        };
    }

    private static BlockVector3 relative(BlockVector3 pos, Rotation rotation, BlockFace face, int distance) {
        BlockFace rotated = rotateFace(face, rotation);
        return pos.add(rotated.getXOffset() * distance, rotated.getYOffset() * distance, rotated.getZOffset() * distance);
    }

    private static BlockVector3 rotateOffset(BlockVector3 pos, Rotation rotation) {
        return switch (rotation) {
            case ROTATE_90 -> new BlockVector3(-pos.getZ(), pos.getY(), pos.getX());
            case ROTATE_180 -> new BlockVector3(-pos.getX(), pos.getY(), -pos.getZ());
            case ROTATE_270 -> new BlockVector3(pos.getZ(), pos.getY(), -pos.getX());
            default -> pos;
        };
    }

    private static BlockVector3 getZeroPositionWithTransform(BlockVector3 zeroPos, Mirror mirror, Rotation rotation, int sizeX, int sizeZ) {
        int x = zeroPos.getX();
        int y = zeroPos.getY();
        int z = zeroPos.getZ();
        int maxX = sizeX - 1;
        int maxZ = sizeZ - 1;
        int mirrorDeltaX = mirror == Mirror.FRONT_BACK ? maxX : 0;
        int mirrorDeltaZ = mirror == Mirror.LEFT_RIGHT ? maxZ : 0;
        return switch (rotation) {
            case ROTATE_270 -> new BlockVector3(x + mirrorDeltaZ, y, z + maxX - mirrorDeltaX);
            case ROTATE_90 -> new BlockVector3(x + maxZ - mirrorDeltaZ, y, z + mirrorDeltaX);
            case ROTATE_180 -> new BlockVector3(x + maxX - mirrorDeltaX, y, z + maxZ - mirrorDeltaZ);
            default -> new BlockVector3(x + mirrorDeltaX, y, z + mirrorDeltaZ);
        };
    }

    private static void shuffle(List<IntPos> values, RandomSourceProvider random) {
        for (int i = values.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            IntPos t = values.get(i);
            values.set(i, values.get(j));
            values.set(j, t);
        }
    }

    private static final class FirstFloorRoomCollection extends FloorRoomCollection {
        @Override
        public String get1x1(RandomSourceProvider random) {
            return "1x1_a" + (random.nextInt(5) + 1);
        }

        @Override
        public String get1x1Secret(RandomSourceProvider random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String get1x2SideEntrance(RandomSourceProvider random, boolean isStairsRoom) {
            return "1x2_a" + (random.nextInt(9) + 1);
        }

        @Override
        public String get1x2FrontEntrance(RandomSourceProvider random, boolean isStairsRoom) {
            return "1x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String get1x2Secret(RandomSourceProvider random) {
            return "1x2_s" + (random.nextInt(2) + 1);
        }

        @Override
        public String get2x2(RandomSourceProvider random) {
            return "2x2_a" + (random.nextInt(4) + 1);
        }

        @Override
        public String get2x2Secret(RandomSourceProvider random) {
            return "2x2_s1";
        }
    }

    private abstract static class FloorRoomCollection {
        public abstract String get1x1(RandomSourceProvider random);

        public abstract String get1x1Secret(RandomSourceProvider random);

        public abstract String get1x2SideEntrance(RandomSourceProvider random, boolean isStairsRoom);

        public abstract String get1x2FrontEntrance(RandomSourceProvider random, boolean isStairsRoom);

        public abstract String get1x2Secret(RandomSourceProvider random);

        public abstract String get2x2(RandomSourceProvider random);

        public abstract String get2x2Secret(RandomSourceProvider random);
    }

    private static final class MansionGrid {
        private static final int ROOM_1x1 = 65536;
        private static final int ROOM_1x2 = 131072;
        private static final int ROOM_2x2 = 262144;
        private static final int ROOM_ORIGIN_FLAG = 1048576;
        private static final int ROOM_DOOR_FLAG = 2097152;
        private static final int ROOM_STAIRS_FLAG = 4194304;
        private static final int ROOM_CORRIDOR_FLAG = 8388608;
        private static final int ROOM_TYPE_MASK = 983040;
        private static final int ROOM_ID_MASK = 65535;
        private final RandomSourceProvider random;
        private final SimpleGrid baseGrid;
        private final SimpleGrid thirdFloorGrid;
        private final SimpleGrid[] floorRooms;
        private final int entranceX;
        private final int entranceY;

        public MansionGrid(RandomSourceProvider random) {
            this.random = random;
            this.entranceX = 7;
            this.entranceY = 4;
            this.baseGrid = new SimpleGrid(11, 11, 5);
            this.baseGrid.set(this.entranceX, this.entranceY, this.entranceX + 1, this.entranceY + 1, 3);
            this.baseGrid.set(this.entranceX - 1, this.entranceY, this.entranceX - 1, this.entranceY + 1, 2);
            this.baseGrid.set(this.entranceX + 2, this.entranceY - 2, this.entranceX + 3, this.entranceY + 3, 5);
            this.baseGrid.set(this.entranceX + 1, this.entranceY - 2, this.entranceX + 1, this.entranceY - 1, 1);
            this.baseGrid.set(this.entranceX + 1, this.entranceY + 2, this.entranceX + 1, this.entranceY + 3, 1);
            this.baseGrid.set(this.entranceX - 1, this.entranceY - 1, 1);
            this.baseGrid.set(this.entranceX - 1, this.entranceY + 2, 1);
            this.baseGrid.set(0, 0, 11, 1, 5);
            this.baseGrid.set(0, 9, 11, 11, 5);
            this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY - 2, BlockFace.WEST, 6);
            this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY + 3, BlockFace.WEST, 6);
            this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY - 1, BlockFace.WEST, 3);
            this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY + 2, BlockFace.WEST, 3);

            while (this.cleanEdges(this.baseGrid)) {
            }

            this.floorRooms = new SimpleGrid[3];
            this.floorRooms[0] = new SimpleGrid(11, 11, 5);
            this.floorRooms[1] = new SimpleGrid(11, 11, 5);
            this.floorRooms[2] = new SimpleGrid(11, 11, 5);
            this.identifyRooms(this.baseGrid, this.floorRooms[0]);
            this.identifyRooms(this.baseGrid, this.floorRooms[1]);
            this.floorRooms[0].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, ROOM_CORRIDOR_FLAG);
            this.floorRooms[1].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, ROOM_CORRIDOR_FLAG);
            this.thirdFloorGrid = new SimpleGrid(this.baseGrid.width, this.baseGrid.height, 5);
            this.setupThirdFloor();
            this.identifyRooms(this.thirdFloorGrid, this.floorRooms[2]);
        }

        public static boolean isHouse(SimpleGrid grid, int x, int y) {
            int value = grid.get(x, y);
            return value == 1 || value == 2 || value == 3 || value == 4;
        }

        public boolean isRoomId(SimpleGrid grid, int x, int y, int floor, int roomId) {
            return (this.floorRooms[floor].get(x, y) & ROOM_ID_MASK) == roomId;
        }

        public BlockFace get1x2RoomDirection(SimpleGrid grid, int x, int y, int floorNum, int roomId) {
            for (BlockFace direction : BlockFace.Plane.HORIZONTAL) {
                if (this.isRoomId(grid, x + direction.getXOffset(), y + direction.getZOffset(), floorNum, roomId)) {
                    return direction;
                }
            }
            return null;
        }

        private void recursiveCorridor(SimpleGrid grid, int x, int y, BlockFace heading, int depth) {
            if (depth <= 0) {
                return;
            }
            grid.set(x, y, 1);
            grid.setIf(x + heading.getXOffset(), y + heading.getZOffset(), 0, 1);

            for (int attempts = 0; attempts < 8; attempts++) {
                BlockFace nextDir = BlockFace.fromHorizontalIndex(this.random.nextInt(4));
                if (nextDir != heading.getOpposite() && (nextDir != BlockFace.EAST || !this.random.nextBoolean())) {
                    int nx = x + heading.getXOffset();
                    int ny = y + heading.getZOffset();
                    if (grid.get(nx + nextDir.getXOffset(), ny + nextDir.getZOffset()) == 0
                            && grid.get(nx + nextDir.getXOffset() * 2, ny + nextDir.getZOffset() * 2) == 0) {
                        this.recursiveCorridor(
                                grid,
                                x + heading.getXOffset() + nextDir.getXOffset(),
                                y + heading.getZOffset() + nextDir.getZOffset(),
                                nextDir,
                                depth - 1
                        );
                        break;
                    }
                }
            }

            BlockFace cw = heading.rotateY();
            BlockFace ccw = heading.rotateYCCW();
            grid.setIf(x + cw.getXOffset(), y + cw.getZOffset(), 0, 2);
            grid.setIf(x + ccw.getXOffset(), y + ccw.getZOffset(), 0, 2);
            grid.setIf(x + heading.getXOffset() + cw.getXOffset(), y + heading.getZOffset() + cw.getZOffset(), 0, 2);
            grid.setIf(x + heading.getXOffset() + ccw.getXOffset(), y + heading.getZOffset() + ccw.getZOffset(), 0, 2);
            grid.setIf(x + heading.getXOffset() * 2, y + heading.getZOffset() * 2, 0, 2);
            grid.setIf(x + cw.getXOffset() * 2, y + cw.getZOffset() * 2, 0, 2);
            grid.setIf(x + ccw.getXOffset() * 2, y + ccw.getZOffset() * 2, 0, 2);
        }

        private boolean cleanEdges(SimpleGrid grid) {
            boolean touched = false;

            for (int y = 0; y < grid.height; y++) {
                for (int x = 0; x < grid.width; x++) {
                    if (grid.get(x, y) == 0) {
                        int directNeighbors = 0;
                        directNeighbors += isHouse(grid, x + 1, y) ? 1 : 0;
                        directNeighbors += isHouse(grid, x - 1, y) ? 1 : 0;
                        directNeighbors += isHouse(grid, x, y + 1) ? 1 : 0;
                        directNeighbors += isHouse(grid, x, y - 1) ? 1 : 0;
                        if (directNeighbors >= 3) {
                            grid.set(x, y, 2);
                            touched = true;
                        } else if (directNeighbors == 2) {
                            int diagonalNeighbors = 0;
                            diagonalNeighbors += isHouse(grid, x + 1, y + 1) ? 1 : 0;
                            diagonalNeighbors += isHouse(grid, x - 1, y + 1) ? 1 : 0;
                            diagonalNeighbors += isHouse(grid, x + 1, y - 1) ? 1 : 0;
                            diagonalNeighbors += isHouse(grid, x - 1, y - 1) ? 1 : 0;
                            if (diagonalNeighbors <= 1) {
                                grid.set(x, y, 2);
                                touched = true;
                            }
                        }
                    }
                }
            }

            return touched;
        }

        private void setupThirdFloor() {
            List<IntPos> potentialRooms = Lists.newArrayList();
            SimpleGrid floor = this.floorRooms[1];

            for (int y = 0; y < this.thirdFloorGrid.height; y++) {
                for (int x = 0; x < this.thirdFloorGrid.width; x++) {
                    int roomData = floor.get(x, y);
                    int roomType = roomData & ROOM_TYPE_MASK;
                    if (roomType == ROOM_1x2 && (roomData & ROOM_DOOR_FLAG) == ROOM_DOOR_FLAG) {
                        potentialRooms.add(new IntPos(x, y));
                    }
                }
            }

            if (potentialRooms.isEmpty()) {
                this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
                return;
            }

            IntPos roomPos = potentialRooms.get(this.random.nextInt(potentialRooms.size()));
            int roomData = floor.get(roomPos.x, roomPos.y);
            floor.set(roomPos.x, roomPos.y, roomData | ROOM_STAIRS_FLAG);
            BlockFace roomDir = this.get1x2RoomDirection(this.baseGrid, roomPos.x, roomPos.y, 1, roomData & ROOM_ID_MASK);
            if (roomDir == null) {
                this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
                return;
            }
            int roomEndX = roomPos.x + roomDir.getXOffset();
            int roomEndY = roomPos.y + roomDir.getZOffset();

            for (int y = 0; y < this.thirdFloorGrid.height; y++) {
                for (int x = 0; x < this.thirdFloorGrid.width; x++) {
                    if (!isHouse(this.baseGrid, x, y)) {
                        this.thirdFloorGrid.set(x, y, 5);
                    } else if (x == roomPos.x && y == roomPos.y) {
                        this.thirdFloorGrid.set(x, y, 3);
                        this.floorRooms[2].set(x, y, ROOM_CORRIDOR_FLAG);
                    } else if (x == roomEndX && y == roomEndY) {
                        this.thirdFloorGrid.set(x, y, 3);
                        this.floorRooms[2].set(x, y, ROOM_CORRIDOR_FLAG);
                    }
                }
            }

            List<BlockFace> potentialCorridors = Lists.newArrayList();
            for (BlockFace direction : BlockFace.Plane.HORIZONTAL) {
                if (this.thirdFloorGrid.get(roomEndX + direction.getXOffset(), roomEndY + direction.getZOffset()) == 0) {
                    potentialCorridors.add(direction);
                }
            }

            if (potentialCorridors.isEmpty()) {
                this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
                floor.set(roomPos.x, roomPos.y, roomData);
                return;
            }

            BlockFace corridorDir = potentialCorridors.get(this.random.nextInt(potentialCorridors.size()));
            this.recursiveCorridor(
                    this.thirdFloorGrid,
                    roomEndX + corridorDir.getXOffset(),
                    roomEndY + corridorDir.getZOffset(),
                    corridorDir,
                    4
            );

            while (this.cleanEdges(this.thirdFloorGrid)) {
            }
        }

        private void identifyRooms(SimpleGrid fromGrid, SimpleGrid roomGrid) {
            List<IntPos> roomPos = new ArrayList<>();

            for (int y = 0; y < fromGrid.height; y++) {
                for (int x = 0; x < fromGrid.width; x++) {
                    if (fromGrid.get(x, y) == 2) {
                        roomPos.add(new IntPos(x, y));
                    }
                }
            }

            shuffle(roomPos, this.random);
            int roomId = 10;

            for (IntPos pos : roomPos) {
                int x = pos.x;
                int y = pos.y;
                if (roomGrid.get(x, y) != 0) {
                    continue;
                }
                int x0 = x;
                int x1 = x;
                int y0 = y;
                int y1 = y;
                int type = ROOM_1x1;
                if (roomGrid.get(x + 1, y) == 0
                        && roomGrid.get(x, y + 1) == 0
                        && roomGrid.get(x + 1, y + 1) == 0
                        && fromGrid.get(x + 1, y) == 2
                        && fromGrid.get(x, y + 1) == 2
                        && fromGrid.get(x + 1, y + 1) == 2) {
                    x1 = x + 1;
                    y1 = y + 1;
                    type = ROOM_2x2;
                } else if (roomGrid.get(x - 1, y) == 0
                        && roomGrid.get(x, y + 1) == 0
                        && roomGrid.get(x - 1, y + 1) == 0
                        && fromGrid.get(x - 1, y) == 2
                        && fromGrid.get(x, y + 1) == 2
                        && fromGrid.get(x - 1, y + 1) == 2) {
                    x0 = x - 1;
                    y1 = y + 1;
                    type = ROOM_2x2;
                } else if (roomGrid.get(x - 1, y) == 0
                        && roomGrid.get(x, y - 1) == 0
                        && roomGrid.get(x - 1, y - 1) == 0
                        && fromGrid.get(x - 1, y) == 2
                        && fromGrid.get(x, y - 1) == 2
                        && fromGrid.get(x - 1, y - 1) == 2) {
                    x0 = x - 1;
                    y0 = y - 1;
                    type = ROOM_2x2;
                } else if (roomGrid.get(x + 1, y) == 0 && fromGrid.get(x + 1, y) == 2) {
                    x1 = x + 1;
                    type = ROOM_1x2;
                } else if (roomGrid.get(x, y + 1) == 0 && fromGrid.get(x, y + 1) == 2) {
                    y1 = y + 1;
                    type = ROOM_1x2;
                } else if (roomGrid.get(x - 1, y) == 0 && fromGrid.get(x - 1, y) == 2) {
                    x0 = x - 1;
                    type = ROOM_1x2;
                } else if (roomGrid.get(x, y - 1) == 0 && fromGrid.get(x, y - 1) == 2) {
                    y0 = y - 1;
                    type = ROOM_1x2;
                }

                int doorX = this.random.nextBoolean() ? x0 : x1;
                int doorY = this.random.nextBoolean() ? y0 : y1;
                int doorFlag = ROOM_DOOR_FLAG;
                if (!fromGrid.edgesTo(doorX, doorY, 1)) {
                    doorX = doorX == x0 ? x1 : x0;
                    doorY = doorY == y0 ? y1 : y0;
                    if (!fromGrid.edgesTo(doorX, doorY, 1)) {
                        doorY = doorY == y0 ? y1 : y0;
                        if (!fromGrid.edgesTo(doorX, doorY, 1)) {
                            doorX = doorX == x0 ? x1 : x0;
                            doorY = doorY == y0 ? y1 : y0;
                            if (!fromGrid.edgesTo(doorX, doorY, 1)) {
                                doorFlag = 0;
                                doorX = x0;
                                doorY = y0;
                            }
                        }
                    }
                }

                for (int ry = y0; ry <= y1; ry++) {
                    for (int rx = x0; rx <= x1; rx++) {
                        if (rx == doorX && ry == doorY) {
                            roomGrid.set(rx, ry, ROOM_ORIGIN_FLAG | doorFlag | type | roomId);
                        } else {
                            roomGrid.set(rx, ry, type | roomId);
                        }
                    }
                }
                roomId++;
            }
        }
    }

    private static final class MansionPiecePlacer {
        private final RandomSourceProvider random;
        private int startX;
        private int startY;

        public MansionPiecePlacer(RandomSourceProvider random) {
            this.random = random;
        }

        public void createMansion(BlockVector3 origin, Rotation rotation, List<WoodlandMansionPiece> pieces, MansionGrid mansion) {
            PlacementData data = new PlacementData();
            data.position = origin;
            data.rotation = rotation;
            data.wallType = "wall_flat";
            PlacementData secondData = new PlacementData();
            this.entrance(pieces, data);
            secondData.position = data.position.up(8);
            secondData.rotation = data.rotation;
            secondData.wallType = "wall_window";

            SimpleGrid baseGrid = mansion.baseGrid;
            SimpleGrid thirdGrid = mansion.thirdFloorGrid;
            this.startX = mansion.entranceX + 1;
            this.startY = mansion.entranceY + 1;
            int endX = mansion.entranceX + 1;
            int endY = mansion.entranceY;
            this.traverseOuterWalls(pieces, data, baseGrid, BlockFace.SOUTH, this.startX, this.startY, endX, endY);
            this.traverseOuterWalls(pieces, secondData, baseGrid, BlockFace.SOUTH, this.startX, this.startY, endX, endY);
            PlacementData thirdData = new PlacementData();
            thirdData.position = data.position.up(19);
            thirdData.rotation = data.rotation;
            thirdData.wallType = "wall_window";
            boolean done = false;

            for (int y = 0; y < thirdGrid.height && !done; y++) {
                for (int x = thirdGrid.width - 1; x >= 0 && !done; x--) {
                    if (MansionGrid.isHouse(thirdGrid, x, y)) {
                        thirdData.position = relative(thirdData.position, rotation, BlockFace.SOUTH, 8 + (y - this.startY) * 8);
                        thirdData.position = relative(thirdData.position, rotation, BlockFace.EAST, (x - this.startX) * 8);
                        this.traverseWallPiece(pieces, thirdData);
                        this.traverseOuterWalls(pieces, thirdData, thirdGrid, BlockFace.SOUTH, x, y, x, y);
                        done = true;
                    }
                }
            }

            this.createRoof(pieces, origin.up(16), rotation, baseGrid, thirdGrid);
            this.createRoof(pieces, origin.up(27), rotation, thirdGrid, null);

            FloorRoomCollection[] roomCollections = new FloorRoomCollection[]{
                    new FirstFloorRoomCollection(),
                    new SecondFloorRoomCollection(),
                    new ThirdFloorRoomCollection()
            };

            for (int floorNum = 0; floorNum < 3; floorNum++) {
                BlockVector3 floorOrigin = origin.up(8 * floorNum + (floorNum == 2 ? 3 : 0));
                SimpleGrid rooms = mansion.floorRooms[floorNum];
                SimpleGrid grid = floorNum == 2 ? thirdGrid : baseGrid;
                String southPiece = floorNum == 0 ? "carpet_south_1" : "carpet_south_2";
                String westPiece = floorNum == 0 ? "carpet_west_1" : "carpet_west_2";

                for (int y = 0; y < grid.height; y++) {
                    for (int x = 0; x < grid.width; x++) {
                        boolean isCorridorCell = grid.get(x, y) == 1;
                        boolean isThirdFloorCorridorStart = floorNum == 2
                                && grid.get(x, y) == 3
                                && (rooms.get(x, y) & 8388608) == 8388608;
                        if (isCorridorCell) {
                            BlockVector3 pos = relative(floorOrigin, rotation, BlockFace.SOUTH, 8 + (y - this.startY) * 8);
                            pos = relative(pos, rotation, BlockFace.EAST, (x - this.startX) * 8);
                            pieces.add(new WoodlandMansionPiece("corridor_floor", pos, rotation));
                            if (grid.get(x, y - 1) == 1
                                    || (rooms.get(x, y - 1) & 8388608) == 8388608
                                    || (floorNum == 2 && grid.get(x, y - 1) == 3)) {
                                pieces.add(new WoodlandMansionPiece("carpet_north", relative(pos, rotation, BlockFace.EAST, 1).up(), rotation));
                            }

                            if (grid.get(x + 1, y) == 1
                                    || (rooms.get(x + 1, y) & 8388608) == 8388608
                                    || (floorNum == 2 && grid.get(x + 1, y) == 3)) {
                                pieces.add(
                                        new WoodlandMansionPiece(
                                                "carpet_east",
                                                relative(relative(pos, rotation, BlockFace.SOUTH, 1), rotation, BlockFace.EAST, 5).up(),
                                                rotation
                                        )
                                );
                            }

                            if (grid.get(x, y + 1) == 1
                                    || (rooms.get(x, y + 1) & 8388608) == 8388608
                                    || (floorNum == 2 && grid.get(x, y + 1) == 3)) {
                                pieces.add(new WoodlandMansionPiece(southPiece, relative(relative(pos, rotation, BlockFace.SOUTH, 5), rotation, BlockFace.WEST, 1), rotation));
                            }

                            if (grid.get(x - 1, y) == 1
                                    || (rooms.get(x - 1, y) & 8388608) == 8388608
                                    || (floorNum == 2 && grid.get(x - 1, y) == 3)) {
                                pieces.add(new WoodlandMansionPiece(westPiece, relative(relative(pos, rotation, BlockFace.WEST, 1), rotation, BlockFace.NORTH, 1), rotation));
                            }
                        } else if (isThirdFloorCorridorStart) {
                            BlockVector3 pos = relative(floorOrigin, rotation, BlockFace.SOUTH, 8 + (y - this.startY) * 8);
                            pos = relative(pos, rotation, BlockFace.EAST, (x - this.startX) * 8);

                            if (grid.get(x, y + 1) == 1) {
                                BlockVector3 southPos = relative(relative(pos, rotation, BlockFace.SOUTH, 5), rotation, BlockFace.WEST, 1);
                                southPos = applyTemplateRotationOffset(southPiece, southPos, rotation);
                                southPos = relative(relative(southPos, rotation, BlockFace.WEST, 2), rotation, BlockFace.SOUTH, 1);
                                pieces.add(new WoodlandMansionPiece(southPiece, southPos, rotation.rotateBy(Rotation.ROTATE_180)));
                            }
                            if (grid.get(x - 1, y) == 1) {
                                BlockVector3 westPos = relative(relative(pos, rotation, BlockFace.WEST, 1), rotation, BlockFace.NORTH, 1);
                                westPos = applyTemplateRotationOffset(westPiece, westPos, rotation);
                                westPos = relative(relative(westPos, rotation, BlockFace.WEST, 2), rotation, BlockFace.SOUTH, 1);
                                pieces.add(new WoodlandMansionPiece(westPiece, westPos, rotation.rotateBy(Rotation.ROTATE_180)));
                            }
                        }
                    }
                }

                String wallPiece = floorNum == 0 ? "indoors_wall_1" : "indoors_wall_2";
                String doorPiece = floorNum == 0 ? "indoors_door_1" : "indoors_door_2";
                List<BlockFace> doorDirs = Lists.newArrayList();

                for (int y = 0; y < grid.height; y++) {
                    for (int x = 0; x < grid.width; x++) {
                        boolean thirdFloorStartRoom = floorNum == 2 && grid.get(x, y) == 3;
                        if (grid.get(x, y) == 2 || thirdFloorStartRoom) {
                            int roomData = rooms.get(x, y);
                            int roomType = roomData & 983040;
                            int roomId = roomData & 65535;
                            thirdFloorStartRoom = thirdFloorStartRoom && (roomData & 8388608) == 8388608;
                            doorDirs.clear();
                            if ((roomData & 2097152) == 2097152) {
                                for (BlockFace direction : BlockFace.Plane.HORIZONTAL) {
                                    if (grid.get(x + direction.getXOffset(), y + direction.getZOffset()) == 1) {
                                        doorDirs.add(direction);
                                    }
                                }
                            }

                            BlockFace doorDir = null;
                            if (!doorDirs.isEmpty()) {
                                doorDir = doorDirs.get(this.random.nextInt(doorDirs.size()));
                            } else if ((roomData & 1048576) == 1048576) {
                                doorDir = BlockFace.UP;
                            }

                            BlockVector3 roomPos = relative(floorOrigin, rotation, BlockFace.SOUTH, 8 + (y - this.startY) * 8);
                            roomPos = relative(roomPos, rotation, BlockFace.EAST, -1 + (x - this.startX) * 8);
                            if (MansionGrid.isHouse(grid, x - 1, y) && !mansion.isRoomId(grid, x - 1, y, floorNum, roomId)) {
                                pieces.add(new WoodlandMansionPiece(doorDir == BlockFace.WEST ? doorPiece : wallPiece, roomPos, rotation));
                            }

                            if (grid.get(x + 1, y) == 1 && !thirdFloorStartRoom) {
                                BlockVector3 posx = relative(roomPos, rotation, BlockFace.EAST, 8);
                                pieces.add(new WoodlandMansionPiece(doorDir == BlockFace.EAST ? doorPiece : wallPiece, posx, rotation));
                            }

                            if (MansionGrid.isHouse(grid, x, y + 1) && !mansion.isRoomId(grid, x, y + 1, floorNum, roomId)) {
                                BlockVector3 posx = relative(relative(roomPos, rotation, BlockFace.SOUTH, 7), rotation, BlockFace.EAST, 7);
                                pieces.add(new WoodlandMansionPiece(doorDir == BlockFace.SOUTH ? doorPiece : wallPiece, posx, rotation.rotateBy(Rotation.ROTATE_90)));
                            }

                            if (grid.get(x, y - 1) == 1 && !thirdFloorStartRoom) {
                                BlockVector3 posx = relative(relative(roomPos, rotation, BlockFace.NORTH, 1), rotation, BlockFace.EAST, 7);
                                pieces.add(new WoodlandMansionPiece(doorDir == BlockFace.NORTH ? doorPiece : wallPiece, posx, rotation.rotateBy(Rotation.ROTATE_90)));
                            }

                            if (roomType == 65536) {
                                this.addRoom1x1(pieces, roomPos, rotation, doorDir, roomCollections[floorNum]);
                            } else if (roomType == 131072 && doorDir != null) {
                                BlockFace roomDir = mansion.get1x2RoomDirection(grid, x, y, floorNum, roomId);
                                boolean isStairsRoom = (roomData & 4194304) == 4194304;
                                this.addRoom1x2(pieces, roomPos, rotation, roomDir, doorDir, roomCollections[floorNum], isStairsRoom);
                            } else if (roomType == 262144 && doorDir != null && doorDir != BlockFace.UP) {
                                BlockFace roomDir = doorDir.rotateY();
                                if (!mansion.isRoomId(grid, x + roomDir.getXOffset(), y + roomDir.getZOffset(), floorNum, roomId)) {
                                    roomDir = roomDir.getOpposite();
                                }

                                this.addRoom2x2(pieces, roomPos, rotation, roomDir, doorDir, roomCollections[floorNum]);
                            } else if (roomType == 262144 && doorDir == BlockFace.UP) {
                                this.addRoom2x2Secret(pieces, roomPos, rotation, roomCollections[floorNum]);
                            }
                        }
                    }
                }
            }
        }

        private void traverseOuterWalls(
                List<WoodlandMansionPiece> pieces,
                PlacementData data,
                SimpleGrid grid,
                BlockFace gridDirection,
                int startX,
                int startY,
                int endX,
                int endY
        ) {
            int gridX = startX;
            int gridY = startY;
            BlockFace startDirection = gridDirection;

            do {
                if (!MansionGrid.isHouse(grid, gridX + gridDirection.getXOffset(), gridY + gridDirection.getZOffset())) {
                    this.traverseTurn(pieces, data);
                    gridDirection = gridDirection.rotateY();
                    if (gridX != endX || gridY != endY || startDirection != gridDirection) {
                        this.traverseWallPiece(pieces, data);
                    }
                } else if (MansionGrid.isHouse(grid, gridX + gridDirection.getXOffset(), gridY + gridDirection.getZOffset())
                        && MansionGrid.isHouse(
                        grid,
                        gridX + gridDirection.getXOffset() + gridDirection.rotateYCCW().getXOffset(),
                        gridY + gridDirection.getZOffset() + gridDirection.rotateYCCW().getZOffset()
                )) {
                    this.traverseInnerTurn(pieces, data);
                    gridX += gridDirection.getXOffset();
                    gridY += gridDirection.getZOffset();
                    gridDirection = gridDirection.rotateYCCW();
                } else {
                    gridX += gridDirection.getXOffset();
                    gridY += gridDirection.getZOffset();
                    if (gridX != endX || gridY != endY || startDirection != gridDirection) {
                        this.traverseWallPiece(pieces, data);
                    }
                }
            } while (gridX != endX || gridY != endY || startDirection != gridDirection);
        }

        private void createRoof(
                List<WoodlandMansionPiece> pieces,
                BlockVector3 roofOrigin,
                Rotation rotation,
                SimpleGrid grid,
                SimpleGrid aboveGrid
        ) {
            for (int y = 0; y < grid.height; y++) {
                for (int x = 0; x < grid.width; x++) {
                    BlockVector3 position = relative(roofOrigin, rotation, BlockFace.SOUTH, 8 + (y - this.startY) * 8);
                    position = relative(position, rotation, BlockFace.EAST, (x - this.startX) * 8);
                    boolean isAbove = aboveGrid != null && MansionGrid.isHouse(aboveGrid, x, y);
                    if (MansionGrid.isHouse(grid, x, y) && !isAbove) {
                        pieces.add(new WoodlandMansionPiece("roof", position.up(3), rotation));
                        if (!MansionGrid.isHouse(grid, x + 1, y)) {
                            BlockVector3 p2 = relative(position, rotation, BlockFace.EAST, 6);
                            pieces.add(new WoodlandMansionPiece("roof_front", p2, rotation));
                        }

                        if (!MansionGrid.isHouse(grid, x - 1, y)) {
                            BlockVector3 p2 = relative(relative(position, rotation, BlockFace.EAST, 0), rotation, BlockFace.SOUTH, 7);
                            pieces.add(new WoodlandMansionPiece("roof_front", p2, rotation.rotateBy(Rotation.ROTATE_180)));
                        }

                        if (!MansionGrid.isHouse(grid, x, y - 1)) {
                            BlockVector3 p2 = relative(position, rotation, BlockFace.WEST, 1);
                            pieces.add(new WoodlandMansionPiece("roof_front", p2, rotation.rotateBy(Rotation.ROTATE_270)));
                        }

                        if (!MansionGrid.isHouse(grid, x, y + 1)) {
                            BlockVector3 p2 = relative(relative(position, rotation, BlockFace.EAST, 6), rotation, BlockFace.SOUTH, 6);
                            pieces.add(new WoodlandMansionPiece("roof_front", p2, rotation.rotateBy(Rotation.ROTATE_90)));
                        }
                    }
                }
            }

            if (aboveGrid != null) {
                for (int y = 0; y < grid.height; y++) {
                    for (int x = 0; x < grid.width; x++) {
                        BlockVector3 pos = relative(roofOrigin, rotation, BlockFace.SOUTH, 8 + (y - this.startY) * 8);
                        pos = relative(pos, rotation, BlockFace.EAST, (x - this.startX) * 8);
                        boolean isAbove = MansionGrid.isHouse(aboveGrid, x, y);
                        if (MansionGrid.isHouse(grid, x, y) && isAbove) {
                            if (!MansionGrid.isHouse(grid, x + 1, y)) {
                                BlockVector3 p2 = relative(pos, rotation, BlockFace.EAST, 7);
                                pieces.add(new WoodlandMansionPiece("small_wall", p2, rotation));
                            }

                            if (!MansionGrid.isHouse(grid, x - 1, y)) {
                                BlockVector3 p2 = relative(relative(pos, rotation, BlockFace.WEST, 1), rotation, BlockFace.SOUTH, 6);
                                pieces.add(new WoodlandMansionPiece("small_wall", p2, rotation.rotateBy(Rotation.ROTATE_180)));
                            }

                            if (!MansionGrid.isHouse(grid, x, y - 1)) {
                                BlockVector3 p2 = relative(relative(pos, rotation, BlockFace.WEST, 0), rotation, BlockFace.NORTH, 1);
                                pieces.add(new WoodlandMansionPiece("small_wall", p2, rotation.rotateBy(Rotation.ROTATE_270)));
                            }

                            if (!MansionGrid.isHouse(grid, x, y + 1)) {
                                BlockVector3 p2 = relative(relative(pos, rotation, BlockFace.EAST, 6), rotation, BlockFace.SOUTH, 7);
                                pieces.add(new WoodlandMansionPiece("small_wall", p2, rotation.rotateBy(Rotation.ROTATE_90)));
                            }

                            if (!MansionGrid.isHouse(grid, x + 1, y)) {
                                if (!MansionGrid.isHouse(grid, x, y - 1)) {
                                    BlockVector3 p2 = relative(relative(pos, rotation, BlockFace.EAST, 7), rotation, BlockFace.NORTH, 2);
                                    pieces.add(new WoodlandMansionPiece("small_wall_corner", p2, rotation));
                                }

                                if (!MansionGrid.isHouse(grid, x, y + 1)) {
                                    BlockVector3 p2 = relative(relative(pos, rotation, BlockFace.EAST, 8), rotation, BlockFace.SOUTH, 7);
                                    pieces.add(new WoodlandMansionPiece("small_wall_corner", p2, rotation.rotateBy(Rotation.ROTATE_90)));
                                }
                            }

                            if (!MansionGrid.isHouse(grid, x - 1, y)) {
                                if (!MansionGrid.isHouse(grid, x, y - 1)) {
                                    BlockVector3 p2 = relative(relative(pos, rotation, BlockFace.WEST, 2), rotation, BlockFace.NORTH, 1);
                                    pieces.add(new WoodlandMansionPiece("small_wall_corner", p2, rotation.rotateBy(Rotation.ROTATE_270)));
                                }

                                if (!MansionGrid.isHouse(grid, x, y + 1)) {
                                    BlockVector3 p2 = relative(relative(pos, rotation, BlockFace.WEST, 1), rotation, BlockFace.SOUTH, 8);
                                    pieces.add(new WoodlandMansionPiece("small_wall_corner", p2, rotation.rotateBy(Rotation.ROTATE_180)));
                                }
                            }
                        }
                    }
                }
            }

            for (int y = 0; y < grid.height; y++) {
                for (int x = 0; x < grid.width; x++) {
                    BlockVector3 pos = relative(roofOrigin, rotation, BlockFace.SOUTH, 8 + (y - this.startY) * 8);
                    pos = relative(pos, rotation, BlockFace.EAST, (x - this.startX) * 8);
                    boolean isAbove = aboveGrid != null && MansionGrid.isHouse(aboveGrid, x, y);
                    if (MansionGrid.isHouse(grid, x, y) && !isAbove) {
                        if (!MansionGrid.isHouse(grid, x + 1, y)) {
                            BlockVector3 p2 = relative(pos, rotation, BlockFace.EAST, 6);
                            if (!MansionGrid.isHouse(grid, x, y + 1)) {
                                BlockVector3 p3 = relative(p2, rotation, BlockFace.SOUTH, 6);
                                pieces.add(new WoodlandMansionPiece("roof_corner", p3, rotation));
                            } else if (MansionGrid.isHouse(grid, x + 1, y + 1)) {
                                BlockVector3 p3 = relative(p2, rotation, BlockFace.SOUTH, 5);
                                pieces.add(new WoodlandMansionPiece("roof_inner_corner", p3, rotation));
                            }

                            if (!MansionGrid.isHouse(grid, x, y - 1)) {
                                pieces.add(new WoodlandMansionPiece("roof_corner", p2, rotation.rotateBy(Rotation.ROTATE_270)));
                            } else if (MansionGrid.isHouse(grid, x + 1, y - 1)) {
                                BlockVector3 p3 = relative(relative(pos, rotation, BlockFace.EAST, 9), rotation, BlockFace.NORTH, 2);
                                pieces.add(new WoodlandMansionPiece("roof_inner_corner", p3, rotation.rotateBy(Rotation.ROTATE_90)));
                            }
                        }

                        if (!MansionGrid.isHouse(grid, x - 1, y)) {
                            BlockVector3 p2 = relative(relative(pos, rotation, BlockFace.EAST, 0), rotation, BlockFace.SOUTH, 0);
                            if (!MansionGrid.isHouse(grid, x, y + 1)) {
                                BlockVector3 p3 = relative(p2, rotation, BlockFace.SOUTH, 6);
                                pieces.add(new WoodlandMansionPiece("roof_corner", p3, rotation.rotateBy(Rotation.ROTATE_90)));
                            } else if (MansionGrid.isHouse(grid, x - 1, y + 1)) {
                                BlockVector3 p3 = relative(relative(p2, rotation, BlockFace.SOUTH, 8), rotation, BlockFace.WEST, 3);
                                pieces.add(new WoodlandMansionPiece("roof_inner_corner", p3, rotation.rotateBy(Rotation.ROTATE_270)));
                            }

                            if (!MansionGrid.isHouse(grid, x, y - 1)) {
                                pieces.add(new WoodlandMansionPiece("roof_corner", p2, rotation.rotateBy(Rotation.ROTATE_180)));
                            } else if (MansionGrid.isHouse(grid, x - 1, y - 1)) {
                                BlockVector3 p3 = relative(p2, rotation, BlockFace.SOUTH, 1);
                                pieces.add(new WoodlandMansionPiece("roof_inner_corner", p3, rotation.rotateBy(Rotation.ROTATE_180)));
                            }
                        }
                    }
                }
            }
        }

        private void entrance(List<WoodlandMansionPiece> pieces, PlacementData data) {
            BlockFace west = rotateFace(BlockFace.WEST, data.rotation);
            pieces.add(new WoodlandMansionPiece("entrance", data.position.add(west.getXOffset() * 9, 0, west.getZOffset() * 9), data.rotation));
            data.position = relative(data.position, data.rotation, BlockFace.SOUTH, 16);
        }

        private void traverseWallPiece(List<WoodlandMansionPiece> pieces, PlacementData data) {
            pieces.add(new WoodlandMansionPiece(data.wallType, relative(data.position, data.rotation, BlockFace.EAST, 7), data.rotation));
            data.position = relative(data.position, data.rotation, BlockFace.SOUTH, 8);
        }

        private void traverseTurn(List<WoodlandMansionPiece> pieces, PlacementData data) {
            data.position = relative(data.position, data.rotation, BlockFace.SOUTH, -1);
            pieces.add(new WoodlandMansionPiece("wall_corner", data.position, data.rotation));
            data.position = relative(data.position, data.rotation, BlockFace.SOUTH, -7);
            data.position = relative(data.position, data.rotation, BlockFace.WEST, -6);
            data.rotation = data.rotation.rotateBy(Rotation.ROTATE_90);
        }

        private void traverseInnerTurn(List<WoodlandMansionPiece> pieces, PlacementData data) {
            data.position = relative(data.position, data.rotation, BlockFace.SOUTH, 6);
            data.position = relative(data.position, data.rotation, BlockFace.EAST, 8);
            data.rotation = data.rotation.rotateBy(Rotation.ROTATE_270);
        }

        private void addRoom1x1(List<WoodlandMansionPiece> pieces, BlockVector3 roomPos, Rotation rotation, BlockFace doorDir, FloorRoomCollection rooms) {
            Rotation pieceRot = Rotation.NONE;
            String roomType = rooms.get1x1(this.random);
            if (doorDir != BlockFace.EAST) {
                if (doorDir == BlockFace.NORTH) {
                    pieceRot = pieceRot.rotateBy(Rotation.ROTATE_270);
                } else if (doorDir == BlockFace.WEST) {
                    pieceRot = pieceRot.rotateBy(Rotation.ROTATE_180);
                } else if (doorDir == BlockFace.SOUTH) {
                    pieceRot = pieceRot.rotateBy(Rotation.ROTATE_90);
                } else {
                    roomType = rooms.get1x1Secret(this.random);
                }
            }

            BlockVector3 orientation = getZeroPositionWithTransform(new BlockVector3(1, 0, 0), Mirror.NONE, pieceRot, 7, 7);
            pieceRot = pieceRot.rotateBy(rotation);
            orientation = rotateOffset(orientation, rotation);
            BlockVector3 pos = roomPos.add(orientation.getX(), 0, orientation.getZ());
            pieces.add(new WoodlandMansionPiece(roomType, pos, pieceRot));
        }

        private void addRoom1x2(
                List<WoodlandMansionPiece> pieces,
                BlockVector3 roomPos,
                Rotation rotation,
                BlockFace roomDir,
                BlockFace doorDir,
                FloorRoomCollection rooms,
                boolean isStairsRoom
        ) {
            if (roomDir == null || doorDir == null) {
                return;
            }
            if (doorDir == BlockFace.EAST && roomDir == BlockFace.SOUTH) {
                BlockVector3 pos = relative(roomPos, rotation, BlockFace.EAST, 1);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2SideEntrance(this.random, isStairsRoom), pos, rotation));
            } else if (doorDir == BlockFace.EAST && roomDir == BlockFace.NORTH) {
                BlockVector3 pos = relative(relative(roomPos, rotation, BlockFace.EAST, 1), rotation, BlockFace.SOUTH, 6);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2SideEntrance(this.random, isStairsRoom), pos, rotation, Mirror.LEFT_RIGHT));
            } else if (doorDir == BlockFace.WEST && roomDir == BlockFace.NORTH) {
                BlockVector3 pos = relative(relative(roomPos, rotation, BlockFace.EAST, 7), rotation, BlockFace.SOUTH, 6);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2SideEntrance(this.random, isStairsRoom), pos, rotation.rotateBy(Rotation.ROTATE_180)));
            } else if (doorDir == BlockFace.WEST && roomDir == BlockFace.SOUTH) {
                BlockVector3 pos = relative(roomPos, rotation, BlockFace.EAST, 7);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2SideEntrance(this.random, isStairsRoom), pos, rotation, Mirror.FRONT_BACK));
            } else if (doorDir == BlockFace.SOUTH && roomDir == BlockFace.EAST) {
                BlockVector3 pos = relative(roomPos, rotation, BlockFace.EAST, 1);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2SideEntrance(this.random, isStairsRoom), pos, rotation.rotateBy(Rotation.ROTATE_90), Mirror.LEFT_RIGHT));
            } else if (doorDir == BlockFace.SOUTH && roomDir == BlockFace.WEST) {
                BlockVector3 pos = relative(roomPos, rotation, BlockFace.EAST, 7);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2SideEntrance(this.random, isStairsRoom), pos, rotation.rotateBy(Rotation.ROTATE_90)));
            } else if (doorDir == BlockFace.NORTH && roomDir == BlockFace.WEST) {
                BlockVector3 pos = relative(relative(roomPos, rotation, BlockFace.EAST, 7), rotation, BlockFace.SOUTH, 6);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2SideEntrance(this.random, isStairsRoom), pos, rotation.rotateBy(Rotation.ROTATE_90), Mirror.FRONT_BACK));
            } else if (doorDir == BlockFace.NORTH && roomDir == BlockFace.EAST) {
                BlockVector3 pos = relative(relative(roomPos, rotation, BlockFace.EAST, 1), rotation, BlockFace.SOUTH, 6);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2SideEntrance(this.random, isStairsRoom), pos, rotation.rotateBy(Rotation.ROTATE_270)));
            } else if (doorDir == BlockFace.SOUTH && roomDir == BlockFace.NORTH) {
                BlockVector3 pos = relative(relative(roomPos, rotation, BlockFace.EAST, 1), rotation, BlockFace.NORTH, 8);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2FrontEntrance(this.random, isStairsRoom), pos, rotation));
            } else if (doorDir == BlockFace.NORTH && roomDir == BlockFace.SOUTH) {
                BlockVector3 pos = relative(relative(roomPos, rotation, BlockFace.EAST, 7), rotation, BlockFace.SOUTH, 14);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2FrontEntrance(this.random, isStairsRoom), pos, rotation.rotateBy(Rotation.ROTATE_180)));
            } else if (doorDir == BlockFace.WEST && roomDir == BlockFace.EAST) {
                BlockVector3 pos = relative(roomPos, rotation, BlockFace.EAST, 15);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2FrontEntrance(this.random, isStairsRoom), pos, rotation.rotateBy(Rotation.ROTATE_90)));
            } else if (doorDir == BlockFace.EAST && roomDir == BlockFace.WEST) {
                BlockVector3 pos = relative(relative(roomPos, rotation, BlockFace.WEST, 7), rotation, BlockFace.SOUTH, 6);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2FrontEntrance(this.random, isStairsRoom), pos, rotation.rotateBy(Rotation.ROTATE_270)));
            } else if (doorDir == BlockFace.UP && roomDir == BlockFace.EAST) {
                BlockVector3 pos = relative(roomPos, rotation, BlockFace.EAST, 15);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2Secret(this.random), pos, rotation.rotateBy(Rotation.ROTATE_90)));
            } else if (doorDir == BlockFace.UP && roomDir == BlockFace.SOUTH) {
                BlockVector3 pos = relative(relative(roomPos, rotation, BlockFace.EAST, 1), rotation, BlockFace.NORTH, 0);
                pieces.add(new WoodlandMansionPiece(rooms.get1x2Secret(this.random), pos, rotation));
            }
        }

        private void addRoom2x2(
                List<WoodlandMansionPiece> pieces,
                BlockVector3 roomPos,
                Rotation rotation,
                BlockFace roomDir,
                BlockFace doorDir,
                FloorRoomCollection rooms
        ) {
            int east = 0;
            int south = 0;
            Rotation rot = rotation;
            Mirror mirror = Mirror.NONE;
            if (doorDir == BlockFace.EAST && roomDir == BlockFace.SOUTH) {
                east = -7;
            } else if (doorDir == BlockFace.EAST && roomDir == BlockFace.NORTH) {
                east = -7;
                south = 6;
                mirror = Mirror.LEFT_RIGHT;
            } else if (doorDir == BlockFace.NORTH && roomDir == BlockFace.EAST) {
                east = 1;
                south = 14;
                rot = rotation.rotateBy(Rotation.ROTATE_270);
            } else if (doorDir == BlockFace.NORTH && roomDir == BlockFace.WEST) {
                east = 7;
                south = 14;
                rot = rotation.rotateBy(Rotation.ROTATE_270);
                mirror = Mirror.LEFT_RIGHT;
            } else if (doorDir == BlockFace.SOUTH && roomDir == BlockFace.WEST) {
                east = 7;
                south = -8;
                rot = rotation.rotateBy(Rotation.ROTATE_90);
            } else if (doorDir == BlockFace.SOUTH && roomDir == BlockFace.EAST) {
                east = 1;
                south = -8;
                rot = rotation.rotateBy(Rotation.ROTATE_90);
                mirror = Mirror.LEFT_RIGHT;
            } else if (doorDir == BlockFace.WEST && roomDir == BlockFace.NORTH) {
                east = 15;
                south = 6;
                rot = rotation.rotateBy(Rotation.ROTATE_180);
            } else if (doorDir == BlockFace.WEST && roomDir == BlockFace.SOUTH) {
                east = 15;
                mirror = Mirror.FRONT_BACK;
            }

            BlockVector3 pos = relative(roomPos, rotation, BlockFace.EAST, east);
            pos = relative(pos, rotation, BlockFace.SOUTH, south);
            pieces.add(new WoodlandMansionPiece(rooms.get2x2(this.random), pos, rot, mirror));
        }

        private void addRoom2x2Secret(List<WoodlandMansionPiece> pieces, BlockVector3 roomPos, Rotation rotation, FloorRoomCollection rooms) {
            BlockVector3 pos = relative(roomPos, rotation, BlockFace.EAST, 1);
            pieces.add(new WoodlandMansionPiece(rooms.get2x2Secret(this.random), pos, rotation, Mirror.NONE));
        }

        private BlockVector3 applyTemplateRotationOffset(String templateName, BlockVector3 pos, Rotation mansionRotation) {
            PNXStructure template = WoodlandMansionPiece.loadTemplate(templateName);
            BlockVector3 localOffset = getZeroPositionWithTransform(new BlockVector3(0, 0, 0), Mirror.NONE, Rotation.ROTATE_180, template.getSizeX(), template.getSizeZ());
            BlockVector3 adjusted = relative(pos, mansionRotation, BlockFace.EAST, localOffset.getX());
            adjusted = relative(adjusted, mansionRotation, BlockFace.SOUTH, localOffset.getZ());
            return adjusted;
        }
    }

    private static final class PlacementData {
        public Rotation rotation;
        public BlockVector3 position;
        public String wallType;
    }

    private static class SecondFloorRoomCollection extends FloorRoomCollection {
        @Override
        public String get1x1(RandomSourceProvider random) {
            return "1x1_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String get1x1Secret(RandomSourceProvider random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String get1x2SideEntrance(RandomSourceProvider random, boolean isStairsRoom) {
            return isStairsRoom ? "1x2_c_stairs" : "1x2_c" + (random.nextInt(4) + 1);
        }

        @Override
        public String get1x2FrontEntrance(RandomSourceProvider random, boolean isStairsRoom) {
            return isStairsRoom ? "1x2_d_stairs" : "1x2_d" + (random.nextInt(5) + 1);
        }

        @Override
        public String get1x2Secret(RandomSourceProvider random) {
            return "1x2_se1";
        }

        @Override
        public String get2x2(RandomSourceProvider random) {
            return "2x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String get2x2Secret(RandomSourceProvider random) {
            return "2x2_s1";
        }
    }

    private static final class SimpleGrid {
        private final int[][] grid;
        private final int width;
        private final int height;
        private final int valueIfOutside;

        public SimpleGrid(int width, int height, int valueIfOutside) {
            this.width = width;
            this.height = height;
            this.valueIfOutside = valueIfOutside;
            this.grid = new int[width][height];
        }

        public void set(int x, int y, int value) {
            if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
                this.grid[x][y] = value;
            }
        }

        public void set(int x0, int y0, int x1, int y1, int value) {
            for (int y = y0; y <= y1; y++) {
                for (int x = x0; x <= x1; x++) {
                    this.set(x, y, value);
                }
            }
        }

        public int get(int x, int y) {
            return x >= 0 && x < this.width && y >= 0 && y < this.height ? this.grid[x][y] : this.valueIfOutside;
        }

        public void setIf(int x, int y, int ifValue, int value) {
            if (this.get(x, y) == ifValue) {
                this.set(x, y, value);
            }
        }

        public boolean edgesTo(int x, int y, int ifValue) {
            return this.get(x - 1, y) == ifValue || this.get(x + 1, y) == ifValue || this.get(x, y + 1) == ifValue || this.get(x, y - 1) == ifValue;
        }
    }

    private static final class ThirdFloorRoomCollection extends SecondFloorRoomCollection {
    }

    private record IntPos(int x, int y) {
    }

    private enum Mirror {
        NONE,
        LEFT_RIGHT,
        FRONT_BACK
    }

    public static final class WoodlandMansionPiece {
        private final String templateName;
        private final PNXStructure template;
        private final BlockVector3 templatePosition;
        private final Rotation rotation;
        private final Mirror mirror;
        private final BoundingBox boundingBox;

        public WoodlandMansionPiece(String templateName, BlockVector3 position, Rotation rotation) {
            this(templateName, position, rotation, Mirror.NONE);
        }

        private WoodlandMansionPiece(String templateName, BlockVector3 position, Rotation rotation, Mirror mirror) {
            this.templateName = templateName;
            this.rotation = rotation;
            this.mirror = mirror;
            this.templatePosition = position;
            this.template = loadTemplate(templateName);
            this.boundingBox = createBoundingBox(position, this.template, rotation, mirror);
        }

        public void place(BlockManager manager) {
            for (PNXStructure.StructureBlockInstance block : this.template.getBlockInstances()) {
                LocalPos transformed = transformLocalVanilla(block.x, block.z, this.mirror, this.rotation);
                BlockState placedState = mirrorState(block.state, this.mirror);
                placedState = rotateState(placedState, this.rotation);
                if (placedState.getIdentifier().equals(BlockStructureVoid.PROPERTIES.getDefaultState().getIdentifier())) {
                    continue;
                }
                int wx = this.templatePosition.getX() + transformed.x;
                int wy = this.templatePosition.getY() + block.y;
                int wz = this.templatePosition.getZ() + transformed.z;
                if (placedState.getIdentifier().equals(BlockAir.STATE.getIdentifier())) {
                    BlockVector3 target = new BlockVector3(wx, wy, wz);
                    if (manager.isCached(target) && !manager.getCachedBlock(wx, wy, wz).isAir()) {
                        continue;
                    }
                }
                manager.setBlockStateAt(
                        wx,
                        wy,
                        wz,
                        placedState
                );
            }
        }

        public BoundingBox boundingBox() {
            return this.boundingBox;
        }

        public String templateName() {
            return this.templateName;
        }

        public BlockVector3 templatePosition() {
            return this.templatePosition;
        }

        public Rotation rotation() {
            return this.rotation;
        }

        private Mirror mirror() {
            return this.mirror;
        }

        private static PNXStructure loadTemplate(String templateName) {
            AbstractStructure structure = Registries.STRUCTURE.get("woodland_mansion/" + templateName);
            if (structure == null) {
                structure = Registries.STRUCTURE.get("woodlandmansion/" + templateName);
            }
            if (!(structure instanceof PNXStructure pnxStructure)) {
                throw new IllegalStateException("Missing Woodland Mansion structure part: " + templateName);
            }
            return pnxStructure;
        }

        private static BoundingBox createBoundingBox(BlockVector3 position, PNXStructure template, Rotation rotation, Mirror mirror) {
            int maxX = template.getSizeX() - 1;
            int maxZ = template.getSizeZ() - 1;
            LocalPos c0 = transformLocalVanilla(0, 0, mirror, rotation);
            LocalPos c1 = transformLocalVanilla(maxX, 0, mirror, rotation);
            LocalPos c2 = transformLocalVanilla(0, maxZ, mirror, rotation);
            LocalPos c3 = transformLocalVanilla(maxX, maxZ, mirror, rotation);
            int minTX = Math.min(Math.min(c0.x, c1.x), Math.min(c2.x, c3.x));
            int maxTX = Math.max(Math.max(c0.x, c1.x), Math.max(c2.x, c3.x));
            int minTZ = Math.min(Math.min(c0.z, c1.z), Math.min(c2.z, c3.z));
            int maxTZ = Math.max(Math.max(c0.z, c1.z), Math.max(c2.z, c3.z));
            return new BoundingBox(
                    position.getX() + minTX,
                    position.getY(),
                    position.getZ() + minTZ,
                    position.getX() + maxTX,
                    position.getY() + template.getSizeY() - 1,
                    position.getZ() + maxTZ
            );
        }

        private static LocalPos transformLocalVanilla(int x, int z, Mirror mirror, Rotation rotation) {
            int tx = x;
            int tz = z;
            boolean wasMirrored = true;
            switch (mirror) {
                case LEFT_RIGHT -> tz = -tz;
                case FRONT_BACK -> tx = -tx;
                default -> wasMirrored = false;
            }

            return switch (rotation) {
                case ROTATE_270 -> new LocalPos(tz, -tx);
                case ROTATE_90 -> new LocalPos(-tz, tx);
                case ROTATE_180 -> new LocalPos(-tx, -tz);
                default -> wasMirrored ? new LocalPos(tx, tz) : new LocalPos(x, z);
            };
        }

        private static BlockState rotateState(BlockState state, Rotation rotation) {
            return switch (rotation) {
                case ROTATE_90 -> Rotation.clockwise90(state);
                case ROTATE_180 -> Rotation.clockwise180(state);
                case ROTATE_270 -> Rotation.counterclockwise90(state);
                default -> state;
            };
        }

        private static BlockState mirrorState(BlockState state, Mirror mirror) {
            if (mirror == Mirror.NONE) {
                return state;
            }

            BlockState mirrored = state;
            var properties = state.toBlock().getProperties();

            Integer facingDirection = mirrored.getPropertyValue(FACING_DIRECTION);
            if (facingDirection != null) {
                int mirroredFacingDirection = mirrorFacingDirection(facingDirection, mirror);
                if (mirroredFacingDirection != facingDirection) {
                    mirrored = mirrored.setPropertyValue(properties, FACING_DIRECTION.createValue(mirroredFacingDirection));
                }
            }

            MinecraftCardinalDirection cardinalDirection = mirrored.getPropertyValue(MINECRAFT_CARDINAL_DIRECTION);
            if (cardinalDirection != null) {
                MinecraftCardinalDirection mirroredCardinalDirection = mirrorCardinalDirection(cardinalDirection, mirror);
                if (mirroredCardinalDirection != cardinalDirection) {
                    mirrored = mirrored.setPropertyValue(properties, MINECRAFT_CARDINAL_DIRECTION.createValue(mirroredCardinalDirection));
                }
            }

            TorchFacingDirection torchFacing = mirrored.getPropertyValue(TORCH_FACING_DIRECTION);
            if (torchFacing != null) {
                TorchFacingDirection mirroredTorchFacing = mirrorTorchDirection(torchFacing, mirror);
                if (mirroredTorchFacing != torchFacing) {
                    mirrored = mirrored.setPropertyValue(properties, TORCH_FACING_DIRECTION.createValue(mirroredTorchFacing));
                }
            }

            LeverDirection leverDirection = mirrored.getPropertyValue(LEVER_DIRECTION);
            if (leverDirection != null) {
                LeverDirection mirroredLeverDirection = mirrorLeverDirection(leverDirection, mirror);
                if (mirroredLeverDirection != leverDirection) {
                    mirrored = mirrored.setPropertyValue(properties, LEVER_DIRECTION.createValue(mirroredLeverDirection));
                }
            }

            Integer weirdoDirection = mirrored.getPropertyValue(WEIRDO_DIRECTION);
            if (weirdoDirection != null) {
                int mirroredWeirdoDirection = mirrorWeirdoDirection(weirdoDirection, mirror);
                if (mirroredWeirdoDirection != weirdoDirection) {
                    mirrored = mirrored.setPropertyValue(properties, WEIRDO_DIRECTION.createValue(mirroredWeirdoDirection));
                }
            }

            return mirrored;
        }

        private static int mirrorFacingDirection(int facingDirection, Mirror mirror) {
            int extraBits = facingDirection & ~0x7;
            BlockFace face = BlockFace.fromIndex(facingDirection & 0x7);
            if (!face.getAxis().isHorizontal()) {
                return facingDirection;
            }
            BlockFace mirroredFace = mirrorHorizontalFace(face, mirror);
            return mirroredFace.getIndex() | extraBits;
        }

        private static MinecraftCardinalDirection mirrorCardinalDirection(MinecraftCardinalDirection direction, Mirror mirror) {
            return switch (direction) {
                case EAST -> mirror == Mirror.FRONT_BACK ? MinecraftCardinalDirection.WEST : MinecraftCardinalDirection.EAST;
                case WEST -> mirror == Mirror.FRONT_BACK ? MinecraftCardinalDirection.EAST : MinecraftCardinalDirection.WEST;
                case NORTH -> mirror == Mirror.LEFT_RIGHT ? MinecraftCardinalDirection.SOUTH : MinecraftCardinalDirection.NORTH;
                case SOUTH -> mirror == Mirror.LEFT_RIGHT ? MinecraftCardinalDirection.NORTH : MinecraftCardinalDirection.SOUTH;
            };
        }

        private static TorchFacingDirection mirrorTorchDirection(TorchFacingDirection direction, Mirror mirror) {
            return switch (direction) {
                case EAST -> mirror == Mirror.FRONT_BACK ? TorchFacingDirection.WEST : TorchFacingDirection.EAST;
                case WEST -> mirror == Mirror.FRONT_BACK ? TorchFacingDirection.EAST : TorchFacingDirection.WEST;
                case NORTH -> mirror == Mirror.LEFT_RIGHT ? TorchFacingDirection.SOUTH : TorchFacingDirection.NORTH;
                case SOUTH -> mirror == Mirror.LEFT_RIGHT ? TorchFacingDirection.NORTH : TorchFacingDirection.SOUTH;
                default -> direction;
            };
        }

        private static LeverDirection mirrorLeverDirection(LeverDirection direction, Mirror mirror) {
            int meta = direction.getMetadata();
            int thrownBit = meta & 0x8;
            int baseMeta = meta & ~0x8;

            if (mirror == Mirror.FRONT_BACK) {
                baseMeta = switch (baseMeta) {
                    case 1 -> 2;
                    case 2 -> 1;
                    default -> baseMeta;
                };
            } else if (mirror == Mirror.LEFT_RIGHT) {
                baseMeta = switch (baseMeta) {
                    case 3 -> 4;
                    case 4 -> 3;
                    default -> baseMeta;
                };
            }

            return LeverDirection.byMetadata(baseMeta | thrownBit);
        }

        private static int mirrorWeirdoDirection(int direction, Mirror mirror) {
            return switch (direction) {
                case 0 -> mirror == Mirror.FRONT_BACK ? 1 : 0;
                case 1 -> mirror == Mirror.FRONT_BACK ? 0 : 1;
                case 2 -> mirror == Mirror.LEFT_RIGHT ? 3 : 2;
                case 3 -> mirror == Mirror.LEFT_RIGHT ? 2 : 3;
                default -> direction;
            };
        }

        private static BlockFace mirrorHorizontalFace(BlockFace face, Mirror mirror) {
            if (mirror == Mirror.FRONT_BACK && face.getAxis() == BlockFace.Axis.X) {
                return face.getOpposite();
            }
            if (mirror == Mirror.LEFT_RIGHT && face.getAxis() == BlockFace.Axis.Z) {
                return face.getOpposite();
            }
            return face;
        }

        private record LocalPos(int x, int z) {
        }
    }

    public static final class PostPlacement {
        private final List<BlockVector3> chests;
        private final List<MobSpawn> mobSpawns;
        private final List<BlockVector3> spiderSpawnerPositions;

        private PostPlacement(List<BlockVector3> chests, List<MobSpawn> mobSpawns, List<BlockVector3> spiderSpawnerPositions) {
            this.chests = List.copyOf(chests);
            this.mobSpawns = List.copyOf(mobSpawns);
            this.spiderSpawnerPositions = List.copyOf(spiderSpawnerPositions);
        }

        public List<BlockVector3> chests() {
            return this.chests;
        }

        public List<MobSpawn> mobSpawns() {
            return this.mobSpawns;
        }

        public List<BlockVector3> spiderSpawnerPositions() {
            return this.spiderSpawnerPositions;
        }
    }

    private static final class MansionChestLoot extends RandomizableContainer {
        private MansionChestLoot() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.LEAD, 20))
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 15))
                    .register(new ItemEntry(Item.ENCHANTED_GOLDEN_APPLE, 2))
                    .register(new ItemEntry(Item.MUSIC_DISC_13, 15))
                    .register(new ItemEntry(Item.MUSIC_DISC_CAT, 15))
                    .register(new ItemEntry(Item.CHAINMAIL_CHESTPLATE, 10))
                    .register(new ItemEntry(Item.DIAMOND_HOE, 15))
                    .register(new ItemEntry(Item.DIAMOND_CHESTPLATE, 5))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 10, getDefaultEnchantments()));
            this.pools.put(pool1.build(), new RollEntry(3, 1, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 4, 1, 10))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 4, 1, 5))
                    .register(new ItemEntry(Item.BREAD, 20))
                    .register(new ItemEntry(BlockID.WHEAT, 0, 4, 1, 20))
                    .register(new ItemEntry(Item.BUCKET, 10))
                    .register(new ItemEntry(Item.REDSTONE, 0, 4, 1, 15))
                    .register(new ItemEntry(Item.COAL, 0, 4, 1, 15))
                    .register(new ItemEntry(Item.MELON_SEEDS, 0, 4, 2, 10))
                    .register(new ItemEntry(Item.PUMPKIN_SEEDS, 0, 4, 2, 10))
                    .register(new ItemEntry(Item.BEETROOT_SEEDS, 0, 4, 2, 10))
                    .register(new ItemEntry(BlockID.RESIN_CLUMP, 0, 4, 2, 50));
            this.pools.put(pool2.build(), new RollEntry(4, 1, pool2.getTotalWeight()));

            PoolBuilder pool3 = new PoolBuilder()
                    .register(new ItemEntry(Item.BONE, 0, 8, 1, 10))
                    .register(new ItemEntry(Item.GUNPOWDER, 0, 8, 1, 10))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 8, 1, 10))
                    .register(new ItemEntry(Item.STRING, 0, 8, 1, 10));
            this.pools.put(pool3.build(), new RollEntry(3, pool3.getTotalWeight()));
        }

        @Override
        public void create(Inventory inventory, RandomSourceProvider random) {
            try {
                super.create(inventory, random);
                if (random.nextBoolean()) {
                    inventory.setItem(
                            random.nextBoundedInt(inventory.getSize()),
                            Item.get(Item.VEX_ARMOR_TRIM_SMITHING_TEMPLATE)
                    );
                }
            } catch (Exception ignored) {
            }
        }
    }
}
