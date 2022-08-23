package cn.nukkit.level.generator.populator.impl.structure.village.structure;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityVillagerV1;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.BlockState;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.FacingDirection;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.TorchFacingDirection;
import cn.nukkit.level.generator.populator.impl.structure.utils.block.state.WeirdoDirection;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.BoundingBox;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.Mth;
import cn.nukkit.level.generator.populator.impl.structure.utils.structure.StructurePiece;
import cn.nukkit.level.generator.populator.impl.structure.village.loot.VillageBlacksmithChest;
import cn.nukkit.level.generator.populator.impl.structure.village.loot.VillageTwoRoomHouseChest;
import cn.nukkit.level.generator.populator.impl.structure.village.populator.PopulatorVillage;
import cn.nukkit.level.generator.task.ActorSpawnTask;
import cn.nukkit.level.generator.task.BlockActorSpawnTask;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.19.21-r6")
public class VillagePieces { //TODO: mossyStoneSelector (zombie village)

    private static final BlockState PLANKS = new BlockState(Block.PLANKS, BlockPlanks.OAK);
    private static final BlockState SPRUCE_PLANKS = new BlockState(Block.PLANKS, BlockPlanks.SPRUCE);
    private static final BlockState ACACIA_PLANKS = new BlockState(Block.PLANKS, BlockPlanks.ACACIA);
    private static final BlockState OAK_FENCE = new BlockState(Block.FENCE, BlockFence.FENCE_OAK);
    private static final BlockState SPRUCE_FENCE = new BlockState(Block.FENCE, BlockFence.FENCE_SPRUCE);
    private static final BlockState ACACIA_FENCE = new BlockState(Block.FENCE, BlockFence.FENCE_ACACIA);
    private static final BlockState OAK_DOOR = new BlockState(Block.WOOD_DOOR_BLOCK);
    private static final BlockState SPRUCE_DOOR = new BlockState(Block.SPRUCE_DOOR_BLOCK);
    private static final BlockState ACACIA_DOOR = new BlockState(Block.ACACIA_DOOR_BLOCK);
    private static final BlockState LOG = new BlockState(Block.LOG);
    private static final BlockState ACACIA_LOG__Y = new BlockState(Block.LOG2, BlockWood2.ACACIA);
    private static final BlockState COBBLESTONE = new BlockState(Block.COBBLESTONE);
    private static final BlockState SANDSTONE = new BlockState(Block.SANDSTONE);
    private static final BlockState SMOOTH_SANDSTONE = new BlockState(Block.SANDSTONE, BlockSandstone.SMOOTH);
    private static final BlockState COBBLESTONE_STAIRS__N = new BlockState(Block.COBBLESTONE_STAIRS, WeirdoDirection.NORTH);
    private static final BlockState OAK_STAIRS__N = new BlockState(Block.WOOD_STAIRS, WeirdoDirection.NORTH);
    private static final BlockState OAK_STAIRS__S = new BlockState(Block.WOOD_STAIRS, WeirdoDirection.SOUTH);
    private static final BlockState COBBLESTONE_STAIRS__E = new BlockState(Block.COBBLESTONE_STAIRS, WeirdoDirection.EAST);
    private static final BlockState OAK_STAIRS__E = new BlockState(Block.WOOD_STAIRS, WeirdoDirection.EAST);
    private static final BlockState COBBLESTONE_STAIRS__W = new BlockState(Block.COBBLESTONE_STAIRS, WeirdoDirection.WEST);
    private static final BlockState OAK_STAIRS__W = new BlockState(Block.WOOD_STAIRS, WeirdoDirection.WEST);
    private static final BlockState GRASS = new BlockState(Block.GRASS);
    private static final BlockState GRASS_PATH = new BlockState(Block.GRASS_PATH);
    private static final BlockState DIRT = new BlockState(Block.DIRT);
    private static final BlockState FARMLAND = new BlockState(Block.FARMLAND);
    private static final BlockState GRAVEL = new BlockState(Block.GRAVEL);
    private static final BlockState FLOWING_WATER = new BlockState(Block.WATER);
    private static final BlockState WATER = new BlockState(Block.STILL_WATER);
    private static final BlockState FLOWING_LAVA = new BlockState(Block.LAVA);
    private static final BlockState IRON_BARS = new BlockState(Block.IRON_BARS);
    private static final BlockState FURNACE = new BlockState(Block.FURNACE, FacingDirection.SOUTH);
    private static final BlockState CRAFTING_TABLE = new BlockState(Block.CRAFTING_TABLE);
    private static final BlockState BOOKSHELF = new BlockState(Block.BOOKSHELF);
    private static final BlockState LADDER__S = new BlockState(Block.LADDER, FacingDirection.SOUTH);
    private static final BlockState LADDER__W = new BlockState(Block.LADDER, FacingDirection.WEST);
    private static final BlockState GLASS_PANE = new BlockState(Block.GLASS_PANE);
    private static final BlockState STONE_SLAB = new BlockState(Block.STONE_SLAB);
    private static final BlockState DOUBLE_STONE_SLAB = new BlockState(Block.DOUBLE_STONE_SLAB);
    private static final BlockState WHEAT = new BlockState(Block.WHEAT_BLOCK);
    private static final BlockState CARROTS = new BlockState(Block.CARROT_BLOCK);
    private static final BlockState POTATOES = new BlockState(Block.POTATO_BLOCK);
    private static final BlockState BEETROOTS = new BlockState(Block.BEETROOT_BLOCK);
    private static final BlockState BLACK_WOOL = new BlockState(Block.WOOL, DyeColor.BLACK.getWoolData());
    private static final BlockState BROWN_CARPET = new BlockState(Block.CARPET, DyeColor.BROWN.getWoolData()); //BE

    public static List<PieceWeight> getStructureVillageWeightedPieceList(NukkitRandom random, int size) {
        List<PieceWeight> weights = Lists.newArrayList();
        weights.add(new PieceWeight(SimpleHouse.class, 4, Mth.nextInt(random, 2 + size, 4 + size * 2)));
        weights.add(new PieceWeight(SmallTemple.class, 20, Mth.nextInt(random, 0 + size, 1 + size)));
        weights.add(new PieceWeight(BookHouse.class, 20, Mth.nextInt(random, 0 + size, 2 + size)));
        weights.add(new PieceWeight(SmallHut.class, 3, Mth.nextInt(random, 2 + size, 5 + size * 3)));
        weights.add(new PieceWeight(PigHouse.class, 15, Mth.nextInt(random, 0 + size, 2 + size)));
        weights.add(new PieceWeight(DoubleFarmland.class, 3, Mth.nextInt(random, 1 + size, 4 + size)));
        weights.add(new PieceWeight(Farmland.class, 3, Mth.nextInt(random, 2 + size, 4 + size * 2)));
        weights.add(new PieceWeight(Smithy.class, 15, Mth.nextInt(random, 0, 1 + size)));
        weights.add(new PieceWeight(TwoRoomHouse.class, 8, Mth.nextInt(random, 0 + size, 3 + size * 2)));
        weights.removeIf(pieceWeight -> (pieceWeight).maxPlaceCount == 0);
        return weights;
    }

    private static int updatePieceWeight(List<PieceWeight> weights) {
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

    //\\ VillagePiece::findAndCreatePieceFactory(StartPiece *,PieceWeight &,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static VillagePiece findAndCreatePieceFactory(StartPiece start, PieceWeight weight, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int componentType) {
        Class<? extends VillagePiece> pieceClass = weight.pieceClass;
        if (pieceClass == SimpleHouse.class) {
            return SimpleHouse.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        } else if (pieceClass == SmallTemple.class) {
            return SmallTemple.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        } else if (pieceClass == BookHouse.class) {
            return BookHouse.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        } else if (pieceClass == SmallHut.class) {
            return SmallHut.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        } else if (pieceClass == PigHouse.class) {
            return PigHouse.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        } else if (pieceClass == DoubleFarmland.class) {
            return DoubleFarmland.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        } else if (pieceClass == Farmland.class) {
            return Farmland.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        } else if (pieceClass == Smithy.class) {
            return Smithy.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        } else if (pieceClass == TwoRoomHouse.class) {
            return TwoRoomHouse.createPiece(start, pieces, random, x, y, z, orientation, componentType);
        }
        return null;
    }

    //\\ VillagePiece::generateAndAddPiece(StartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static VillagePiece generateAndAddPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
        int total = updatePieceWeight(start.availablePieces);
        if (total > 0) {
            for (int i = 0; i < 5; ++i) {
                int target = random.nextBoundedInt(total);

                for (PieceWeight weight : start.availablePieces) {
                    target -= weight.weight;

                    if (target < 0) {
                        if (!weight.doPlace(genDepth) || weight == start.previousPiece && start.availablePieces.size() > 1) {
                            break;
                        }

                        VillagePiece piece = findAndCreatePieceFactory(start, weight, pieces, random, x, y, z, orientation, genDepth);
                        if (piece != null) {
                            ++weight.placeCount;
                            start.previousPiece = weight;

                            if (!weight.isValid()) {
                                start.availablePieces.remove(weight);
                            }

                            return piece;
                        }
                    }
                }
            }

            BoundingBox boundingBox = LightPost.findPieceBox(start, pieces, random, x, y, z, orientation);
            if (boundingBox != null) {
                return new LightPost(start, genDepth, random, boundingBox, orientation);
            }
        }
        return null;
    }

    //\\ VillagePiece::generatePieceFromSmallDoor(StartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static StructurePiece generatePieceFromSmallDoor(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
        if (genDepth <= 50 && Math.abs(x - start.getBoundingBox().x0) <= 112 && Math.abs(z - start.getBoundingBox().z0) <= 112) {
            StructurePiece piece = generateAndAddPiece(start, pieces, random, x, y, z, orientation, genDepth + 1);
            if (piece != null) {
                pieces.add(piece);
                start.pendingHouses.add(piece);
                return piece;
            }
        }
        return null;
    }

    //\\ VillagePiece::generateAndAddRoadPiece(StartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
    private static StructurePiece generateAndAddRoadPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
        if (genDepth <= 3 + start.size && Math.abs(x - start.getBoundingBox().x0) <= 112 && Math.abs(z - start.getBoundingBox().z0) <= 112) {
            BoundingBox boundingBox = StraightRoad.findPieceBox(start, pieces, random, x, y, z, orientation);
            if (boundingBox != null && boundingBox.y0 > 10) {
                StructurePiece piece = new StraightRoad(start, genDepth, random, boundingBox, orientation);
                pieces.add(piece);
                start.pendingRoads.add(piece);
                return piece;
            }
        }
        return null;
    }

    public static class PieceWeight {

        public Class<? extends VillagePiece> pieceClass;
        public final int weight;
        public int placeCount;
        public int maxPlaceCount;

        public PieceWeight(Class<? extends VillagePiece> pieceClass, int weight, int maxPlaceCount) {
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

    abstract static class VillagePiece extends StructurePiece {

        protected int horizPos = -1;
        private volatile int villagerCount;
        protected PopulatorVillage.Type type;
        protected boolean isZombieVillage;

        protected int yOffset;

        protected VillagePiece(StartPiece start, int genDepth) {
            super(genDepth);

            if (start != null) {
                this.type = start.type;
                this.isZombieVillage = start.isZombieVillage;
                this.yOffset = start.yOffset;
            } else {
                this.type = PopulatorVillage.Type.PLAINS;
            }
        }

        public VillagePiece(CompoundTag tag) {
            super(tag);
            this.horizPos = tag.getInt("HPos");
            this.villagerCount = tag.getInt("VCount");
            this.type = PopulatorVillage.Type.byId(tag.getByte("Type"));

            if (tag.getBoolean("Desert")) {
                this.type = PopulatorVillage.Type.DESERT;
            }

            this.isZombieVillage = tag.getBoolean("Zombie");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            tag.putInt("HPos", this.horizPos);
            tag.putInt("VCount", this.villagerCount);
            tag.putByte("Type", this.type.ordinal());
            tag.putBoolean("Zombie", this.isZombieVillage);
        }

        @Nullable
        protected StructurePiece generateChildLeft(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int yOffset, int horizontalOffset) {
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    case SOUTH:
                    default:
                        return generatePieceFromSmallDoor(start, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + yOffset, this.boundingBox.z0 + horizontalOffset, BlockFace.WEST, this.getGenDepth());
                    case WEST:
                    case EAST:
                        return generatePieceFromSmallDoor(start, pieces, random, this.boundingBox.x0 + horizontalOffset, this.boundingBox.y0 + yOffset, this.boundingBox.z0 - 1, BlockFace.NORTH, this.getGenDepth());
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece generateChildRight(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int yOffset, int horizontalOffset) {
            BlockFace orientation = this.getOrientation();
            if (orientation != null) {
                switch (orientation) {
                    case NORTH:
                    case SOUTH:
                    default:
                        return generatePieceFromSmallDoor(start, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + yOffset, this.boundingBox.z0 + horizontalOffset, BlockFace.EAST, this.getGenDepth());
                    case WEST:
                    case EAST:
                        return generatePieceFromSmallDoor(start, pieces, random, this.boundingBox.x0 + horizontalOffset, this.boundingBox.y0 + yOffset, this.boundingBox.z1 + 1, BlockFace.SOUTH, this.getGenDepth());
                }
            }
            return null;
        }

        //\\ VillagePiece::getAverageGroundHeight(BlockSource *,BoundingBox const &)
        protected int getAverageGroundHeight(ChunkManager level, BoundingBox boundingBox) {
            int sum = 0;
            int count = 0;
            BlockVector3 vec = new BlockVector3();

            for (int x = this.boundingBox.x0; x <= this.boundingBox.x1; ++x) {
                for (int z = this.boundingBox.z0; z <= this.boundingBox.z1; ++z) {
                    vec.setComponents(x, 64 + this.yOffset, z);

                    if (boundingBox.isInside(vec)) {
                        BaseFullChunk chunk = level.getChunk(x >> 4, z >> 4);
                        if (chunk == null) {
                            sum += 63 + 1 - 1 + this.yOffset;
                        } else {
                            int cx = x & 0xf;
                            int cz = z & 0xf;
                            int y = chunk.getHighestBlockAt(cx, cz);
                            int id = chunk.getBlockId(cx, y, cz);
                            while (Block.transparent[id] && y > 63 + 1 - 1 + this.yOffset) {
                                id = chunk.getBlockId(cx, --y, cz);
                            }
                            sum += Math.max(y, 63 + 1 - 1 + this.yOffset);
                        }
                        ++count;
                    }
                }
            }

            if (count == 0) {
                return -1;
            }
            return sum / count;
        }

        protected static boolean isOkBox(BoundingBox boundingBox) {
            return boundingBox != null && boundingBox.y0 > 10;
        }

        //\\ VillagePiece::spawnVillagers(BlockSource *,BoundingBox const &,int,int,int,int)
        protected void spawnVillagers(ChunkManager level, BoundingBox boundingBox, int x, int y, int z, int maxVillagerCount) {
            if (this.villagerCount < maxVillagerCount) {
                for (int count = this.villagerCount; count < maxVillagerCount; ++count) {
                    int worldX = this.getWorldX(x + count, z);
                    int worldY = this.getWorldY(y);
                    int worldZ = this.getWorldZ(x + count, z);

                    if (!boundingBox.isInside(new BlockVector3(worldX, worldY, worldZ))) {
                        break;
                    }

                    ++this.villagerCount;

                    BaseFullChunk chunk = level.getChunk(worldX >> 4, worldZ >> 4);
                    if (chunk != null) {
                        CompoundTag nbt = Entity.getDefaultNBT(new Vector3(worldX + .5, worldY, worldZ + .5));

                        if (this.isZombieVillage) {
                            nbt.putString("id", "ZombieVillager") // ZombieVillagerV1
                                    .putInt("Profession", this.getVillagerProfession(count, EntityVillagerV1.PROFESSION_FARMER));
                        } else {
                            nbt.putString("id", "Villager") // VillagerV1
                                    .putInt("Profession", this.getVillagerProfession(count, ThreadLocalRandom.current().nextInt(6)));
                        }

                        Server.getInstance().getScheduler().scheduleTask(new ActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                    }
                }
            }
        }

        protected int getVillagerProfession(int villagerCount, int profession) {
            return profession;
        }

        protected BlockState getSpecificBlock(BlockState block) {
            switch (this.type) {
                case DESERT:
                    switch (block.getId()) {
                        case Block.LOG:
                        case Block.LOG2:
                        case Block.COBBLESTONE:
                        case Block.GRAVEL:
                            return SANDSTONE;
                        case Block.PLANKS:
                            return SMOOTH_SANDSTONE;
                        case Block.WOOD_STAIRS:
                        case Block.COBBLESTONE_STAIRS:
                            return new BlockState(Block.SANDSTONE_STAIRS, block.getMeta());
                    }
                    break;
                case TAIGA:
                case COLD:
                    switch (block.getId()) {
                        case Block.LOG:
                        case Block.LOG2:
                            switch (block.getMeta() | 0b11) {
                                case 0b11:
                                default:
                                    return new BlockState(Block.LOG, BlockWood.SPRUCE);
                                case 0b111:
                                    return new BlockState(Block.LOG, BlockWood.SPRUCE | 0b101);
                                case 0b1011:
                                    return new BlockState(Block.LOG, BlockWood.SPRUCE | 0b1001);
                            }
                        case Block.PLANKS:
                            return SPRUCE_PLANKS;
                        case Block.WOOD_STAIRS:
                            return new BlockState(Block.SPRUCE_WOOD_STAIRS, block.getMeta());
                        case Block.FENCE:
                            return SPRUCE_FENCE;
                    }
                    break;
                case SAVANNA:
                    switch (block.getId()) {
                        case Block.LOG:
                        case Block.LOG2:
                            switch (block.getMeta() | 0b11) {
                                case 0b11:
                                default:
                                    return new BlockState(Block.LOG2, BlockWood2.ACACIA);
                                case 0b111:
                                    return new BlockState(Block.LOG2, BlockWood2.ACACIA | 0b101);
                                case 0b1011:
                                    return new BlockState(Block.LOG2, BlockWood2.ACACIA | 0b1001);
                            }
                        case Block.PLANKS:
                            return ACACIA_PLANKS;
                        case Block.WOOD_STAIRS:
                            return new BlockState(Block.ACACIA_WOOD_STAIRS, block.getMeta());
                        case Block.COBBLESTONE:
                            return ACACIA_LOG__Y;
                        case Block.FENCE:
                            return ACACIA_FENCE;
                    }
                    break;
            }
            return block;
        }

        protected BlockState getDoorBlock() {
            switch (this.type) {
                case SAVANNA:
                    return ACACIA_DOOR;
                case TAIGA:
                case COLD:
                    return SPRUCE_DOOR;
                default:
                    return OAK_DOOR;
            }
        }

        protected void placeDoor(ChunkManager level, BoundingBox boundingBox, NukkitRandom random, int x, int y, int z, BlockFace orientation) {
            if (!this.isZombieVillage) {
                this.generateDoor(level, boundingBox, random, x, y, z, BlockFace.NORTH, this.getDoorBlock());
            }
        }

        protected void placeTorch(ChunkManager level, BlockFace orientation, int x, int y, int z, BoundingBox boundingBox) {
            if (!this.isZombieVillage) {
                switch (orientation) {
                    case NORTH:
                    default:
                        this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.NORTH), x, y, z, boundingBox);
                        break;
                    case SOUTH:
                        this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.SOUTH), x, y, z, boundingBox);
                        break;
                    case EAST:
                        this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.EAST), x, y, z, boundingBox);
                        break;
                    case WEST:
                        this.placeBlock(level, new BlockState(Block.TORCH, TorchFacingDirection.WEST), x, y, z, boundingBox);
                        break;
                }
            }
        }

        @Override
        protected void fillColumnDown(ChunkManager level, BlockState block, int x, int y, int z, BoundingBox boundingBox) {
            super.fillColumnDown(level, this.getSpecificBlock(block), x, y, z, boundingBox);
        }

        protected void setType(PopulatorVillage.Type type) {
            this.type = type;
        }
    }

    public abstract static class Road extends VillagePiece {

        protected Road(StartPiece start, int genDepth) {
            super(start, genDepth);
        }

        protected Road(CompoundTag tag) {
            super(tag);
        }
    }

    public static class StartPiece extends Well {

        public ChunkManager world;
        public int size;
        public PieceWeight previousPiece;
        public List<PieceWeight> availablePieces;
        public List<StructurePiece> pendingHouses = Lists.newArrayList();
        public List<StructurePiece> pendingRoads = Lists.newArrayList();

        //\\ VillageStart::VillageStart(BiomeSource *,Random &,int,int,int)
        public StartPiece(ChunkManager level, int genDepth, NukkitRandom random, int x, int z, List<PieceWeight> availablePieces, int size, boolean isNukkitGenerator) {
            super(null, 0, random, x, z);
            this.world = level;
            this.availablePieces = availablePieces;
            this.size = size;

            int biome;
            BaseFullChunk chunk = level.getChunk(x >> 4, z >> 4);
            if (chunk != null) {
                biome = chunk.getBiomeId(x & 0xf, z & 0xf);
            } else {
                biome = EnumBiome.OCEAN.id;
            }

            if (biome == EnumBiome.DESERT.id) {
                this.setType(PopulatorVillage.Type.DESERT);
            } else if (biome == EnumBiome.SAVANNA.id) {
                this.setType(PopulatorVillage.Type.SAVANNA);
            } else if (biome == EnumBiome.TAIGA.id) {
                this.setType(PopulatorVillage.Type.TAIGA);
            } else if (biome == EnumBiome.COLD_TAIGA.id || biome == EnumBiome.ICE_PLAINS.id) {
                this.setType(PopulatorVillage.Type.COLD);
            }

            this.isZombieVillage = random.nextBoundedInt(50) == 0;

            this.yOffset = isNukkitGenerator ? 2 : 0;
        }

        public StartPiece(CompoundTag tag) {
            super(tag);
            this.availablePieces = Lists.newArrayList();
        }

        @Override
        public String getType() {
            return "ViStart";
        }
    }

    public static class Well extends VillagePiece {

        public Well(StartPiece start, int genDepth, NukkitRandom random, int x, int z) {
            super(start, genDepth);
            this.setOrientation(BlockFace.Plane.HORIZONTAL.random(random));
            this.boundingBox = new BoundingBox(x, 64, z, x + 6 - 1, 78, z + 6 - 1);
        }

        public Well(CompoundTag tag) {
            super(tag);
        }

        @Override
        public String getType() {
            return "ViW";
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y1 - 4, this.boundingBox.z0 + 1, BlockFace.WEST, this.getGenDepth());
            generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y1 - 4, this.boundingBox.z0 + 1, BlockFace.EAST, this.getGenDepth());
            generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y1 - 4, this.boundingBox.z0 - 1, BlockFace.NORTH, this.getGenDepth());
            generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x0 + 1, this.boundingBox.y1 - 4, this.boundingBox.z1 + 1, BlockFace.SOUTH, this.getGenDepth());
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 3, 0);
            }

            BlockState cobble = this.getSpecificBlock(COBBLESTONE);
            BlockState fence = this.getSpecificBlock(OAK_FENCE);

            this.generateBox(level, boundingBox, 1, 0, 1, 4, 12, 4, cobble, FLOWING_WATER, false);
            this.placeBlock(level, BlockState.AIR, 2, 12, 2, boundingBox);
            this.placeBlock(level, BlockState.AIR, 3, 12, 2, boundingBox);
            this.placeBlock(level, BlockState.AIR, 2, 12, 3, boundingBox);
            this.placeBlock(level, BlockState.AIR, 3, 12, 3, boundingBox);
            this.placeBlock(level, fence, 1, 13, 1, boundingBox);
            this.placeBlock(level, fence, 1, 14, 1, boundingBox);
            this.placeBlock(level, fence, 4, 13, 1, boundingBox);
            this.placeBlock(level, fence, 4, 14, 1, boundingBox);
            this.placeBlock(level, fence, 1, 13, 4, boundingBox);
            this.placeBlock(level, fence, 1, 14, 4, boundingBox);
            this.placeBlock(level, fence, 4, 13, 4, boundingBox);
            this.placeBlock(level, fence, 4, 14, 4, boundingBox);
            this.generateBox(level, boundingBox, 1, 15, 1, 4, 15, 4, cobble, cobble, false);

            for (int x = 0; x <= 5; ++x) {
                for (int z = 0; z <= 5; ++z) {
                    if (x == 0 || x == 5 || z == 0 || z == 5) {
                        this.placeBlock(level, cobble, x, 11, z, boundingBox);
                        this.fillAirColumnUp(level, x, 12, z, boundingBox);
                    }
                }
            }

            return true;
        }
    }

    public static class SimpleHouse extends VillagePiece {

        private final boolean hasTerrace;

        public SimpleHouse(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.hasTerrace = random.nextBoolean();
        }

        public SimpleHouse(CompoundTag tag) {
            super(tag);
            this.hasTerrace = tag.getBoolean("Terrace");
        }

        @Override
        public String getType() {
            return "ViSH";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Terrace", this.hasTerrace);
        }

        public static SimpleHouse createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 5, 6, 5, orientation);
            return StructurePiece.findCollisionPiece(pieces, boundingBox) != null ? null : new SimpleHouse(start, genDepth, random, boundingBox, orientation);
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 6 - 1, 0);
            }

            BlockState cobble = this.getSpecificBlock(COBBLESTONE);
            BlockState planks = this.getSpecificBlock(PLANKS);
            BlockState stairsN = this.getSpecificBlock(COBBLESTONE_STAIRS__N);
            BlockState log = this.getSpecificBlock(LOG);
            BlockState fence = this.getSpecificBlock(OAK_FENCE);

            this.generateBox(level, boundingBox, 0, 0, 0, 4, 0, 4, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 4, 0, 4, 4, 4, log, log, false);
            this.generateBox(level, boundingBox, 1, 4, 1, 3, 4, 3, planks, planks, false);
            this.placeBlock(level, cobble, 0, 1, 0, boundingBox);
            this.placeBlock(level, cobble, 0, 2, 0, boundingBox);
            this.placeBlock(level, cobble, 0, 3, 0, boundingBox);
            this.placeBlock(level, cobble, 4, 1, 0, boundingBox);
            this.placeBlock(level, cobble, 4, 2, 0, boundingBox);
            this.placeBlock(level, cobble, 4, 3, 0, boundingBox);
            this.placeBlock(level, cobble, 0, 1, 4, boundingBox);
            this.placeBlock(level, cobble, 0, 2, 4, boundingBox);
            this.placeBlock(level, cobble, 0, 3, 4, boundingBox);
            this.placeBlock(level, cobble, 4, 1, 4, boundingBox);
            this.placeBlock(level, cobble, 4, 2, 4, boundingBox);
            this.placeBlock(level, cobble, 4, 3, 4, boundingBox);
            this.generateBox(level, boundingBox, 0, 1, 1, 0, 3, 3, planks, planks, false);
            this.generateBox(level, boundingBox, 4, 1, 1, 4, 3, 3, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 1, 4, 3, 3, 4, planks, planks, false);
            this.placeBlock(level, GLASS_PANE, 0, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 2, 4, boundingBox);
            this.placeBlock(level, GLASS_PANE, 4, 2, 2, boundingBox);
            this.placeBlock(level, planks, 1, 1, 0, boundingBox);
            this.placeBlock(level, planks, 1, 2, 0, boundingBox);
            this.placeBlock(level, planks, 1, 3, 0, boundingBox);
            this.placeBlock(level, planks, 2, 3, 0, boundingBox);
            this.placeBlock(level, planks, 3, 3, 0, boundingBox);
            this.placeBlock(level, planks, 3, 2, 0, boundingBox);
            this.placeBlock(level, planks, 3, 1, 0, boundingBox);

            if (this.getBlock(level, 2, 0, -1, boundingBox).equals(BlockState.AIR) && !this.getBlock(level, 2, -1, -1, boundingBox).equals(BlockState.AIR)) {
                this.placeBlock(level, stairsN, 2, 0, -1, boundingBox);

                if (this.getBlock(level, 2, -1, -1, boundingBox).getId() == Block.GRASS_PATH) {
                    this.placeBlock(level, GRASS, 2, -1, -1, boundingBox);
                }
            }

            this.generateBox(level, boundingBox, 1, 1, 1, 3, 3, 3, BlockState.AIR, BlockState.AIR, false);

            if (this.hasTerrace) {
                this.placeBlock(level, fence, 0, 5, 0, boundingBox);
                this.placeBlock(level, fence, 1, 5, 0, boundingBox);
                this.placeBlock(level, fence, 2, 5, 0, boundingBox);
                this.placeBlock(level, fence, 3, 5, 0, boundingBox);
                this.placeBlock(level, fence, 4, 5, 0, boundingBox);
                this.placeBlock(level, fence, 0, 5, 4, boundingBox);
                this.placeBlock(level, fence, 1, 5, 4, boundingBox);
                this.placeBlock(level, fence, 2, 5, 4, boundingBox);
                this.placeBlock(level, fence, 3, 5, 4, boundingBox);
                this.placeBlock(level, fence, 4, 5, 4, boundingBox);
                this.placeBlock(level, fence, 4, 5, 1, boundingBox);
                this.placeBlock(level, fence, 4, 5, 2, boundingBox);
                this.placeBlock(level, fence, 4, 5, 3, boundingBox);
                this.placeBlock(level, fence, 0, 5, 1, boundingBox);
                this.placeBlock(level, fence, 0, 5, 2, boundingBox);
                this.placeBlock(level, fence, 0, 5, 3, boundingBox);
            }

            if (this.hasTerrace) {
                BlockState ladderS = LADDER__S;
                this.placeBlock(level, ladderS, 3, 1, 3, boundingBox);
                this.placeBlock(level, ladderS, 3, 2, 3, boundingBox);
                this.placeBlock(level, ladderS, 3, 3, 3, boundingBox);
                this.placeBlock(level, ladderS, 3, 4, 3, boundingBox);
            }

            this.placeTorch(level, BlockFace.NORTH, 2, 3, 1, boundingBox);

            for (int x = 0; x < 5; ++x) {
                for (int z = 0; z < 5; ++z) {
                    this.fillAirColumnUp(level, x, 6, z, boundingBox);
                    this.fillColumnDown(level, cobble, x, -1, z, boundingBox);
                }
            }

            this.spawnVillagers(level, boundingBox, 1, 1, 2, 1);
            return true;
        }
    }

    public static class SmallTemple extends VillagePiece {

        public SmallTemple(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public SmallTemple(CompoundTag tag) {
            super(tag);
        }

        @Override
        public String getType() {
            return "ViST";
        }

        public static SmallTemple createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 5, 12, 9, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new SmallTemple(start, genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 12 - 1, 0);
            }

            BlockState cobble = COBBLESTONE;
            BlockState stairsN = this.getSpecificBlock(COBBLESTONE_STAIRS__N);
            BlockState stairsW = this.getSpecificBlock(COBBLESTONE_STAIRS__W);
            BlockState stairsE = this.getSpecificBlock(COBBLESTONE_STAIRS__E);

            this.generateBox(level, boundingBox, 1, 1, 1, 3, 3, 7, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 1, 5, 1, 3, 9, 3, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 1, 0, 0, 3, 0, 8, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 1, 0, 3, 10, 0, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 1, 1, 0, 10, 3, cobble, cobble, false);
            this.generateBox(level, boundingBox, 4, 1, 1, 4, 10, 3, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 0, 4, 0, 4, 7, cobble, cobble, false);
            this.generateBox(level, boundingBox, 4, 0, 4, 4, 4, 7, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 1, 8, 3, 4, 8, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 5, 4, 3, 10, 4, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 5, 5, 3, 5, 7, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 9, 0, 4, 9, 4, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 4, 0, 4, 4, 4, cobble, cobble, false);
            this.placeBlock(level, cobble, 0, 11, 2, boundingBox);
            this.placeBlock(level, cobble, 4, 11, 2, boundingBox);
            this.placeBlock(level, cobble, 2, 11, 0, boundingBox);
            this.placeBlock(level, cobble, 2, 11, 4, boundingBox);
            this.placeBlock(level, cobble, 1, 1, 6, boundingBox);
            this.placeBlock(level, cobble, 1, 1, 7, boundingBox);
            this.placeBlock(level, cobble, 2, 1, 7, boundingBox);
            this.placeBlock(level, cobble, 3, 1, 6, boundingBox);
            this.placeBlock(level, cobble, 3, 1, 7, boundingBox);
            this.placeBlock(level, stairsN, 1, 1, 5, boundingBox);
            this.placeBlock(level, stairsN, 2, 1, 6, boundingBox);
            this.placeBlock(level, stairsN, 3, 1, 5, boundingBox);
            this.placeBlock(level, stairsW, 1, 2, 7, boundingBox);
            this.placeBlock(level, stairsE, 3, 2, 7, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 3, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 4, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 4, 3, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 6, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 7, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 4, 6, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 4, 7, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 6, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 7, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 6, 4, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 7, 4, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 3, 6, boundingBox);
            this.placeBlock(level, GLASS_PANE, 4, 3, 6, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 3, 8, boundingBox);
            this.placeTorch(level, BlockFace.SOUTH, 2, 4, 7, boundingBox);
            this.placeTorch(level, BlockFace.EAST, 1, 4, 6, boundingBox);
            this.placeTorch(level, BlockFace.WEST, 3, 4, 6, boundingBox);
            this.placeTorch(level, BlockFace.NORTH, 2, 4, 5, boundingBox);

            for (int y = 1; y <= 9; ++y) {
                this.placeBlock(level, LADDER__W, 3, y, 3, boundingBox);
            }

            this.placeBlock(level, BlockState.AIR, 2, 1, 0, boundingBox);
            this.placeBlock(level, BlockState.AIR, 2, 2, 0, boundingBox);
            this.placeDoor(level, boundingBox, random, 2, 1, 0, BlockFace.NORTH);

            if (this.getBlock(level, 2, 0, -1, boundingBox).equals(BlockState.AIR) && !this.getBlock(level, 2, -1, -1, boundingBox).equals(BlockState.AIR)) {
                this.placeBlock(level, stairsN, 2, 0, -1, boundingBox);

                if (this.getBlock(level, 2, -1, -1, boundingBox).getId() == Block.GRASS_PATH) {
                    this.placeBlock(level, GRASS, 2, -1, -1, boundingBox);
                }
            }

            for (int x = 0; x < 5; ++x) {
                for (int z = 0; z < 9; ++z) {
                    this.fillAirColumnUp(level, x, 12, z, boundingBox);
                    this.fillColumnDown(level, cobble, x, -1, z, boundingBox);
                }
            }

            this.spawnVillagers(level, boundingBox, 2, 1, 2, 1);
            return true;
        }

        @Override
        protected int getVillagerProfession(int villagerCount, int profession) {
            return EntityVillagerV1.PROFESSION_PRIEST;
        }
    }

    public static class BookHouse extends VillagePiece {

        public BookHouse(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public BookHouse(CompoundTag tag) {
            super(tag);
        }

        @Override
        public String getType() {
            return "ViBH";
        }

        public static BookHouse createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 9, 9, 6, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new BookHouse(start, genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 9 - 1, 0);
            }

            BlockState cobble = this.getSpecificBlock(COBBLESTONE);
            BlockState stairsN = this.getSpecificBlock(OAK_STAIRS__N);
            BlockState stairsS = this.getSpecificBlock(OAK_STAIRS__S);
            BlockState stairsE = this.getSpecificBlock(OAK_STAIRS__E);
            BlockState planks = this.getSpecificBlock(PLANKS);
            BlockState cobbleStairsN = this.getSpecificBlock(COBBLESTONE_STAIRS__N);
            BlockState fence = this.getSpecificBlock(OAK_FENCE);

            this.generateBox(level, boundingBox, 1, 1, 1, 7, 5, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 8, 0, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 5, 0, 8, 5, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 6, 1, 8, 6, 4, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 7, 2, 8, 7, 3, cobble, cobble, false);

            for (int i = -1; i <= 2; ++i) {
                for (int x = 0; x <= 8; ++x) {
                    this.placeBlock(level, stairsN, x, 6 + i, i, boundingBox);
                    this.placeBlock(level, stairsS, x, 6 + i, 5 - i, boundingBox);
                }
            }

            this.generateBox(level, boundingBox, 0, 1, 0, 0, 1, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 1, 5, 8, 1, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 8, 1, 0, 8, 1, 4, cobble, cobble, false);
            this.generateBox(level, boundingBox, 2, 1, 0, 7, 1, 0, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 2, 0, 0, 4, 0, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 2, 5, 0, 4, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 8, 2, 5, 8, 4, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 8, 2, 0, 8, 4, 0, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 2, 1, 0, 4, 4, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 2, 5, 7, 4, 5, planks, planks, false);
            this.generateBox(level, boundingBox, 8, 2, 1, 8, 4, 4, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 2, 0, 7, 4, 0, planks, planks, false);
            this.placeBlock(level, GLASS_PANE, 4, 2, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 5, 2, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 6, 2, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 4, 3, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 5, 3, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 6, 3, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 3, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 3, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 3, 3, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 2, 3, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 3, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 3, 3, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 2, 5, boundingBox);
            this.placeBlock(level, GLASS_PANE, 3, 2, 5, boundingBox);
            this.placeBlock(level, GLASS_PANE, 5, 2, 5, boundingBox);
            this.placeBlock(level, GLASS_PANE, 6, 2, 5, boundingBox);
            this.generateBox(level, boundingBox, 1, 4, 1, 7, 4, 1, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 4, 4, 7, 4, 4, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 3, 4, 7, 3, 4, BOOKSHELF, BOOKSHELF, false);
            this.placeBlock(level, planks, 7, 1, 4, boundingBox);
            this.placeBlock(level, stairsE, 7, 1, 3, boundingBox);
            this.placeBlock(level, stairsN, 6, 1, 4, boundingBox);
            this.placeBlock(level, stairsN, 5, 1, 4, boundingBox);
            this.placeBlock(level, stairsN, 4, 1, 4, boundingBox);
            this.placeBlock(level, stairsN, 3, 1, 4, boundingBox);
            this.placeBlock(level, fence, 6, 1, 3, boundingBox);
            this.placeBlock(level, BROWN_CARPET, 6, 2, 3, boundingBox);
            this.placeBlock(level, fence, 4, 1, 3, boundingBox);
            this.placeBlock(level, BROWN_CARPET, 4, 2, 3, boundingBox);
            this.placeBlock(level, CRAFTING_TABLE, 7, 1, 1, boundingBox);
            this.placeBlock(level, BlockState.AIR, 1, 1, 0, boundingBox);
            this.placeBlock(level, BlockState.AIR, 1, 2, 0, boundingBox);
            this.placeDoor(level, boundingBox, random, 1, 1, 0, BlockFace.NORTH);

            if (this.getBlock(level, 1, 0, -1, boundingBox).equals(BlockState.AIR) && !this.getBlock(level, 1, -1, -1, boundingBox).equals(BlockState.AIR)) {
                this.placeBlock(level, cobbleStairsN, 1, 0, -1, boundingBox);

                if (this.getBlock(level, 1, -1, -1, boundingBox).getId() == Block.GRASS_PATH) {
                    this.placeBlock(level, GRASS, 1, -1, -1, boundingBox);
                }
            }

            for (int x = 0; x < 9; ++x) {
                for (int z = 0; z < 6; ++z) {
                    this.fillAirColumnUp(level, x, 9, z, boundingBox);
                    this.fillColumnDown(level, cobble, x, -1, z, boundingBox);
                }
            }

            this.spawnVillagers(level, boundingBox, 2, 1, 2, 1);
            return true;
        }

        @Override
        protected int getVillagerProfession(int villagerCount, int profession) {
            return EntityVillagerV1.PROFESSION_LIBRARIAN;
        }
    }

    public static class SmallHut extends VillagePiece {

        private final boolean hasCompoundRoof;
        private final int tablePos;

        public SmallHut(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.hasCompoundRoof = random.nextBoolean();
            this.tablePos = random.nextBoundedInt(3);
        }

        public SmallHut(CompoundTag tag) {
            super(tag);
            this.hasCompoundRoof = tag.getBoolean("C");
            this.tablePos = tag.getInt("T");
        }

        @Override
        public String getType() {
            return "ViSmH";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putInt("T", this.tablePos);
            tag.putBoolean("C", this.hasCompoundRoof);
        }

        //\\ SmallHut::createPiece(StartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int,int)
        public static SmallHut createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 4, 6, 5, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new SmallHut(start, genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 6 - 1, 0);
            }

            BlockState cobble = this.getSpecificBlock(COBBLESTONE);
            BlockState planks = this.getSpecificBlock(PLANKS);
            BlockState stairsN = this.getSpecificBlock(COBBLESTONE_STAIRS__N);
            BlockState log = this.getSpecificBlock(LOG);
            BlockState fence = this.getSpecificBlock(OAK_FENCE);

            this.generateBox(level, boundingBox, 1, 1, 1, 3, 5, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 3, 0, 4, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 0, 1, 2, 0, 3, DIRT, DIRT, false);

            if (this.hasCompoundRoof) {
                this.generateBox(level, boundingBox, 1, 4, 1, 2, 4, 3, log, log, false);
            } else {
                this.generateBox(level, boundingBox, 1, 5, 1, 2, 5, 3, log, log, false);
            }

            this.placeBlock(level, log, 1, 4, 0, boundingBox);
            this.placeBlock(level, log, 2, 4, 0, boundingBox);
            this.placeBlock(level, log, 1, 4, 4, boundingBox);
            this.placeBlock(level, log, 2, 4, 4, boundingBox);
            this.placeBlock(level, log, 0, 4, 1, boundingBox);
            this.placeBlock(level, log, 0, 4, 2, boundingBox);
            this.placeBlock(level, log, 0, 4, 3, boundingBox);
            this.placeBlock(level, log, 3, 4, 1, boundingBox);
            this.placeBlock(level, log, 3, 4, 2, boundingBox);
            this.placeBlock(level, log, 3, 4, 3, boundingBox);
            this.generateBox(level, boundingBox, 0, 1, 0, 0, 3, 0, log, log, false);
            this.generateBox(level, boundingBox, 3, 1, 0, 3, 3, 0, log, log, false);
            this.generateBox(level, boundingBox, 0, 1, 4, 0, 3, 4, log, log, false);
            this.generateBox(level, boundingBox, 3, 1, 4, 3, 3, 4, log, log, false);
            this.generateBox(level, boundingBox, 0, 1, 1, 0, 3, 3, planks, planks, false);
            this.generateBox(level, boundingBox, 3, 1, 1, 3, 3, 3, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 1, 0, 2, 3, 0, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 1, 4, 2, 3, 4, planks, planks, false);
            this.placeBlock(level, GLASS_PANE, 0, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 3, 2, 2, boundingBox);

            if (this.tablePos > 0) {
                this.placeBlock(level, fence, this.tablePos, 1, 3, boundingBox);
                this.placeBlock(level, BROWN_CARPET, this.tablePos, 2, 3, boundingBox);
            }

            this.placeBlock(level, BlockState.AIR, 1, 1, 0, boundingBox);
            this.placeBlock(level, BlockState.AIR, 1, 2, 0, boundingBox);
            this.placeDoor(level, boundingBox, random, 1, 1, 0, BlockFace.NORTH);

            if (this.getBlock(level, 1, 0, -1, boundingBox).equals(BlockState.AIR) && !this.getBlock(level, 1, -1, -1, boundingBox).equals(BlockState.AIR)) {
                this.placeBlock(level, stairsN, 1, 0, -1, boundingBox);

                if (this.getBlock(level, 1, -1, -1, boundingBox).getId() == Block.GRASS_PATH) {
                    this.placeBlock(level, GRASS, 1, -1, -1, boundingBox);
                }
            }

            for (int x = 0; x < 4; ++x) {
                for (int z = 0; z < 5; ++z) {
                    this.fillAirColumnUp(level, x, 6, z, boundingBox);
                    this.fillColumnDown(level, cobble, x, -1, z, boundingBox);
                }
            }

            this.spawnVillagers(level, boundingBox, 1, 1, 2, 1);
            return true;
        }
    }

    public static class PigHouse extends VillagePiece {

        public PigHouse(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public PigHouse(CompoundTag tag) {
            super(tag);
        }

        @Override
        public String getType() {
            return "ViPH";
        }

        public static PigHouse createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 9, 7, 11, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new PigHouse(start, genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 7 - 1, 0);
            }

            BlockState cobble = this.getSpecificBlock(COBBLESTONE);
            BlockState stairsN = this.getSpecificBlock(OAK_STAIRS__N);
            BlockState stairsS = this.getSpecificBlock(OAK_STAIRS__S);
            BlockState stairsW = this.getSpecificBlock(OAK_STAIRS__W);
            BlockState planks = this.getSpecificBlock(PLANKS);
            BlockState log = this.getSpecificBlock(LOG);
            BlockState fence = this.getSpecificBlock(OAK_FENCE);

            this.generateBox(level, boundingBox, 1, 1, 1, 7, 4, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 2, 1, 6, 8, 4, 10, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 2, 0, 6, 8, 0, 10, DIRT, DIRT, false);
            this.placeBlock(level, cobble, 6, 0, 6, boundingBox);
            this.generateBox(level, boundingBox, 2, 1, 6, 2, 1, 10, fence, fence, false);
            this.generateBox(level, boundingBox, 8, 1, 6, 8, 1, 10, fence, fence, false);
            this.generateBox(level, boundingBox, 3, 1, 10, 7, 1, 10, fence, fence, false);
            this.generateBox(level, boundingBox, 1, 0, 1, 7, 0, 4, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 0, 3, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 8, 0, 0, 8, 3, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 0, 0, 7, 1, 0, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 0, 5, 7, 1, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 2, 0, 7, 3, 0, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 2, 5, 7, 3, 5, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 4, 1, 8, 4, 1, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 4, 4, 8, 4, 4, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 5, 2, 8, 5, 3, planks, planks, false);
            this.placeBlock(level, planks, 0, 4, 2, boundingBox);
            this.placeBlock(level, planks, 0, 4, 3, boundingBox);
            this.placeBlock(level, planks, 8, 4, 2, boundingBox);
            this.placeBlock(level, planks, 8, 4, 3, boundingBox);

            for (int i = -1; i <= 2; ++i) {
                for (int x = 0; x <= 8; ++x) {
                    this.placeBlock(level, stairsN, x, 4 + i, i, boundingBox);
                    this.placeBlock(level, stairsS, x, 4 + i, 5 - i, boundingBox);
                }
            }

            this.placeBlock(level, log, 0, 2, 1, boundingBox);
            this.placeBlock(level, log, 0, 2, 4, boundingBox);
            this.placeBlock(level, log, 8, 2, 1, boundingBox);
            this.placeBlock(level, log, 8, 2, 4, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 3, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 2, 3, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 2, 5, boundingBox);
            this.placeBlock(level, GLASS_PANE, 3, 2, 5, boundingBox);
            this.placeBlock(level, GLASS_PANE, 5, 2, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 6, 2, 5, boundingBox);
            this.placeBlock(level, fence, 2, 1, 3, boundingBox);
            this.placeBlock(level, BROWN_CARPET, 2, 2, 3, boundingBox);
            this.placeBlock(level, planks, 1, 1, 4, boundingBox);
            this.placeBlock(level, stairsN, 2, 1, 4, boundingBox);
            this.placeBlock(level, stairsW, 1, 1, 3, boundingBox);
            this.generateBox(level, boundingBox, 5, 0, 1, 7, 0, 3, DOUBLE_STONE_SLAB, DOUBLE_STONE_SLAB, false);
            this.placeBlock(level, DOUBLE_STONE_SLAB, 6, 1, 1, boundingBox);
            this.placeBlock(level, DOUBLE_STONE_SLAB, 6, 1, 2, boundingBox);
            this.placeBlock(level, BlockState.AIR, 2, 1, 0, boundingBox);
            this.placeBlock(level, BlockState.AIR, 2, 2, 0, boundingBox);
            this.placeTorch(level, BlockFace.NORTH, 2, 3, 1, boundingBox);
            this.placeDoor(level, boundingBox, random, 2, 1, 0, BlockFace.NORTH);

            if (this.getBlock(level, 2, 0, -1, boundingBox).equals(BlockState.AIR) && !this.getBlock(level, 2, -1, -1, boundingBox).equals(BlockState.AIR)) {
                this.placeBlock(level, stairsN, 2, 0, -1, boundingBox);

                if (this.getBlock(level, 2, -1, -1, boundingBox).getId() == Block.GRASS_PATH) {
                    this.placeBlock(level, GRASS, 2, -1, -1, boundingBox);
                }
            }

            this.placeBlock(level, BlockState.AIR, 6, 1, 5, boundingBox);
            this.placeBlock(level, BlockState.AIR, 6, 2, 5, boundingBox);
            this.placeTorch(level, BlockFace.SOUTH, 6, 3, 4, boundingBox);
            this.placeDoor(level, boundingBox, random, 6, 1, 5, BlockFace.SOUTH);

            for (int x = 0; x < 9; ++x) {
                for (int z = 0; z < 5; ++z) {
                    this.fillAirColumnUp(level, x, 7, z, boundingBox);
                    this.fillColumnDown(level, cobble, x, -1, z, boundingBox);
                }
            }

            this.spawnVillagers(level, boundingBox, 4, 1, 2, 2);
            return true;
        }

        @Override //\\ PigHouse::getVillagerProfession(int)
        protected int getVillagerProfession(int villagerCount, int profession) {
            return villagerCount == 0 ? EntityVillagerV1.PROFESSION_BUTCHER : super.getVillagerProfession(villagerCount, profession);
        }
    }

    public static class DoubleFarmland extends VillagePiece {

        private final BlockState cropA;
        private final BlockState cropB;
        private final BlockState cropC;
        private final BlockState cropD;

        public DoubleFarmland(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.cropA = Farmland.selectCrops(random);
            this.cropB = Farmland.selectCrops(random);
            this.cropC = Farmland.selectCrops(random);
            this.cropD = Farmland.selectCrops(random);
        }

        public DoubleFarmland(CompoundTag tag) {
            super(tag);
            this.cropA = new BlockState(tag.getInt("CA"));
            this.cropB = new BlockState(tag.getInt("CB"));
            this.cropC = new BlockState(tag.getInt("CC"));
            this.cropD = new BlockState(tag.getInt("CD"));
        }

        @Override
        public String getType() {
            return "ViDF";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putInt("CA", this.cropA.getId());
            tag.putInt("CB", this.cropB.getId());
            tag.putInt("CC", this.cropC.getId());
            tag.putInt("CD", this.cropD.getId());
        }

        public static DoubleFarmland createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 13, 4, 9, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new DoubleFarmland(start, genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 4 - 1, 0);
            }

            BlockState log = this.getSpecificBlock(LOG);

            this.generateBox(level, boundingBox, 0, 1, 0, 12, 4, 8, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 1, 0, 1, 2, 0, 7, FARMLAND, FARMLAND, false);
            this.generateBox(level, boundingBox, 4, 0, 1, 5, 0, 7, FARMLAND, FARMLAND, false);
            this.generateBox(level, boundingBox, 7, 0, 1, 8, 0, 7, FARMLAND, FARMLAND, false);
            this.generateBox(level, boundingBox, 10, 0, 1, 11, 0, 7, FARMLAND, FARMLAND, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 0, 0, 8, log, log, false);
            this.generateBox(level, boundingBox, 6, 0, 0, 6, 0, 8, log, log, false);
            this.generateBox(level, boundingBox, 12, 0, 0, 12, 0, 8, log, log, false);
            this.generateBox(level, boundingBox, 1, 0, 0, 11, 0, 0, log, log, false);
            this.generateBox(level, boundingBox, 1, 0, 8, 11, 0, 8, log, log, false);
            this.generateBox(level, boundingBox, 3, 0, 1, 3, 0, 7, WATER, WATER, false);
            this.generateBox(level, boundingBox, 9, 0, 1, 9, 0, 7, WATER, WATER, false);

            if (this.type != PopulatorVillage.Type.COLD) { //BE
                for (int z = 1; z <= 7; ++z) {
                    int maxAgeA = 7;
                    int minAgeA = maxAgeA / 3;
                    this.placeBlock(level, new BlockState(this.cropA.getId(), Mth.nextInt(random, minAgeA, maxAgeA)), 1, 1, z, boundingBox);
                    this.placeBlock(level, new BlockState(this.cropA.getId(), Mth.nextInt(random, minAgeA, maxAgeA)), 2, 1, z, boundingBox);
                    int maxAgeB = 7;
                    int minAgeB = maxAgeB / 3;
                    this.placeBlock(level, new BlockState(this.cropB.getId(), Mth.nextInt(random, minAgeB, maxAgeB)), 4, 1, z, boundingBox);
                    this.placeBlock(level, new BlockState(this.cropB.getId(), Mth.nextInt(random, minAgeB, maxAgeB)), 5, 1, z, boundingBox);
                    int maxAgeC = 7;
                    int minAgeC = maxAgeC / 3;
                    this.placeBlock(level, new BlockState(this.cropC.getId(), Mth.nextInt(random, minAgeC, maxAgeC)), 7, 1, z, boundingBox);
                    this.placeBlock(level, new BlockState(this.cropC.getId(), Mth.nextInt(random, minAgeC, maxAgeC)), 8, 1, z, boundingBox);
                    int maxAgeD = 7;
                    int minAgeD = maxAgeD / 3;
                    this.placeBlock(level, new BlockState(this.cropD.getId(), Mth.nextInt(random, minAgeD, maxAgeD)), 10, 1, z, boundingBox);
                    this.placeBlock(level, new BlockState(this.cropD.getId(), Mth.nextInt(random, minAgeD, maxAgeD)), 11, 1, z, boundingBox);
                }
            }

            for (int x = 0; x < 13; ++x) {
                for (int z = 0; z < 9; ++z) {
                    this.fillAirColumnUp(level, x, 4, z, boundingBox);
                    this.fillColumnDown(level, DIRT, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class Farmland extends VillagePiece {

        private final BlockState cropA;
        private final BlockState cropB;

        public Farmland(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.cropA = selectCrops(random);
            this.cropB = selectCrops(random);
        }

        public Farmland(CompoundTag tag) {
            super(tag);
            this.cropA = new BlockState(tag.getInt("CA"));
            this.cropB = new BlockState(tag.getInt("CB"));
        }

        @Override
        public String getType() {
            return "ViF";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putInt("CA", this.cropA.getId());
            tag.putInt("CB", this.cropA.getId());
        }

        //\\ Farmland::selectCrops(Random &,StartPiece &)
        protected static BlockState selectCrops(NukkitRandom random) {
            switch (random.nextBoundedInt(10)) {
                case 0:
                case 1:
                    return CARROTS;
                case 2:
                case 3:
                    return POTATOES;
                case 4:
                    return BEETROOTS;
                default:
                    return WHEAT;
            }
        }

        public static Farmland createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 7, 4, 9, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Farmland(start, genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 4 - 1, 0);
            }

            BlockState log = this.getSpecificBlock(LOG);

            this.generateBox(level, boundingBox, 0, 1, 0, 6, 4, 8, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 1, 0, 1, 2, 0, 7, FARMLAND, FARMLAND, false);
            this.generateBox(level, boundingBox, 4, 0, 1, 5, 0, 7, FARMLAND, FARMLAND, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 0, 0, 8, log, log, false);
            this.generateBox(level, boundingBox, 6, 0, 0, 6, 0, 8, log, log, false);
            this.generateBox(level, boundingBox, 1, 0, 0, 5, 0, 0, log, log, false);
            this.generateBox(level, boundingBox, 1, 0, 8, 5, 0, 8, log, log, false);
            this.generateBox(level, boundingBox, 3, 0, 1, 3, 0, 7, WATER, WATER, false);

            if (this.type != PopulatorVillage.Type.COLD) { //BE
                for (int z = 1; z <= 7; ++z) {
                    int maxAgeA = 7;
                    int minAgeA = maxAgeA / 3;
                    this.placeBlock(level, new BlockState(this.cropA.getId(), Mth.nextInt(random, minAgeA, maxAgeA)), 1, 1, z, boundingBox);
                    this.placeBlock(level, new BlockState(this.cropA.getId(), Mth.nextInt(random, minAgeA, maxAgeA)), 2, 1, z, boundingBox);
                    int maxAgeB = 7;
                    int minAgeB = maxAgeB / 3;
                    this.placeBlock(level, new BlockState(this.cropB.getId(), Mth.nextInt(random, minAgeB, maxAgeB)), 4, 1, z, boundingBox);
                    this.placeBlock(level, new BlockState(this.cropB.getId(), Mth.nextInt(random, minAgeB, maxAgeB)), 5, 1, z, boundingBox);
                }
            }

            for (int x = 0; x < 7; ++x) {
                for (int z = 0; z < 9; ++z) {
                    this.fillAirColumnUp(level, x, 4, z, boundingBox);
                    this.fillColumnDown(level, DIRT, x, -1, z, boundingBox);
                }
            }

            return true;
        }
    }

    public static class Smithy extends VillagePiece {

        private boolean hasPlacedChest;

        public Smithy(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public Smithy(CompoundTag tag) {
            super(tag);
            this.hasPlacedChest = tag.getBoolean("Chest");
        }

        @Override
        public String getType() {
            return "ViS";
        }

        public static Smithy createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 10, 6, 7, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Smithy(start, genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putBoolean("Chest", this.hasPlacedChest);
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 6 - 1, 0);
            }

            BlockState cobble = COBBLESTONE;
            BlockState stairsN = this.getSpecificBlock(OAK_STAIRS__N);
            BlockState stairsW = this.getSpecificBlock(OAK_STAIRS__W);
            BlockState planks = this.getSpecificBlock(PLANKS);
            BlockState cobbleStairsN = this.getSpecificBlock(COBBLESTONE_STAIRS__N);
            BlockState log = this.getSpecificBlock(LOG);
            BlockState fence = this.getSpecificBlock(OAK_FENCE);

            this.generateBox(level, boundingBox, 0, 1, 0, 9, 4, 6, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 9, 0, 6, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 4, 0, 9, 4, 6, cobble, cobble, false);
            this.generateBox(level, boundingBox, 0, 5, 0, 9, 5, 6, STONE_SLAB, STONE_SLAB, false);
            this.generateBox(level, boundingBox, 1, 5, 1, 8, 5, 5, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 1, 1, 0, 2, 3, 0, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 1, 0, 0, 4, 0, log, log, false);
            this.generateBox(level, boundingBox, 3, 1, 0, 3, 4, 0, log, log, false);
            this.generateBox(level, boundingBox, 0, 1, 6, 0, 4, 6, log, log, false);
            this.placeBlock(level, planks, 3, 3, 1, boundingBox);
            this.generateBox(level, boundingBox, 3, 1, 2, 3, 3, 2, planks, planks, false);
            this.generateBox(level, boundingBox, 4, 1, 3, 5, 3, 3, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 1, 1, 0, 3, 5, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 1, 6, 5, 3, 6, planks, planks, false);
            this.generateBox(level, boundingBox, 5, 1, 0, 5, 3, 0, fence, fence, false);
            this.generateBox(level, boundingBox, 9, 1, 0, 9, 3, 0, fence, fence, false);
            this.generateBox(level, boundingBox, 6, 1, 4, 9, 4, 6, cobble, cobble, false);
            this.placeBlock(level, FLOWING_LAVA, 7, 1, 5, boundingBox);
            this.placeBlock(level, FLOWING_LAVA, 8, 1, 5, boundingBox);
            this.placeBlock(level, IRON_BARS, 9, 2, 5, boundingBox);
            this.placeBlock(level, IRON_BARS, 9, 2, 4, boundingBox);
            this.generateBox(level, boundingBox, 7, 2, 4, 8, 2, 5, BlockState.AIR, BlockState.AIR, false);
            this.placeBlock(level, cobble, 6, 1, 3, boundingBox);

            this.placeBlock(level, FURNACE, 6, 2, 3, boundingBox);
            this.placeBlock(level, FURNACE, 6, 3, 3, boundingBox);

            BlockVector3 vec = new BlockVector3(this.getWorldX(6, 3), this.getWorldY(2), this.getWorldZ(6, 3));
            if (boundingBox.isInside(vec)) {
                BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                if (chunk != null) {
                    Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(),
                            BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.FURNACE)));
                }
            }
            vec = vec.up();
            if (boundingBox.isInside(vec)) {
                BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                if (chunk != null) {
                    Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(),
                            BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.FURNACE)));
                }
            }

            this.placeBlock(level, DOUBLE_STONE_SLAB, 8, 1, 1, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 4, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 2, 6, boundingBox);
            this.placeBlock(level, GLASS_PANE, 4, 2, 6, boundingBox);
            this.placeBlock(level, fence, 2, 1, 4, boundingBox);
            this.placeBlock(level, BROWN_CARPET, 2, 2, 4, boundingBox);
            this.placeBlock(level, planks, 1, 1, 5, boundingBox);
            this.placeBlock(level, stairsN, 2, 1, 5, boundingBox);
            this.placeBlock(level, stairsW, 1, 1, 4, boundingBox);

            if (!this.hasPlacedChest && boundingBox.isInside(new BlockVector3(this.getWorldX(5, 5), this.getWorldY(1), this.getWorldZ(5, 5)))) {
                this.hasPlacedChest = true;

                //\\ StructureHelpers::createChest(v7, v6, (int *)v4, v5, 5, 1, 5, v197, (__int64)&v204);
                BlockFace orientation = this.getOrientation();
                this.placeBlock(level, new BlockState(Block.CHEST, (orientation == null ? BlockFace.NORTH : orientation).getOpposite().getIndex()), 5, 1, 5, boundingBox);

                vec = new BlockVector3(this.getWorldX(5, 5), this.getWorldY(1), this.getWorldZ(5, 5));
                if (boundingBox.isInside(vec)) {
                    BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                    if (chunk != null) {
                        CompoundTag nbt = BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.CHEST);
                        ListTag<CompoundTag> itemList = new ListTag<>("Items");
                        VillageBlacksmithChest.get().create(itemList, random);
                        nbt.putList(itemList);
                        Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                    }
                }
            }

            for (int x = 6; x <= 8; ++x) {
                if (this.getBlock(level, x, 0, -1, boundingBox).equals(BlockState.AIR) && !this.getBlock(level, x, -1, -1, boundingBox).equals(BlockState.AIR)) {
                    this.placeBlock(level, cobbleStairsN, x, 0, -1, boundingBox);

                    if (this.getBlock(level, x, -1, -1, boundingBox).getId() == Block.GRASS_PATH) {
                        this.placeBlock(level, GRASS, x, -1, -1, boundingBox);
                    }
                }
            }

            for (int x = 0; x < 10; ++x) {
                for (int z = 0; z < 7; ++z) {
                    this.fillAirColumnUp(level, x, 6, z, boundingBox);
                    this.fillColumnDown(level, cobble, x, -1, z, boundingBox);
                }
            }

            this.spawnVillagers(level, boundingBox, 7, 1, 1, 1);
            return true;
        }

        @Override
        protected int getVillagerProfession(int villagerCount, int profession) {
            return EntityVillagerV1.PROFESSION_BLACKSMITH;
        }
    }

    public static class TwoRoomHouse extends VillagePiece {

        public TwoRoomHouse(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public TwoRoomHouse(CompoundTag tag) {
            super(tag);
        }

        @Override
        public String getType() {
            return "ViTRH";
        }

        public static TwoRoomHouse createPiece(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation, int genDepth) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 9, 7, 12, orientation);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new TwoRoomHouse(start, genDepth, random, boundingBox, orientation) : null;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 7 - 1, 0);
            }

            BlockState cobble = this.getSpecificBlock(COBBLESTONE);
            BlockState stairsN = this.getSpecificBlock(OAK_STAIRS__N);
            BlockState stairsS = this.getSpecificBlock(OAK_STAIRS__S);
            BlockState stairsE = this.getSpecificBlock(OAK_STAIRS__E);
            BlockState stairsW = this.getSpecificBlock(OAK_STAIRS__W);
            BlockState planks = this.getSpecificBlock(PLANKS);
            BlockState log = this.getSpecificBlock(LOG);

            this.generateBox(level, boundingBox, 1, 1, 1, 7, 4, 4, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 2, 1, 6, 8, 4, 10, BlockState.AIR, BlockState.AIR, false);
            this.generateBox(level, boundingBox, 2, 0, 5, 8, 0, 10, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 0, 1, 7, 0, 4, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 0, 0, 0, 3, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 8, 0, 0, 8, 3, 10, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 0, 0, 7, 2, 0, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 0, 5, 2, 1, 5, cobble, cobble, false);
            this.generateBox(level, boundingBox, 2, 0, 6, 2, 3, 10, cobble, cobble, false);
            this.generateBox(level, boundingBox, 3, 0, 10, 7, 3, 10, cobble, cobble, false);
            this.generateBox(level, boundingBox, 1, 2, 0, 7, 3, 0, planks, planks, false);
            this.generateBox(level, boundingBox, 1, 2, 5, 2, 3, 5, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 4, 1, 8, 4, 1, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 4, 4, 3, 4, 4, planks, planks, false);
            this.generateBox(level, boundingBox, 0, 5, 2, 8, 5, 3, planks, planks, false);
            this.placeBlock(level, planks, 0, 4, 2, boundingBox);
            this.placeBlock(level, planks, 0, 4, 3, boundingBox);
            this.placeBlock(level, planks, 8, 4, 2, boundingBox);
            this.placeBlock(level, planks, 8, 4, 3, boundingBox);
            this.placeBlock(level, planks, 8, 4, 4, boundingBox);

            for (int i = -1; i <= 2; ++i) {
                for (int x = 0; x <= 8; ++x) {
                    this.placeBlock(level, stairsN, x, 4 + i, i, boundingBox);

                    if ((i > -1 || x <= 1) && (i > 0 || x <= 3) && (i > 1 || x <= 4 || x >= 6)) {
                        this.placeBlock(level, stairsS, x, 4 + i, 5 - i, boundingBox);
                    }
                }
            }

            this.generateBox(level, boundingBox, 3, 4, 5, 3, 4, 10, planks, planks, false);
            this.generateBox(level, boundingBox, 7, 4, 2, 7, 4, 10, planks, planks, false);
            this.generateBox(level, boundingBox, 4, 5, 4, 4, 5, 10, planks, planks, false);
            this.generateBox(level, boundingBox, 6, 5, 4, 6, 5, 10, planks, planks, false);
            this.generateBox(level, boundingBox, 5, 6, 3, 5, 6, 10, planks, planks, false);

            for (int i = 4; i >= 1; --i) {
                this.placeBlock(level, planks, i, 2 + i, 7 - i, boundingBox);

                for (int z = 8 - i; z <= 10; ++z) {
                    this.placeBlock(level, stairsE, i, 2 + i, z, boundingBox);
                }
            }

            this.placeBlock(level, planks, 6, 6, 3, boundingBox);
            this.placeBlock(level, planks, 7, 5, 4, boundingBox);
            this.placeBlock(level, stairsW, 6, 6, 4, boundingBox);

            for (int i = 6; i <= 8; ++i) {
                for (int z = 5; z <= 10; ++z) {
                    this.placeBlock(level, stairsW, i, 12 - i, z, boundingBox);
                }
            }

            this.placeBlock(level, log, 0, 2, 1, boundingBox);
            this.placeBlock(level, log, 0, 2, 4, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 0, 2, 3, boundingBox);
            this.placeBlock(level, log, 4, 2, 0, boundingBox);
            this.placeBlock(level, GLASS_PANE, 5, 2, 0, boundingBox);
            this.placeBlock(level, log, 6, 2, 0, boundingBox);
            this.placeBlock(level, log, 8, 2, 1, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 2, 2, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 2, 3, boundingBox);
            this.placeBlock(level, log, 8, 2, 4, boundingBox);
            this.placeBlock(level, planks, 8, 2, 5, boundingBox);
            this.placeBlock(level, log, 8, 2, 6, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 2, 7, boundingBox);
            this.placeBlock(level, GLASS_PANE, 8, 2, 8, boundingBox);
            this.placeBlock(level, log, 8, 2, 9, boundingBox);
            this.placeBlock(level, log, 2, 2, 6, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 2, 7, boundingBox);
            this.placeBlock(level, GLASS_PANE, 2, 2, 8, boundingBox);
            this.placeBlock(level, log, 2, 2, 9, boundingBox);
            this.placeBlock(level, log, 4, 4, 10, boundingBox);
            this.placeBlock(level, GLASS_PANE, 5, 4, 10, boundingBox);
            this.placeBlock(level, log, 6, 4, 10, boundingBox);
            this.placeBlock(level, planks, 5, 5, 10, boundingBox);
            this.placeBlock(level, BlockState.AIR, 2, 1, 0, boundingBox);
            this.placeBlock(level, BlockState.AIR, 2, 2, 0, boundingBox);
            this.placeTorch(level, BlockFace.NORTH, 2, 3, 1, boundingBox);
            this.placeDoor(level, boundingBox, random, 2, 1, 0, BlockFace.NORTH);
            this.generateBox(level, boundingBox, 1, 0, -1, 3, 2, -1, BlockState.AIR, BlockState.AIR, false);

            if (this.getBlock(level, 2, 0, -1, boundingBox).equals(BlockState.AIR) && !this.getBlock(level, 2, -1, -1, boundingBox).equals(BlockState.AIR)) {
                this.placeBlock(level, stairsN, 2, 0, -1, boundingBox);

                if (this.getBlock(level, 2, -1, -1, boundingBox).getId() == Block.GRASS_PATH) {
                    this.placeBlock(level, GRASS, 2, -1, -1, boundingBox);
                }
            }

            for (int x = 0; x < 9; ++x) {
                for (int z = 0; z < 5; ++z) {
                    this.fillAirColumnUp(level, x, 7, z, boundingBox);
                    this.fillColumnDown(level, cobble, x, -1, z, boundingBox);
                }
            }

            for (int x = 2; x < 9; ++x) {
                for (int z = 5; z < 11; ++z) {
                    this.fillAirColumnUp(level, x, 7, z, boundingBox);
                    this.fillColumnDown(level, cobble, x, -1, z, boundingBox);
                }
            }

            if (this.type == PopulatorVillage.Type.COLD) { //BE
                //\\ StructureHelpers::createChest(v7, v6, 5, 1, 9, v279, (unsigned __int64)&Dst, v284, v285, v288, v289);
                BlockFace orientation = this.getOrientation();
                this.placeBlock(level, new BlockState(Block.CHEST, (orientation == null ? BlockFace.NORTH : orientation).getOpposite().getIndex()), 5, 1, 9, boundingBox);

                BlockVector3 vec = new BlockVector3(this.getWorldX(5, 9), this.getWorldY(1), this.getWorldZ(5, 9));
                if (boundingBox.isInside(vec)) {
                    BaseFullChunk chunk = level.getChunk(vec.x >> 4, vec.z >> 4);
                    if (chunk != null) {
                        CompoundTag nbt = BlockEntity.getDefaultCompound(vec.asVector3(), BlockEntity.CHEST);
                        ListTag<CompoundTag> itemList = new ListTag<>("Items");
                        VillageTwoRoomHouseChest.get().create(itemList, random);
                        nbt.putList(itemList);
                        Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                    }
                }
            }

            this.spawnVillagers(level, boundingBox, 4, 1, 2, 2);
            return true;
        }
    }

    public static class LightPost extends VillagePiece {

        public LightPost(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
        }

        public LightPost(CompoundTag tag) {
            super(tag);
        }

        @Override
        public String getType() {
            return "ViL";
        }

        public static BoundingBox findPieceBox(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation) {
            BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 3, 4, 2, orientation);
            return StructurePiece.findCollisionPiece(pieces, boundingBox) != null ? null : boundingBox;
        }

        @Override
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            if (this.horizPos < 0) {
                this.horizPos = this.getAverageGroundHeight(level, boundingBox);

                if (this.horizPos < 0) {
                    return true;
                }

                this.boundingBox.move(0, this.horizPos - this.boundingBox.y1 + 4 - 1, 0);
            }

            BlockState fence = this.getSpecificBlock(OAK_FENCE);
            this.generateBox(level, boundingBox, 0, 0, 0, 2, 3, 1, BlockState.AIR, BlockState.AIR, false);
            this.placeBlock(level, fence, 1, 0, 0, boundingBox);
            this.placeBlock(level, fence, 1, 1, 0, boundingBox);
            this.placeBlock(level, fence, 1, 2, 0, boundingBox);
            this.placeBlock(level, BLACK_WOOL, 1, 3, 0, boundingBox);
            this.placeTorch(level, BlockFace.EAST, 2, 3, 0, boundingBox);
            this.placeTorch(level, BlockFace.NORTH, 1, 3, 1, boundingBox);
            this.placeTorch(level, BlockFace.WEST, 0, 3, 0, boundingBox);
            this.placeTorch(level, BlockFace.SOUTH, 1, 3, -1, boundingBox);
            return true;
        }
    }

    public static class StraightRoad extends Road {

        private final int length;

        public StraightRoad(StartPiece start, int genDepth, NukkitRandom random, BoundingBox boundingBox, BlockFace orientation) {
            super(start, genDepth);
            this.setOrientation(orientation);
            this.boundingBox = boundingBox;
            this.length = Math.max(boundingBox.getXSpan(), boundingBox.getZSpan());
        }

        public StraightRoad(CompoundTag tag) {
            super(tag);
            this.length = tag.getInt("Length");
        }

        @Override
        public String getType() {
            return "ViSR";
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putInt("Length", this.length);
        }

        @Override //\\ StraightRoad::addChildren(StructurePiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &)
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
            boolean success = false;

            for (int offset = random.nextBoundedInt(5); offset < this.length - 8; offset += 2 + random.nextBoundedInt(5)) {
                StructurePiece result = this.generateChildLeft((StartPiece) piece, pieces, random, 0, offset);
                if (result != null) {
                    offset += Math.max(result.getBoundingBox().getXSpan(), result.getBoundingBox().getZSpan());
                    success = true;
                }
            }

            for (int offset = random.nextBoundedInt(5); offset < this.length - 8; offset += 2 + random.nextBoundedInt(5)) {
                StructurePiece result = this.generateChildRight((StartPiece) piece, pieces, random, 0, offset);
                if (result != null) {
                    offset += Math.max(result.getBoundingBox().getXSpan(), result.getBoundingBox().getZSpan());
                    success = true;
                }
            }

            BlockFace orientation = this.getOrientation();

            if (success && random.nextBoundedInt(3) > 0 && orientation != null) {
                switch (orientation) {
                    case NORTH:
                    default:
                        generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0, BlockFace.WEST, this.getGenDepth());
                        break;
                    case SOUTH:
                        generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z1 - 2, BlockFace.WEST, this.getGenDepth());
                        break;
                    case WEST:
                        generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 - 1, BlockFace.NORTH, this.getGenDepth());
                        break;
                    case EAST:
                        generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x1 - 2, this.boundingBox.y0, this.boundingBox.z0 - 1, BlockFace.NORTH, this.getGenDepth());
                }
            }

            if (success && random.nextBoundedInt(3) > 0 && orientation != null) {
                switch (orientation) {
                    case NORTH:
                    default:
                        generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0, BlockFace.EAST, this.getGenDepth());
                        break;
                    case SOUTH:
                        generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z1 - 2, BlockFace.EAST, this.getGenDepth());
                        break;
                    case WEST:
                        generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z1 + 1, BlockFace.SOUTH, this.getGenDepth());
                        break;
                    case EAST:
                        generateAndAddRoadPiece((StartPiece) piece, pieces, random, this.boundingBox.x1 - 2, this.boundingBox.y0, this.boundingBox.z1 + 1, BlockFace.SOUTH, this.getGenDepth());
                }
            }
        }

        //\\ StraightRoad::findPieceBox(StartPiece *,std::vector<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>,std::allocator<std::unique_ptr<StructurePiece,std::default_delete<StructurePiece>>>> &,Random &,int,int,int,int)
        public static BoundingBox findPieceBox(StartPiece start, List<StructurePiece> pieces, NukkitRandom random, int x, int y, int z, BlockFace orientation) {
            for (int i = 7 * Mth.nextInt(random, 3, 5); i >= 7; i -= 7) {
                BoundingBox boundingBox = BoundingBox.orientBox(x, y, z, 0, 0, 0, 3, 3, i, orientation);

                if (StructurePiece.findCollisionPiece(pieces, boundingBox) == null) {
                    return boundingBox;
                }
            }

            return null;
        }

        @Override //\\ StraightRoad::postProcess(BlockSource *,Random &,BoundingBox const &)
        public boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
            BlockState path = this.getSpecificBlock(GRASS_PATH);
            BlockState planks = this.getSpecificBlock(PLANKS);
            BlockState gravel = this.getSpecificBlock(GRAVEL);
            BlockState cobble = this.getSpecificBlock(COBBLESTONE);

            for (int x = this.boundingBox.x0; x <= this.boundingBox.x1; ++x) {
                for (int z = this.boundingBox.z0; z <= this.boundingBox.z1; ++z) {
                    BlockVector3 vec = new BlockVector3(x, 64 + this.yOffset, z);

                    if (boundingBox.isInside(vec)) {
                        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
                        if (chunk == null) {
                            vec.y = 63 - 1 + this.yOffset;
                        } else {
                            int cx = x & 0xf;
                            int cz = z & 0xf;
                            int y = chunk.getHighestBlockAt(cx, cz);
                            int id = chunk.getBlockId(cx, y, cz);
                            while (Block.transparent[id] && y > 63 - 1 + this.yOffset) {
                                id = chunk.getBlockId(cx, --y, cz);
                            }
                            vec.y = y;
                        }

                        if (vec.y < 63 + this.yOffset) {
                            vec.y = 63 - 1 + this.yOffset;
                        }

                        while (vec.y >= 63 - 1 + this.yOffset) {
                            int block = level.getBlockIdAt(vec.x, vec.y, vec.z);

                            if (block == Block.GRASS && level.getBlockIdAt(vec.x, vec.y + 1, vec.z) == Block.AIR) {
                                level.setBlockAt(vec.x, vec.y, vec.z, path.getId(), path.getMeta());
                                break;
                            }
                            if (block == Block.WATER || block == Block.STILL_WATER || block == Block.LAVA || block == Block.STILL_LAVA) {
                                level.setBlockAt(vec.x, vec.y, vec.z, planks.getId(), planks.getMeta());
                                break;
                            }
                            if (block == Block.SAND || block == Block.SANDSTONE || block == Block.RED_SANDSTONE) {
                                level.setBlockAt(vec.x, vec.y, vec.z, gravel.getId(), gravel.getMeta());
                                level.setBlockAt(vec.x, vec.y - 1, vec.z, cobble.getId(), cobble.getMeta());
                                break;
                            }

                            --vec.y;
                        }
                    }
                }
            }

            return true;
        }
    }

    public static void init() {
        //NOOP
    }
}
