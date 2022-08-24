package cn.nukkit.level.generator.populator.impl.structure.mineshaft.structure;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockPlanks;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityCaveSpider;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.mineshaft.PopulatorMineshaft;
import cn.nukkit.level.generator.populator.impl.structure.mineshaft.loot.MineshaftChest;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.BlockState;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.RailDirection;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.TorchFacingDirection;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.BoundingBox;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructurePiece;
import cn.nukkit.level.generator.task.ActorSpawnTask;
import cn.nukkit.level.generator.task.BlockActorSpawnTask;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntArrayTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class MineshaftPieces {

    private static final BlockState OAK_PLANKS = new BlockState(Block.PLANKS, BlockPlanks.OAK);
    private static final BlockState DARK_OAK_PLANKS = new BlockState(Block.PLANKS, BlockPlanks.DARK_OAK);
    private static final BlockState OAK_FENCE = new BlockState(Block.FENCE, BlockFence.FENCE_OAK);
    private static final BlockState DARK_OAK_FENCE = new BlockState(Block.FENCE, BlockFence.FENCE_DARK_OAK);
    private static final BlockState COBWEB = new BlockState(Block.COBWEB);
    private static final BlockState DIRT = new BlockState(Block.DIRT);
    private static final BlockState SPAWNER = new BlockState(Block.MONSTER_SPAWNER);
    private static final BlockState TORCH__N = new BlockState(Block.TORCH, TorchFacingDirection.NORTH);
    private static final BlockState TORCH__S = new BlockState(Block.TORCH, TorchFacingDirection.SOUTH);
    private static final BlockState RAIL__NS = new BlockState(Block.RAIL, RailDirection.NORTH_SOUTH);
    private static final BlockState RAIL__EW = new BlockState(Block.RAIL, RailDirection.EAST_WEST);

    @Nullable //\\ MineshaftPiece::createRandomShaftPiece(MineshaftData &,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static MineshaftPiece createRandomShaftPiece(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, @Nullable BlockFace orientation, int genDepth, PopulatorMineshaft.Type type) {
        int chance = random.nextBoundedInt(100);
        if (chance >= 80) {
            BoundingBox boundingBox = MineshaftCrossing.findCrossing(pieces, random, x, y, z, orientation);
            if (boundingBox != null) {
                return new MineshaftCrossing(genDepth, boundingBox, orientation, type);
            }
        } else if (chance >= 70) {
            BoundingBox boundingBox = MineshaftStairs.findStairs(pieces, random, x, y, z, orientation);
            if (boundingBox != null) {
                return new MineshaftStairs(genDepth, boundingBox, orientation, type);
            }
        } else {
            BoundingBox boundingBox = MineshaftCorridor.findCorridorSize(pieces, random, x, y, z, orientation);
            if (boundingBox != null) {
                return new MineshaftCorridor(genDepth, random, boundingBox, orientation, type);
            }
        }
        return null;
    }

    @Nullable //\\ MineshaftPiece::generateAndAddPiece(StructurePiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static MineshaftPiece generateAndAddPiece(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
        if (genDepth <= 8 && Math.abs(x - piece.getBoundingBox().x0) <= 80 && Math.abs(z - piece.getBoundingBox().z0) <= 80) {
            MineshaftPiece result = createRandomShaftPiece(pieces, random, x, y, z, orientation, genDepth + 1, ((MineshaftPiece) piece).type);
            if (result != null) {
                pieces.add(result);
                result.addChildren(piece, pieces, random);
            }
            return result;
        }
        return null;
    }

    abstract static class MineshaftPiece extends StructurePiece {

        protected PopulatorMineshaft.Type type;

        public MineshaftPiece(int genDepth, PopulatorMineshaft.Type type) {
            super(genDepth);
            this.type = type;
        }

        public MineshaftPiece(CompoundTag tag) {
            super(tag);
            this.type = PopulatorMineshaft.Type.byId(tag.getInt("MST"));
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            tag.putInt("MST", this.type.ordinal());
        }

        protected BlockState getPlanksBlock() {
            switch (this.type) {
                case NORMAL:
                default:
                    return OAK_PLANKS;
                case MESA:
                    return DARK_OAK_PLANKS;
            }
        }

        protected BlockState getFenceBlock() {
            switch (this.type) {
                case NORMAL:
                default:
                    return OAK_FENCE;
                case MESA:
                    return DARK_OAK_FENCE;
            }
        }

        //\\ MineshaftPiece::_isSupportingBox(int,int,BlockSource *,int,int)
        protected boolean isSupportingBox(ChunkManager level, BoundingBox boundingBox, int x0, int x1, int y, int z) {
            for (int x = x0; x <= x1; ++x) {
                if (this.getBlock(level, x, y + 1, z, boundingBox).equals(BlockState.AIR)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class MineshaftRoom extends MineshaftPiece {

        private final List<BoundingBox> childEntranceBoxes = Lists.newLinkedList();

        //\\ MineshaftStart::MineshaftStart(BiomeSource &,Random &,ChunkPos const &,short)
        public MineshaftRoom(int genDepth, NukkitRandom random, int x, int z, PopulatorMineshaft.Type type) {
            super(genDepth, type);
            this.type = type;
            this.boundingBox = new BoundingBox(x, 50, z, x + 7 + random.nextBoundedInt(6), 54 + random.nextBoundedInt(6), z + 7 + random.nextBoundedInt(6));
        }

        public MineshaftRoom(CompoundTag tag) {
            super(tag);
            tag.getList("Entrances", IntArrayTag.class).getAll().forEach(arrayTag -> this.childEntranceBoxes.add(new BoundingBox(arrayTag.data)));
        }

        @Override //\\ MineshaftRoom::getType() // 1297306189i64;
        public String getType() {
            return "MSRoom";
        }

        @Override //\\ MineshaftRoom::addChildren(StructurePiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &)
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            int genDepth = this.getGenDepth();
            int yOffset = this.boundingBox.getYSpan() - 3 - 1;
            if (yOffset <= 0) {
                yOffset = 1;
            }

            for (int x = 0; x < this.boundingBox.getXSpan(); x += 4) {
                x += random.nextBoundedInt(this.boundingBox.getXSpan());
                if (x + 3 > this.boundingBox.getXSpan()) {
                    break;
                }

                MineshaftPiece next = generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + x, this.boundingBox.y0 + random.nextBoundedInt(yOffset) + 1, this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth);
                if (next != null) {
                    BoundingBox boundingBox = next.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(boundingBox.x0, boundingBox.y0, this.boundingBox.z0, boundingBox.x1, boundingBox.y1, this.boundingBox.z0 + 1));
                }
            }

            for (int x = 0; x < this.boundingBox.getXSpan(); x += 4) {
                x += random.nextBoundedInt(this.boundingBox.getXSpan());
                if (x + 3 > this.boundingBox.getXSpan()) {
                    break;
                }

                MineshaftPiece next = generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + x, this.boundingBox.y0 + random.nextBoundedInt(yOffset) + 1, this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth);
                if (next != null) {
                    BoundingBox boundingBox = next.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(boundingBox.x0, boundingBox.y0, this.boundingBox.z1 - 1, boundingBox.x1, boundingBox.y1, this.boundingBox.z1));
                }
            }

            for (int z = 0; z < this.boundingBox.getZSpan(); z += 4) {
                z += random.nextBoundedInt(this.boundingBox.getZSpan());
                if (z + 3 > this.boundingBox.getZSpan()) {
                    break;
                }

                MineshaftPiece next = generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + random.nextBoundedInt(yOffset) + 1, this.boundingBox.z0 + z, BlockFace.WEST, genDepth);
                if (next != null) {
                    BoundingBox boundingBox = next.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x0, boundingBox.y0, boundingBox.z0, this.boundingBox.x0 + 1, boundingBox.y1, boundingBox.z1));
                }
            }

            for (int z = 0; z < this.boundingBox.getZSpan(); z += 4) {
                z += random.nextBoundedInt(this.boundingBox.getZSpan());
                if (z + 3 > this.boundingBox.getZSpan()) {
                    break;
                }

                MineshaftPiece next = generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + random.nextBoundedInt(yOffset) + 1, this.boundingBox.z0 + z, BlockFace.EAST, genDepth);
                if (next != null) {
                    BoundingBox boundingBox = next.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x1 - 1, boundingBox.y0, boundingBox.z0, this.boundingBox.x1, boundingBox.y1, boundingBox.z1));
                }
            }
        }

        @Override //\\ MineshaftRoom::postProcess(BlockSource *,Random &,BoundingBox const &)
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.edgesLiquid(level, boundingBox)) {
                return false;
            }

            this.generateBox(level, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y0, this.boundingBox.z1, DIRT, BlockState.AIR, true);
            this.generateBox(level, boundingBox, this.boundingBox.x0, this.boundingBox.y0 + 1, this.boundingBox.z0, this.boundingBox.x1, Math.min(this.boundingBox.y0 + 3, this.boundingBox.y1), this.boundingBox.z1, BlockState.AIR, BlockState.AIR, false);

            for (BoundingBox childEntranceBox : this.childEntranceBoxes) {
                this.generateBox(level, boundingBox, childEntranceBox.x0, childEntranceBox.y1 - 2, childEntranceBox.z0, childEntranceBox.x1, childEntranceBox.y1, childEntranceBox.z1, BlockState.AIR, BlockState.AIR, false);
            }

            this.generateUpperHalfSphere(level, boundingBox, this.boundingBox.x0, this.boundingBox.y0 + 4, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1, BlockState.AIR, false);
            return true;
        }

        public void move(int x, int y, int z) {
            super.move(x, y, z);
            for (BoundingBox childEntranceBox : this.childEntranceBoxes) {
                childEntranceBox.move(x, y, z);
            }
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            ListTag<IntArrayTag> entrances = new ListTag<>();
            for (BoundingBox childEntranceBox : this.childEntranceBoxes) {
                entrances.add(childEntranceBox.createTag());
            }
            tag.put("Entrances", entrances);
        }
    }

    public static class MineshaftCorridor extends MineshaftPiece {

        private final boolean hasRails;
        private final boolean spiderCorridor;
        private boolean hasPlacedSpider;
        private final int numSections;

        public MineshaftCorridor(int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation, PopulatorMineshaft.Type type) {
            super(genDepth, type);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.hasRails = random.nextBoundedInt(3) == 0;
            this.spiderCorridor = !this.hasRails && random.nextBoundedInt(23) == 0;
            if (this.getOrientation() == null || this.getOrientation().getAxis() == BlockFace.Axis.Z) {
                this.numSections = boundingBox.getZSpan() / 5;
            } else {
                this.numSections = boundingBox.getXSpan() / 5;
            }
        }

        public MineshaftCorridor(CompoundTag tag) {
            super(tag);
            this.hasRails = tag.getBoolean("hr");
            this.spiderCorridor = tag.getBoolean("sc");
            this.hasPlacedSpider = tag.getBoolean("hps");
            this.numSections = tag.getInt("Num");
        }

        @Override //\\ MineshaftCorridor::getType() // 1297302351i64;
        public String getType() {
            return "MSCorridor";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("hr", this.hasRails);
            tag.putBoolean("sc", this.spiderCorridor);
            tag.putBoolean("hps", this.hasPlacedSpider);
            tag.putInt("Num", this.numSections);
        }

        @Nullable //\\ MineshaftCorridor::findCorridorSize(std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int)
        public static BoundingBox findCorridorSize(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation) {
            BoundingBox boundingBox = new BoundingBox(x, y, z, x, y + 3 - 1, z);

            int count = random.nextBoundedInt(3) + 2;
            for (int i; count > 0; --count) {
                i = count * 5;

                switch (orientation) {
                    case NORTH:
                    default:
                        boundingBox.x1 = x + 3 - 1;
                        boundingBox.z0 = z - (i - 1);
                        break;
                    case SOUTH:
                        boundingBox.x1 = x + 3 - 1;
                        boundingBox.z1 = z + i - 1;
                        break;
                    case WEST:
                        boundingBox.x0 = x - (i - 1);
                        boundingBox.z1 = z + 3 - 1;
                        break;
                    case EAST:
                        boundingBox.x1 = x + i - 1;
                        boundingBox.z1 = z + 3 - 1;
                        break;
                }

                if (StructurePiece.findCollisionPiece(pieces, boundingBox) == null) {
                    break;
                }
            }

            return count > 0 ? boundingBox : null;
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            int genDepth = this.getGenDepth();
            int target = random.nextBoundedInt(4);
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    default:
                        if (target <= 1) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z0 - 1, orientation, genDepth);
                        } else if (target == 2) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z0, BlockFace.WEST, genDepth);
                        } else {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z0, BlockFace.EAST, genDepth);
                        }
                        break;
                    case SOUTH:
                        if (target <= 1) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z1 + 1, orientation, genDepth);
                        } else if (target == 2) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z1 - 3, BlockFace.WEST, genDepth);
                        } else {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z1 - 3, BlockFace.EAST, genDepth);
                        }
                        break;
                    case WEST:
                        if (target <= 1) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z0, orientation, genDepth);
                        } else if (target == 2) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth);
                        } else {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth);
                        }
                        break;
                    case EAST:
                        if (target <= 1) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z0, orientation, genDepth);
                        } else if (target == 2) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth);
                        } else {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + random.nextBoundedInt(3), this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth);
                        }
                }
            }

            if (genDepth < 8) {
                if (orientation != BlockFace.NORTH && orientation != BlockFace.SOUTH) {
                    for (int x = this.boundingBox.x0 + 3; x + 3 <= this.boundingBox.x1; x += 5) {
                        int type = random.nextBoundedInt(5);
                        if (type == 0) {
                            generateAndAddPiece(piece, pieces, random, x, this.boundingBox.y0, this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth + 1);
                        } else if (type == 1) {
                            generateAndAddPiece(piece, pieces, random, x, this.boundingBox.y0, this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth + 1);
                        }
                    }
                } else {
                    for (int z = this.boundingBox.z0 + 3; z + 3 <= this.boundingBox.z1; z += 5) {
                        int type = random.nextBoundedInt(5);
                        if (type == 0) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0, z, BlockFace.WEST, genDepth + 1);
                        } else if (type == 1) {
                            generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0, z, BlockFace.EAST, genDepth + 1);
                        }
                    }
                }
            }
        }

        protected boolean createChest(ChunkManager level, BoundingBox boundingBox, NukkitRandom random, int x, int y, int z) {
            BlockVector3 vec = new BlockVector3(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));

            if (boundingBox.isInside(vec) && level.getBlockIdAt(vec.x, vec.y, vec.z) == Block.AIR && level.getBlockIdAt(vec.x, vec.y - 1, vec.z) != Block.AIR) {
                this.placeBlock(level, random.nextBoolean() ? RAIL__NS : RAIL__EW, x, y, z, boundingBox);

                //\\ MineshaftCorridor::postProcessMobsAt(BlockSource *,Random &,BoundingBox const &)
                BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                if (chunk != null) {
                    CompoundTag nbt = Entity.getDefaultNBT(vec.asVector3().add(.5, 0.5, .5))
                            .putString("id", "MinecartChest");

                    ListTag<CompoundTag> itemList = new ListTag<>("Items");
                    MineshaftChest.get().create(itemList, random);
                    nbt.putList(itemList);

                    Server.getInstance().getScheduler().scheduleTask(new ActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                }

                return true;
            }

            return false;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.edgesLiquid(level, boundingBox)) {
                return false;
            }

            int z1 = this.numSections * 5 - 1;
            this.generateBox(level, boundingBox, 0, 0, 0, 2, 1, z1, BlockState.AIR, BlockState.AIR, false);
            this.generateMaybeBox(level, boundingBox, random, 50, 0, 2, 0, 2, 2, z1, BlockState.AIR, BlockState.AIR, false, false);
            if (this.spiderCorridor) {
                this.generateMaybeBox(level, boundingBox, random, 60, 0, 0, 0, 2, 1, z1, COBWEB, BlockState.AIR, false, true);
            }

            for (int i = 0; i < this.numSections; ++i) {
                int z = 2 + i * 5;

                this.placeSupport(level, boundingBox, 0, 0, z, 2, 2, random);
                this.placeCobWeb(level, boundingBox, random, 10, 0, 2, z - 1);
                this.placeCobWeb(level, boundingBox, random, 10, 2, 2, z - 1);
                this.placeCobWeb(level, boundingBox, random, 10, 0, 2, z + 1);
                this.placeCobWeb(level, boundingBox, random, 10, 2, 2, z + 1);
                this.placeCobWeb(level, boundingBox, random, 5, 0, 2, z - 2);
                this.placeCobWeb(level, boundingBox, random, 5, 2, 2, z - 2);
                this.placeCobWeb(level, boundingBox, random, 5, 0, 2, z + 2);
                this.placeCobWeb(level, boundingBox, random, 5, 2, 2, z + 2);

                if (random.nextBoundedInt(100) == 0) {
                    this.createChest(level, boundingBox, random, 2, 0, z - 1);
                }
                if (random.nextBoundedInt(100) == 0) {
                    this.createChest(level, boundingBox, random, 0, 0, z + 1);
                }

                if (this.spiderCorridor && !this.hasPlacedSpider) {
                    int pz = z - 1 + random.nextBoundedInt(3);
                    int worldX = this.getWorldX(1, pz);
                    int worldZ = this.getWorldZ(1, pz);
                    BlockVector3 vec = new BlockVector3(worldX, this.getWorldY(0), worldZ);

                    if (boundingBox.isInside(vec) && this.isInterior(level, 1, 0, pz, boundingBox)) {
                        this.hasPlacedSpider = true;
                        level.setBlockAt(vec.x, vec.y, vec.z, SPAWNER.getId(), SPAWNER.getMeta());

                        BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                        if (chunk != null) {
                            Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(),
                                    BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.MOB_SPAWNER)
                                            .putInt("EntityId", EntityCaveSpider.NETWORK_ID)));
                        }
                    }
                }
            }

            BlockState planks = this.getPlanksBlock();
            for (int x = 0; x <= 2; ++x) {
                for (int z = 0; z <= z1; ++z) {
                    BlockState block = this.getBlock(level, x, -1, z, boundingBox);
                    if (block.equals(BlockState.AIR) && this.isInterior(level, x, -1, z, boundingBox)) {
                        this.placeBlock(level, planks, x, -1, z, boundingBox);
                    }
                }
            }

            if (this.hasRails) {
                for (int z = 0; z <= z1; ++z) {
                    BlockState block = this.getBlock(level, 1, -1, z, boundingBox);
                    int id = level.getBlockIdAt(this.getWorldX(1, z), this.getWorldY(-1), this.getWorldZ(1, z));
                    if (!block.equals(BlockState.AIR) && Block.solid[id] && !Block.transparent[id]) {
                        this.maybeGenerateBlock(level, boundingBox, random, this.isInterior(level, 1, 0, z, boundingBox) ? 70 : 90, 1, 0, z, RAIL__NS);
                    }
                }
            }

            return true;
        }

        //\\ MineshaftCorridor::_placeSupport(BlockSource *,BoundingBox const &,int,int,int,int,int,Random &)
        private void placeSupport(ChunkManager level, BoundingBox boundingBox, int x0, int y0, int z, int y1, int x1, NukkitRandom random) {
            if (this.isSupportingBox(level, boundingBox, x0, x1, y1, z)) {
                BlockState fence = this.getFenceBlock();
                this.generateBox(level, boundingBox, x0, y0, z, x0, y1 - 1, z, fence, BlockState.AIR, false);
                this.generateBox(level, boundingBox, x1, y0, z, x1, y1 - 1, z, fence, BlockState.AIR, false);

                BlockState planks = this.getPlanksBlock();
                if (random.nextBoundedInt(4) == 0) {
                    this.generateBox(level, boundingBox, x0, y1, z, x0, y1, z, planks, BlockState.AIR, false);
                    this.generateBox(level, boundingBox, x1, y1, z, x1, y1, z, planks, BlockState.AIR, false);
                } else {
                    this.generateBox(level, boundingBox, x0, y1, z, x1, y1, z, planks, BlockState.AIR, false);
                    this.maybeGenerateBlock(level, boundingBox, random, 5, x0 + 1, y1, z - 1, TORCH__S);
                    this.maybeGenerateBlock(level, boundingBox, random, 5, x0 + 1, y1, z + 1, TORCH__N);
                }
            }
        }

        //\\ MineshaftCorridor::_placeCobWeb(BlockSource *,BoundingBox const &,Random &,float,int,int,int)
        private void placeCobWeb(ChunkManager level, BoundingBox boundingBox, NukkitRandom random, int probability, int x, int y, int z) {
            if (this.isInterior(level, x, y, z, boundingBox)) {
                this.maybeGenerateBlock(level, boundingBox, random, probability, x, y, z, COBWEB);
            }
        }
    }

    public static class MineshaftCrossing extends MineshaftPiece {

        private final BlockFace direction;
        private final boolean isTwoFloored;

        public MineshaftCrossing(int genDepth, BoundingBox boundingBox, @Nullable BlockFace orientation, PopulatorMineshaft.Type type) {
            super(genDepth, type);
            this.direction = orientation;
            this.boundingBox = boundingBox;
            this.isTwoFloored = boundingBox.getYSpan() > 3;
        }

        public MineshaftCrossing(CompoundTag tag) {
            super(tag);
            this.isTwoFloored = tag.getBoolean("tf");
            this.direction = BlockFace.fromHorizontalIndex(tag.getInt("D"));
        }

        @Override //\\ MineshaftCrossing::getType() // 1297302354i64;
        public String getType() {
            return "MSCrossing";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("tf", this.isTwoFloored);
            tag.putInt("D", this.direction.getHorizontalIndex());
        }

        @Nullable
        public static BoundingBox findCrossing(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation) {
            BoundingBox boundingBox = new BoundingBox(x, y, z, x, y + 3 - 1, z);
            if (random.nextBoundedInt(4) == 0) {
                boundingBox.y1 += 4;
            }

            switch (orientation) {
                case NORTH:
                default:
                    boundingBox.x0 = x - 1;
                    boundingBox.x1 = x + 3;
                    boundingBox.z0 = z - 4;
                    break;
                case SOUTH:
                    boundingBox.x0 = x - 1;
                    boundingBox.x1 = x + 3;
                    boundingBox.z1 = z + 3 + 1;
                    break;
                case WEST:
                    boundingBox.x0 = x - 4;
                    boundingBox.z0 = z - 1;
                    boundingBox.z1 = z + 3;
                    break;
                case EAST:
                    boundingBox.x1 = x + 3 + 1;
                    boundingBox.z0 = z - 1;
                    boundingBox.z1 = z + 3;
            }

            return StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? boundingBox : null;
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            int genDepth = this.getGenDepth();
            switch (this.direction) {
                case NORTH:
                default:
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth);
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, BlockFace.WEST, genDepth);
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, BlockFace.EAST, genDepth);
                    break;
                case SOUTH:
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth);
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, BlockFace.WEST, genDepth);
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, BlockFace.EAST, genDepth);
                    break;
                case WEST:
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth);
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth);
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, BlockFace.WEST, genDepth);
                    break;
                case EAST:
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth);
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth);
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, BlockFace.EAST, genDepth);
            }

            if (this.isTwoFloored) {
                if (random.nextBoolean()) {
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth);
                }
                if (random.nextBoolean()) {
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, BlockFace.WEST, genDepth);
                }
                if (random.nextBoolean()) {
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, BlockFace.EAST, genDepth);
                }
                if (random.nextBoolean()) {
                    generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth);
                }
            }
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.edgesLiquid(level, boundingBox)) {
                return false;
            }

            if (this.isTwoFloored) {
                this.generateBox(level, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1, BlockState.AIR, BlockState.AIR, false);
                this.generateBox(level, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1 - 1, BlockState.AIR, BlockState.AIR, false);
                this.generateBox(level, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y1 - 2, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, BlockState.AIR, BlockState.AIR, false);
                this.generateBox(level, boundingBox, this.boundingBox.x0, this.boundingBox.y1 - 2, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, BlockState.AIR, BlockState.AIR, false);
                this.generateBox(level, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3, this.boundingBox.z0 + 1, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3, this.boundingBox.z1 - 1, BlockState.AIR, BlockState.AIR, false);
            } else {
                this.generateBox(level, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, BlockState.AIR, BlockState.AIR, false);
                this.generateBox(level, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, BlockState.AIR, BlockState.AIR, false);
            }

            this.placeSupportPillar(level, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(level, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);
            this.placeSupportPillar(level, boundingBox, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(level, boundingBox, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);

            BlockState planks = this.getPlanksBlock();
            for (int x = this.boundingBox.x0; x <= this.boundingBox.x1; ++x) {
                for (int z = this.boundingBox.z0; z <= this.boundingBox.z1; ++z) {
                    if (this.getBlock(level, x, this.boundingBox.y0 - 1, z, boundingBox).equals(BlockState.AIR) && this.isInterior(level, x, this.boundingBox.y0 - 1, z, boundingBox)) {
                        this.placeBlock(level, planks, x, this.boundingBox.y0 - 1, z, boundingBox);
                    }
                }
            }

            return true;
        }

        //\\ MineshaftCrossing::_placeSupportPillar(BlockSource *,BoundingBox const &,int,int,int,int)
        private void placeSupportPillar(ChunkManager level, BoundingBox boundingBox, int x, int y0, int z, int y1) {
            if (!this.getBlock(level, x, y1 + 1, z, boundingBox).equals(BlockState.AIR)) {
                this.generateBox(level, boundingBox, x, y0, z, x, y1, z, this.getPlanksBlock(), BlockState.AIR, false);
            }
        }
    }

    public static class MineshaftStairs extends MineshaftPiece {

        public MineshaftStairs(int genDepth, BoundingBox boundingBox, BlockFace orientation, PopulatorMineshaft.Type type) {
            super(genDepth, type);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public MineshaftStairs(CompoundTag tag) {
            super(tag);
        }

        @Override//\\ MineshaftStairs::getType() // 1297306452i64;
        public String getType() {
            return "MSStairs";
        }

        @Nullable
        public static BoundingBox findStairs(List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation) {
            BoundingBox boundingBox = new BoundingBox(x, y - 5, z, x, y + 3 - 1, z);
            switch (orientation) {
                case NORTH:
                default:
                    boundingBox.x1 = x + 3 - 1;
                    boundingBox.z0 = z - 8;
                    break;
                case SOUTH:
                    boundingBox.x1 = x + 3 - 1;
                    boundingBox.z1 = z + 8;
                    break;
                case WEST:
                    boundingBox.x0 = x - 8;
                    boundingBox.z1 = z + 3 - 1;
                    break;
                case EAST:
                    boundingBox.x1 = x + 8;
                    boundingBox.z1 = z + 3 - 1;
            }

            return StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? boundingBox : null;
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            int genDepth = this.getGenDepth();
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    default:
                        generateAndAddPiece(piece, pieces, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 - 1, BlockFace.NORTH, genDepth);
                        break;
                    case SOUTH:
                        generateAndAddPiece(piece, pieces, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z1 + 1, BlockFace.SOUTH, genDepth);
                        break;
                    case WEST:
                        generateAndAddPiece(piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0, BlockFace.WEST, genDepth);
                        break;
                    case EAST:
                        generateAndAddPiece(piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0, BlockFace.EAST, genDepth);
                }
            }
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.edgesLiquid(level, boundingBox)) {
                return false;
            }

            this.generateBox(level, boundingBox, 0, 5, 0, 2, 7, 1, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 0, 7, 2, 2, 8, BlockState.AIR, BlockState.AIR, false);

            for (int i = 0; i < 5; ++i) {
                this.generateBox(level, boundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, BlockState.AIR, BlockState.AIR, false);
            }

            return true;
        }
    }

    public static void init() {
        //NOOP
    }
}
