package cn.nukkit.level.generator.populator.impl.structure.netherfortress.structure;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.netherfortress.loot.NetherBridgeChest;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.LiquidUpdater;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.BlockState;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.WeirdoDirection;
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
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.21-r6")
public class NetherBridgePieces {

    private static final BlockState NETHER_BRICKS = new BlockState(Block.NETHER_BRICKS);
    private static final BlockState NETHER_BRICK_FENCE = new BlockState(Block.NETHER_BRICK_FENCE);
    private static final BlockState SOUL_SAND = new BlockState(Block.SOUL_SAND);
    private static final BlockState NETHER_WART = new BlockState(Block.NETHER_WART_BLOCK);
    private static final BlockState LAVA = new BlockState(Block.LAVA);
    private static final BlockState SPAWNER = new BlockState(Block.MONSTER_SPAWNER);

    private static final PieceWeight[] BRIDGE_PIECE_WEIGHTS = new PieceWeight[]{
            new PieceWeight(BridgeStraight.class, 30, 0, true),
            new PieceWeight(BridgeCrossing.class, 10, 4),
            new PieceWeight(RoomCrossing.class, 10, 4),
            new PieceWeight(StairsRoom.class, 10, 3),
            new PieceWeight(MonsterThrone.class, 5, 2),
            new PieceWeight(CastleEntrance.class, 5, 1)
    };
    private static final PieceWeight[] CASTLE_PIECE_WEIGHTS = new PieceWeight[]{
            new PieceWeight(CastleSmallCorridorPiece.class, 25, 0, true),
            new PieceWeight(CastleSmallCorridorCrossingPiece.class, 15, 5),
            new PieceWeight(CastleSmallCorridorRightTurnPiece.class, 5, 10),
            new PieceWeight(CastleSmallCorridorLeftTurnPiece.class, 5, 10),
            new PieceWeight(CastleCorridorStairsPiece.class, 10, 3, true),
            new PieceWeight(CastleCorridorTBalconyPiece.class, 7, 2),
            new PieceWeight(CastleStalkRoom.class, 5, 2)
    };

    //\\ NetherFortressPiece::findAndCreateBridgePieceFactory(std::basic_string<char,std::char_traits<char>,std::allocator<char>> const &,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static NetherBridgePiece findAndCreateBridgePieceFactory(PieceWeight weight, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
        Class<? extends NetherBridgePiece> pieceClass = weight.pieceClass;
        if (pieceClass == BridgeStraight.class) {
            return BridgeStraight.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == BridgeCrossing.class) {
            return BridgeCrossing.createPiece(pieces, x, y, z, orientation, genDepth);
        } else if (pieceClass == RoomCrossing.class) {
            return RoomCrossing.createPiece(pieces, x, y, z, orientation, genDepth);
        } else if (pieceClass == StairsRoom.class) {
            return StairsRoom.createPiece(pieces, x, y, z, genDepth, orientation);
        } else if (pieceClass == MonsterThrone.class) {
            return MonsterThrone.createPiece(pieces, x, y, z, genDepth, orientation);
        } else if (pieceClass == CastleEntrance.class) {
            return CastleEntrance.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == CastleSmallCorridorPiece.class) {
            return CastleSmallCorridorPiece.createPiece(pieces, x, y, z, orientation, genDepth);
        } else if (pieceClass == CastleSmallCorridorRightTurnPiece.class) {
            return CastleSmallCorridorRightTurnPiece.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == CastleSmallCorridorLeftTurnPiece.class) {
            return CastleSmallCorridorLeftTurnPiece.createPiece(pieces, random, x, y, z, orientation, genDepth);
        } else if (pieceClass == CastleCorridorStairsPiece.class) {
            return CastleCorridorStairsPiece.createPiece(pieces, x, y, z, orientation, genDepth);
        } else if (pieceClass == CastleCorridorTBalconyPiece.class) {
            return CastleCorridorTBalconyPiece.createPiece(pieces, x, y, z, orientation, genDepth);
        } else if (pieceClass == CastleSmallCorridorCrossingPiece.class) {
            return CastleSmallCorridorCrossingPiece.createPiece(pieces, x, y, z, orientation, genDepth);
        } else if (pieceClass == CastleStalkRoom.class) {
            return CastleStalkRoom.createPiece(pieces, x, y, z, orientation, genDepth);
        }
        return null;
    }

    static class PieceWeight {

        public Class<? extends NetherBridgePiece> pieceClass;
        public int weight;
        public int placeCount;
        public int maxPlaceCount;
        public boolean allowInRow;

        public PieceWeight(Class<? extends NetherBridgePiece> pieceClass, int weight, int maxPlaceCount) {
            this(pieceClass, weight, maxPlaceCount, false);
        }

        public PieceWeight(Class<? extends NetherBridgePiece> pieceClass, int weight, int maxPlaceCount, boolean allowInRow) {
            this.pieceClass = pieceClass;
            this.weight = weight;
            this.maxPlaceCount = maxPlaceCount;
            this.allowInRow = allowInRow;
        }

        public boolean doPlace(int genDepth) {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }

        public boolean isValid() {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
    }

    abstract static class NetherBridgePiece extends StructurePiece {

        protected NetherBridgePiece(int genDepth) {
            super(genDepth);
        }

        public NetherBridgePiece(CompoundTag tag) {
            super(tag);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            //NOOP
        }

        private int updatePieceWeight(List<PieceWeight> weights) {
            boolean success = false;
            int total = 0;

            for (PieceWeight weight : weights) {
                if (weight.maxPlaceCount > 0 && weight.placeCount < weight.maxPlaceCount) {
                    success = true;
                }
                total += weight.weight;
            }

            return success ? total : -1;
        }

        //\\ NetherFortressPiece::generatePiece(NBStartPiece *,std::vector<PieceWeight,std::allocator<PieceWeight>> &,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
        private NetherBridgePiece generatePiece(StartPiece start, List<PieceWeight> weights, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            int total = this.updatePieceWeight(weights);
            if (total > 0 && genDepth <= 30) {
                for (int i = 0; i < 5; ++i) {
                    int target = random.nextBoundedInt(total);

                    for (PieceWeight weight : weights) {
                        target -= weight.weight;

                        if (target < 0) {
                            if (!weight.doPlace(genDepth) || weight == start.previousPiece && !weight.allowInRow) {
                                break;
                            }

                            NetherBridgePiece piece = findAndCreateBridgePieceFactory(weight, pieces, random, x, y, z, orientation, genDepth);
                            if (piece != null) {
                                ++weight.placeCount;
                                start.previousPiece = weight;

                                if (!weight.isValid()) {
                                    weights.remove(weight);
                                }

                                return piece;
                            }
                        }
                    }
                }
            }

            return BridgeEndFiller.createPiece(pieces, random, x, y, z, orientation, genDepth);
        }

        //\\ NetherFortressPiece::generateAndAddPiece(NBStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int,bool)
        private StructurePiece generateAndAddPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, @Nullable BlockFace orientation, int genDepth, boolean isCastle) {
            if (Math.abs(x - start.getBoundingBox().x0) > 112 || Math.abs(z - start.getBoundingBox().z0) > 112) {
                return BridgeEndFiller.createPiece(pieces, random, x, y, z, orientation, genDepth);
            }

            List<PieceWeight> availablePieces;
            if (isCastle) {
                availablePieces = start.availableCastlePieces;
            } else {
                availablePieces = start.availableBridgePieces;
            }

            StructurePiece piece = this.generatePiece(start, availablePieces, pieces, random, x, y, z, orientation, genDepth + 1);
            if (piece != null) {
                pieces.add(piece);
                start.pendingChildren.add(piece);
            }

            return piece;
        }

        @Nullable
        //\\ NetherFortressPiece::generateChildForward(NBStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,bool)
        protected StructurePiece generateChildForward(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int horizontalOffset, int yOffset, boolean isCastle) {
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    default:
                        return this.generateAndAddPiece(start, pieces, random, this.boundingBox.x0 + horizontalOffset, this.boundingBox.y0 + yOffset, this.boundingBox.z0 - 1, orientation, this.getGenDepth(), isCastle);
                    case SOUTH:
                        return this.generateAndAddPiece(start, pieces, random, this.boundingBox.x0 + horizontalOffset, this.boundingBox.y0 + yOffset, this.boundingBox.z1 + 1, orientation, this.getGenDepth(), isCastle);
                    case WEST:
                        return this.generateAndAddPiece(start, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + yOffset, this.boundingBox.z0 + horizontalOffset, orientation, this.getGenDepth(), isCastle);
                    case EAST:
                        return this.generateAndAddPiece(start, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + yOffset, this.boundingBox.z0 + horizontalOffset, orientation, this.getGenDepth(), isCastle);
                }
            }
            return null;
        }

        @Nullable
        //\\ NetherFortressPiece::generateChildLeft(NBStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,bool)
        protected StructurePiece generateChildLeft(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int yOffset, int horizontalOffset, boolean isCastle) {
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    case SOUTH:
                    default:
                        return this.generateAndAddPiece(start, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + yOffset, this.boundingBox.z0 + horizontalOffset, BlockFace.WEST, this.getGenDepth(), isCastle);
                    case WEST:
                    case EAST:
                        return this.generateAndAddPiece(start, pieces, random, this.boundingBox.x0 + horizontalOffset, this.boundingBox.y0 + yOffset, this.boundingBox.z0 - 1, BlockFace.NORTH, this.getGenDepth(), isCastle);
                }
            }
            return null;
        }

        @Nullable
        //\\ NetherFortressPiece::generateChildRight(NBStartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,bool)
        protected StructurePiece generateChildRight(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int yOffset, int horizontalOffset, boolean isCastle) {
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    case SOUTH:
                    default:
                        return this.generateAndAddPiece(start, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + yOffset, this.boundingBox.z0 + horizontalOffset, BlockFace.EAST, this.getGenDepth(), isCastle);
                    case WEST:
                    case EAST:
                        return this.generateAndAddPiece(start, pieces, random, this.boundingBox.x0 + horizontalOffset, this.boundingBox.y0 + yOffset, this.boundingBox.z1 + 1, BlockFace.SOUTH, this.getGenDepth(), isCastle);
                }
            }
            return null;
        }

        protected static boolean isOkBox(BoundingBox boundingBox) {
            return boundingBox != null && boundingBox.y0 > 10;
        }
    }

    public static class StartPiece extends BridgeCrossing {

        public PieceWeight previousPiece;
        public List<PieceWeight> availableBridgePieces;
        public List<PieceWeight> availableCastlePieces;
        public final List<StructurePiece> pendingChildren;

        //\\ NBStartPiece::NBStartPiece(Random &,int,int)
        public StartPiece(NukkitRandom random, int x, int z) {
            super(random, x, z);
            this.pendingChildren = Lists.newArrayList();

            this.availableBridgePieces = Lists.newArrayList();
            for (PieceWeight weight : NetherBridgePieces.BRIDGE_PIECE_WEIGHTS) {
                weight.placeCount = 0;
                this.availableBridgePieces.add(weight);
            }

            this.availableCastlePieces = Lists.newArrayList();
            for (PieceWeight weight : NetherBridgePieces.CASTLE_PIECE_WEIGHTS) {
                weight.placeCount = 0;
                this.availableCastlePieces.add(weight);
            }
        }

        public StartPiece(CompoundTag tag) {
            super(tag);
            this.pendingChildren = Lists.newArrayList();
        }

        @Override //\\ NBBridgeCrossing::getType(void) // 1312965458i64
        public String getType() {
            return "NeStart";
        }
    }

    public static class BridgeStraight extends NetherBridgePiece {

        public BridgeStraight(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public BridgeStraight(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBBridgeStraight::getType(void) // 1312969556i64
        public String getType() {
            return "NeBS";
        }

        @Override //\\ NBBridgeStraight::addChildren(StructurePiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &)
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildForward((StartPiece) piece, pieces, random, 1, 3, false);
        }

        public static BridgeStraight createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -3, 0, 5, 10, 19, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new BridgeStraight(genDepth, random, boundingBox, orientation) : null;
        }

        @Override //\\ NBBridgeStraight::postProcess(BlockSource *,Random &,BoundingBox const &)
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 3, 0, 4, 4, 18, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 5, 0, 3, 7, 18, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 5, 0, 0, 5, 18, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 5, 0, 4, 5, 18, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 4, 2, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 13, 4, 2, 18, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 1, 3, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 0, 15, 4, 1, 18, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 0; x <= 4; ++x) {
                for (int z = 0; z <= 2; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, 18 - z, boundingBox);
                }
            }

            this.generateBox(level, boundingBox, 0, 1, 1, 0, 4, 1, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 3, 4, 0, 4, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 3, 14, 0, 4, 14, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 1, 17, 0, 4, 17, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 4, 1, 1, 4, 4, 1, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 4, 3, 4, 4, 4, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 4, 3, 14, 4, 4, 14, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 4, 1, 17, 4, 4, 17, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);

            return true;
        }
    }

    public static class BridgeEndFiller extends NetherBridgePiece {

        private final int selfSeed;

        public BridgeEndFiller(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.selfSeed = random.nextInt();
        }

        public BridgeEndFiller(CompoundTag tag) {
            super(tag);
            this.selfSeed = tag.getInt("Seed");
        }

        @Override //\\ NBBridgeEndFiller::getType(void) // 1312965958i64
        public String getType() {
            return "NeBEF";
        }

        public static BridgeEndFiller createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -3, 0, 5, 10, 8, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new BridgeEndFiller(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putInt("Seed", this.selfSeed);
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            NukkitRandom rand = new NukkitRandom(this.selfSeed);

            for (int x = 0; x <= 4; ++x) {
                for (int y = 3; y <= 4; ++y) {
                    this.generateBox(level, boundingBox, x, y, 0, x, y, rand.nextBoundedInt(8), NETHER_BRICKS, NETHER_BRICKS, false);
                }
            }

            this.generateBox(level, boundingBox, 0, 5, 0, 0, 5, rand.nextBoundedInt(8), NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 5, 0, 4, 5, rand.nextBoundedInt(8), NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 0; x <= 4; ++x) {
                this.generateBox(level, boundingBox, x, 2, 0, x, 2, rand.nextBoundedInt(5), NETHER_BRICKS, NETHER_BRICKS, false);
            }

            for (int x = 0; x <= 4; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    this.generateBox(level, boundingBox, x, y, 0, x, y, rand.nextBoundedInt(3), NETHER_BRICKS, NETHER_BRICKS, false);
                }
            }

            return true;
        }
    }

    public static class BridgeCrossing extends NetherBridgePiece {

        protected BridgeCrossing(NukkitRandom random, int x, int z) {
            super(0);
            this.setOrientation(BlockFace.Plane.HORIZONTAL.random(random));
            this.boundingBox = new BoundingBox(x, 64, z, x + 19 - 1, 73, z + 19 - 1);
        }

        public BridgeCrossing(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public BridgeCrossing(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBBridgeCrossing::getType(void) // 1312965458i64
        public String getType() {
            return "NeBCr";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildForward((StartPiece) piece, pieces, random, 8, 3, false);
            this.generateChildLeft((StartPiece) piece, pieces, random, 3, 8, false);
            this.generateChildRight((StartPiece) piece, pieces, random, 3, 8, false);
        }

        public static BridgeCrossing createPiece(List<StructurePiece> pieces, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -8, -3, 0, 19, 10, 19, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new BridgeCrossing(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 7, 3, 0, 11, 4, 18, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 3, 7, 18, 4, 11, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 8, 5, 0, 10, 7, 18, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 5, 8, 18, 7, 10, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 7, 5, 0, 7, 5, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 7, 5, 11, 7, 5, 18, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 11, 5, 0, 11, 5, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 11, 5, 11, 11, 5, 18, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 5, 7, 7, 5, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 11, 5, 7, 18, 5, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 5, 11, 7, 5, 11, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 11, 5, 11, 18, 5, 11, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 7, 2, 0, 11, 2, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 7, 2, 13, 11, 2, 18, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 7, 0, 0, 11, 1, 3, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 7, 0, 15, 11, 1, 18, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 7; x <= 11; ++x) {
                for (int z = 0; z <= 2; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, 18 - z, boundingBox);
                }
            }

            this.generateBox(level, boundingBox, 0, 2, 7, 5, 2, 11, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 13, 2, 7, 18, 2, 11, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 0, 7, 3, 1, 11, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 15, 0, 7, 18, 1, 11, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 0; x <= 2; ++x) {
                for (int z = 7; z <= 11; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                    this.fillColumnDown(level, NETHER_BRICKS, 18 - x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class RoomCrossing extends NetherBridgePiece {

        public RoomCrossing(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public RoomCrossing(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBRoomCrossing::getType(void) // 1312969283i64
        public String getType() {
            return "NeRC";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildForward((StartPiece) piece, pieces, random, 2, 0, false);
            this.generateChildLeft((StartPiece) piece, pieces, random, 0, 2, false);
            this.generateChildRight((StartPiece) piece, pieces, random, 0, 2, false);
        }

        public static RoomCrossing createPiece(List<StructurePiece> pieces, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -2, 0, 0, 7, 9, 7, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new RoomCrossing(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 6, 1, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 6, 7, 6, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 1, 6, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 6, 1, 6, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 2, 0, 6, 6, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 2, 6, 6, 6, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 6, 1, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 5, 0, 6, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 2, 0, 6, 6, 1, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 2, 5, 6, 6, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 6, 0, 4, 6, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 0, 4, 5, 0, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 2, 6, 6, 4, 6, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 6, 4, 5, 6, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 6, 2, 0, 6, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 5, 2, 0, 5, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 6, 6, 2, 6, 6, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 5, 2, 6, 5, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);

            for (int x = 0; x <= 6; ++x) {
                for (int z = 0; z <= 6; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class StairsRoom extends NetherBridgePiece {

        public StairsRoom(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public StairsRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBStairsRoom::getType(void) // 1312969554i64
        public String getType() {
            return "NeSR";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildRight((StartPiece) piece, pieces, random, 6, 2, false);
        }

        public static StairsRoom createPiece(List<StructurePiece> pieces, int x, int y, int z, int genDepth, BlockFace orientation) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -2, 0, 0, 7, 11, 7, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new StairsRoom(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 6, 1, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 6, 10, 6, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 1, 8, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 2, 0, 6, 8, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 1, 0, 8, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 2, 1, 6, 8, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 2, 6, 5, 8, 6, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 3, 2, 0, 5, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 6, 3, 2, 6, 5, 2, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 6, 3, 4, 6, 5, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.placeBlock(level, NETHER_BRICKS, 5, 2, 5, boundingBox);
            this.generateBox(level, boundingBox, 4, 2, 5, 4, 3, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 3, 2, 5, 3, 4, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 2, 5, 2, 5, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 2, 5, 1, 6, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 7, 1, 5, 7, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 8, 2, 6, 8, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 2, 6, 0, 4, 8, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 0, 4, 5, 0, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);

            for (int x = 0; x <= 6; ++x) {
                for (int z = 0; z <= 6; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class MonsterThrone extends NetherBridgePiece {

        private boolean hasPlacedSpawner;

        public MonsterThrone(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public MonsterThrone(CompoundTag tag) {
            super(tag);
            this.hasPlacedSpawner = tag.getBoolean("Mob");
        }

        @Override //\\ NBMonsterThrone::getType(void) // 1312968020i64
        public String getType() {
            return "NeMT";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Mob", this.hasPlacedSpawner);
        }

        public static MonsterThrone createPiece(List<StructurePiece> pieces, int x, int y, int z, int genDepth, BlockFace orientation) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -2, 0, 0, 7, 8, 9, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new MonsterThrone(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 2, 0, 6, 7, 7, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 1, 0, 0, 5, 1, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 2, 1, 5, 2, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 3, 2, 5, 3, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 4, 3, 5, 4, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 2, 0, 1, 4, 2, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 2, 0, 5, 4, 2, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 5, 2, 1, 5, 3, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 5, 2, 5, 5, 3, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 5, 3, 0, 5, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 5, 3, 6, 5, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 5, 8, 5, 5, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.placeBlock(level, NETHER_BRICK_FENCE, 1, 6, 3, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 5, 6, 3, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 0, 6, 3, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 6, 6, 3, boundingBox);
            this.generateBox(level, boundingBox, 0, 6, 4, 0, 6, 7, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 6, 6, 4, 6, 6, 7, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.placeBlock(level, NETHER_BRICK_FENCE, 0, 6, 8, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 6, 6, 8, boundingBox);
            this.generateBox(level, boundingBox, 1, 6, 8, 5, 6, 8, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.placeBlock(level, NETHER_BRICK_FENCE, 1, 7, 8, boundingBox);
            this.generateBox(level, boundingBox, 2, 7, 8, 4, 7, 8, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.placeBlock(level, NETHER_BRICK_FENCE, 5, 7, 8, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 2, 8, 8, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 3, 8, 8, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 4, 8, 8, boundingBox);

            if (!this.hasPlacedSpawner) {
                BlockVector3 vec = new BlockVector3(this.getWorldX(3, 5), this.getWorldY(5), this.getWorldZ(3, 5));
                if (boundingBox.isInside(vec)) {
                    this.hasPlacedSpawner = true;
                    level.setBlockAt(vec.x, vec.y, vec.z, SPAWNER.getId(), SPAWNER.getMeta());

                    BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                    if (chunk != null) {
                        Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(),
                                BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.MOB_SPAWNER)
                                        .putInt("EntityId", EntityBlaze.NETWORK_ID)));
                    }
                }
            }

            for (int x = 0; x <= 6; ++x) {
                for (int z = 0; z <= 6; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class CastleEntrance extends NetherBridgePiece {

        public CastleEntrance(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public CastleEntrance(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBCastleEntrance::getType(void) // 1313031502i64
        public String getType() {
            return "NeCE";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildForward((StartPiece) piece, pieces, random, 5, 3, true);
        }

        public static CastleEntrance createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new CastleEntrance(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 3, 0, 12, 4, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 5, 0, 12, 13, 12, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 5, 0, 1, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 11, 5, 0, 12, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 11, 4, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 8, 5, 11, 10, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 9, 11, 7, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 0, 4, 12, 1, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 8, 5, 0, 10, 12, 1, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 9, 0, 7, 12, 1, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 11, 2, 10, 12, 10, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 8, 0, 7, 8, 0, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);

            for (int i = 1; i <= 11; i += 2) {
                this.generateBox(level, boundingBox, i, 10, 0, i, 11, 0, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.generateBox(level, boundingBox, i, 10, 12, i, 11, 12, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.generateBox(level, boundingBox, 0, 10, i, 0, 11, i, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.generateBox(level, boundingBox, 12, 10, i, 12, 11, i, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.placeBlock(level, NETHER_BRICKS, i, 13, 0, boundingBox);
                this.placeBlock(level, NETHER_BRICKS, i, 13, 12, boundingBox);
                this.placeBlock(level, NETHER_BRICKS, 0, 13, i, boundingBox);
                this.placeBlock(level, NETHER_BRICKS, 12, 13, i, boundingBox);

                if (i != 11) {
                    this.placeBlock(level, NETHER_BRICK_FENCE, i + 1, 13, 0, boundingBox);
                    this.placeBlock(level, NETHER_BRICK_FENCE, i + 1, 13, 12, boundingBox);
                    this.placeBlock(level, NETHER_BRICK_FENCE, 0, 13, i + 1, boundingBox);
                    this.placeBlock(level, NETHER_BRICK_FENCE, 12, 13, i + 1, boundingBox);
                }
            }

            this.placeBlock(level, NETHER_BRICK_FENCE, 0, 13, 0, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 0, 13, 12, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 12, 13, 12, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 12, 13, 0, boundingBox);

            for (int z = 3; z <= 9; z += 2) {
                this.generateBox(level, boundingBox, 1, 7, z, 1, 8, z, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.generateBox(level, boundingBox, 11, 7, z, 11, 8, z, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            }

            this.generateBox(level, boundingBox, 4, 2, 0, 8, 2, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 4, 12, 2, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 0, 0, 8, 1, 3, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 0, 9, 8, 1, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 0, 4, 3, 1, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 9, 0, 4, 12, 1, 8, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 4; x <= 8; ++x) {
                for (int l = 0; l <= 2; ++l) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, l, boundingBox);
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, 12 - l, boundingBox);
                }
            }

            for (int x = 0; x <= 2; ++x) {
                for (int n = 4; n <= 8; ++n) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, n, boundingBox);
                    this.fillColumnDown(level, NETHER_BRICKS, 12 - x, -1, n, boundingBox);
                }
            }

            this.generateBox(level, boundingBox, 5, 5, 5, 7, 5, 7, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 1, 6, 6, 4, 6, BlockState.AIR, BlockState.AIR, false);
            this.placeBlock(level, NETHER_BRICKS, 6, 0, 6, boundingBox);
            this.placeBlock(level, LAVA, 6, 5, 6, boundingBox);

            BlockVector3 vec = new BlockVector3(this.getWorldX(6, 6), this.getWorldY(5), this.getWorldZ(6, 6));
            if (boundingBox.isInside(vec)) {
                LiquidUpdater.lavaSpread(level, vec);
            }

            return true;
        }
    }

    public static class CastleStalkRoom extends NetherBridgePiece {

        public CastleStalkRoom(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public CastleStalkRoom(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBCastleStalkRoom::getType(void) // 1313035090i64
        public String getType() {
            return "NeCSR";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildForward((StartPiece) piece, pieces, random, 5, 3, true);
            this.generateChildForward((StartPiece) piece, pieces, random, 5, 11, true);
        }

        public static CastleStalkRoom createPiece(List<StructurePiece> pieces, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new CastleStalkRoom(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 3, 0, 12, 4, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 5, 0, 12, 13, 12, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 5, 0, 1, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 11, 5, 0, 12, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 11, 4, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 8, 5, 11, 10, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 9, 11, 7, 12, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 0, 4, 12, 1, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 8, 5, 0, 10, 12, 1, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 5, 9, 0, 7, 12, 1, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 11, 2, 10, 12, 10, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int i = 1; i <= 11; i += 2) {
                this.generateBox(level, boundingBox, i, 10, 0, i, 11, 0, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.generateBox(level, boundingBox, i, 10, 12, i, 11, 12, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.generateBox(level, boundingBox, 0, 10, i, 0, 11, i, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.generateBox(level, boundingBox, 12, 10, i, 12, 11, i, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.placeBlock(level, NETHER_BRICKS, i, 13, 0, boundingBox);
                this.placeBlock(level, NETHER_BRICKS, i, 13, 12, boundingBox);
                this.placeBlock(level, NETHER_BRICKS, 0, 13, i, boundingBox);
                this.placeBlock(level, NETHER_BRICKS, 12, 13, i, boundingBox);

                if (i != 11) {
                    this.placeBlock(level, NETHER_BRICK_FENCE, i + 1, 13, 0, boundingBox);
                    this.placeBlock(level, NETHER_BRICK_FENCE, i + 1, 13, 12, boundingBox);
                    this.placeBlock(level, NETHER_BRICK_FENCE, 0, 13, i + 1, boundingBox);
                    this.placeBlock(level, NETHER_BRICK_FENCE, 12, 13, i + 1, boundingBox);
                }
            }

            this.placeBlock(level, NETHER_BRICK_FENCE, 0, 13, 0, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 0, 13, 12, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 12, 13, 12, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 12, 13, 0, boundingBox);

            for (int z = 3; z <= 9; z += 2) {
                this.generateBox(level, boundingBox, 1, 7, z, 1, 8, z, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                this.generateBox(level, boundingBox, 11, 7, z, 11, 8, z, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            }

            BlockState statrsN = new BlockState(Block.NETHER_BRICKS_STAIRS, WeirdoDirection.NORTH);
            for (int y = 0; y <= 6; ++y) {
                int z = y + 4;

                for (int x = 5; x <= 7; ++x) {
                    this.placeBlock(level, statrsN, x, 5 + y, z, boundingBox);
                }

                if (z >= 5 && z <= 8) {
                    this.generateBox(level, boundingBox, 5, 5, z, 7, y + 4, z, NETHER_BRICKS, NETHER_BRICKS, false);
                } else if (z >= 9 && z <= 10) {
                    this.generateBox(level, boundingBox, 5, 8, z, 7, y + 4, z, NETHER_BRICKS, NETHER_BRICKS, false);
                }

                if (y >= 1) {
                    this.generateBox(level, boundingBox, 5, 6 + y, z, 7, 9 + y, z, BlockState.AIR, BlockState.AIR, false);
                }
            }

            for (int x = 5; x <= 7; ++x) {
                this.placeBlock(level, statrsN, x, 12, 11, boundingBox);
            }

            this.generateBox(level, boundingBox, 5, 6, 7, 5, 7, 7, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 7, 6, 7, 7, 7, 7, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 5, 13, 12, 7, 13, 12, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 2, 5, 2, 3, 5, 3, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 9, 3, 5, 10, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 2, 5, 4, 2, 5, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 9, 5, 2, 10, 5, 3, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 9, 5, 9, 10, 5, 10, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 10, 5, 4, 10, 5, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            BlockState statrsW = new BlockState(Block.NETHER_BRICKS_STAIRS, WeirdoDirection.WEST);
            this.placeBlock(level, statrsW, 4, 5, 2, boundingBox);
            this.placeBlock(level, statrsW, 4, 5, 3, boundingBox);
            this.placeBlock(level, statrsW, 4, 5, 9, boundingBox);
            this.placeBlock(level, statrsW, 4, 5, 10, boundingBox);
            BlockState statrsE = new BlockState(Block.NETHER_BRICKS_STAIRS, WeirdoDirection.EAST);
            this.placeBlock(level, statrsE, 8, 5, 2, boundingBox);
            this.placeBlock(level, statrsE, 8, 5, 3, boundingBox);
            this.placeBlock(level, statrsE, 8, 5, 9, boundingBox);
            this.placeBlock(level, statrsE, 8, 5, 10, boundingBox);
            this.generateBox(level, boundingBox, 3, 4, 4, 4, 4, 8, SOUL_SAND, SOUL_SAND, false);
            this.generateBox(level, boundingBox, 8, 4, 4, 9, 4, 8, SOUL_SAND, SOUL_SAND, false);
            this.generateBox(level, boundingBox, 3, 5, 4, 4, 5, 8, NETHER_WART, NETHER_WART, false);
            this.generateBox(level, boundingBox, 8, 5, 4, 9, 5, 8, NETHER_WART, NETHER_WART, false);
            this.generateBox(level, boundingBox, 4, 2, 0, 8, 2, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 4, 12, 2, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 0, 0, 8, 1, 3, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 0, 9, 8, 1, 12, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 0, 4, 3, 1, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 9, 0, 4, 12, 1, 8, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 4; x <= 8; ++x) {
                for (int z = 0; z <= 2; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, 12 - z, boundingBox);
                }
            }

            for (int x = 0; x <= 2; ++x) {
                for (int z = 4; z <= 8; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                    this.fillColumnDown(level, NETHER_BRICKS, 12 - x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class CastleSmallCorridorPiece extends NetherBridgePiece {

        public CastleSmallCorridorPiece(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public CastleSmallCorridorPiece(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBCastleSmallCorridorPiece::getType(void) // 1313035075i64
        public String getType() {
            return "NeSC";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildForward((StartPiece) piece, pieces, random, 1, 0, true);
        }

        public static CastleSmallCorridorPiece createPiece(List<StructurePiece> pieces, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new CastleSmallCorridorPiece(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 1, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 4, 5, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 5, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 2, 0, 4, 5, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 3, 1, 0, 4, 1, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 3, 3, 0, 4, 3, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 4, 3, 1, 4, 4, 1, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 4, 3, 3, 4, 4, 3, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 6, 0, 4, 6, 4, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 0; x <= 4; ++x) {
                for (int z = 0; z <= 4; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class CastleSmallCorridorCrossingPiece extends NetherBridgePiece {

        public CastleSmallCorridorCrossingPiece(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public CastleSmallCorridorCrossingPiece(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBCastleSmallCorridorCrossingPiece::getType(void) // 1313030979i64
        public String getType() {
            return "NeSCSC";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildForward((StartPiece) piece, pieces, random, 1, 0, true);
            this.generateChildLeft((StartPiece) piece, pieces, random, 0, 1, true);
            this.generateChildRight((StartPiece) piece, pieces, random, 0, 1, true);
        }

        public static CastleSmallCorridorCrossingPiece createPiece(List<StructurePiece> pieces, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new CastleSmallCorridorCrossingPiece(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 1, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 4, 5, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 5, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 2, 0, 4, 5, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 4, 0, 5, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 2, 4, 4, 5, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 6, 0, 4, 6, 4, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 0; x <= 4; ++x) {
                for (int z = 0; z <= 4; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class CastleSmallCorridorRightTurnPiece extends NetherBridgePiece {

        private boolean isNeedingChest;

        public CastleSmallCorridorRightTurnPiece(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.isNeedingChest = (random.nextBoundedInt(3) == 0);
        }

        public CastleSmallCorridorRightTurnPiece(CompoundTag tag) {
            super(tag);
            this.isNeedingChest = tag.getBoolean("Chest");
        }

        @Override //\\ NBCastleSmallCorridorRightTurnPiece::getType(void) // 1313034836i64
        public String getType() {
            return "NeSCRT";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Chest", this.isNeedingChest);
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildRight((StartPiece) piece, pieces, random, 0, 1, true);
        }

        public static CastleSmallCorridorRightTurnPiece createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new CastleSmallCorridorRightTurnPiece(genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 1, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 4, 5, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 5, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 3, 1, 0, 4, 1, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 3, 3, 0, 4, 3, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 4, 2, 0, 4, 5, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 2, 4, 4, 5, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 3, 4, 1, 4, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 3, 3, 4, 3, 4, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);

            if (this.isNeedingChest && boundingBox.isInside(new BlockVector3(this.getWorldX(1, 3), this.getWorldY(2), this.getWorldZ(1, 3)))) {
                this.isNeedingChest = false;

                BlockFace orientation = this.getOrientation();
                this.placeBlock(level, new BlockState(Block.CHEST, (orientation == null ? BlockFace.NORTH : orientation).getOpposite().getIndex()), 1, 2, 3, boundingBox);

                BlockVector3 vec = new BlockVector3(this.getWorldX(1, 3), this.getWorldY(2), this.getWorldZ(1, 3));
                if (boundingBox.isInside(vec)) {
                    BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                    if (chunk != null) {
                        CompoundTag nbt = BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.CHEST);
                        ListTag<CompoundTag> itemList = new ListTag<>("Items");
                        NetherBridgeChest.get().create(itemList, random);
                        nbt.putList(itemList);
                        Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                    }
                }
            }

            this.generateBox(level, boundingBox, 0, 6, 0, 4, 6, 4, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 0; x <= 4; ++x) {
                for (int z = 0; z <= 4; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class CastleSmallCorridorLeftTurnPiece extends NetherBridgePiece {

        private boolean isNeedingChest;

        public CastleSmallCorridorLeftTurnPiece(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.isNeedingChest = (random.nextBoundedInt(3) == 0);
        }

        public CastleSmallCorridorLeftTurnPiece(CompoundTag tag) {
            super(tag);
            this.isNeedingChest = tag.getBoolean("Chest");
        }

        @Override //\\ NBCastleSmallCorridorLeftTurnPiece::getType(void) // 1313033300i64
        public String getType() {
            return "NeSCLT";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Chest", this.isNeedingChest);
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildLeft((StartPiece) piece, pieces, random, 0, 1, true);
        }

        public static CastleSmallCorridorLeftTurnPiece createPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
            if (!NetherBridgePiece.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(pieces, boundingBox) != null) {
                return null;
            }
            return new CastleSmallCorridorLeftTurnPiece(genDepth, random, boundingBox, orientation);
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 4, 1, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 4, 5, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 4, 2, 0, 4, 5, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 4, 3, 1, 4, 4, 1, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 4, 3, 3, 4, 4, 3, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 5, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 4, 3, 5, 4, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 3, 4, 1, 4, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 3, 3, 4, 3, 4, 4, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);

            if (this.isNeedingChest && boundingBox.isInside(new BlockVector3(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
                this.isNeedingChest = false;

                BlockFace orientation = this.getOrientation();
                this.placeBlock(level, new BlockState(Block.CHEST, (orientation == null ? BlockFace.NORTH : orientation).getOpposite().getIndex()), 3, 2, 3, boundingBox);

                BlockVector3 vec = new BlockVector3(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3));
                if (boundingBox.isInside(vec)) {
                    BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                    if (chunk != null) {
                        CompoundTag nbt = BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.CHEST);
                        ListTag<CompoundTag> itemList = new ListTag<>("Items");
                        NetherBridgeChest.get().create(itemList, random);
                        nbt.putList(itemList);
                        Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                    }
                }
            }

            this.generateBox(level, boundingBox, 0, 6, 0, 4, 6, 4, NETHER_BRICKS, NETHER_BRICKS, false);

            for (int x = 0; x <= 4; ++x) {
                for (int z = 0; z <= 4; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class CastleCorridorStairsPiece extends NetherBridgePiece {

        public CastleCorridorStairsPiece(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public CastleCorridorStairsPiece(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBCastleCorridorStairsPiece::getType(void) // 1313035092i64
        public String getType() {
            return "NeCCS";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            this.generateChildForward((StartPiece) piece, pieces, random, 1, 0, true);
        }

        public static CastleCorridorStairsPiece createPiece(List<StructurePiece> pieces, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -1, -7, 0, 5, 14, 10, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new CastleCorridorStairsPiece(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            BlockState stairsS = new BlockState(Block.NETHER_BRICKS_STAIRS, WeirdoDirection.SOUTH);
            for (int i = 0; i <= 9; ++i) {
                int maxY = Math.max(1, 7 - i);
                int miny = Math.min(Math.max(maxY + 5, 14 - i), 13);

                this.generateBox(level, boundingBox, 0, 0, i, 4, maxY, i, NETHER_BRICKS, NETHER_BRICKS, false);
                this.generateBox(level, boundingBox, 1, maxY + 1, i, 3, miny - 1, i, BlockState.AIR, BlockState.AIR, false);

                if (i <= 6) {
                    this.placeBlock(level, stairsS, 1, maxY + 1, i, boundingBox);
                    this.placeBlock(level, stairsS, 2, maxY + 1, i, boundingBox);
                    this.placeBlock(level, stairsS, 3, maxY + 1, i, boundingBox);
                }

                this.generateBox(level, boundingBox, 0, miny, i, 4, miny, i, NETHER_BRICKS, NETHER_BRICKS, false);
                this.generateBox(level, boundingBox, 0, maxY + 1, i, 0, miny - 1, i, NETHER_BRICKS, NETHER_BRICKS, false);
                this.generateBox(level, boundingBox, 4, maxY + 1, i, 4, miny - 1, i, NETHER_BRICKS, NETHER_BRICKS, false);

                if ((i & 0x1) == 0x0) {
                    this.generateBox(level, boundingBox, 0, maxY + 2, i, 0, maxY + 3, i, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                    this.generateBox(level, boundingBox, 4, maxY + 2, i, 4, maxY + 3, i, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
                }

                for (int x = 0; x <= 4; ++x) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, i, boundingBox);
                }
            }
            return true;
        }
    }

    public static class CastleCorridorTBalconyPiece extends NetherBridgePiece {

        public CastleCorridorTBalconyPiece(int genDepth, BoundingBox boundingBox, BlockFace orientation) {
            super(genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public CastleCorridorTBalconyPiece(CompoundTag tag) {
            super(tag);
        }

        @Override //\\ NBCastleCorridorTBalconyPiece::getType(void) // 1313030721i64
        public String getType() {
            return "NeCTB";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            int horizontalOffset = 1;
            BlockFace orientation = this.getOrientation();
            if (orientation == BlockFace.WEST || orientation == BlockFace.NORTH) {
                horizontalOffset = 5;
            }

            this.generateChildLeft((StartPiece) piece, pieces, random, 0, horizontalOffset, random.nextBoundedInt(8) > 0);
            this.generateChildRight((StartPiece) piece, pieces, random, 0, horizontalOffset, random.nextBoundedInt(8) > 0);
        }

        public static CastleCorridorTBalconyPiece createPiece(List<StructurePiece> pieces, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, -3, 0, 0, 9, 7, 9, orientation);
            return NetherBridgePiece.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ?
                    new CastleCorridorTBalconyPiece(genDepth, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            this.generateBox(level, boundingBox, 0, 0, 0, 8, 1, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 8, 5, 8, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 6, 0, 8, 6, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 2, 5, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 2, 0, 8, 5, 0, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 3, 0, 1, 4, 0, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 7, 3, 0, 7, 4, 0, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 2, 4, 8, 2, 8, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 1, 4, 2, 2, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 6, 1, 4, 7, 2, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 1, 3, 8, 7, 3, 8, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.placeBlock(level, NETHER_BRICK_FENCE, 0, 3, 8, boundingBox);
            this.placeBlock(level, NETHER_BRICK_FENCE, 8, 3, 8, boundingBox);
            this.generateBox(level, boundingBox, 0, 3, 6, 0, 3, 7, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 8, 3, 6, 8, 3, 7, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 0, 3, 4, 0, 5, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 8, 3, 4, 8, 5, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 3, 5, 2, 5, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 6, 3, 5, 7, 5, 5, NETHER_BRICKS, NETHER_BRICKS, false);
            this.generateBox(level, boundingBox, 1, 4, 5, 1, 5, 5, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);
            this.generateBox(level, boundingBox, 7, 4, 5, 7, 5, 5, NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, false);

            for (int x = 0; x <= 8; ++x) {
                for (int z = 0; z <= 5; ++z) {
                    this.fillColumnDown(level, NETHER_BRICKS, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }
}
