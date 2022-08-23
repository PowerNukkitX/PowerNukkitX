package cn.nukkit.level.generator.populator.impl.structure.oceanmonument.structure;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockPrismarine;
import cn.nukkit.block.BlockSponge;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.oceanmonument.util.NukkitCollections;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.BlockState;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.BoundingBox;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructurePiece;
import cn.nukkit.level.generator.task.ActorSpawnTask;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class OceanMonumentPieces {

    private static final BlockState PRISMARINE = new BlockState(Block.PRISMARINE, BlockPrismarine.NORMAL);
    private static final BlockState PRISMARINE_BRICKS = new BlockState(Block.PRISMARINE, BlockPrismarine.BRICKS);
    private static final BlockState DARK_PRISMARINE = new BlockState(Block.PRISMARINE, BlockPrismarine.DARK);
    private static final BlockState SEA_LANTERN = new BlockState(Block.SEA_LANTERN);
    private static final BlockState WATER = new BlockState(Block.STILL_WATER);
    private static final BlockState FLOWING_WATER = new BlockState(Block.WATER);
    private static final BlockState WET_SPONGE = new BlockState(Block.SPONGE, BlockSponge.WET);
    private static final BlockState GOLD_BLOCK = new BlockState(Block.GOLD_BLOCK);
    private static final BlockState ICE = new BlockState(Block.ICE);
    private static final BlockState PACKED_ICE = new BlockState(Block.PACKED_ICE);
    //private static final BlockState BLUE_ICE = new BlockState(Block.BLUE_ICE);

    public abstract static class OceanMonumentPiece extends StructurePiece {

        protected static BlockState BASE_GRAY = PRISMARINE;
        protected static BlockState BASE_LIGHT = PRISMARINE_BRICKS;
        protected static BlockState BASE_BLACK = DARK_PRISMARINE;
        protected static BlockState DOT_DECO_DATA = BASE_LIGHT;
        protected static BlockState LAMP_BLOCK = SEA_LANTERN;
        protected static BlockState FILL_BLOCK = WATER;

        protected static Set<BlockState> FILL_KEEP = ImmutableSet.<BlockState>builder()
                .add(ICE)
                .add(PACKED_ICE)
                //.add(BLUE_ICE)
                .add(FLOWING_WATER)
                .add(WATER)
                .build();

        protected static int GRIDROOM_SOURCE_INDEX = getRoomIndex(2, 0, 0);
        protected static int GRIDROOM_TOP_CONNECT_INDEX = getRoomIndex(2, 2, 0);
        protected static int GRIDROOM_LEFTWING_CONNECT_INDEX = getRoomIndex(0, 1, 0);
        protected static int GRIDROOM_RIGHTWING_CONNECT_INDEX = getRoomIndex(4, 1, 0);

        protected RoomDefinition roomDefinition;

        protected static int getRoomIndex(int x, int y, int z) {
            return y * 25 + z * 5 + x;
        }

        public OceanMonumentPiece(int genDepth) {
            super(genDepth);
        }

        public OceanMonumentPiece(BlockFace orientation, BoundingBox boundingBox) {
            super(1);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        protected OceanMonumentPiece(int genDepth, BlockFace orientation, RoomDefinition roomDefinition, int x, int y, int z) {
            super(genDepth);
            this.setOrientation(orientation);
            this.roomDefinition = roomDefinition;

            int index = roomDefinition.index;
            int indexX = index % 5;
            int indexZ = index / 5 % 5;
            int indexY = index / 25;

            if (orientation == BlockFace.NORTH || orientation == BlockFace.SOUTH) {
                this.boundingBox = new BoundingBox(0, 0, 0, x * 8 - 1, y * 4 - 1, z * 8 - 1);
            } else {
                this.boundingBox = new BoundingBox(0, 0, 0, z * 8 - 1, y * 4 - 1, x * 8 - 1);
            }

            switch (orientation) {
                case NORTH:
                    this.boundingBox.move(indexX * 8, indexY * 4, -(indexZ + z) * 8 + 1);
                    break;
                case SOUTH:
                    this.boundingBox.move(indexX * 8, indexY * 4, indexZ * 8);
                    break;
                case WEST:
                    this.boundingBox.move(-(indexZ + z) * 8 + 1, indexY * 4, indexX * 8);
                    break;
                default:
                    this.boundingBox.move(indexZ * 8, indexY * 4, indexX * 8);
                    break;
            }
        }

        public OceanMonumentPiece(CompoundTag tag) {
            super(tag);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {

        }

        //\\ OceanMonumentPiece::generateWaterBox(BlockSource *,BoundingBox const &,int,int,int,int,int,int,bool)
        protected void generateWaterBox(ChunkManager level, BoundingBox boundingBox, int x0, int y0, int z0, int x1, int y1, int z1) {
            for (int y = y0; y <= y1; ++y) {
                for (int x = x0; x <= x1; ++x) {
                    for (int z = z0; z <= z1; ++z) {
                        BlockState block = this.getBlock(level, x, y, z, boundingBox);
                        if (!FILL_KEEP.contains(block)) {
                            if (this.getWorldY(y) >= 63 && block.getId() != Block.WATER && block.getId() != Block.STILL_WATER) { // != FILL_BLOCK
                                this.placeBlock(level, BlockState.AIR, x, y, z, boundingBox);
                            } else {
                                this.placeBlock(level, FILL_BLOCK, x, y, z, boundingBox);
                            }
                        }
                    }
                }
            }
        }

        //\\ OceanMonumentPiece::generateDefaultFloor(BlockSource *,BoundingBox const &,int,int,bool)
        protected void generateDefaultFloor(ChunkManager level, BoundingBox boundingBox, int x, int z, boolean hasOpening) {
            if (hasOpening) {
                this.generateBox(level, boundingBox, x + 0, 0, z + 0, x + 2, 0, z + 8 - 1, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, x + 5, 0, z + 0, x + 8 - 1, 0, z + 8 - 1, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, x + 3, 0, z + 0, x + 4, 0, z + 2, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, x + 3, 0, z + 5, x + 4, 0, z + 8 - 1, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, x + 3, 0, z + 2, x + 4, 0, z + 2, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, x + 3, 0, z + 5, x + 4, 0, z + 5, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, x + 2, 0, z + 3, x + 2, 0, z + 4, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, x + 5, 0, z + 3, x + 5, 0, z + 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
                this.generateBox(level, boundingBox, x + 0, 0, z + 0, x + 8 - 1, 0, z + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            }
        }

        //\\ OceanMonumentPiece::generateBoxOnFillOnly(BlockSource *,BoundingBox const &,int,int,int,int,int,int,Block const &)
        protected void generateBoxOnFillOnly(ChunkManager level, BoundingBox boundingBox, int x0, int y0, int z0, int x1, int y1, int z1, BlockState block) {
            for (int y = y0; y <= y1; ++y) {
                for (int x = x0; x <= x1; ++x) {
                    for (int z = z0; z <= z1; ++z) {
                        BlockState current = this.getBlock(level, x, y, z, boundingBox);
                        if (current.getId() == Block.WATER || current.getId() == Block.STILL_WATER) { // == FILL_BLOCK
                            this.placeBlock(level, block, x, y, z, boundingBox);
                        }
                    }
                }
            }
        }

        //\\ OceanMonumentPiece::chunkIntersects(BoundingBox const &,int,int,int,int)
        protected boolean chunkIntersects(BoundingBox boundingBox, int x0, int z0, int x1, int z1) {
            int worldX0 = this.getWorldX(x0, z0);
            int worldZ0 = this.getWorldZ(x0, z0);
            int worldX1 = this.getWorldX(x1, z1);
            int worldZ1 = this.getWorldZ(x1, z1);
            return boundingBox.intersects(Math.min(worldX0, worldX1), Math.min(worldZ0, worldZ1), Math.max(worldX0, worldX1), Math.max(worldZ0, worldZ1));
        }

        //\\ OceanMonumentPiece::spawnElder(BlockSource *,BoundingBox const &,int,int,int)
        protected boolean spawnElder(ChunkManager level, BoundingBox boundingBox, int x, int y, int z) {
            int worldX = this.getWorldX(x, z);
            int worldY = this.getWorldY(y);
            int worldZ = this.getWorldZ(x, z);

            if (boundingBox.isInside(new BlockVector3(worldX, worldY, worldZ))) {
                BaseFullChunk chunk = level.getChunk(worldX >> 4, worldZ >> 4);
                if (chunk != null) {
                    CompoundTag nbt = Entity.getDefaultNBT(new Vector3(worldX + .5, worldY, worldZ + .5))
                            .putString("id", "ElderGuardian");

                    Server.getInstance().getScheduler().scheduleTask(new ActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                }

                return true;
            }

            return false;
        }
    }

    public static class MonumentBuilding extends OceanMonumentPiece {

        private RoomDefinition sourceRoom;
        private RoomDefinition coreRoom;
        private final List<OceanMonumentPiece> childPieces;

        public MonumentBuilding(NukkitRandom random, int x, int z, BlockFace orientation) {
            super(0);
            this.childPieces = Lists.newArrayList();
            this.setOrientation(orientation);
            this.boundingBox = new BoundingBox(x, 39, z, x + 58 - 1, 61, z + 58 - 1);

            List<RoomDefinition> roomDefinitions = this.generateRoomGraph(random);
            this.sourceRoom.claimed = true;
            this.childPieces.add(new OceanMonumentEntryRoom(orientation, this.sourceRoom));
            this.childPieces.add(new OceanMonumentCoreRoom(orientation, this.coreRoom));

            List<MonumentRoomFitter> fitters = Lists.newArrayList();
            fitters.add(new FitDoubleXYRoom());
            fitters.add(new FitDoubleYZRoom());
            fitters.add(new FitDoubleZRoom());
            fitters.add(new FitDoubleXRoom());
            fitters.add(new FitDoubleYRoom());
            fitters.add(new FitSimpleTopRoom());
            fitters.add(new FitSimpleRoom());

            for (RoomDefinition roomDefinition : roomDefinitions) {
                if (!roomDefinition.claimed && !roomDefinition.isSpecial()) {
                    for (MonumentRoomFitter fitter : fitters) {
                        if (fitter.fits(roomDefinition)) {
                            this.childPieces.add(fitter.create(orientation, roomDefinition, random));
                            break;
                        }
                    }
                }
            }

            int y = this.boundingBox.y0;
            int worldX = this.getWorldX(9, 22);
            int worldZ = this.getWorldZ(9, 22);

            for (OceanMonumentPiece piece : this.childPieces) {
                piece.getBoundingBox().move(worldX, y, worldZ);
            }

            int mainDesign = random.nextInt();
            this.childPieces.add(new OceanMonumentWingRoom(orientation, BoundingBox.createProper(this.getWorldX(1, 1), this.getWorldY(1), this.getWorldZ(1, 1), this.getWorldX(23, 21), this.getWorldY(8), this.getWorldZ(23, 21)), mainDesign++));
            this.childPieces.add(new OceanMonumentWingRoom(orientation, BoundingBox.createProper(this.getWorldX(34, 1), this.getWorldY(1), this.getWorldZ(34, 1), this.getWorldX(56, 21), this.getWorldY(8), this.getWorldZ(56, 21)), mainDesign++));
            this.childPieces.add(new OceanMonumentPenthouse(orientation, BoundingBox.createProper(this.getWorldX(22, 22), this.getWorldY(13), this.getWorldZ(22, 22), this.getWorldX(35, 35), this.getWorldY(17), this.getWorldZ(35, 35))));
        }

        public MonumentBuilding(CompoundTag tag) {
            super(tag);
            this.childPieces = Lists.newArrayList();
        }

        @Override
        public String getType() {
            return "OMB";
        }

        private List<RoomDefinition> generateRoomGraph(NukkitRandom random) {
            RoomDefinition[] roomDefinitions = new RoomDefinition[75];
            for (int x = 0; x < 5; ++x) {
                for (int z = 0; z < 4; ++z) {
                    int index = getRoomIndex(x, 0, z);
                    roomDefinitions[index] = new RoomDefinition(index);
                }
            }
            for (int x = 0; x < 5; ++x) {
                for (int z = 0; z < 4; ++z) {
                    int index = getRoomIndex(x, 1, z);
                    roomDefinitions[index] = new RoomDefinition(index);
                }
            }
            for (int x = 1; x < 4; ++x) {
                for (int z = 0; z < 2; ++z) {
                    int index = getRoomIndex(x, 2, z);
                    roomDefinitions[index] = new RoomDefinition(index);
                }
            }

            this.sourceRoom = roomDefinitions[GRIDROOM_SOURCE_INDEX];

            for (int xI = 0; xI < 5; ++xI) {
                for (int zI = 0; zI < 5; ++zI) {
                    for (int yI = 0; yI < 3; ++yI) {
                        int index = getRoomIndex(xI, yI, zI);
                        if (roomDefinitions[index] != null) {
                            for (BlockFace orientation : BlockFace.values()) {
                                int xx = xI + orientation.getUnitVector().getFloorX();
                                int yy = yI + orientation.getUnitVector().getFloorY();
                                int zz = zI + orientation.getUnitVector().getFloorZ();

                                if (xx >= 0 && xx < 5 && zz >= 0 && zz < 5 && yy >= 0 && yy < 3) {
                                    int i = getRoomIndex(xx, yy, zz);

                                    if (roomDefinitions[i] != null) {
                                        if (zz == zI) {
                                            roomDefinitions[index].setConnection(orientation, roomDefinitions[i]);
                                        } else {
                                            roomDefinitions[index].setConnection(orientation.getOpposite(), roomDefinitions[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            RoomDefinition room1003 = new RoomDefinition(1003);
            RoomDefinition room1001 = new RoomDefinition(1001);
            RoomDefinition room1002 = new RoomDefinition(1002);

            roomDefinitions[GRIDROOM_TOP_CONNECT_INDEX].setConnection(BlockFace.UP, room1003);
            roomDefinitions[GRIDROOM_LEFTWING_CONNECT_INDEX].setConnection(BlockFace.SOUTH, room1001);
            roomDefinitions[GRIDROOM_RIGHTWING_CONNECT_INDEX].setConnection(BlockFace.SOUTH, room1002);

            room1003.claimed = true;
            room1001.claimed = true;
            room1002.claimed = true;
            this.sourceRoom.isSource = true;
            (this.coreRoom = roomDefinitions[getRoomIndex(random.nextBoundedInt(4), 0, 2)]).claimed = true;
            this.coreRoom.connections[BlockFace.EAST.getIndex()].claimed = true;
            this.coreRoom.connections[BlockFace.NORTH.getIndex()].claimed = true;
            this.coreRoom.connections[BlockFace.EAST.getIndex()].connections[BlockFace.NORTH.getIndex()].claimed = true;
            this.coreRoom.connections[BlockFace.UP.getIndex()].claimed = true;
            this.coreRoom.connections[BlockFace.EAST.getIndex()].connections[BlockFace.UP.getIndex()].claimed = true;
            this.coreRoom.connections[BlockFace.NORTH.getIndex()].connections[BlockFace.UP.getIndex()].claimed = true;
            this.coreRoom.connections[BlockFace.EAST.getIndex()].connections[BlockFace.NORTH.getIndex()].connections[BlockFace.UP.getIndex()].claimed = true;

            List<RoomDefinition> rooms = Lists.newArrayList();
            for (RoomDefinition room : roomDefinitions) {
                if (room != null) {
                    room.updateOpenings();
                    rooms.add(room);
                }
            }

            room1003.updateOpenings();
            NukkitCollections.shuffle(rooms, random);
            int findCount = 1;

            for (RoomDefinition room : rooms) {
                int foundCount = 0;
                int count = 0;
                while (foundCount < 2 && count < 5) {
                    ++count;
                    int index = random.nextBoundedInt(6);
                    if (room.hasOpening[index]) {
                        int oppositeIndex = BlockFace.fromIndex(index).getOpposite().getIndex();
                        room.hasOpening[index] = false;
                        room.connections[index].hasOpening[oppositeIndex] = false;
                        if (room.findSource(findCount++) && room.connections[index].findSource(findCount++)) {
                            ++foundCount;
                        } else {
                            room.hasOpening[index] = true;
                            room.connections[index].hasOpening[oppositeIndex] = true;
                        }
                    }
                }
            }

            rooms.add(room1003);
            rooms.add(room1001);
            rooms.add(room1002);

            return rooms;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateWaterBox(level, boundingBox, 0, 0, 0, 58, 64 - this.boundingBox.y0, 58);
            this.generateWing(false, 0, level, random, boundingBox);
            this.generateWing(true, 33, level, random, boundingBox);
            this.generateEntranceArchs(level, random, boundingBox);
            this.generateEntranceWall(level, random, boundingBox);
            this.generateRoofPiece(level, random, boundingBox);
            this.generateLowerWall(level, random, boundingBox);
            this.generateMiddleWall(level, random, boundingBox);
            this.generateUpperWall(level, random, boundingBox);

            for (int xI = 0; xI < 7; ++xI) {
                int zI = 0;
                while (zI < 7) {
                    if (zI == 0 && xI == 3) {
                        zI = 6;
                    }

                    int x = xI * 9;
                    int z = zI * 9;
                    for (int xOffset = 0; xOffset < 4; ++xOffset) {
                        for (int zOffset = 0; zOffset < 4; ++zOffset) {
                            this.placeBlock(level, BASE_LIGHT, x + xOffset, 0, z + zOffset, boundingBox);
                            this.fillColumnDown(level, BASE_LIGHT, x + xOffset, -1, z + zOffset, boundingBox);
                        }
                    }

                    if (xI == 0 || xI == 6) {
                        ++zI;
                    } else {
                        zI += 6;
                    }
                }
            }

            for (int i = 0; i < 5; ++i) {
                this.generateWaterBox(level, boundingBox, -1 - i, 0 + i * 2, -1 - i, -1 - i, 23, 58 + i);
                this.generateWaterBox(level, boundingBox, 58 + i, 0 + i * 2, -1 - i, 58 + i, 23, 58 + i);
                this.generateWaterBox(level, boundingBox, 0 - i, 0 + i * 2, -1 - i, 57 + i, 23, -1 - i);
                this.generateWaterBox(level, boundingBox, 0 - i, 0 + i * 2, 58 + i, 57 + i, 23, 58 + i);
            }

            for (OceanMonumentPiece piece : this.childPieces) {
                if (piece.getBoundingBox().intersects(boundingBox)) {
                    piece.postProcess(level, random, boundingBox, chunkX, chunkZ);
                }
            }

            return true;
        }

        private void generateWing(boolean reverse, int x, ChunkManager level, NukkitRandom random, BoundingBox boundingBox) {
            if (this.chunkIntersects(boundingBox, x, 0, x + 23, 20)) {
                this.generateBox(level, boundingBox, x + 0, 0, 0, x + 24, 0, 20, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, x + 0, 1, 0, x + 24, 10, 20);

                for (int k = 0; k < 4; ++k) {
                    this.generateBox(level, boundingBox, x + k, k + 1, k, x + k, k + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x + k + 7, k + 5, k + 7, x + k + 7, k + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x + 17 - k, k + 5, k + 7, x + 17 - k, k + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x + 24 - k, k + 1, k, x + 24 - k, k + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x + k + 1, k + 1, k, x + 23 - k, k + 1, k, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x + k + 8, k + 5, k + 7, x + 16 - k, k + 5, k + 7, BASE_LIGHT, BASE_LIGHT, false);
                }

                this.generateBox(level, boundingBox, x + 4, 4, 4, x + 6, 4, 20, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, x + 7, 4, 4, x + 17, 4, 6, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, x + 18, 4, 4, x + 20, 4, 20, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, x + 11, 8, 11, x + 13, 8, 20, BASE_GRAY, BASE_GRAY, false);

                this.placeBlock(level, DOT_DECO_DATA, x + 12, 9, 12, boundingBox);
                this.placeBlock(level, DOT_DECO_DATA, x + 12, 9, 15, boundingBox);
                this.placeBlock(level, DOT_DECO_DATA, x + 12, 9, 18, boundingBox);

                int xx = x + (reverse ? 5 : 19);

                for (int z = 20; z >= 5; z -= 3) {
                    this.placeBlock(level, DOT_DECO_DATA, x + (reverse ? 19 : 5), 5, z, boundingBox);
                }

                for (int z = 19; z >= 7; z -= 3) {
                    this.placeBlock(level, DOT_DECO_DATA, xx, 5, z, boundingBox);
                }

                for (int i = 0; i < 4; ++i) {
                    this.placeBlock(level, DOT_DECO_DATA, reverse ? (x + 24 - (17 - i * 3)) : (x + 17 - i * 3), 5, 5, boundingBox);
                }

                this.placeBlock(level, DOT_DECO_DATA, xx, 5, 5, boundingBox);
                this.generateBox(level, boundingBox, x + 11, 1, 12, x + 13, 7, 12, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, x + 12, 1, 11, x + 12, 7, 13, BASE_GRAY, BASE_GRAY, false);
            }
        }

        private void generateEntranceArchs(ChunkManager level, NukkitRandom random, BoundingBox boundingBox) {
            if (this.chunkIntersects(boundingBox, 22, 5, 35, 17)) {
                this.generateWaterBox(level, boundingBox, 25, 0, 0, 32, 8, 20);

                for (int z = 0; z < 4; ++z) {
                    this.generateBox(level, boundingBox, 24, 2, 5 + z * 4, 24, 4, 5 + z * 4, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 22, 4, 5 + z * 4, 23, 4, 5 + z * 4, BASE_LIGHT, BASE_LIGHT, false);
                    this.placeBlock(level, BASE_LIGHT, 25, 5, 5 + z * 4, boundingBox);
                    this.placeBlock(level, BASE_LIGHT, 26, 6, 5 + z * 4, boundingBox);
                    this.placeBlock(level, LAMP_BLOCK, 26, 5, 5 + z * 4, boundingBox);
                    this.generateBox(level, boundingBox, 33, 2, 5 + z * 4, 33, 4, 5 + z * 4, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 34, 4, 5 + z * 4, 35, 4, 5 + z * 4, BASE_LIGHT, BASE_LIGHT, false);
                    this.placeBlock(level, BASE_LIGHT, 32, 5, 5 + z * 4, boundingBox);
                    this.placeBlock(level, BASE_LIGHT, 31, 6, 5 + z * 4, boundingBox);
                    this.placeBlock(level, LAMP_BLOCK, 31, 5, 5 + z * 4, boundingBox);
                    this.generateBox(level, boundingBox, 27, 6, 5 + z * 4, 30, 6, 5 + z * 4, BASE_GRAY, BASE_GRAY, false);
                }
            }
        }

        private void generateEntranceWall(ChunkManager level, NukkitRandom random, BoundingBox boundingBox) {
            if (this.chunkIntersects(boundingBox, 15, 20, 42, 21)) {
                this.generateBox(level, boundingBox, 15, 0, 21, 42, 0, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 26, 1, 21, 31, 3, 21);
                this.generateBox(level, boundingBox, 21, 12, 21, 36, 12, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 17, 11, 21, 40, 11, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 16, 10, 21, 41, 10, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 15, 7, 21, 42, 9, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 16, 6, 21, 41, 6, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 17, 5, 21, 40, 5, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 21, 4, 21, 36, 4, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 22, 3, 21, 26, 3, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 31, 3, 21, 35, 3, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 23, 2, 21, 25, 2, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 32, 2, 21, 34, 2, 21, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 28, 4, 20, 29, 4, 21, BASE_LIGHT, BASE_LIGHT, false);

                this.placeBlock(level, BASE_LIGHT, 27, 3, 21, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 30, 3, 21, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 26, 2, 21, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 31, 2, 21, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 25, 1, 21, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 32, 1, 21, boundingBox);

                for (int i = 0; i < 7; ++i) {
                    this.placeBlock(level, BASE_BLACK, 28 - i, 6 + i, 21, boundingBox);
                    this.placeBlock(level, BASE_BLACK, 29 + i, 6 + i, 21, boundingBox);
                }

                for (int i = 0; i < 4; ++i) {
                    this.placeBlock(level, BASE_BLACK, 28 - i, 9 + i, 21, boundingBox);
                    this.placeBlock(level, BASE_BLACK, 29 + i, 9 + i, 21, boundingBox);
                }

                this.placeBlock(level, BASE_BLACK, 28, 12, 21, boundingBox);
                this.placeBlock(level, BASE_BLACK, 29, 12, 21, boundingBox);

                for (int x = 0; x < 3; ++x) {
                    this.placeBlock(level, BASE_BLACK, 22 - x * 2, 8, 21, boundingBox);
                    this.placeBlock(level, BASE_BLACK, 22 - x * 2, 9, 21, boundingBox);
                    this.placeBlock(level, BASE_BLACK, 35 + x * 2, 8, 21, boundingBox);
                    this.placeBlock(level, BASE_BLACK, 35 + x * 2, 9, 21, boundingBox);
                }

                this.generateWaterBox(level, boundingBox, 15, 13, 21, 42, 15, 21);
                this.generateWaterBox(level, boundingBox, 15, 1, 21, 15, 6, 21);
                this.generateWaterBox(level, boundingBox, 16, 1, 21, 16, 5, 21);
                this.generateWaterBox(level, boundingBox, 17, 1, 21, 20, 4, 21);
                this.generateWaterBox(level, boundingBox, 21, 1, 21, 21, 3, 21);
                this.generateWaterBox(level, boundingBox, 22, 1, 21, 22, 2, 21);
                this.generateWaterBox(level, boundingBox, 23, 1, 21, 24, 1, 21);
                this.generateWaterBox(level, boundingBox, 42, 1, 21, 42, 6, 21);
                this.generateWaterBox(level, boundingBox, 41, 1, 21, 41, 5, 21);
                this.generateWaterBox(level, boundingBox, 37, 1, 21, 40, 4, 21);
                this.generateWaterBox(level, boundingBox, 36, 1, 21, 36, 3, 21);
                this.generateWaterBox(level, boundingBox, 33, 1, 21, 34, 1, 21);
                this.generateWaterBox(level, boundingBox, 35, 1, 21, 35, 2, 21);
            }
        }

        private void generateRoofPiece(ChunkManager level, NukkitRandom random, BoundingBox boundingBox) {
            if (this.chunkIntersects(boundingBox, 21, 21, 36, 36)) {
                this.generateBox(level, boundingBox, 21, 0, 22, 36, 0, 36, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 21, 1, 22, 36, 23, 36);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, BASE_LIGHT, BASE_LIGHT, false);
                }

                this.generateBox(level, boundingBox, 25, 16, 25, 32, 16, 32, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 25, 17, 25, 25, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 32, 17, 25, 32, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 25, 17, 32, 25, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 32, 17, 32, 32, 19, 32, BASE_LIGHT, BASE_LIGHT, false);

                this.placeBlock(level, BASE_LIGHT, 26, 20, 26, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 27, 21, 27, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 27, 20, 27, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 26, 20, 31, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 27, 21, 30, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 27, 20, 30, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 31, 20, 31, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 30, 21, 30, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 30, 20, 30, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 31, 20, 26, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 30, 21, 27, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 30, 20, 27, boundingBox);

                this.generateBox(level, boundingBox, 28, 21, 27, 29, 21, 27, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 27, 21, 28, 27, 21, 29, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 28, 21, 30, 29, 21, 30, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 30, 21, 28, 30, 21, 29, BASE_GRAY, BASE_GRAY, false);
            }
        }

        private void generateLowerWall(ChunkManager level, NukkitRandom random, BoundingBox boundingBox) {
            if (this.chunkIntersects(boundingBox, 0, 21, 6, 58)) {
                this.generateBox(level, boundingBox, 0, 0, 21, 6, 0, 57, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 0, 1, 21, 6, 7, 57);
                this.generateBox(level, boundingBox, 4, 4, 21, 6, 4, 53, BASE_GRAY, BASE_GRAY, false);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, i, i + 1, 21, i, i + 1, 57 - i, BASE_LIGHT, BASE_LIGHT, false);
                }

                for (int z = 23; z < 53; z += 3) {
                    this.placeBlock(level, DOT_DECO_DATA, 5, 5, z, boundingBox);
                }

                this.placeBlock(level, DOT_DECO_DATA, 5, 5, 52, boundingBox);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, i, i + 1, 21, i, i + 1, 57 - i, BASE_LIGHT, BASE_LIGHT, false);
                }

                this.generateBox(level, boundingBox, 4, 1, 52, 6, 3, 52, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 5, 1, 51, 5, 3, 53, BASE_GRAY, BASE_GRAY, false);
            }

            if (this.chunkIntersects(boundingBox, 51, 21, 58, 58)) {
                this.generateBox(level, boundingBox, 51, 0, 21, 57, 0, 57, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 51, 1, 21, 57, 7, 57);
                this.generateBox(level, boundingBox, 51, 4, 21, 53, 4, 53, BASE_GRAY, BASE_GRAY, false);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, BASE_LIGHT, BASE_LIGHT, false);
                }

                for (int z = 23; z < 53; z += 3) {
                    this.placeBlock(level, DOT_DECO_DATA, 52, 5, z, boundingBox);
                }

                this.placeBlock(level, DOT_DECO_DATA, 52, 5, 52, boundingBox);
                this.generateBox(level, boundingBox, 51, 1, 52, 53, 3, 52, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 52, 1, 51, 52, 3, 53, BASE_GRAY, BASE_GRAY, false);
            }

            if (this.chunkIntersects(boundingBox, 0, 51, 57, 57)) {
                this.generateBox(level, boundingBox, 7, 0, 51, 50, 0, 57, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 7, 1, 51, 50, 10, 57);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, BASE_LIGHT, BASE_LIGHT, false);
                }
            }
        }

        private void generateMiddleWall(ChunkManager level, NukkitRandom random, BoundingBox boundingBox) {
            if (this.chunkIntersects(boundingBox, 7, 21, 13, 50)) {
                this.generateBox(level, boundingBox, 7, 0, 21, 13, 0, 50, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 7, 1, 21, 13, 10, 50);
                this.generateBox(level, boundingBox, 11, 8, 21, 13, 8, 53, BASE_GRAY, BASE_GRAY, false);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, i + 7, i + 5, 21, i + 7, i + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
                }

                for (int z = 21; z <= 45; z += 3) {
                    this.placeBlock(level, DOT_DECO_DATA, 12, 9, z, boundingBox);
                }
            }

            if (this.chunkIntersects(boundingBox, 44, 21, 50, 54)) {
                this.generateBox(level, boundingBox, 44, 0, 21, 50, 0, 50, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 44, 1, 21, 50, 10, 50);
                this.generateBox(level, boundingBox, 44, 8, 21, 46, 8, 53, BASE_GRAY, BASE_GRAY, false);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, 50 - i, i + 5, 21, 50 - i, i + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
                }

                for (int z = 21; z <= 45; z += 3) {
                    this.placeBlock(level, DOT_DECO_DATA, 45, 9, z, boundingBox);
                }
            }

            if (this.chunkIntersects(boundingBox, 8, 44, 49, 54)) {
                this.generateBox(level, boundingBox, 14, 0, 44, 43, 0, 50, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 14, 1, 44, 43, 10, 50);

                for (int x = 12; x <= 45; x += 3) {
                    this.placeBlock(level, DOT_DECO_DATA, x, 9, 45, boundingBox);
                    this.placeBlock(level, DOT_DECO_DATA, x, 9, 52, boundingBox);
                    if (x == 12 || x == 18 || x == 24 || x == 33 || x == 39 || x == 45) {
                        this.placeBlock(level, DOT_DECO_DATA, x, 9, 47, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 9, 50, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 10, 45, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 10, 46, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 10, 51, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 10, 52, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 11, 47, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 11, 50, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 12, 48, boundingBox);
                        this.placeBlock(level, DOT_DECO_DATA, x, 12, 49, boundingBox);
                    }
                }

                for (int i = 0; i < 3; ++i) {
                    this.generateBox(level, boundingBox, 8 + i, 5 + i, 54, 49 - i, 5 + i, 54, BASE_GRAY, BASE_GRAY, false);
                }

                this.generateBox(level, boundingBox, 11, 8, 54, 46, 8, 54, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 14, 8, 44, 43, 8, 53, BASE_GRAY, BASE_GRAY, false);
            }
        }

        private void generateUpperWall(ChunkManager level, NukkitRandom random, BoundingBox boundingBox) {
            if (this.chunkIntersects(boundingBox, 14, 21, 20, 43)) {
                this.generateBox(level, boundingBox, 14, 0, 21, 20, 0, 43, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 14, 1, 22, 20, 14, 43);
                this.generateBox(level, boundingBox, 18, 12, 22, 20, 12, 39, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 18, 12, 21, 20, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, BASE_LIGHT, BASE_LIGHT, false);
                }

                for (int z = 23; z <= 39; z += 3) {
                    this.placeBlock(level, DOT_DECO_DATA, 19, 13, z, boundingBox);
                }
            }

            if (this.chunkIntersects(boundingBox, 37, 21, 43, 43)) {
                this.generateBox(level, boundingBox, 37, 0, 21, 43, 0, 43, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 37, 1, 22, 43, 14, 43);
                this.generateBox(level, boundingBox, 37, 12, 22, 39, 12, 39, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 37, 12, 21, 39, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, BASE_LIGHT, BASE_LIGHT, false);
                }

                for (int z = 23; z <= 39; z += 3) {
                    this.placeBlock(level, DOT_DECO_DATA, 38, 13, z, boundingBox);
                }
            }

            if (this.chunkIntersects(boundingBox, 15, 37, 42, 43)) {
                this.generateBox(level, boundingBox, 21, 0, 37, 36, 0, 43, BASE_GRAY, BASE_GRAY, false);
                this.generateWaterBox(level, boundingBox, 21, 1, 37, 36, 14, 43);
                this.generateBox(level, boundingBox, 21, 12, 37, 36, 12, 39, BASE_GRAY, BASE_GRAY, false);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, BASE_LIGHT, BASE_LIGHT, false);
                }

                for (int x = 21; x <= 36; x += 3) {
                    this.placeBlock(level, DOT_DECO_DATA, x, 13, 38, boundingBox);
                }
            }
        }
    }

    public static class OceanMonumentEntryRoom extends OceanMonumentPiece {

        public OceanMonumentEntryRoom(BlockFace orientation, RoomDefinition roomDefinition) {
            super(1, orientation, roomDefinition, 1, 1, 1);
        }

        public OceanMonumentEntryRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentEntryRoom::getType(void) // 1330464082i64
        public String getType() {
            return "OMEntry";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 3, 0, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 1, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 2, 0, 7, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 0, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 0, 2, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 1, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);

            if (this.roomDefinition.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 7, 4, 2, 7);
            }
            if (this.roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 1, 3, 1, 2, 4);
            }
            if (this.roomDefinition.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 6, 1, 3, 7, 2, 4);
            }

            return true;
        }
    }

    public static class OceanMonumentSimpleRoom extends OceanMonumentPiece {

        private int mainDesign;

        public OceanMonumentSimpleRoom(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random) {
            super(1, orientation, roomDefinition, 1, 1, 1);
            this.mainDesign = random.nextBoundedInt(3);
        }

        public OceanMonumentSimpleRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentSimpleRoom::getType(void) // 1330467666i64
        public String getType() {
            return "OMSimple";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(level, boundingBox, 0, 0, this.roomDefinition.hasOpening[BlockFace.DOWN.getIndex()]);
            }

            if (this.roomDefinition.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 4, 1, 6, 4, 6, BASE_GRAY);
            }

            boolean mainDesign = this.mainDesign != 0 && random.nextBoolean() && !this.roomDefinition.hasOpening[BlockFace.DOWN.getIndex()] && !this.roomDefinition.hasOpening[BlockFace.UP.getIndex()] && this.roomDefinition.countOpenings() > 1;

            if (this.mainDesign == 0) {
                this.generateBox(level, boundingBox, 0, 1, 0, 2, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 3, 0, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 2, 0, 0, 2, 2, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 1, 2, 0, 2, 2, 0, BASE_GRAY, BASE_GRAY, false);
                this.placeBlock(level, LAMP_BLOCK, 1, 2, 1, boundingBox);
                this.generateBox(level, boundingBox, 5, 1, 0, 7, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 5, 3, 0, 7, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 7, 2, 0, 7, 2, 2, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 5, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
                this.placeBlock(level, LAMP_BLOCK, 6, 2, 1, boundingBox);
                this.generateBox(level, boundingBox, 0, 1, 5, 2, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 3, 5, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 2, 5, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 1, 2, 7, 2, 2, 7, BASE_GRAY, BASE_GRAY, false);
                this.placeBlock(level, LAMP_BLOCK, 1, 2, 6, boundingBox);
                this.generateBox(level, boundingBox, 5, 1, 5, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 5, 3, 5, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 7, 2, 5, 7, 2, 7, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 5, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
                this.placeBlock(level, LAMP_BLOCK, 6, 2, 6, boundingBox);

                if (this.roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                    this.generateBox(level, boundingBox, 3, 3, 0, 4, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
                } else {
                    this.generateBox(level, boundingBox, 3, 3, 0, 4, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 3, 2, 0, 4, 2, 0, BASE_GRAY, BASE_GRAY, false);
                    this.generateBox(level, boundingBox, 3, 1, 0, 4, 1, 1, BASE_LIGHT, BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[BlockFace.NORTH.getIndex()]) {
                    this.generateBox(level, boundingBox, 3, 3, 7, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                } else {
                    this.generateBox(level, boundingBox, 3, 3, 6, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 3, 2, 7, 4, 2, 7, BASE_GRAY, BASE_GRAY, false);
                    this.generateBox(level, boundingBox, 3, 1, 6, 4, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                    this.generateBox(level, boundingBox, 0, 3, 3, 0, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
                } else {
                    this.generateBox(level, boundingBox, 0, 3, 3, 1, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 0, 2, 3, 0, 2, 4, BASE_GRAY, BASE_GRAY, false);
                    this.generateBox(level, boundingBox, 0, 1, 3, 1, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[BlockFace.EAST.getIndex()]) {
                    this.generateBox(level, boundingBox, 7, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
                } else {
                    this.generateBox(level, boundingBox, 6, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 7, 2, 3, 7, 2, 4, BASE_GRAY, BASE_GRAY, false);
                    this.generateBox(level, boundingBox, 6, 1, 3, 7, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
                }
            } else if (this.mainDesign == 1) {
                this.generateBox(level, boundingBox, 2, 1, 2, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 2, 1, 5, 2, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 5, 1, 5, 5, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 5, 1, 2, 5, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
                this.placeBlock(level, LAMP_BLOCK, 2, 2, 2, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 2, 2, 5, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 5, 2, 5, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 5, 2, 2, boundingBox);
                this.generateBox(level, boundingBox, 0, 1, 0, 1, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 1, 1, 0, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 1, 7, 1, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 1, 6, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 6, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 7, 1, 6, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 6, 1, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 7, 1, 1, 7, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
                this.placeBlock(level, BASE_GRAY, 1, 2, 0, boundingBox);
                this.placeBlock(level, BASE_GRAY, 0, 2, 1, boundingBox);
                this.placeBlock(level, BASE_GRAY, 1, 2, 7, boundingBox);
                this.placeBlock(level, BASE_GRAY, 0, 2, 6, boundingBox);
                this.placeBlock(level, BASE_GRAY, 6, 2, 7, boundingBox);
                this.placeBlock(level, BASE_GRAY, 7, 2, 6, boundingBox);
                this.placeBlock(level, BASE_GRAY, 6, 2, 0, boundingBox);
                this.placeBlock(level, BASE_GRAY, 7, 2, 1, boundingBox);

                if (!this.roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                    this.generateBox(level, boundingBox, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 1, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
                    this.generateBox(level, boundingBox, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
                }
                if (!this.roomDefinition.hasOpening[BlockFace.NORTH.getIndex()]) {
                    this.generateBox(level, boundingBox, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 1, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
                    this.generateBox(level, boundingBox, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                }
                if (!this.roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                    this.generateBox(level, boundingBox, 0, 3, 1, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 0, 2, 1, 0, 2, 6, BASE_GRAY, BASE_GRAY, false);
                    this.generateBox(level, boundingBox, 0, 1, 1, 0, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
                }
                if (!this.roomDefinition.hasOpening[BlockFace.EAST.getIndex()]) {
                    this.generateBox(level, boundingBox, 7, 3, 1, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 7, 2, 1, 7, 2, 6, BASE_GRAY, BASE_GRAY, false);
                    this.generateBox(level, boundingBox, 7, 1, 1, 7, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
                }
            } else if (this.mainDesign == 2) {
                this.generateBox(level, boundingBox, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);

                if (this.roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                    this.generateWaterBox(level, boundingBox, 3, 1, 0, 4, 2, 0);
                }
                if (this.roomDefinition.hasOpening[BlockFace.NORTH.getIndex()]) {
                    this.generateWaterBox(level, boundingBox, 3, 1, 7, 4, 2, 7);
                }
                if (this.roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                    this.generateWaterBox(level, boundingBox, 0, 1, 3, 0, 2, 4);
                }
                if (this.roomDefinition.hasOpening[BlockFace.EAST.getIndex()]) {
                    this.generateWaterBox(level, boundingBox, 7, 1, 3, 7, 2, 4);
                }
            }

            if (mainDesign) {
                this.generateBox(level, boundingBox, 3, 1, 3, 4, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 3, 2, 3, 4, 2, 4, BASE_GRAY, BASE_GRAY, false);
                this.generateBox(level, boundingBox, 3, 3, 3, 4, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            }

            return true;
        }
    }

    public static class OceanMonumentSimpleTopRoom extends OceanMonumentPiece {

        public OceanMonumentSimpleTopRoom(BlockFace orientation, RoomDefinition roomDefinition) {
            super(1, orientation, roomDefinition, 1, 1, 1);
        }

        public OceanMonumentSimpleTopRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentSimpleTopRoom::getType(void) // 1330467922i64
        public String getType() {
            return "OMSimpleT";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(level, boundingBox, 0, 0, this.roomDefinition.hasOpening[BlockFace.DOWN.getIndex()]);
            }

            if (this.roomDefinition.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 4, 1, 6, 4, 6, BASE_GRAY);
            }

            for (int x = 1; x <= 6; ++x) {
                for (int z = 1; z <= 6; ++z) {
                    if (random.nextBoundedInt(3) != 0) {
                        this.generateBox(level, boundingBox, x, 2 + ((random.nextBoundedInt(4) != 0) ? 1 : 0), z, x, 3, z, WET_SPONGE, WET_SPONGE, false);
                    }
                }
            }

            this.generateBox(level, boundingBox, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);

            if (this.roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 0, 4, 2, 0);
            }

            return true;
        }
    }

    public static class OceanMonumentDoubleYRoom extends OceanMonumentPiece {

        public OceanMonumentDoubleYRoom(BlockFace orientation, RoomDefinition roomDefinition) {
            super(1, orientation, roomDefinition, 1, 2, 1);
        }

        public OceanMonumentDoubleYRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentDoubleYRoom::getType(void) // 1330459225i64
        public String getType() {
            return "OMDYR";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(level, boundingBox, 0, 0, this.roomDefinition.hasOpening[BlockFace.DOWN.getIndex()]);
            }

            RoomDefinition roomU = this.roomDefinition.connections[BlockFace.UP.getIndex()];
            if (roomU.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 8, 1, 6, 8, 6, BASE_GRAY);
            }

            this.generateBox(level, boundingBox, 0, 4, 0, 0, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 7, 4, 0, 7, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 4, 0, 6, 4, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 4, 7, 6, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 2, 4, 1, 2, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 4, 2, 1, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 4, 1, 5, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 4, 2, 6, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 2, 4, 5, 2, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 4, 5, 1, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 4, 5, 5, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 4, 5, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);

            RoomDefinition roomDefinition = this.roomDefinition;
            for (int y = 1; y <= 5; y += 4) {
                int z = 0;
                if (roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                    this.generateBox(level, boundingBox, 2, y, z, 2, y + 2, z, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 5, y, z, 5, y + 2, z, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 3, y + 2, z, 4, y + 2, z, BASE_LIGHT, BASE_LIGHT, false);
                } else {
                    this.generateBox(level, boundingBox, 0, y, z, 7, y + 2, z, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 0, y + 1, z, 7, y + 1, z, BASE_GRAY, BASE_GRAY, false);
                }

                z = 7;
                if (roomDefinition.hasOpening[BlockFace.NORTH.getIndex()]) {
                    this.generateBox(level, boundingBox, 2, y, z, 2, y + 2, z, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 5, y, z, 5, y + 2, z, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 3, y + 2, z, 4, y + 2, z, BASE_LIGHT, BASE_LIGHT, false);
                } else {
                    this.generateBox(level, boundingBox, 0, y, z, 7, y + 2, z, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, 0, y + 1, z, 7, y + 1, z, BASE_GRAY, BASE_GRAY, false);
                }

                int x = 0;
                if (roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                    this.generateBox(level, boundingBox, x, y, 2, x, y + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x, y, 5, x, y + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x, y + 2, 3, x, y + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
                } else {
                    this.generateBox(level, boundingBox, x, y, 0, x, y + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x, y + 1, 0, x, y + 1, 7, BASE_GRAY, BASE_GRAY, false);
                }

                x = 7;
                if (roomDefinition.hasOpening[BlockFace.EAST.getIndex()]) {
                    this.generateBox(level, boundingBox, x, y, 2, x, y + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x, y, 5, x, y + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x, y + 2, 3, x, y + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
                } else {
                    this.generateBox(level, boundingBox, x, y, 0, x, y + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x, y + 1, 0, x, y + 1, 7, BASE_GRAY, BASE_GRAY, false);
                }

                roomDefinition = roomU;
            }

            return true;
        }
    }

    public static class OceanMonumentDoubleXRoom extends OceanMonumentPiece {

        public OceanMonumentDoubleXRoom(BlockFace orientation, RoomDefinition roomDefinition) {
            super(1, orientation, roomDefinition, 2, 1, 1);
        }

        public OceanMonumentDoubleXRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentDoubleXRoom::getType(void) // 1330459224i64
        public String getType() {
            return "OMDXR";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            RoomDefinition roomE = this.roomDefinition.connections[BlockFace.EAST.getIndex()];
            RoomDefinition roomDefinition = this.roomDefinition;

            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(level, boundingBox, 8, 0, roomE.hasOpening[BlockFace.DOWN.getIndex()]);
                this.generateDefaultFloor(level, boundingBox, 0, 0, roomDefinition.hasOpening[BlockFace.DOWN.getIndex()]);
            }

            if (roomDefinition.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 4, 1, 7, 4, 6, BASE_GRAY);
            }

            if (roomE.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 8, 4, 1, 14, 4, 6, BASE_GRAY);
            }

            this.generateBox(level, boundingBox, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 15, 3, 0, 15, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 3, 0, 15, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 3, 7, 14, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 15, 2, 0, 15, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 1, 2, 0, 15, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 1, 2, 7, 14, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 15, 1, 0, 15, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 0, 15, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 1, 0, 10, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 2, 0, 9, 2, 3, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 5, 3, 0, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);

            this.placeBlock(level, LAMP_BLOCK, 6, 2, 3, boundingBox);
            this.placeBlock(level, LAMP_BLOCK, 9, 2, 3, boundingBox);

            if (roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            if (roomDefinition.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 7, 4, 2, 7);
            }
            if (roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 1, 3, 0, 2, 4);
            }

            if (roomE.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 11, 1, 0, 12, 2, 0);
            }
            if (roomE.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 11, 1, 7, 12, 2, 7);
            }
            if (roomE.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 15, 1, 3, 15, 2, 4);
            }

            return true;
        }
    }

    public static class OceanMonumentDoubleZRoom extends OceanMonumentPiece {

        public OceanMonumentDoubleZRoom(BlockFace orientation, RoomDefinition roomDefinition) {
            super(1, orientation, roomDefinition, 1, 1, 2);
        }

        public OceanMonumentDoubleZRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentDoubleZRoom::getType(void) // 1330459226i64
        public String getType() {
            return "OMDZR";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            RoomDefinition roomN = this.roomDefinition.connections[BlockFace.NORTH.getIndex()];
            RoomDefinition roomDefinition = this.roomDefinition;

            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(level, boundingBox, 0, 8, roomN.hasOpening[BlockFace.DOWN.getIndex()]);
                this.generateDefaultFloor(level, boundingBox, 0, 0, roomDefinition.hasOpening[BlockFace.DOWN.getIndex()]);
            }

            if (roomDefinition.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 4, 1, 6, 4, 7, BASE_GRAY);
            }

            if (roomN.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 4, 8, 6, 4, 14, BASE_GRAY);
            }

            this.generateBox(level, boundingBox, 0, 3, 0, 0, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 7, 3, 0, 7, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 3, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 3, 15, 6, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 2, 15, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 7, 2, 0, 7, 2, 15, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 1, 2, 0, 7, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 1, 2, 15, 6, 2, 15, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 0, 1, 0, 0, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 7, 1, 0, 7, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 0, 7, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 15, 6, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 1, 1, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 1, 1, 6, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 3, 1, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 3, 1, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 1, 13, 1, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 1, 13, 6, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 3, 13, 1, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 3, 13, 6, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 2, 1, 6, 2, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 1, 6, 5, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 2, 1, 9, 2, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 1, 9, 5, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 3, 2, 6, 4, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 3, 2, 9, 4, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 2, 2, 7, 2, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 2, 7, 5, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(level, LAMP_BLOCK, 2, 2, 5, boundingBox);
            this.placeBlock(level, LAMP_BLOCK, 5, 2, 5, boundingBox);
            this.placeBlock(level, LAMP_BLOCK, 2, 2, 10, boundingBox);
            this.placeBlock(level, LAMP_BLOCK, 5, 2, 10, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 2, 3, 5, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 5, 3, 5, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 2, 3, 10, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 5, 3, 10, boundingBox);

            if (roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            if (roomDefinition.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 7, 1, 3, 7, 2, 4);
            }
            if (roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 1, 3, 0, 2, 4);
            }

            if (roomN.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 15, 4, 2, 15);
            }
            if (roomN.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 1, 11, 0, 2, 12);
            }
            if (roomN.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 7, 1, 11, 7, 2, 12);
            }

            return true;
        }
    }

    public static class OceanMonumentDoubleXYRoom extends OceanMonumentPiece {

        public OceanMonumentDoubleXYRoom(BlockFace orientation, RoomDefinition roomDefinition) {
            super(1, orientation, roomDefinition, 2, 2, 1);
        }

        public OceanMonumentDoubleXYRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentDoubleXYRoom::getType(void) // 1330468953i64
        public String getType() {
            return "OMDXYR";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            RoomDefinition roomE = this.roomDefinition.connections[BlockFace.EAST.getIndex()];
            RoomDefinition roomDefinition = this.roomDefinition;
            RoomDefinition roomU = roomDefinition.connections[BlockFace.UP.getIndex()];
            RoomDefinition roomEU = roomE.connections[BlockFace.UP.getIndex()];

            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(level, boundingBox, 8, 0, roomE.hasOpening[BlockFace.DOWN.getIndex()]);
                this.generateDefaultFloor(level, boundingBox, 0, 0, roomDefinition.hasOpening[BlockFace.DOWN.getIndex()]);
            }

            if (roomU.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 8, 1, 7, 8, 6, BASE_GRAY);
            }

            if (roomEU.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 8, 8, 1, 14, 8, 6, BASE_GRAY);
            }

            for (int y = 1; y <= 7; ++y) {
                BlockState block = BASE_LIGHT;
                if (y == 2 || y == 6) {
                    block = BASE_GRAY;
                }

                this.generateBox(level, boundingBox, 0, y, 0, 0, y, 7, block, block, false);
                this.generateBox(level, boundingBox, 15, y, 0, 15, y, 7, block, block, false);
                this.generateBox(level, boundingBox, 1, y, 0, 15, y, 0, block, block, false);
                this.generateBox(level, boundingBox, 1, y, 7, 14, y, 7, block, block, false);
            }

            this.generateBox(level, boundingBox, 2, 1, 3, 2, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 3, 1, 2, 4, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 3, 1, 5, 4, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 13, 1, 3, 13, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 11, 1, 2, 12, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 11, 1, 5, 12, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 1, 3, 5, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 1, 3, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 7, 2, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 5, 2, 5, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 5, 2, 10, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 5, 5, 5, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 5, 5, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(level, BASE_LIGHT, 6, 6, 2, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 9, 6, 2, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 6, 6, 5, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 9, 6, 5, boundingBox);
            this.generateBox(level, boundingBox, 5, 4, 3, 6, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 9, 4, 3, 10, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(level, LAMP_BLOCK, 5, 4, 2, boundingBox);
            this.placeBlock(level, LAMP_BLOCK, 5, 4, 5, boundingBox);
            this.placeBlock(level, LAMP_BLOCK, 10, 4, 2, boundingBox);
            this.placeBlock(level, LAMP_BLOCK, 10, 4, 5, boundingBox);

            if (roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            if (roomDefinition.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 7, 4, 2, 7);
            }
            if (roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 1, 3, 0, 2, 4);
            }

            if (roomE.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 11, 1, 0, 12, 2, 0);
            }
            if (roomE.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 11, 1, 7, 12, 2, 7);
            }
            if (roomE.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 15, 1, 3, 15, 2, 4);
            }

            if (roomU.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 5, 0, 4, 6, 0);
            }
            if (roomU.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 5, 7, 4, 6, 7);
            }
            if (roomU.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 5, 3, 0, 6, 4);
            }

            if (roomEU.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 11, 5, 0, 12, 6, 0);
            }
            if (roomEU.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 11, 5, 7, 12, 6, 7);
            }
            if (roomEU.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 15, 5, 3, 15, 6, 4);
            }

            return true;
        }
    }

    public static class OceanMonumentDoubleYZRoom extends OceanMonumentPiece {

        public OceanMonumentDoubleYZRoom(BlockFace orientation, RoomDefinition roomDefinition) {
            super(1, orientation, roomDefinition, 1, 2, 2);
        }

        public OceanMonumentDoubleYZRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentDoubleYZRoom::getType(void) // 1330469210i64
        public String getType() {
            return "OMDYZR";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            RoomDefinition roomN = this.roomDefinition.connections[BlockFace.NORTH.getIndex()];
            RoomDefinition roomDefinition = this.roomDefinition;
            RoomDefinition roomNU = roomN.connections[BlockFace.UP.getIndex()];
            RoomDefinition roomU = roomDefinition.connections[BlockFace.UP.getIndex()];

            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(level, boundingBox, 0, 8, roomN.hasOpening[BlockFace.DOWN.getIndex()]);
                this.generateDefaultFloor(level, boundingBox, 0, 0, roomDefinition.hasOpening[BlockFace.DOWN.getIndex()]);
            }

            if (roomU.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 8, 1, 6, 8, 7, BASE_GRAY);
            }

            if (roomNU.connections[BlockFace.UP.getIndex()] == null) {
                this.generateBoxOnFillOnly(level, boundingBox, 1, 8, 8, 6, 8, 14, BASE_GRAY);
            }

            for (int y = 1; y <= 7; ++y) {
                BlockState block = BASE_LIGHT;
                if (y == 2 || y == 6) {
                    block = BASE_GRAY;
                }

                this.generateBox(level, boundingBox, 0, y, 0, 0, y, 15, block, block, false);
                this.generateBox(level, boundingBox, 7, y, 0, 7, y, 15, block, block, false);
                this.generateBox(level, boundingBox, 1, y, 0, 6, y, 0, block, block, false);
                this.generateBox(level, boundingBox, 1, y, 15, 6, y, 15, block, block, false);
            }

            for (int y = 1; y <= 7; ++y) {
                BlockState block = BASE_BLACK;
                if (y == 2 || y == 6) {
                    block = LAMP_BLOCK;
                }

                this.generateBox(level, boundingBox, 3, y, 7, 4, y, 8, block, block, false);
            }

            if (roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 0, 4, 2, 0);
            }
            if (roomDefinition.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 7, 1, 3, 7, 2, 4);
            }
            if (roomDefinition.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 1, 3, 0, 2, 4);
            }

            if (roomN.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 1, 15, 4, 2, 15);
            }
            if (roomN.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 1, 11, 0, 2, 12);
            }
            if (roomN.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 7, 1, 11, 7, 2, 12);
            }

            if (roomU.hasOpening[BlockFace.SOUTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 5, 0, 4, 6, 0);
            }
            if (roomU.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 7, 5, 3, 7, 6, 4);
                this.generateBox(level, boundingBox, 5, 4, 2, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 6, 1, 2, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 6, 1, 5, 6, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            }
            if (roomU.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 5, 3, 0, 6, 4);
                this.generateBox(level, boundingBox, 1, 4, 2, 2, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 1, 1, 2, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 1, 1, 5, 1, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (roomNU.hasOpening[BlockFace.NORTH.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 3, 5, 15, 4, 6, 15);
            }
            if (roomNU.hasOpening[BlockFace.WEST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 0, 5, 11, 0, 6, 12);
                this.generateBox(level, boundingBox, 1, 4, 10, 2, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 1, 1, 10, 1, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 1, 1, 13, 1, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
            }
            if (roomNU.hasOpening[BlockFace.EAST.getIndex()]) {
                this.generateWaterBox(level, boundingBox, 7, 5, 11, 7, 6, 12);
                this.generateBox(level, boundingBox, 5, 4, 10, 6, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 6, 1, 10, 6, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 6, 1, 13, 6, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
            }

            return true;
        }
    }

    public static class OceanMonumentCoreRoom extends OceanMonumentPiece {

        public OceanMonumentCoreRoom(BlockFace orientation, RoomDefinition roomDefinition) {
            super(1, orientation, roomDefinition, 2, 2, 2);
        }

        public OceanMonumentCoreRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentCoreRoom::getType(void) // 1330463570i64
        public String getType() {
            return "OMCR";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBoxOnFillOnly(level, boundingBox, 1, 8, 0, 14, 8, 14, BASE_GRAY);
            this.generateBox(level, boundingBox, 0, 7, 0, 0, 7, 15, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 15, 7, 0, 15, 7, 15, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 7, 0, 15, 7, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 7, 15, 14, 7, 15, BASE_LIGHT, BASE_LIGHT, false);

            for (int y = 1; y <= 6; ++y) {
                BlockState block = BASE_LIGHT;
                if (y == 2 || y == 6) {
                    block = BASE_GRAY;
                }

                for (int x = 0; x <= 15; x += 15) {
                    this.generateBox(level, boundingBox, x, y, 0, x, y, 1, block, block, false);
                    this.generateBox(level, boundingBox, x, y, 6, x, y, 9, block, block, false);
                    this.generateBox(level, boundingBox, x, y, 14, x, y, 15, block, block, false);
                }

                this.generateBox(level, boundingBox, 1, y, 0, 1, y, 0, block, block, false);
                this.generateBox(level, boundingBox, 6, y, 0, 9, y, 0, block, block, false);
                this.generateBox(level, boundingBox, 14, y, 0, 14, y, 0, block, block, false);
                this.generateBox(level, boundingBox, 1, y, 15, 14, y, 15, block, block, false);
            }

            this.generateBox(level, boundingBox, 6, 3, 6, 9, 6, 9, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 7, 4, 7, 8, 5, 8, GOLD_BLOCK, GOLD_BLOCK, false);

            for (int y = 3; y <= 6; y += 3) {
                for (int x = 6; x <= 9; x += 3) {
                    this.placeBlock(level, LAMP_BLOCK, x, y, 6, boundingBox);
                    this.placeBlock(level, LAMP_BLOCK, x, y, 9, boundingBox);
                }
            }

            this.generateBox(level, boundingBox, 5, 1, 6, 5, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 1, 9, 5, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 1, 6, 10, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 1, 9, 10, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 1, 5, 6, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 9, 1, 5, 9, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, 1, 10, 6, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 9, 1, 10, 9, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 2, 5, 5, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 2, 10, 5, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 2, 5, 10, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 2, 10, 10, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 7, 1, 5, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 7, 1, 10, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 5, 7, 9, 5, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 10, 7, 9, 10, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 7, 5, 6, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 7, 10, 6, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 9, 7, 5, 14, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 9, 7, 10, 14, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 2, 1, 2, 2, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 3, 1, 2, 3, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 13, 1, 2, 13, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 12, 1, 2, 12, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 2, 1, 12, 2, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 3, 1, 13, 3, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 13, 1, 12, 13, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 12, 1, 13, 12, 1, 13, BASE_LIGHT, BASE_LIGHT, false);

            return true;
        }
    }

    public static class OceanMonumentWingRoom extends OceanMonumentPiece {

        private int mainDesign;

        public OceanMonumentWingRoom(BlockFace orientation, BoundingBox roomDefinition, int mainDesign) {
            super(orientation, roomDefinition);
            this.mainDesign = (mainDesign & 0x1);
        }

        public OceanMonumentWingRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentWingRoom::getType(void) // 1330468690i64
        public String getType() {
            return "OMWR";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.mainDesign == 0) {
                for (int i = 0; i < 4; ++i) {
                    this.generateBox(level, boundingBox, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, BASE_LIGHT, BASE_LIGHT, false);
                }

                this.generateBox(level, boundingBox, 7, 0, 6, 15, 0, 16, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 6, 0, 6, 6, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 16, 0, 6, 16, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 7, 1, 7, 7, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 15, 1, 7, 15, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 7, 1, 6, 9, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 13, 1, 6, 15, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 8, 1, 7, 9, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 13, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 9, 0, 5, 13, 0, 5, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 10, 0, 7, 12, 0, 7, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 8, 0, 10, 8, 0, 12, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 14, 0, 10, 14, 0, 12, BASE_BLACK, BASE_BLACK, false);

                for (int z = 18; z >= 7; z -= 3) {
                    this.placeBlock(level, LAMP_BLOCK, 6, 3, z, boundingBox);
                    this.placeBlock(level, LAMP_BLOCK, 16, 3, z, boundingBox);
                }

                this.placeBlock(level, LAMP_BLOCK, 10, 0, 10, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 12, 0, 10, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 10, 0, 12, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 12, 0, 12, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 8, 3, 6, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 14, 3, 6, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 4, 2, 4, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 4, 1, 4, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 4, 0, 4, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 18, 2, 4, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 18, 1, 4, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 18, 0, 4, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 4, 2, 18, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 4, 1, 18, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 4, 0, 18, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 18, 2, 18, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 18, 1, 18, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 18, 0, 18, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 9, 7, 20, boundingBox);
                this.placeBlock(level, BASE_LIGHT, 13, 7, 20, boundingBox);
                this.generateBox(level, boundingBox, 6, 0, 21, 7, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 15, 0, 21, 16, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
                this.spawnElder(level, boundingBox, 11, 2, 16);
            } else if (this.mainDesign == 1) {
                this.generateBox(level, boundingBox, 9, 3, 18, 13, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 9, 0, 18, 9, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
                this.generateBox(level, boundingBox, 13, 0, 18, 13, 2, 18, BASE_LIGHT, BASE_LIGHT, false);

                int x = 9;
                for (int n = 0; n < 2; ++n) {
                    this.placeBlock(level, BASE_LIGHT, x, 6, 20, boundingBox);
                    this.placeBlock(level, LAMP_BLOCK, x, 5, 20, boundingBox);
                    this.placeBlock(level, BASE_LIGHT, x, 4, 20, boundingBox);
                    x = 13;
                }

                this.generateBox(level, boundingBox, 7, 3, 7, 15, 3, 14, BASE_LIGHT, BASE_LIGHT, false);

                x = 10;
                for (int o = 0; o < 2; ++o) {
                    this.generateBox(level, boundingBox, x, 0, 10, x, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x, 0, 12, x, 6, 12, BASE_LIGHT, BASE_LIGHT, false);
                    this.placeBlock(level, LAMP_BLOCK, x, 0, 10, boundingBox);
                    this.placeBlock(level, LAMP_BLOCK, x, 0, 12, boundingBox);
                    this.placeBlock(level, LAMP_BLOCK, x, 4, 10, boundingBox);
                    this.placeBlock(level, LAMP_BLOCK, x, 4, 12, boundingBox);
                    x = 12;
                }

                x = 8;
                for (int p = 0; p < 2; ++p) {
                    this.generateBox(level, boundingBox, x, 0, 7, x, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
                    this.generateBox(level, boundingBox, x, 0, 14, x, 2, 14, BASE_LIGHT, BASE_LIGHT, false);
                    x = 14;
                }

                this.generateBox(level, boundingBox, 8, 3, 8, 8, 3, 13, BASE_BLACK, BASE_BLACK, false);
                this.generateBox(level, boundingBox, 14, 3, 8, 14, 3, 13, BASE_BLACK, BASE_BLACK, false);
                this.spawnElder(level, boundingBox, 11, 5, 13);
            }

            return true;
        }
    }

    public static class OceanMonumentPenthouse extends OceanMonumentPiece {

        public OceanMonumentPenthouse(BlockFace orientation, BoundingBox boundingBox) {
            super(orientation, boundingBox);
        }

        public OceanMonumentPenthouse(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ OceanMonumentPenthouse::getType(void) // 1330466888i64
        public String getType() {
            return "OMPenthouse";
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 2, -1, 2, 11, -1, 11, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 0, -1, 0, 1, -1, 11, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 12, -1, 0, 13, -1, 11, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 2, -1, 0, 11, -1, 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 2, -1, 12, 11, -1, 13, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 0, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 13, 0, 0, 13, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 0, 0, 12, 0, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 1, 0, 13, 12, 0, 13, BASE_LIGHT, BASE_LIGHT, false);

            for (int i = 2; i <= 11; i += 3) {
                this.placeBlock(level, LAMP_BLOCK, 0, 0, i, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, 13, 0, i, boundingBox);
                this.placeBlock(level, LAMP_BLOCK, i, 0, 0, boundingBox);
            }

            this.generateBox(level, boundingBox, 2, 0, 3, 4, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 9, 0, 3, 11, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 4, 0, 9, 9, 0, 11, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(level, BASE_LIGHT, 5, 0, 8, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 8, 0, 8, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 10, 0, 10, boundingBox);
            this.placeBlock(level, BASE_LIGHT, 3, 0, 10, boundingBox);
            this.generateBox(level, boundingBox, 3, 0, 3, 3, 0, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 10, 0, 3, 10, 0, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(level, boundingBox, 6, 0, 10, 7, 0, 10, BASE_BLACK, BASE_BLACK, false);

            int x = 3;
            for (int i = 0; i < 2; ++i) {
                for (int z = 2; z <= 8; z += 3) {
                    this.generateBox(level, boundingBox, x, 0, z, x, 2, z, BASE_LIGHT, BASE_LIGHT, false);
                }
                x = 10;
            }

            this.generateBox(level, boundingBox, 5, 0, 10, 5, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 8, 0, 10, 8, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(level, boundingBox, 6, -1, 7, 7, -1, 8, BASE_BLACK, BASE_BLACK, false);
            this.generateWaterBox(level, boundingBox, 6, -1, 3, 7, -1, 4);
            this.spawnElder(level, boundingBox, 6, 1, 6);

            return true;
        }
    }

    static class RoomDefinition {

        private final int index;
        private final RoomDefinition[] connections;
        private final boolean[] hasOpening;
        private boolean claimed;
        private boolean isSource;
        private int scanIndex;

        public RoomDefinition(int index) {
            this.connections = new RoomDefinition[6];
            this.hasOpening = new boolean[6];
            this.index = index;
        }

        public void setConnection(BlockFace orientation, RoomDefinition roomDefinition) {
            this.connections[orientation.getIndex()] = roomDefinition;
            roomDefinition.connections[orientation.getOpposite().getIndex()] = this;
        }

        public void updateOpenings() {
            for (int i = 0; i < 6; ++i) {
                this.hasOpening[i] = (this.connections[i] != null);
            }
        }

        public boolean findSource(int scanIndex) {
            if (this.isSource) {
                return true;
            }

            this.scanIndex = scanIndex;

            for (int i = 0; i < 6; ++i) {
                if (this.connections[i] != null && this.hasOpening[i] && this.connections[i].scanIndex != scanIndex && this.connections[i].findSource(scanIndex)) {
                    return true;
                }
            }

            return false;
        }

        public boolean isSpecial() {
            return this.index >= 75;
        }

        public int countOpenings() {
            int count = 0;

            for (int i = 0; i < 6; ++i) {
                if (this.hasOpening[i]) {
                    ++count;
                }
            }

            return count;
        }
    }

    static class FitSimpleRoom implements MonumentRoomFitter {

        @Override
        public boolean fits(RoomDefinition roomDefinition) {
            return true;
        }

        @Override
        public OceanMonumentPiece create(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random) {
            roomDefinition.claimed = true;
            return new OceanMonumentSimpleRoom(orientation, roomDefinition, random);
        }
    }

    static class FitSimpleTopRoom implements MonumentRoomFitter {

        @Override
        public boolean fits(RoomDefinition roomDefinition) {
            return !roomDefinition.hasOpening[BlockFace.WEST.getIndex()] && !roomDefinition.hasOpening[BlockFace.EAST.getIndex()] && !roomDefinition.hasOpening[BlockFace.NORTH.getIndex()] && !roomDefinition.hasOpening[BlockFace.SOUTH.getIndex()] && !roomDefinition.hasOpening[BlockFace.UP.getIndex()];
        }

        @Override
        public OceanMonumentPiece create(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random) {
            roomDefinition.claimed = true;
            return new OceanMonumentSimpleTopRoom(orientation, roomDefinition);
        }
    }

    static class FitDoubleYRoom implements MonumentRoomFitter {

        @Override
        public boolean fits(RoomDefinition roomDefinition) {
            return roomDefinition.hasOpening[BlockFace.UP.getIndex()] && !roomDefinition.connections[BlockFace.UP.getIndex()].claimed;
        }

        @Override
        public OceanMonumentPiece create(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random) {
            roomDefinition.claimed = true;
            roomDefinition.connections[BlockFace.UP.getIndex()].claimed = true;
            return new OceanMonumentDoubleYRoom(orientation, roomDefinition);
        }
    }

    static class FitDoubleXRoom implements MonumentRoomFitter {

        @Override
        public boolean fits(RoomDefinition roomDefinition) {
            return roomDefinition.hasOpening[BlockFace.EAST.getIndex()] && !roomDefinition.connections[BlockFace.EAST.getIndex()].claimed;
        }

        @Override
        public OceanMonumentPiece create(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random) {
            roomDefinition.claimed = true;
            roomDefinition.connections[BlockFace.EAST.getIndex()].claimed = true;
            return new OceanMonumentDoubleXRoom(orientation, roomDefinition);
        }
    }

    static class FitDoubleZRoom implements MonumentRoomFitter {

        @Override
        public boolean fits(RoomDefinition roomDefinition) {
            return roomDefinition.hasOpening[BlockFace.NORTH.getIndex()] && !roomDefinition.connections[BlockFace.NORTH.getIndex()].claimed;
        }

        @Override
        public OceanMonumentPiece create(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random) {
            RoomDefinition room = roomDefinition;
            if (!roomDefinition.hasOpening[BlockFace.NORTH.getIndex()] || roomDefinition.connections[BlockFace.NORTH.getIndex()].claimed) {
                room = roomDefinition.connections[BlockFace.SOUTH.getIndex()];
            }
            room.claimed = true;
            room.connections[BlockFace.NORTH.getIndex()].claimed = true;
            return new OceanMonumentDoubleZRoom(orientation, room);
        }
    }

    static class FitDoubleXYRoom implements MonumentRoomFitter {

        @Override
        public boolean fits(RoomDefinition roomDefinition) {
            if (roomDefinition.hasOpening[BlockFace.EAST.getIndex()] && !roomDefinition.connections[BlockFace.EAST.getIndex()].claimed && roomDefinition.hasOpening[BlockFace.UP.getIndex()] && !roomDefinition.connections[BlockFace.UP.getIndex()].claimed) {
                RoomDefinition room = roomDefinition.connections[BlockFace.EAST.getIndex()];
                return room.hasOpening[BlockFace.UP.getIndex()] && !room.connections[BlockFace.UP.getIndex()].claimed;
            }
            return false;
        }

        @Override
        public OceanMonumentPiece create(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random) {
            roomDefinition.claimed = true;
            roomDefinition.connections[BlockFace.EAST.getIndex()].claimed = true;
            roomDefinition.connections[BlockFace.UP.getIndex()].claimed = true;
            roomDefinition.connections[BlockFace.EAST.getIndex()].connections[BlockFace.UP.getIndex()].claimed = true;
            return new OceanMonumentDoubleXYRoom(orientation, roomDefinition);
        }
    }

    static class FitDoubleYZRoom implements MonumentRoomFitter {

        @Override
        public boolean fits(RoomDefinition roomDefinition) {
            if (roomDefinition.hasOpening[BlockFace.NORTH.getIndex()] && !roomDefinition.connections[BlockFace.NORTH.getIndex()].claimed && roomDefinition.hasOpening[BlockFace.UP.getIndex()] && !roomDefinition.connections[BlockFace.UP.getIndex()].claimed) {
                RoomDefinition room = roomDefinition.connections[BlockFace.NORTH.getIndex()];
                return room.hasOpening[BlockFace.UP.getIndex()] && !room.connections[BlockFace.UP.getIndex()].claimed;
            }
            return false;
        }

        @Override
        public OceanMonumentPiece create(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random) {
            roomDefinition.claimed = true;
            roomDefinition.connections[BlockFace.NORTH.getIndex()].claimed = true;
            roomDefinition.connections[BlockFace.UP.getIndex()].claimed = true;
            roomDefinition.connections[BlockFace.NORTH.getIndex()].connections[BlockFace.UP.getIndex()].claimed = true;
            return new OceanMonumentDoubleYZRoom(orientation, roomDefinition);
        }
    }

    interface MonumentRoomFitter {

        boolean fits(RoomDefinition roomDefinition);

        OceanMonumentPiece create(BlockFace orientation, RoomDefinition roomDefinition, NukkitRandom random);
    }
}
