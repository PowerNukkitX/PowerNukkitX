package cn.nukkit.level.generator.populator.impl.structure.stronghold.structure;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.mob.EntitySilverfish;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.stronghold.loot.StrongholdCorridorChest;
import cn.nukkit.level.generator.populator.impl.structure.stronghold.loot.StrongholdCrossingChest;
import cn.nukkit.level.generator.populator.impl.structure.stronghold.loot.StrongholdLibraryChest;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.*;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.BoundingBox;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructurePiece;
import cn.nukkit.level.generator.task.BlockActorSpawnTask;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class StrongholdPieces {

    private static final BlockState INFESTED_STONE_BRICKS = new BlockState(Block.MONSTER_EGG, BlockMonsterEgg.STONE_BRICK);
    private static final BlockState STONE_BRICKS = new BlockState(Block.STONE_BRICKS, BlockBricksStone.NORMAL);
    private static final BlockState MOSSY_STONE_BRICKS = new BlockState(Block.STONE_BRICKS, BlockBricksStone.MOSSY);
    private static final BlockState CRACKED_STONE_BRICKS = new BlockState(Block.STONE_BRICKS, BlockBricksStone.CRACKED);
    private static final BlockState STONE_BRICK_SLAB = new BlockState(Block.STONE_SLAB, BlockSlabStone.STONE_BRICK);
    private static final BlockState SMOOTH_STONE_SLAB = new BlockState(Block.STONE_SLAB, BlockSlabStone.STONE);
    private static final BlockState SMOOTH_STONE_SLAB_DOUBLE = new BlockState(Block.DOUBLE_STONE_SLAB, BlockDoubleSlabStone.STONE);
    private static final BlockState COBBLESTONE = new BlockState(Block.COBBLESTONE);
    private static final BlockState WATER = new BlockState(Block.STILL_WATER);
    private static final BlockState LAVA = new BlockState(Block.STILL_LAVA);
    private static final BlockState OAK_FENCE = new BlockState(Block.FENCE, BlockFence.FENCE_OAK);
    private static final BlockState OAK_PLANKS = new BlockState(Block.PLANKS, BlockPlanks.OAK);
    private static final BlockState OAK_DOOR = new BlockState(Block.WOOD_DOOR_BLOCK);
    private static final BlockState IRON_DOOR = new BlockState(Block.IRON_DOOR_BLOCK);
    private static final BlockState IRON_BARS = new BlockState(Block.IRON_BARS);
    private static final BlockState TORCH = new BlockState(Block.TORCH, TorchFacingDirection.TOP);
    private static final BlockState END_PORTAL = new BlockState(Block.END_PORTAL);
    private static final BlockState BOOKSHELF = new BlockState(Block.BOOKSHELF);
    private static final BlockState COBWEB = new BlockState(Block.COBWEB);
    private static final BlockState SPAWNER = new BlockState(Block.MONSTER_SPAWNER);

    private static final Object lock = new Object();

    private static final PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new PieceWeight[]{
            new PieceWeight(Straight.class, 40, 0),
            new PieceWeight(PrisonHall.class, 5, 5),
            new PieceWeight(LeftTurn.class, 20, 0),
            new PieceWeight(RightTurn.class, 20, 0),
            new PieceWeight(RoomCrossing.class, 10, 6),
            new PieceWeight(StraightStairsDown.class, 5, 5),
            new PieceWeight(StairsDown.class, 5, 5),
            new PieceWeight(FiveCrossing.class, 5, 4),
            new PieceWeight(ChestCorridor.class, 5, 4),
            new PieceWeight(Library.class, 10, 2) {
                @Override
                public boolean doPlace(int genDepth) {
                    return super.doPlace(genDepth) && genDepth > 4;
                }
            },
            new PieceWeight(PortalRoom.class, 20, 1) {
                @Override
                public boolean doPlace(int genDepth) {
                    return super.doPlace(genDepth) && genDepth > 5;
                }
            }
    };

    private static List<PieceWeight> currentPieces;
    private static Class<? extends StructurePiece> imposedPiece;
    private static int totalWeight;

    private static final SmoothStoneSelector SMOOTH_STONE_SELECTOR = new SmoothStoneSelector();

    public static Object getLock() {
        return lock;
    }

    public static void resetPieces() {
        currentPieces = Lists.newArrayList();

        for (PieceWeight weight : STRONGHOLD_PIECE_WEIGHTS) {
            weight.placeCount = 0;
            currentPieces.add(weight);
        }

        imposedPiece = null;
    }

    private static boolean updatePieceWeight() {
        boolean success = false;
        totalWeight = 0;

        PieceWeight weight;
        for (Iterator<PieceWeight> iterator = currentPieces.iterator(); iterator.hasNext(); totalWeight += weight.weight) {
            weight = iterator.next();
            if (weight.maxPlaceCount > 0 && weight.placeCount < weight.maxPlaceCount) {
                success = true;
            }
        }

        return success;
    }

    //\\ StrongholdPiece::findAndCreatePieceFactory(std::basic_string<char,std::char_traits<char>,std::allocator<char>> const &,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static StrongholdPiece findAndCreatePieceFactory(Class<? extends StructurePiece> pieceClass, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, @Nullable BlockFace orientation, int genDepth) {
        if (pieceClass == Straight.class) {
            return Straight.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == PrisonHall.class) {
            return PrisonHall.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == LeftTurn.class) {
            return LeftTurn.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == RightTurn.class) {
            return RightTurn.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == RoomCrossing.class) {
            return RoomCrossing.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == StraightStairsDown.class) {
            return StraightStairsDown.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == StairsDown.class) {
            return StairsDown.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == FiveCrossing.class) {
            return FiveCrossing.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == ChestCorridor.class) {
            return ChestCorridor.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == Library.class) {
            return Library.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == PortalRoom.class) {
            return PortalRoom.createPiece(pieces, x, y, z, orientation, genDepth);
        }
        return null;
    }

    @Nullable //\\ StrongholdPiece::generatePieceFromSmallDoor(SHStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random const &,int,int,int,int,int)
    private static StrongholdPiece generatePieceFromSmallDoor(StartPiece piece, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
        if (updatePieceWeight()) {
            if (imposedPiece != null) {
                StrongholdPiece result = findAndCreatePieceFactory(imposedPiece, pieces, random, x, y, z, orientation, genDepth);
                imposedPiece = null;
                if (result != null) {
                    return result;
                }
            }

            for (int i = 0; i < 5; i++) {
                int target = random.nextBoundedInt(totalWeight);

                for (PieceWeight weight : currentPieces) {
                    target -= weight.weight;

                    if (target < 0) {
                        if (!weight.doPlace(genDepth) || weight == piece.previousPiece) {
                            break;
                        }

                        StrongholdPiece result = findAndCreatePieceFactory(weight.pieceClass, pieces, random, x, y, z, orientation, genDepth);
                        if (result != null) {
                            ++weight.placeCount;
                            piece.previousPiece = weight;
                            if (!weight.isValid()) {
                                currentPieces.remove(weight);
                            }

                            return result;
                        }
                    }
                }
            }

            BoundingBox boundingBox = FillerCorridor.findPieceBox(pieces, random, x, y, z, orientation);
            if (boundingBox != null && boundingBox.y0 > 1) {
                return new FillerCorridor(genDepth, boundingBox, orientation);
            }
        }

        return null;
    }

    @Nullable //\\ StrongholdPiece::generateAndAddPiece(SHStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static StructurePiece generateAndAddPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, @Nullable BlockFace orientation, int genDepth) {
        if (genDepth > 50) {
            return null;
        } else if (Math.abs(x - start.getBoundingBox().x0) <= 112 && Math.abs(z - start.getBoundingBox().z0) <= 112) {
            StructurePiece piece = generatePieceFromSmallDoor(start, pieces, random, x, y, z, orientation, genDepth + 1);
            if (piece != null) {
                pieces.add(piece);
                start.pendingChildren.add(piece);
            }

            return piece;
        } else {
            return null;
        }
    }

    static class PieceWeight {

        public final Class<? extends StructurePiece> pieceClass;
        public final int weight;
        public int placeCount;
        public final int maxPlaceCount;

        public PieceWeight(Class<? extends StructurePiece> pieceClass, int weight, int maxPlaceCount) {
            this.pieceClass = pieceClass;
            this.weight = weight;
            this.maxPlaceCount = maxPlaceCount;
        }

        public boolean doPlace(int genDepth) {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }

        public boolean isValid() {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
    }

    abstract static class StrongholdPiece extends StructurePiece {

        protected SmallDoorType entryDoor;

        protected StrongholdPiece(int genDepth) {
            super(genDepth);
            this.entryDoor = SmallDoorType.OPENING;
        }

        public StrongholdPiece(CompoundTag tag) {
            super(tag);
            this.entryDoor = SmallDoorType.OPENING;
            this.entryDoor = SmallDoorType.valueOf(tag.getString("EntryDoor"));
        }

        @Override //\\ SHStartPiece::getType(void) // 1397248082i64
        public String getType() {
            return "SHStart";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            tag.putString("EntryDoor", this.entryDoor.name());
        }

        //\\ StrongholdPiece::generateSmallDoor(BlockSource *,Random &,BoundingBox const &,StrongholdPiece::SmallDoorType,int,int,int)
        protected void generateSmallDoor(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, SmallDoorType type, int x, int y, int z) {
            switch (type) {
                case OPENING:
                    this.generateBox(level, boundingBox, x, y, z, x + 3 - 1, y + 3 - 1, z, BlockState.AIR, BlockState.AIR, false);
                    break;
                case WOOD_DOOR:
                    this.placeBlock(level, STONE_BRICKS, x, y, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x, y + 1, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x, y + 2, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x + 1, y + 2, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x + 2, y + 2, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x + 2, y + 1, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x + 2, y, z, boundingBox);

                    this.placeBlock(level, OAK_DOOR, x + 1, y, z, boundingBox);
                    this.placeBlock(level, new BlockState(Block.WOOD_DOOR_BLOCK, UpperBlockBit.UPPER), x + 1, y + 1, z, boundingBox);
                    break;
                case GRATES:
                    this.placeBlock(level, BlockState.AIR, x + 1, y, z, boundingBox);
                    this.placeBlock(level, BlockState.AIR, x + 1, y + 1, z, boundingBox);

                    this.placeBlock(level, IRON_BARS, x, y, z, boundingBox);
                    this.placeBlock(level, IRON_BARS, x, y + 1, z, boundingBox);
                    this.placeBlock(level, IRON_BARS, x, y + 2, z, boundingBox);
                    this.placeBlock(level, IRON_BARS, x + 1, y + 2, z, boundingBox);
                    this.placeBlock(level, IRON_BARS, x + 2, y + 2, z, boundingBox);
                    this.placeBlock(level, IRON_BARS, x + 2, y + 1, z, boundingBox);
                    this.placeBlock(level, IRON_BARS, x + 2, y, z, boundingBox);
                    break;
                case IRON_DOOR:
                    this.placeBlock(level, STONE_BRICKS, x, y, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x, y + 1, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x, y + 2, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x + 1, y + 2, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x + 2, y + 2, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x + 2, y + 1, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, x + 2, y, z, boundingBox);

                    this.placeBlock(level, IRON_DOOR, x + 1, y, z, boundingBox);
                    this.placeBlock(level, new BlockState(Block.IRON_DOOR_BLOCK, UpperBlockBit.UPPER), x + 1, y + 1, z, boundingBox);

                    this.placeBlock(level, new BlockState(Block.STONE_BUTTON, FacingDirection.NORTH), x + 2, y + 1, z + 1, boundingBox);
                    this.placeBlock(level, new BlockState(Block.STONE_BUTTON, FacingDirection.SOUTH), x + 2, y + 1, z - 1, boundingBox);
            }
        }

        protected SmallDoorType randomSmallDoor(NukkitRandom random) {
            int i = random.nextBoundedInt(5);
            switch (i) {
                case 0:
                case 1:
                default:
                    return SmallDoorType.OPENING;
                case 2:
                    return SmallDoorType.WOOD_DOOR;
                case 3:
                    return SmallDoorType.GRATES;
                case 4:
                    return SmallDoorType.IRON_DOOR;
            }
        }

        @Nullable //\\ StrongholdPiece::generateSmallDoorChildForward(SHStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int)
        protected StructurePiece generateSmallDoorChildForward(StartPiece piece, List<StructurePiece> pieces, NukkitRandom random, int x, int y) {
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                        return generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + x, this.boundingBox.y0 + y, this.boundingBox.z0 - 1, orientation, this.getGenDepth());
                    case SOUTH:
                        return generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + x, this.boundingBox.y0 + y, this.boundingBox.z1 + 1, orientation, this.getGenDepth());
                    case WEST:
                        return generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + y, this.boundingBox.z0 + x, orientation, this.getGenDepth());
                    case EAST:
                        return generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + y, this.boundingBox.z0 + x, orientation, this.getGenDepth());
                }
            }

            return null;
        }

        @Nullable //\\ StrongholdPiece::generateSmallDoorChildLeft(SHStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int)
        protected StructurePiece generateSmallDoorChildLeft(StartPiece piece, List<StructurePiece> pieces, NukkitRandom random, int y, int z) {
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    case SOUTH:
                        return generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + y, this.boundingBox.z0 + z, BlockFace.WEST, this.getGenDepth());
                    case WEST:
                    case EAST:
                        return generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + z, this.boundingBox.y0 + y, this.boundingBox.z0 - 1, BlockFace.NORTH, this.getGenDepth());
                }
            }

            return null;
        }

        @Nullable //\\ StrongholdPiece::generateSmallDoorChildRight(SHStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int)
        protected StructurePiece generateSmallDoorChildRight(StartPiece piece, List<StructurePiece> pieces, NukkitRandom random, int y, int z) {
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    case SOUTH:
                        return generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + y, this.boundingBox.z0 + z, BlockFace.EAST, this.getGenDepth());
                    case WEST:
                    case EAST:
                        return generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + z, this.boundingBox.y0 + y, this.boundingBox.z1 + 1, BlockFace.SOUTH, this.getGenDepth());
                }
            }

            return null;
        }

        protected static boolean isOkBox(BoundingBox boundingBox) {
            return boundingBox != null && boundingBox.y0 > 10;
        }

        public enum SmallDoorType {
            OPENING,
            WOOD_DOOR,
            GRATES,
            IRON_DOOR;
        }
    }

    public static class FillerCorridor extends StrongholdPiece {

        private final int steps;

        public FillerCorridor(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.steps = orientation != BlockFace.NORTH && orientation != BlockFace.SOUTH ? boundingBox.getXSpan() : boundingBox.getZSpan();
        }

        public FillerCorridor(CompoundTag tag) {
            super(tag);
            this.steps = tag.getInt("Steps");
        }

        @Override //\\ SHFillerCorridor::getType(void) // 1397245513i64
        public String getType() {
            return "SHFC";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putInt("Steps", this.steps);
        }

        //\\ SHFillerCorridor::findPieceBox(std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int)
        public static BoundingBox findPieceBox(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 4, orientation);
            StructurePiece piece = StructurePiece.findCollisionPiece(pieces, boundingBox);
            if (piece != null) {
                if (piece.getBoundingBox().y0 == boundingBox.y0) {
                    for (int m = 3; m >= 1; --m) {
                        boundingBox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, m - 1, orientation);
                        if (!piece.getBoundingBox().intersects(boundingBox)) {
                            return BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, m, orientation);
                        }
                    }
                }

            }
            return null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            for (int z = 0; z < this.steps; ++z) {
                this.placeBlock(level, STONE_BRICKS, 0, 0, z, boundingBox);
                this.placeBlock(level, STONE_BRICKS, 1, 0, z, boundingBox);
                this.placeBlock(level, STONE_BRICKS, 2, 0, z, boundingBox);
                this.placeBlock(level, STONE_BRICKS, 3, 0, z, boundingBox);
                this.placeBlock(level, STONE_BRICKS, 4, 0, z, boundingBox);

                for (int y = 1; y <= 3; ++y) {
                    this.placeBlock(level, STONE_BRICKS, 0, y, z, boundingBox);
                    this.placeBlock(level, BlockState.AIR, 1, y, z, boundingBox);
                    this.placeBlock(level, BlockState.AIR, 2, y, z, boundingBox);
                    this.placeBlock(level, BlockState.AIR, 3, y, z, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, 4, y, z, boundingBox);
                }

                this.placeBlock(level, STONE_BRICKS, 0, 4, z, boundingBox);
                this.placeBlock(level, STONE_BRICKS, 1, 4, z, boundingBox);
                this.placeBlock(level, STONE_BRICKS, 2, 4, z, boundingBox);
                this.placeBlock(level, STONE_BRICKS, 3, 4, z, boundingBox);
                this.placeBlock(level, STONE_BRICKS, 4, 4, z, boundingBox);
            }

            return true;
        }
    }

    public static class StairsDown extends StrongholdPiece {

        private final boolean isSource;

        public StairsDown(int genDepth, NukkitRandom random, int x, int z) {
            super(genDepth);
            this.isSource = true;
            this.setOrientation(BlockFace.Plane.HORIZONTAL.random(random));
            this.entryDoor = SmallDoorType.OPENING;
            this.boundingBox = new BoundingBox(x, 64, z, x + 5 - 1, 74, z + 5 - 1);
        }

        public StairsDown(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.isSource = false;
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public StairsDown(CompoundTag tag) {
            super(tag);
            this.isSource = tag.getBoolean("Source");
        }

        @Override //\\ SHStairsDown::getType(void) // 1397248836i64
        public String getType() {
            return "SHSD";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Source", this.isSource);
        }

        @Override //\\ SHStairsDown::addChildren(StructurePiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &)
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            if (this.isSource) {
                imposedPiece = FiveCrossing.class;
            }

            this.generateSmallDoorChildForward((StartPiece) piece, pieces, random, 1, 1);
        }

        //\\ SHStairsDown::createPiece(std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
        public static StairsDown createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -7, 0, 5, 11, 5, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new StairsDown(genDepth, random, boundingBox, orientation) : null;
        }

        @Override //\\ SHStairsDown::postProcess(BlockSource *,Random &,BoundingBox const &)
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 10, 4, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 1, 7, 0);
            this.generateSmallDoor(level, random, boundingBox, SmallDoorType.OPENING, 1, 1, 4);

            this.placeBlock(level, STONE_BRICKS, 2, 6, 1, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 1, 5, 1, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 1, 6, 1, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 1, 5, 2, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 1, 4, 3, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 1, 5, 3, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 2, 4, 3, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 3, 3, 3, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 3, 4, 3, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 3, 3, 2, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 3, 2, 1, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 3, 3, 1, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 2, 2, 1, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 1, 1, 1, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 1, 2, 1, boundingBox);
            this.placeBlock(level, STONE_BRICKS, 1, 1, 2, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 1, 1, 3, boundingBox);
            return true;
        }
    }

    public static class StartPiece extends StairsDown {

        public PieceWeight previousPiece;
        @Nullable
        public PortalRoom portalRoomPiece;
        public final List<StructurePiece> pendingChildren = Lists.newArrayList();

        public StartPiece(NukkitRandom random, int x, int z) {
            super(0, random, x, z);
        }
    }

    public static class Straight extends StrongholdPiece {

        private final boolean leftChild;
        private final boolean rightChild;

        public Straight(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
            this.leftChild = random.nextBoundedInt(2) == 0;
            this.rightChild = random.nextBoundedInt(2) == 0;
        }

        public Straight(CompoundTag tag) {
            super(tag);
            this.leftChild = tag.getBoolean("Left");
            this.rightChild = tag.getBoolean("Right");
        }

        @Override //\\ SHStraight::getType(void) // 1397248852i64
        public String getType() {
            return "SHS";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Left", this.leftChild);
            tag.putBoolean("Right", this.rightChild);
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateSmallDoorChildForward((StartPiece) piece, pieces, random, 1, 1);

            if (this.leftChild) {
                this.generateSmallDoorChildLeft((StartPiece) piece, pieces, random, 1, 2);
            }
            if (this.rightChild) {
                this.generateSmallDoorChildRight((StartPiece) piece, pieces, random, 1, 2);
            }
        }

        public static Straight createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Straight(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 4, 6, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateSmallDoor(level, random, boundingBox, SmallDoorType.OPENING, 1, 1, 6);

            BlockState torchE = new BlockState(Block.TORCH, TorchFacingDirection.EAST);
            BlockState torchW = new BlockState(Block.TORCH, TorchFacingDirection.WEST);
            this.maybeGenerateBlock(level, boundingBox, random, 10, 1, 2, 1, torchE);
            this.maybeGenerateBlock(level, boundingBox, random, 10, 3, 2, 1, torchW);
            this.maybeGenerateBlock(level, boundingBox, random, 10, 1, 2, 5, torchE);
            this.maybeGenerateBlock(level, boundingBox, random, 10, 3, 2, 5, torchW);

            if (this.leftChild) {
                this.generateBox(level, boundingBox, 0, 1, 2, 0, 3, 4, BlockState.AIR, BlockState.AIR, false);
            }
            if (this.rightChild) {
                this.generateBox(level, boundingBox, 4, 1, 2, 4, 3, 4, BlockState.AIR, BlockState.AIR, false);
            }

            return true;
        }
    }

    public static class ChestCorridor extends StrongholdPiece {

        private boolean hasPlacedChest;

        public ChestCorridor(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public ChestCorridor(CompoundTag tag) {
            super(tag);
            this.hasPlacedChest = tag.getBoolean("Chest");
        }

        @Override //\\ SHChestCorridor::getType(void) // 1397244744i64
        public String getType() {
            return "SHCC";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Chest", this.hasPlacedChest);
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateSmallDoorChildForward((StartPiece) piece, pieces, random, 1, 1);
        }

        public static ChestCorridor createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new ChestCorridor(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 4, 6, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateSmallDoor(level, random, boundingBox, SmallDoorType.OPENING, 1, 1, 6);
            this.generateBox(level, boundingBox, 3, 1, 2, 3, 1, 4, STONE_BRICKS, STONE_BRICKS, false);

            this.placeBlock(level, STONE_BRICK_SLAB, 3, 1, 1, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 3, 1, 5, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 3, 2, 2, boundingBox);
            this.placeBlock(level, STONE_BRICK_SLAB, 3, 2, 4, boundingBox);

            for (int i = 2; i <= 4; ++i) {
                this.placeBlock(level, STONE_BRICK_SLAB, 2, 1, i, boundingBox);
            }

            if (!this.hasPlacedChest && boundingBox.isInside(new BlockVector3(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
                this.hasPlacedChest = true;

                BlockFace orientation = this.getOrientation();
                this.placeBlock(level, new BlockState(Block.CHEST, (orientation == null ? BlockFace.NORTH : orientation).getOpposite().getIndex()), 3, 2, 3, boundingBox);

                BlockVector3 vec = new BlockVector3(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3));
                if (boundingBox.isInside(vec)) {
                    BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                    if (chunk != null) {
                        CompoundTag nbt = BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.CHEST);
                        ListTag<CompoundTag> itemList = new ListTag<>("Items");
                        StrongholdCorridorChest.get().create(itemList, random);
                        nbt.putList(itemList);
                        Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                    }
                }
            }

            return true;
        }
    }

    public static class StraightStairsDown extends StrongholdPiece {

        public StraightStairsDown(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public StraightStairsDown(CompoundTag tag) {
            super(tag);
        }

        @Override
        public String getType() {
            return "SHSSD";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateSmallDoorChildForward((StartPiece) piece, pieces, random, 1, 1);
        }

        public static StraightStairsDown createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -7, 0, 5, 11, 8, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new StraightStairsDown(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 10, 7, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 1, 7, 0);
            this.generateSmallDoor(level, random, boundingBox, SmallDoorType.OPENING, 1, 1, 7);

            BlockState stairsS = new BlockState(Block.COBBLESTONE_STAIRS, WeirdoDirection.SOUTH);
            for (int i = 0; i < 6; ++i) {
                this.placeBlock(level, stairsS, 1, 6 - i, 1 + i, boundingBox);
                this.placeBlock(level, stairsS, 2, 6 - i, 1 + i, boundingBox);
                this.placeBlock(level, stairsS, 3, 6 - i, 1 + i, boundingBox);

                if (i < 5) {
                    this.placeBlock(level, STONE_BRICKS, 1, 5 - i, 1 + i, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, 2, 5 - i, 1 + i, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, 3, 5 - i, 1 + i, boundingBox);
                }
            }

            return true;
        }
    }

    public abstract static class Turn extends StrongholdPiece {

        protected Turn(int genDepth) {
            super(genDepth);
        }

        public Turn(CompoundTag tag) {
            super(tag);
        }
    }

    public static class LeftTurn extends Turn {

        public LeftTurn(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public LeftTurn(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ SHLeftTurn::getType(void) // 1397247060i64
        public String getType() {
            return "SHLT";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            BlockFace orientation = this.getOrientation();
            if (orientation != BlockFace.NORTH && orientation != BlockFace.EAST) {
                this.generateSmallDoorChildRight((StartPiece) piece, pieces, random, 1, 1);
            } else {
                this.generateSmallDoorChildLeft((StartPiece) piece, pieces, random, 1, 1);
            }
        }

        public static LeftTurn createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 5, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new LeftTurn(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 4, 4, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 1, 1, 0);

            BlockFace orientation = this.getOrientation();
            if (orientation != BlockFace.NORTH && orientation != BlockFace.EAST) {
                this.generateBox(level, boundingBox, 4, 1, 1, 4, 3, 3, BlockState.AIR, BlockState.AIR, false);
            } else {
                this.generateBox(level, boundingBox, 0, 1, 1, 0, 3, 3, BlockState.AIR, BlockState.AIR, false);
            }

            return true;
        }
    }

    public static class RightTurn extends Turn {

        public RightTurn(int i, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(i);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public RightTurn(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ SHRightTurn::getType(void) // 1397248596i64
        public String getType() {
            return "SHRT";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            BlockFace orientation = this.getOrientation();
            if (orientation != BlockFace.NORTH && orientation != BlockFace.EAST) {
                this.generateSmallDoorChildLeft((StartPiece) piece, pieces, random, 1, 1);
            } else {
                this.generateSmallDoorChildRight((StartPiece) piece, pieces, random, 1, 1);
            }
        }

        public static RightTurn createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 5, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new RightTurn(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 4, 4, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 1, 1, 0);

            BlockFace orientation = this.getOrientation();
            if (orientation != BlockFace.NORTH && orientation != BlockFace.EAST) {
                this.generateBox(level, boundingBox, 0, 1, 1, 0, 3, 3, BlockState.AIR, BlockState.AIR, false);
            } else {
                this.generateBox(level, boundingBox, 4, 1, 1, 4, 3, 3, BlockState.AIR, BlockState.AIR, false);
            }

            return true;
        }
    }

    public static class RoomCrossing extends StrongholdPiece {

        protected final int type;

        public RoomCrossing(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
            this.type = random.nextBoundedInt(5);
        }

        public RoomCrossing(CompoundTag tag) {
            super(tag);
            this.type = tag.getInt("Type");
        }

        @Override //\\ SHRoomCrossing::getType(void) // 1397248579i64
        public String getType() {
            return "SHRC";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putInt("Type", this.type);
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateSmallDoorChildForward((StartPiece) piece, pieces, random, 4, 1);
            this.generateSmallDoorChildLeft((StartPiece) piece, pieces, random, 1, 4);
            this.generateSmallDoorChildRight((StartPiece) piece, pieces, random, 1, 4);
        }

        public static RoomCrossing createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -4, -1, 0, 11, 7, 11, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new RoomCrossing(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 10, 6, 10, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 4, 1, 0);
            this.generateBox(level, boundingBox, 4, 1, 10, 6, 3, 10, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 1, 4, 0, 3, 6, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 10, 1, 4, 10, 3, 6, BlockState.AIR, BlockState.AIR, false);

            switch (this.type) {
                case 0:
                    this.placeBlock(level, STONE_BRICKS, 5, 1, 5, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, 5, 2, 5, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, 5, 3, 5, boundingBox);

                    this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.WEST), 4, 3, 5, boundingBox);
                    this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.EAST), 6, 3, 5, boundingBox);
                    this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.SOUTH), 5, 3, 4, boundingBox);
                    this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.NORTH), 5, 3, 6, boundingBox);

                    this.placeBlock(level, SMOOTH_STONE_SLAB, 4, 1, 4, boundingBox);
                    this.placeBlock(level, SMOOTH_STONE_SLAB, 4, 1, 5, boundingBox);
                    this.placeBlock(level, SMOOTH_STONE_SLAB, 4, 1, 6, boundingBox);
                    this.placeBlock(level, SMOOTH_STONE_SLAB, 6, 1, 4, boundingBox);
                    this.placeBlock(level, SMOOTH_STONE_SLAB, 6, 1, 5, boundingBox);
                    this.placeBlock(level, SMOOTH_STONE_SLAB, 6, 1, 6, boundingBox);
                    this.placeBlock(level, SMOOTH_STONE_SLAB, 5, 1, 4, boundingBox);
                    this.placeBlock(level, SMOOTH_STONE_SLAB, 5, 1, 6, boundingBox);
                    break;
                case 1:
                    for (int i = 0; i < 5; ++i) {
                        this.placeBlock(level, STONE_BRICKS, 3, 1, 3 + i, boundingBox);
                        this.placeBlock(level, STONE_BRICKS, 7, 1, 3 + i, boundingBox);
                        this.placeBlock(level, STONE_BRICKS, 3 + i, 1, 3, boundingBox);
                        this.placeBlock(level, STONE_BRICKS, 3 + i, 1, 7, boundingBox);
                    }

                    this.placeBlock(level, STONE_BRICKS, 5, 1, 5, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, 5, 2, 5, boundingBox);
                    this.placeBlock(level, STONE_BRICKS, 5, 3, 5, boundingBox);

                    this.placeBlock(level, WATER, 5, 4, 5, boundingBox);

                    BlockState water1 = new BlockState(Block.STILL_WATER, 1);
                    this.placeBlock(level, water1, 6, 4, 5, boundingBox);
                    this.placeBlock(level, water1, 4, 4, 5, boundingBox);
                    this.placeBlock(level, water1, 5, 4, 6, boundingBox);
                    this.placeBlock(level, water1, 5, 4, 4, boundingBox);
                    this.placeBlock(level, water1, 6, 1, 4, boundingBox);
                    this.placeBlock(level, water1, 6, 1, 6, boundingBox);
                    this.placeBlock(level, water1, 4, 1, 4, boundingBox);
                    this.placeBlock(level, water1, 4, 1, 6, boundingBox);

                    BlockState water9 = new BlockState(Block.STILL_WATER, 9);
                    for (int y = 1; y < 4; ++y) {
                        this.placeBlock(level, water9, 6, y, 5, boundingBox);
                        this.placeBlock(level, water9, 4, y, 5, boundingBox);
                        this.placeBlock(level, water9, 5, y, 6, boundingBox);
                        this.placeBlock(level, water9, 5, y, 4, boundingBox);
                    }

                    break;
                case 2:
                    for (int z = 1; z <= 9; ++z) {
                        this.placeBlock(level, COBBLESTONE, 1, 3, z, boundingBox);
                        this.placeBlock(level, COBBLESTONE, 9, 3, z, boundingBox);
                    }
                    for (int x = 1; x <= 9; ++x) {
                        this.placeBlock(level, COBBLESTONE, x, 3, 1, boundingBox);
                        this.placeBlock(level, COBBLESTONE, x, 3, 9, boundingBox);
                    }

                    this.placeBlock(level, COBBLESTONE, 5, 1, 4, boundingBox);
                    this.placeBlock(level, COBBLESTONE, 5, 1, 6, boundingBox);
                    this.placeBlock(level, COBBLESTONE, 5, 3, 4, boundingBox);
                    this.placeBlock(level, COBBLESTONE, 5, 3, 6, boundingBox);
                    this.placeBlock(level, COBBLESTONE, 4, 1, 5, boundingBox);
                    this.placeBlock(level, COBBLESTONE, 6, 1, 5, boundingBox);
                    this.placeBlock(level, COBBLESTONE, 4, 3, 5, boundingBox);
                    this.placeBlock(level, COBBLESTONE, 6, 3, 5, boundingBox);

                    for (int y = 1; y <= 3; ++y) {
                        this.placeBlock(level, COBBLESTONE, 4, y, 4, boundingBox);
                        this.placeBlock(level, COBBLESTONE, 6, y, 4, boundingBox);
                        this.placeBlock(level, COBBLESTONE, 4, y, 6, boundingBox);
                        this.placeBlock(level, COBBLESTONE, 6, y, 6, boundingBox);
                    }

                    this.placeBlock(level, TORCH, 5, 3, 5, boundingBox);

                    for (int z = 2; z <= 8; ++z) {
                        this.placeBlock(level, OAK_PLANKS, 2, 3, z, boundingBox);
                        this.placeBlock(level, OAK_PLANKS, 3, 3, z, boundingBox);

                        if (z <= 3 || z >= 7) {
                            this.placeBlock(level, OAK_PLANKS, 4, 3, z, boundingBox);
                            this.placeBlock(level, OAK_PLANKS, 5, 3, z, boundingBox);
                            this.placeBlock(level, OAK_PLANKS, 6, 3, z, boundingBox);
                        }

                        this.placeBlock(level, OAK_PLANKS, 7, 3, z, boundingBox);
                        this.placeBlock(level, OAK_PLANKS, 8, 3, z, boundingBox);
                    }

                    BlockState ladderW = new BlockState(Block.LADDER, FacingDirection.WEST);
                    this.placeBlock(level, ladderW, 9, 1, 3, boundingBox);
                    this.placeBlock(level, ladderW, 9, 2, 3, boundingBox);
                    this.placeBlock(level, ladderW, 9, 3, 3, boundingBox);

                    BlockFace orientation = this.getOrientation();
                    this.placeBlock(level, new BlockState(Block.CHEST, (orientation == null ? BlockFace.NORTH : orientation).getOpposite().getIndex()), 3, 4, 8, boundingBox);

                    BlockVector3 vec = new BlockVector3(this.getWorldX(3, 8), this.getWorldY(4), this.getWorldZ(3, 8));
                    if (boundingBox.isInside(vec)) {
                        BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                        if (chunk != null) {
                            CompoundTag nbt = BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.CHEST);
                            ListTag<CompoundTag> itemList = new ListTag<>("Items");
                            StrongholdCrossingChest.get().create(itemList, random);
                            nbt.putList(itemList);
                            Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                        }
                    }
            }

            return true;
        }
    }

    public static class PrisonHall extends StrongholdPiece {

        public PrisonHall(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public PrisonHall(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ SHPrisonHall::getType(void) // 1397248072i64
        public String getType() {
            return "SHPH";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateSmallDoorChildForward((StartPiece) piece, pieces, random, 1, 1);
        }

        public static PrisonHall createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 9, 5, 11, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new PrisonHall(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 8, 4, 10, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateBox(level, boundingBox, 1, 1, 10, 3, 3, 10, BlockState.AIR, BlockState.AIR, false);

            this.generateBox(level, boundingBox, 4, 1, 1, 4, 3, 1, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 4, 1, 3, 4, 3, 3, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 4, 1, 7, 4, 3, 7, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 4, 1, 9, 4, 3, 9, false, random, SMOOTH_STONE_SELECTOR);

            for (int y = 1; y <= 3; ++y) {
                this.placeBlock(level, IRON_BARS, 4, y, 4, boundingBox);
                this.placeBlock(level, IRON_BARS, 4, y, 5, boundingBox);
                this.placeBlock(level, IRON_BARS, 4, y, 6, boundingBox);
                this.placeBlock(level, IRON_BARS, 5, y, 5, boundingBox);
                this.placeBlock(level, IRON_BARS, 6, y, 5, boundingBox);
                this.placeBlock(level, IRON_BARS, 7, y, 5, boundingBox);
            }

            this.placeBlock(level, IRON_BARS, 4, 3, 2, boundingBox);
            this.placeBlock(level, IRON_BARS, 4, 3, 8, boundingBox);

            BlockState doorW = new BlockState(Block.IRON_DOOR_BLOCK, Direction.WEST);
            BlockState doorU = new BlockState(Block.IRON_DOOR_BLOCK, UpperBlockBit.UPPER);
            this.placeBlock(level, doorW, 4, 1, 2, boundingBox);
            this.placeBlock(level, doorU, 4, 2, 2, boundingBox);
            this.placeBlock(level, doorW, 4, 1, 8, boundingBox);
            this.placeBlock(level, doorU, 4, 2, 8, boundingBox);

            return true;
        }
    }

    public static class Library extends StrongholdPiece {

        private final boolean isTall;

        public Library(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
            this.isTall = boundingBox.getYSpan() > 6;
        }

        public Library(CompoundTag tag) {
            super(tag);
            this.isTall = tag.getBoolean("Tall");
        }

        @Override //\\ SHLibrary::getType(void) // 1397247049i64
        public String getType() {
            return "SHLi";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Tall", this.isTall);
        }

        public static Library createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -4, -1, 0, 14, 11, 15, orientation);
            if (!isOkBox(boundingBox) || StructurePiece.findCollisionPiece(pieces, boundingBox) != null) {
                boundingBox = BoundingBox.orientBox(x, y, z, -4, -1, 0, 14, 6, 15, orientation);
                if (!isOkBox(boundingBox) || StructurePiece.findCollisionPiece(pieces, boundingBox) != null) {
                    return null;
                }
            }

            return new Library(genDepth, random, boundingBox, orientation);
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            int height = 11;
            if (!this.isTall) {
                height = 6;
            }

            this.generateBox(level, boundingBox, 0, 0, 0, 13, height - 1, 14, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 4, 1, 0);
            this.generateMaybeBox(level, boundingBox, random, 7, 2, 1, 1, 11, 4, 13, COBWEB, COBWEB, false, false);

            for (int z = 1; z <= 13; ++z) {
                if ((z - 1) % 4 == 0) {
                    this.generateBox(level, boundingBox, 1, 1, z, 1, 4, z, OAK_PLANKS, OAK_PLANKS, false);
                    this.generateBox(level, boundingBox, 12, 1, z, 12, 4, z, OAK_PLANKS, OAK_PLANKS, false);
                    this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.EAST), 2, 3, z, boundingBox);
                    this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.WEST), 11, 3, z, boundingBox);
                    if (this.isTall) {
                        this.generateBox(level, boundingBox, 1, 6, z, 1, 9, z, OAK_PLANKS, OAK_PLANKS, false);
                        this.generateBox(level, boundingBox, 12, 6, z, 12, 9, z, OAK_PLANKS, OAK_PLANKS, false);
                    }
                } else {
                    this.generateBox(level, boundingBox, 1, 1, z, 1, 4, z, BOOKSHELF, BOOKSHELF, false);
                    this.generateBox(level, boundingBox, 12, 1, z, 12, 4, z, BOOKSHELF, BOOKSHELF, false);
                    if (this.isTall) {
                        this.generateBox(level, boundingBox, 1, 6, z, 1, 9, z, BOOKSHELF, BOOKSHELF, false);
                        this.generateBox(level, boundingBox, 12, 6, z, 12, 9, z, BOOKSHELF, BOOKSHELF, false);
                    }
                }
            }

            for (int z = 3; z < 12; z += 2) {
                this.generateBox(level, boundingBox, 3, 1, z, 4, 3, z, BOOKSHELF, BOOKSHELF, false);
                this.generateBox(level, boundingBox, 6, 1, z, 7, 3, z, BOOKSHELF, BOOKSHELF, false);
                this.generateBox(level, boundingBox, 9, 1, z, 10, 3, z, BOOKSHELF, BOOKSHELF, false);
            }

            if (this.isTall) {
                this.generateBox(level, boundingBox, 1, 5, 1, 3, 5, 13, OAK_PLANKS, OAK_PLANKS, false);
                this.generateBox(level, boundingBox, 10, 5, 1, 12, 5, 13, OAK_PLANKS, OAK_PLANKS, false);
                this.generateBox(level, boundingBox, 4, 5, 1, 9, 5, 2, OAK_PLANKS, OAK_PLANKS, false);
                this.generateBox(level, boundingBox, 4, 5, 12, 9, 5, 13, OAK_PLANKS, OAK_PLANKS, false);
                this.placeBlock(level, OAK_PLANKS, 9, 5, 11, boundingBox);
                this.placeBlock(level, OAK_PLANKS, 8, 5, 11, boundingBox);
                this.placeBlock(level, OAK_PLANKS, 9, 5, 10, boundingBox);
                this.generateBox(level, boundingBox, 3, 6, 3, 3, 6, 11, OAK_FENCE, OAK_FENCE, false);
                this.generateBox(level, boundingBox, 10, 6, 3, 10, 6, 9, OAK_FENCE, OAK_FENCE, false);
                this.generateBox(level, boundingBox, 4, 6, 2, 9, 6, 2, OAK_FENCE, OAK_FENCE, false);
                this.generateBox(level, boundingBox, 4, 6, 12, 7, 6, 12, OAK_FENCE, OAK_FENCE, false);
                this.placeBlock(level, OAK_FENCE, 3, 6, 2, boundingBox);
                this.placeBlock(level, OAK_FENCE, 3, 6, 12, boundingBox);
                this.placeBlock(level, OAK_FENCE, 10, 6, 2, boundingBox);

                for (int i = 0; i <= 2; ++i) {
                    this.placeBlock(level, OAK_FENCE, 8 + i, 6, 12 - i, boundingBox);
                    if (i != 2) {
                        this.placeBlock(level, OAK_FENCE, 8 + i, 6, 11 - i, boundingBox);
                    }
                }

                BlockState ladderS = new BlockState(Block.LADDER, FacingDirection.SOUTH);
                this.placeBlock(level, ladderS, 10, 1, 13, boundingBox);
                this.placeBlock(level, ladderS, 10, 2, 13, boundingBox);
                this.placeBlock(level, ladderS, 10, 3, 13, boundingBox);
                this.placeBlock(level, ladderS, 10, 4, 13, boundingBox);
                this.placeBlock(level, ladderS, 10, 5, 13, boundingBox);
                this.placeBlock(level, ladderS, 10, 6, 13, boundingBox);
                this.placeBlock(level, ladderS, 10, 7, 13, boundingBox);

                this.placeBlock(level, OAK_FENCE, 6, 9, 7, boundingBox);
                this.placeBlock(level, OAK_FENCE, 7, 9, 7, boundingBox);
                this.placeBlock(level, OAK_FENCE, 6, 8, 7, boundingBox);
                this.placeBlock(level, OAK_FENCE, 7, 8, 7, boundingBox);
                this.placeBlock(level, OAK_FENCE, 6, 7, 7, boundingBox);
                this.placeBlock(level, OAK_FENCE, 7, 7, 7, boundingBox);
                this.placeBlock(level, OAK_FENCE, 5, 7, 7, boundingBox);
                this.placeBlock(level, OAK_FENCE, 8, 7, 7, boundingBox);
                this.placeBlock(level, OAK_FENCE, 6, 7, 6, boundingBox);
                this.placeBlock(level, OAK_FENCE, 6, 7, 8, boundingBox);
                this.placeBlock(level, OAK_FENCE, 7, 7, 6, boundingBox);
                this.placeBlock(level, OAK_FENCE, 7, 7, 8, boundingBox);

                this.placeBlock(level, TORCH, 5, 8, 7, boundingBox);
                this.placeBlock(level, TORCH, 8, 8, 7, boundingBox);
                this.placeBlock(level, TORCH, 6, 8, 6, boundingBox);
                this.placeBlock(level, TORCH, 6, 8, 8, boundingBox);
                this.placeBlock(level, TORCH, 7, 8, 6, boundingBox);
                this.placeBlock(level, TORCH, 7, 8, 8, boundingBox);
            }

            BlockFace orientation = this.getOrientation();
            BlockState chest = new BlockState(Block.CHEST, (orientation == null ? BlockFace.NORTH : orientation).getOpposite().getIndex());
            this.placeBlock(level, chest, 3, 3, 5, boundingBox);

            BlockVector3 vec = new BlockVector3(this.getWorldX(3, 5), this.getWorldY(3), this.getWorldZ(3, 5));
            if (boundingBox.isInside(vec)) {
                BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                if (chunk != null) {
                    CompoundTag nbt = BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.CHEST);
                    ListTag<CompoundTag> itemList = new ListTag<>("Items");
                    StrongholdLibraryChest.get().create(itemList, random);
                    nbt.putList(itemList);
                    Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                }
            }

            if (this.isTall) {
                this.placeBlock(level, BlockState.AIR, 12, 9, 1, boundingBox);
                this.placeBlock(level, chest, 12, 8, 1, boundingBox);

                vec.setComponents(this.getWorldX(12, 1), this.getWorldY(8), this.getWorldZ(12, 1));
                if (boundingBox.isInside(vec)) {
                    BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                    if (chunk != null) {
                        CompoundTag nbt = BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.CHEST);
                        ListTag<CompoundTag> itemList = new ListTag<>("Items");
                        StrongholdLibraryChest.get().create(itemList, random);
                        nbt.putList(itemList);
                        Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                    }
                }
            }

            return true;
        }
    }

    public static class FiveCrossing extends StrongholdPiece {

        private final boolean leftLow;
        private final boolean leftHigh;
        private final boolean rightLow;
        private final boolean rightHigh;

        public FiveCrossing(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
            this.leftLow = random.nextBoolean();
            this.leftHigh = random.nextBoolean();
            this.rightLow = random.nextBoolean();
            this.rightHigh = random.nextBoundedInt(3) > 0;
        }

        public FiveCrossing(CompoundTag tag) {
            super(tag);
            this.leftLow = tag.getBoolean("leftLow");
            this.leftHigh = tag.getBoolean("leftHigh");
            this.rightLow = tag.getBoolean("rightLow");
            this.rightHigh = tag.getBoolean("rightHigh");
        }

        @Override //\\ SHFiveCrossing::getType(void) // 1397241155i64
        public String getType() {
            return "SH5C";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("leftLow", this.leftLow);
            tag.putBoolean("leftHigh", this.leftHigh);
            tag.putBoolean("rightLow", this.rightLow);
            tag.putBoolean("rightHigh", this.rightHigh);
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            int lowX = 3;
            int highX = 5;

            BlockFace orientation = this.getOrientation();
            if (orientation == BlockFace.WEST || orientation == BlockFace.NORTH) {
                lowX = 8 - lowX;
                highX = 8 - highX;
            }

            this.generateSmallDoorChildForward((StartPiece) piece, pieces, random, 5, 1);

            if (this.leftLow) {
                this.generateSmallDoorChildLeft((StartPiece) piece, pieces, random, lowX, 1);
            }
            if (this.leftHigh) {
                this.generateSmallDoorChildLeft((StartPiece) piece, pieces, random, highX, 7);
            }
            if (this.rightLow) {
                this.generateSmallDoorChildRight((StartPiece) piece, pieces, random, lowX, 1);
            }
            if (this.rightHigh) {
                this.generateSmallDoorChildRight((StartPiece) piece, pieces, random, highX, 7);
            }
        }

        public static FiveCrossing createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -4, -3, 0, 10, 9, 11, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new FiveCrossing(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 9, 8, 10, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, this.entryDoor, 4, 3, 0);

            if (this.leftLow) {
                this.generateBox(level, boundingBox, 0, 3, 1, 0, 5, 3, BlockState.AIR, BlockState.AIR, false);
            }
            if (this.rightLow) {
                this.generateBox(level, boundingBox, 9, 3, 1, 9, 5, 3, BlockState.AIR, BlockState.AIR, false);
            }
            if (this.leftHigh) {
                this.generateBox(level, boundingBox, 0, 5, 7, 0, 7, 9, BlockState.AIR, BlockState.AIR, false);
            }
            if (this.rightHigh) {
                this.generateBox(level, boundingBox, 9, 5, 7, 9, 7, 9, BlockState.AIR, BlockState.AIR, false);
            }

            this.generateBox(level, boundingBox, 5, 1, 10, 7, 3, 10, BlockState.AIR, BlockState.AIR, false);

            this.generateBox(level, boundingBox, 1, 2, 1, 8, 2, 6, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 4, 1, 5, 4, 4, 9, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 8, 1, 5, 8, 4, 9, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 1, 4, 7, 3, 4, 9, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 1, 3, 5, 3, 3, 6, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 1, 3, 4, 3, 3, 4, STONE_BRICK_SLAB, STONE_BRICK_SLAB, false);
            this.generateBox(level, boundingBox, 1, 4, 6, 3, 4, 6, STONE_BRICK_SLAB, STONE_BRICK_SLAB, false);
            this.generateBox(level, boundingBox, 5, 1, 7, 7, 1, 8, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 5, 1, 9, 7, 1, 9, STONE_BRICK_SLAB, STONE_BRICK_SLAB, false);
            this.generateBox(level, boundingBox, 5, 2, 7, 7, 2, 7, STONE_BRICK_SLAB, STONE_BRICK_SLAB, false);
            this.generateBox(level, boundingBox, 4, 5, 7, 4, 5, 9, STONE_BRICK_SLAB, STONE_BRICK_SLAB, false);
            this.generateBox(level, boundingBox, 8, 5, 7, 8, 5, 9, STONE_BRICK_SLAB, STONE_BRICK_SLAB, false);
            this.generateBox(level, boundingBox, 5, 5, 7, 7, 5, 9, SMOOTH_STONE_SLAB_DOUBLE, SMOOTH_STONE_SLAB_DOUBLE, false);

            this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.SOUTH), 6, 5, 6, boundingBox);

            return true;
        }
    }

    public static class PortalRoom extends StrongholdPiece {

        private boolean hasPlacedSpawner;

        public PortalRoom(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public PortalRoom(CompoundTag tag) {
            super(tag);
            this.hasPlacedSpawner = tag.getBoolean("Mob");
        }

        @Override
        public String getType() {
            return "SHPR";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Mob", this.hasPlacedSpawner);
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            if (piece != null) {
                ((StartPiece) piece).portalRoomPiece = this;
            }
        }

        public static PortalRoom createPiece(List<StructurePiece> pieces, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -4, -1, 0, 11, 8, 16, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new PortalRoom(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 10, 7, 15, false, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(level, random, boundingBox, SmallDoorType.GRATES, 4, 1, 0);

            int y = 6;
            this.generateBox(level, boundingBox, 1, y, 1, 1, y, 14, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 9, y, 1, 9, y, 14, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 2, y, 1, 8, y, 2, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 2, y, 14, 8, y, 14, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 1, 1, 1, 2, 1, 4, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 8, 1, 1, 9, 1, 4, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 1, 1, 1, 1, 1, 3, LAVA, LAVA, false);
            this.generateBox(level, boundingBox, 9, 1, 1, 9, 1, 3, LAVA, LAVA, false);
            this.generateBox(level, boundingBox, 3, 1, 8, 7, 1, 12, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 4, 1, 9, 6, 1, 11, LAVA, LAVA, false);

            for (int z = 3; z < 14; z += 2) {
                this.generateBox(level, boundingBox, 0, 3, z, 0, 4, z, IRON_BARS, IRON_BARS, false);
                this.generateBox(level, boundingBox, 10, 3, z, 10, 4, z, IRON_BARS, IRON_BARS, false);
            }
            for (int x = 2; x < 9; x += 2) {
                this.generateBox(level, boundingBox, x, 3, 15, x, 4, 15, IRON_BARS, IRON_BARS, false);
            }

            this.generateBox(level, boundingBox, 4, 1, 5, 6, 1, 7, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 4, 2, 6, 6, 2, 7, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(level, boundingBox, 4, 3, 7, 6, 3, 7, false, random, SMOOTH_STONE_SELECTOR);

            BlockState stairsN = new BlockState(Block.STONE_BRICK_STAIRS, WeirdoDirection.NORTH);
            for (int x = 4; x <= 6; ++x) {
                this.placeBlock(level, stairsN, x, 1, 4, boundingBox);
                this.placeBlock(level, stairsN, x, 2, 5, boundingBox);
                this.placeBlock(level, stairsN, x, 3, 6, boundingBox);
            }

            BlockState frameN = new BlockState(Block.END_PORTAL_FRAME, Direction.NORTH);
            BlockState frameS = new BlockState(Block.END_PORTAL_FRAME, Direction.SOUTH);
            BlockState frameE = new BlockState(Block.END_PORTAL_FRAME, Direction.EAST);
            BlockState frameW = new BlockState(Block.END_PORTAL_FRAME, Direction.WEST);

            boolean actived = true;
            boolean[] hasEye = new boolean[12];

            for (int i = 0; i < hasEye.length; ++i) {
                hasEye[i] = random.nextBoundedInt(100) > 90;
                actived &= hasEye[i];
            }

            this.placeBlock(level, hasEye[0] ? new BlockState(Block.END_PORTAL_FRAME, frameN.getMeta() | EndPortalEyeBit.HAS_EYE) : frameN, 4, 3, 8, boundingBox);
            this.placeBlock(level, hasEye[1] ? new BlockState(Block.END_PORTAL_FRAME, frameN.getMeta() | EndPortalEyeBit.HAS_EYE) : frameN, 5, 3, 8, boundingBox);
            this.placeBlock(level, hasEye[2] ? new BlockState(Block.END_PORTAL_FRAME, frameN.getMeta() | EndPortalEyeBit.HAS_EYE) : frameN, 6, 3, 8, boundingBox);
            this.placeBlock(level, hasEye[3] ? new BlockState(Block.END_PORTAL_FRAME, frameS.getMeta() | EndPortalEyeBit.HAS_EYE) : frameS, 4, 3, 12, boundingBox);
            this.placeBlock(level, hasEye[4] ? new BlockState(Block.END_PORTAL_FRAME, frameS.getMeta() | EndPortalEyeBit.HAS_EYE) : frameS, 5, 3, 12, boundingBox);
            this.placeBlock(level, hasEye[5] ? new BlockState(Block.END_PORTAL_FRAME, frameS.getMeta() | EndPortalEyeBit.HAS_EYE) : frameS, 6, 3, 12, boundingBox);
            this.placeBlock(level, hasEye[6] ? new BlockState(Block.END_PORTAL_FRAME, frameE.getMeta() | EndPortalEyeBit.HAS_EYE) : frameE, 3, 3, 9, boundingBox);
            this.placeBlock(level, hasEye[7] ? new BlockState(Block.END_PORTAL_FRAME, frameE.getMeta() | EndPortalEyeBit.HAS_EYE) : frameE, 3, 3, 10, boundingBox);
            this.placeBlock(level, hasEye[8] ? new BlockState(Block.END_PORTAL_FRAME, frameE.getMeta() | EndPortalEyeBit.HAS_EYE) : frameE, 3, 3, 11, boundingBox);
            this.placeBlock(level, hasEye[9] ? new BlockState(Block.END_PORTAL_FRAME, frameW.getMeta() | EndPortalEyeBit.HAS_EYE) : frameW, 7, 3, 9, boundingBox);
            this.placeBlock(level, hasEye[10] ? new BlockState(Block.END_PORTAL_FRAME, frameW.getMeta() | EndPortalEyeBit.HAS_EYE) : frameW, 7, 3, 10, boundingBox);
            this.placeBlock(level, hasEye[11] ? new BlockState(Block.END_PORTAL_FRAME, frameW.getMeta() | EndPortalEyeBit.HAS_EYE) : frameW, 7, 3, 11, boundingBox);

            if (actived) {
                this.placeBlock(level, END_PORTAL, 4, 3, 9, boundingBox);
                this.placeBlock(level, END_PORTAL, 5, 3, 9, boundingBox);
                this.placeBlock(level, END_PORTAL, 6, 3, 9, boundingBox);
                this.placeBlock(level, END_PORTAL, 4, 3, 10, boundingBox);
                this.placeBlock(level, END_PORTAL, 5, 3, 10, boundingBox);
                this.placeBlock(level, END_PORTAL, 6, 3, 10, boundingBox);
                this.placeBlock(level, END_PORTAL, 4, 3, 11, boundingBox);
                this.placeBlock(level, END_PORTAL, 5, 3, 11, boundingBox);
                this.placeBlock(level, END_PORTAL, 6, 3, 11, boundingBox);
            }

            if (!this.hasPlacedSpawner) {
                BlockVector3 vec = new BlockVector3(this.getWorldX(5, 6), this.getWorldY(3), this.getWorldZ(5, 6));
                if (boundingBox.isInside(vec)) {
                    this.hasPlacedSpawner = true;
                    level.setBlockAt(vec.x, vec.y, vec.z, SPAWNER.getId(), SPAWNER.getMeta());

                    BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                    if (chunk != null) {
                        Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(),
                                BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.MOB_SPAWNER)
                                        .putInt("EntityId", EntitySilverfish.NETWORK_ID)));
                    }
                }
            }

            return true;
        }
    }

    static class SmoothStoneSelector extends StructurePiece.BlockSelector {

        public void next(NukkitRandom random, int x, int y, int z, boolean hasNext) {
            if (hasNext) {
                int chance = random.nextBoundedInt(100);
                if (chance < 20) {
                    this.next = CRACKED_STONE_BRICKS;
                } else if (chance < 50) {
                    this.next = MOSSY_STONE_BRICKS;
                } else if (chance < 55) {
                    this.next = INFESTED_STONE_BRICKS;
                } else {
                    this.next = STONE_BRICKS;
                }
            } else {
                this.next = BlockState.AIR;
            }
        }
    }

    public static void init() {
        //NOOP
    }
}
