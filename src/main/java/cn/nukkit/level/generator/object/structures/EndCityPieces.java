package cn.nukkit.level.generator.object.structures;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockBrewingStand;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockFrame;
import cn.nukkit.block.BlockStandingBanner;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStructureBlock;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.structure.AbstractStructure;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.types.Rotation;
import cn.nukkit.registry.Registries;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.random.RandomSourceProvider;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public final class EndCityPieces {

    private static final int MAX_GEN_DEPTH = 8;
    private static final int[][] TOWER_BRIDGES = {
            {Rotation.NONE.ordinal(), 1, -1, 0},
            {Rotation.ROTATE_90.ordinal(), 6, -1, 1},
            {Rotation.ROTATE_270.ordinal(), 0, -1, 5},
            {Rotation.ROTATE_180.ordinal(), 5, -1, 6}
    };
    private static final int[][] FAT_TOWER_BRIDGES = {
            {Rotation.NONE.ordinal(), 4, -1, 0},
            {Rotation.ROTATE_90.ordinal(), 12, -1, 4},
            {Rotation.ROTATE_270.ordinal(), 0, -1, 8},
            {Rotation.ROTATE_180.ordinal(), 8, -1, 12}
    };
    private static final EndCityLoot END_CITY_LOOT = new EndCityLoot();
    private static final SectionGenerator HOUSE_TOWER_GENERATOR = new SectionGenerator() {
        @Override
        public boolean generate(int genDepth, EndCityPiece parent, BlockVector3 offset, List<EndCityPiece> pieces, RandomSourceProvider random) {
            if (genDepth > MAX_GEN_DEPTH) {
                return false;
            }

            Rotation rotation = parent.rotation();
            EndCityPiece lastPiece = addHelper(pieces, addPiece(parent, offset, "base_floor", rotation, true));
            int numFloors = random.nextInt(3);
            if (numFloors == 0) {
                addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 4, -1), "base_roof", rotation, true));
            } else if (numFloors == 1) {
                lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 0, -1), "second_floor_2", rotation, false));
                lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 8, -1), "second_roof", rotation, false));
                recursiveChildren(TOWER_GENERATOR, genDepth + 1, lastPiece, null, pieces, random);
            } else {
                lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 0, -1), "second_floor_2", rotation, false));
                lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 4, -1), "third_floor_2", rotation, false));
                lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 8, -1), "third_roof", rotation, true));
                recursiveChildren(TOWER_GENERATOR, genDepth + 1, lastPiece, null, pieces, random);
            }
            return true;
        }
    };
    private static final SectionGenerator TOWER_GENERATOR = new SectionGenerator() {
        @Override
        public boolean generate(int genDepth, EndCityPiece parent, BlockVector3 offset, List<EndCityPiece> pieces, RandomSourceProvider random) {
            Rotation rotation = parent.rotation();
            EndCityPiece lastPiece = addHelper(
                    pieces,
                    addPiece(parent, new BlockVector3(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", rotation, true)
            );
            lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(0, 7, 0), "tower_piece", rotation, true));
            EndCityPiece bridgePiece = random.nextInt(3) == 0 ? lastPiece : null;
            int towerHeight = 1 + random.nextInt(3);

            for (int i = 0; i < towerHeight; i++) {
                lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(0, 4, 0), "tower_piece", rotation, true));
                if (i < towerHeight - 1 && random.nextBoolean()) {
                    bridgePiece = lastPiece;
                }
            }

            if (bridgePiece != null) {
                for (int[] bridge : TOWER_BRIDGES) {
                    if (random.nextBoolean()) {
                        EndCityPiece bridgeStart = addHelper(
                                pieces,
                                addPiece(
                                        bridgePiece,
                                        new BlockVector3(bridge[1], bridge[2], bridge[3]),
                                        "bridge_end",
                                        rotate(rotation, Rotation.from(bridge[0])),
                                        true
                                )
                        );
                        recursiveChildren(TOWER_BRIDGE_GENERATOR, genDepth + 1, bridgeStart, null, pieces, random);
                    }
                }

                addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 4, -1), "tower_top", rotation, true));
            } else {
                if (genDepth != 7) {
                    return recursiveChildren(FAT_TOWER_GENERATOR, genDepth + 1, lastPiece, null, pieces, random);
                }

                addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 4, -1), "tower_top", rotation, true));
            }

            return true;
        }
    };
    private static final SectionGenerator TOWER_BRIDGE_GENERATOR = new SectionGenerator() {
        private boolean shipCreated;

        @Override
        public void init() {
            this.shipCreated = false;
        }

        @Override
        public boolean generate(int genDepth, EndCityPiece parent, BlockVector3 offset, List<EndCityPiece> pieces, RandomSourceProvider random) {
            Rotation rotation = parent.rotation();
            int bridgeLength = random.nextInt(4) + 1;
            EndCityPiece lastPiece = addHelper(pieces, addPiece(parent, new BlockVector3(0, 0, -4), "bridge_piece", rotation, true));
            lastPiece.setGenDepth(-1);
            int nextY = 0;

            for (int i = 0; i < bridgeLength; i++) {
                if (random.nextBoolean()) {
                    lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(0, nextY, -4), "bridge_piece", rotation, true));
                    nextY = 0;
                } else {
                    if (random.nextBoolean()) {
                        lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(0, nextY, -4), "bridge_steep_stairs", rotation, true));
                    } else {
                        lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(0, nextY, -8), "bridge_gentle_stairs", rotation, true));
                    }
                    nextY = 4;
                }
            }

            if (!this.shipCreated && random.nextInt(10 - genDepth) == 0) {
                addHelper(
                        pieces,
                        addPiece(lastPiece, new BlockVector3(-8 + random.nextInt(8), nextY, -70 + random.nextInt(10)), "ship", rotation, true)
                );
                this.shipCreated = true;
            } else if (!recursiveChildren(HOUSE_TOWER_GENERATOR, genDepth + 1, lastPiece, new BlockVector3(-3, nextY + 1, -11), pieces, random)) {
                return false;
            }

            lastPiece = addHelper(
                    pieces,
                    addPiece(lastPiece, new BlockVector3(4, nextY, 0), "bridge_end", rotate(rotation, Rotation.ROTATE_180), true)
            );
            lastPiece.setGenDepth(-1);
            return true;
        }
    };
    private static final SectionGenerator FAT_TOWER_GENERATOR = new SectionGenerator() {
        @Override
        public boolean generate(int genDepth, EndCityPiece parent, BlockVector3 offset, List<EndCityPiece> pieces, RandomSourceProvider random) {
            Rotation rotation = parent.rotation();
            EndCityPiece lastPiece = addHelper(pieces, addPiece(parent, new BlockVector3(-3, 4, -3), "fat_tower_base", rotation, true));
            lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(0, 4, 0), "fat_tower_middle", rotation, true));

            for (int i = 0; i < 2 && random.nextInt(3) != 0; i++) {
                lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(0, 8, 0), "fat_tower_middle", rotation, true));

                for (int[] bridge : FAT_TOWER_BRIDGES) {
                    if (random.nextBoolean()) {
                        EndCityPiece bridgeStart = addHelper(
                                pieces,
                                addPiece(
                                        lastPiece,
                                        new BlockVector3(bridge[1], bridge[2], bridge[3]),
                                        "bridge_end",
                                        rotate(rotation, Rotation.from(bridge[0])),
                                        true
                                )
                        );
                        recursiveChildren(TOWER_BRIDGE_GENERATOR, genDepth + 1, bridgeStart, null, pieces, random);
                    }
                }
            }

            addHelper(pieces, addPiece(lastPiece, new BlockVector3(-2, 8, -2), "fat_tower_top", rotation, true));
            return true;
        }
    };

    private EndCityPieces() {
    }

    public static List<EndCityPiece> generate(BlockVector3 origin, Rotation rotation, RandomSourceProvider random) {
        FAT_TOWER_GENERATOR.init();
        HOUSE_TOWER_GENERATOR.init();
        TOWER_BRIDGE_GENERATOR.init();
        TOWER_GENERATOR.init();

        List<EndCityPiece> pieces = new ArrayList<>();
        EndCityPiece lastPiece = addHelper(pieces, new EndCityPiece("base_floor", origin, rotation, true));
        lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 0, -1), "second_floor_1", rotation, false));
        lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 4, -1), "third_floor_1", rotation, false));
        lastPiece = addHelper(pieces, addPiece(lastPiece, new BlockVector3(-1, 8, -1), "third_roof", rotation, true));
        recursiveChildren(TOWER_GENERATOR, 1, lastPiece, null, pieces, random);
        return pieces;
    }

    public static PostPlacement place(BlockManager manager, BlockVector3 origin, Rotation rotation, RandomSourceProvider random) {
        List<EndCityPiece> pieces = generate(origin, rotation, random);
        List<BlockVector3> chests = new ArrayList<>();
        List<BlockVector3> banners = new ArrayList<>();
        List<BlockVector3> itemFrames = new ArrayList<>();
        List<BlockVector3> brewingStands = new ArrayList<>();
        List<BlockVector3> shulkerMarkers = new ArrayList<>();

        for (EndCityPiece piece : pieces) {
            piece.place(manager);
        }

        for (var block : manager.getBlocks()) {
            switch (block) {
                case BlockStructureBlock ignored -> {
                    shulkerMarkers.add(block.asBlockVector3());
                    manager.setBlockStateAt(block, BlockAir.STATE);
                }
                case BlockChest ignored -> {
                    chests.add(block.asBlockVector3());
                }
                case BlockStandingBanner ignored -> {
                    banners.add(block.asBlockVector3());
                }
                case BlockFrame ignored -> {
                    itemFrames.add(block.asBlockVector3());
                }
                case BlockBrewingStand ignored -> {
                    brewingStands.add(block.asBlockVector3());
                }
                default -> {}
            }
        }

        return new PostPlacement(chests, banners, itemFrames, brewingStands, shulkerMarkers);
    }

    public static void populatePlacedData(Level level, List<BlockVector3> chests, List<BlockVector3> banners, List<BlockVector3> itemFrames, List<BlockVector3> brewingStands, List<BlockVector3> shulkerMarkers, RandomSourceProvider random) {
        for (BlockVector3 chestPos : chests) {
            if (level.getBlock(chestPos.getX(), chestPos.getY(), chestPos.getZ()) instanceof BlockChest chest) {
                for(int i = 0; i < 3; i++) END_CITY_LOOT.create(chest.getOrCreateBlockEntity().getInventory(), random);
            }
        }
        for (BlockVector3 bannerPos : banners) {
            if (level.getBlock(bannerPos.getX(), bannerPos.getY(), bannerPos.getZ()) instanceof BlockStandingBanner bannerBlock) {
                BlockEntityBanner banner = bannerBlock.getBlockEntity();
                if (banner == null) {
                    banner = bannerBlock.createBlockEntity(new CompoundTag().putInt("Base", DyeColor.PURPLE.getDyeData() & 0x0f));
                }
                banner.setBaseColor(DyeColor.PURPLE);
                banner.spawnToAll();
            }
        }
        for (BlockVector3 framePos : itemFrames) {
            if (level.getBlock(framePos.getX(), framePos.getY(), framePos.getZ()) instanceof BlockFrame frameBlock) {
                BlockEntityItemFrame itemFrame = frameBlock.getOrCreateBlockEntity();
                itemFrame.setItem(Item.get(ItemID.ELYTRA));
                itemFrame.setItemRotation(0);
                itemFrame.spawnToAll();
            }
        }
        for (BlockVector3 brewingStandPos : brewingStands) {
            if (level.getBlock(brewingStandPos.getX(), brewingStandPos.getY(), brewingStandPos.getZ()) instanceof BlockBrewingStand brewingStand) {
                BlockEntityBrewingStand blockEntity = brewingStand.getOrCreateBlockEntity();
                blockEntity.getInventory().setResult(1, ItemPotion.fromPotion(PotionType.HEALING));
                blockEntity.getInventory().setResult(3, ItemPotion.fromPotion(PotionType.HEALING));
            }
        }
        for (BlockVector3 shulkerPos : shulkerMarkers) {
            if (level.getBlock(shulkerPos.getX(), shulkerPos.getY() - 1, shulkerPos.getZ()) instanceof BlockChest) {
                continue;
            }
            Entity shulker = Entity.createEntity(EntityID.SHULKER, new Position(
                    shulkerPos.getX() + 0.5,
                    shulkerPos.getY(),
                    shulkerPos.getZ() + 0.5,
                    level
            )); 
            if (shulker != null) {
                shulker.setPersistent(true);
                shulker.spawnToAll();
            }
        }
    }

    private static EndCityPiece addPiece(EndCityPiece parent, BlockVector3 offset, String templateName, Rotation rotation, boolean overwrite) {
        EndCityPiece child = new EndCityPiece(templateName, parent.templatePosition(), rotation, overwrite);
        BlockVector3 parentOffset = transform(offset, parent.sizeX(), parent.sizeZ(), parent.rotation());
        BlockVector3 childOriginOffset = transform(new BlockVector3(0, 0, 0), child.sizeX(), child.sizeZ(), child.rotation());
        BlockVector3 origin = parent.templatePosition().add(parentOffset).subtract(childOriginOffset);
        child.move(origin.getX() - child.templatePosition().getX(), origin.getY() - child.templatePosition().getY(), origin.getZ() - child.templatePosition().getZ());
        return child;
    }

    private static EndCityPiece addHelper(List<EndCityPiece> pieces, EndCityPiece piece) {
        pieces.add(piece);
        return piece;
    }

    private static boolean recursiveChildren(
            SectionGenerator generator,
            int genDepth,
            EndCityPiece parent,
            BlockVector3 offset,
            List<EndCityPiece> pieces,
            RandomSourceProvider random
    ) {
        if (genDepth > MAX_GEN_DEPTH) {
            return false;
        }

        List<EndCityPiece> childPieces = Lists.newArrayList();
        if (generator.generate(genDepth, parent, offset, childPieces, random)) {
            boolean collision = false;
            int childTag = random.nextInt();

            for (EndCityPiece child : childPieces) {
                child.setGenDepth(childTag);
                EndCityPiece collisionPiece = findCollisionPiece(pieces, child.boundingBox());
                if (collisionPiece != null && collisionPiece.getGenDepth() != parent.getGenDepth()) {
                    collision = true;
                    break;
                }
            }

            if (!collision) {
                pieces.addAll(childPieces);
                return true;
            }
        }

        return false;
    }

    private static EndCityPiece findCollisionPiece(List<EndCityPiece> pieces, BoundingBox boundingBox) {
        for (EndCityPiece piece : pieces) {
            if (piece.boundingBox().intersects(boundingBox)) {
                return piece;
            }
        }
        return null;
    }

    private static Rotation rotate(Rotation base, Rotation add) {
        return Rotation.from((base.ordinal() + add.ordinal()) & 3);
    }

    private static BlockVector3 transform(BlockVector3 vector, int sizeX, int sizeZ, Rotation rotation) {
        return switch (rotation) {
            case ROTATE_90 -> new BlockVector3(sizeZ - 1 - vector.getZ(), vector.getY(), vector.getX());
            case ROTATE_180 -> new BlockVector3(sizeX - 1 - vector.getX(), vector.getY(), sizeZ - 1 - vector.getZ());
            case ROTATE_270 -> new BlockVector3(vector.getZ(), vector.getY(), sizeX - 1 - vector.getX());
            default -> vector;
        };
    }

    private static Rotation toTemplateRotation(Rotation rotation) {
        return switch (rotation) {
            case ROTATE_90 -> Rotation.ROTATE_270;
            case ROTATE_270 -> Rotation.ROTATE_90;
            default -> rotation;
        };
    }

    public static final class PostPlacement {
        private final List<BlockVector3> chests;
        private final List<BlockVector3> banners;
        private final List<BlockVector3> itemFrames;
        private final List<BlockVector3> brewingStands;
        private final List<BlockVector3> shulkerMarkers;

        private PostPlacement(List<BlockVector3> chests, List<BlockVector3> banners, List<BlockVector3> itemFrames, List<BlockVector3> brewingStands, List<BlockVector3> shulkerMarkers) {
            this.chests = List.copyOf(chests);
            this.banners = List.copyOf(banners);
            this.itemFrames = List.copyOf(itemFrames);
            this.brewingStands = List.copyOf(brewingStands);
            this.shulkerMarkers = List.copyOf(shulkerMarkers);
        }

        public List<BlockVector3> chests() {
            return this.chests;
        }

        public List<BlockVector3> banners() {
            return this.banners;
        }

        public List<BlockVector3> itemFrames() {
            return this.itemFrames;
        }

        public List<BlockVector3> brewingStands() {
            return this.brewingStands;
        }

        public List<BlockVector3> shulkerMarkers() {
            return this.shulkerMarkers;
        }
    }

    public static final class EndCityPiece {
        private final String templateName;
        private final PNXStructure template;
        private final int sizeX;
        private final int sizeZ;
        private final Rotation rotation;
        private final boolean overwrite;
        private BoundingBox boundingBox;
        private BlockVector3 templatePosition;
        private int genDepth;

        public EndCityPiece(String templateName, BlockVector3 position, Rotation rotation, boolean overwrite) {
            this.templateName = templateName;
            this.rotation = rotation;
            this.overwrite = overwrite;
            this.templatePosition = position;
            PNXStructure baseTemplate = loadTemplate(templateName, Rotation.NONE);
            this.sizeX = baseTemplate.getSizeX();
            this.sizeZ = baseTemplate.getSizeZ();
            if(this.templateName.equals("ship")) {
                BlockState ITEM_FRAME = BlockFrame.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(BlockFace.SOUTH.getIndex()));
                baseTemplate.setBlock(
                        6,
                        5,
                        7,
                        ITEM_FRAME
                );
            }
            this.template = rotation == Rotation.NONE ? baseTemplate : baseTemplate.rotate(toTemplateRotation(rotation), rotation);
            this.boundingBox = createBoundingBox(position, this.template);
        }

        public void move(int x, int y, int z) {
            this.templatePosition = this.templatePosition.add(x, y, z);
            this.boundingBox.move(x, y, z);
        }

        public void place(BlockManager manager) {
            for (PNXStructure.StructureBlockInstance block : this.template.getBlockInstances()) {
                if (!this.overwrite && block.state.equals(BlockAir.STATE)) {
                    continue;
                }
                manager.setBlockStateAt(
                        this.templatePosition.getX() + block.x,
                        this.templatePosition.getY() + block.y,
                        this.templatePosition.getZ() + block.z,
                        block.state
                );
            }
        }

        public Rotation rotation() {
            return this.rotation;
        }

        public int sizeX() {
            return this.sizeX;
        }

        public int sizeZ() {
            return this.sizeZ;
        }

        public BoundingBox boundingBox() {
            return this.boundingBox;
        }

        public BlockVector3 templatePosition() {
            return this.templatePosition;
        }

        public int getGenDepth() {
            return this.genDepth;
        }

        public void setGenDepth(int genDepth) {
            this.genDepth = genDepth;
        }

        private static PNXStructure loadTemplate(String templateName, Rotation rotation) {
            AbstractStructure structure = Registries.STRUCTURE.get("end_city/" + templateName);
            if (structure == null) {
                structure = Registries.STRUCTURE.get("endcity/" + templateName);
            }
            if (!(structure instanceof PNXStructure pnxStructure)) {
                throw new IllegalStateException("Missing End City structure part: " + templateName);
            }
            return rotation == Rotation.NONE ? pnxStructure : pnxStructure.rotate(rotation);
        }

        private static BoundingBox createBoundingBox(BlockVector3 position, PNXStructure template) {
            return new BoundingBox(
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    position.getX() + template.getSizeX() - 1,
                    position.getY() + template.getSizeY() - 1,
                    position.getZ() + template.getSizeZ() - 1
            );
        }

    }

    private interface SectionGenerator {
        default void init() {
        }

        boolean generate(int genDepth, EndCityPiece parent, BlockVector3 offset, List<EndCityPiece> pieces, RandomSourceProvider random);
    }

    private static final class EndCityLoot extends RandomizableContainer {
        private EndCityLoot() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.DIAMOND, 0, 7, 2, 5))
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 8, 4, 10))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 7, 2, 15))
                    .register(new ItemEntry(Item.EMERALD, 0, 6, 2, 2))
                    .register(new ItemEntry(Item.BEETROOT_SEEDS, 0, 10, 1, 5))
                    .register(new ItemEntry(Item.SADDLE, 3))
                    .register(new ItemEntry(Item.COPPER_HORSE_ARMOR, 1))
                    .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 1))
                    .register(new ItemEntry(Item.GOLDEN_HORSE_ARMOR, 1))
                    .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 1))
                    .register(new ItemEntry(Item.DIAMOND_SWORD, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_SPEAR, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_BOOTS, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_CHESTPLATE, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_LEGGINGS, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_HELMET, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_PICKAXE, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_SHOVEL, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.IRON_SWORD, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.IRON_BOOTS, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.IRON_CHESTPLATE, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.IRON_LEGGINGS, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.IRON_HELMET, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.IRON_PICKAXE, 0, 1, 1, 3, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.IRON_SHOVEL, 0, 1, 1, 3, getDefaultEnchantments()));
            this.pools.put(pool1.build(), new RollEntry(6, 2, pool1.getTotalWeight()));
        }

        @Override
        public void create(Inventory inventory, RandomSourceProvider random) {
            try {
                List<Integer> freeSlots = new ArrayList<>(inventory.getSize());
                for (int i = 0; i < inventory.getSize(); i++) {
                    freeSlots.add(i);
                }

                this.pools.forEach((pool, roll) -> {
                    int rolls = roll.getMin() == -1 ? roll.getMax() : NukkitMath.randomRange(random, roll.getMin(), roll.getMax());
                    for (int i = 0; i < rolls && !freeSlots.isEmpty(); i++) {
                        int result = random.nextBoundedInt(roll.getTotalWeight());
                        for (ItemEntry entry : pool) {
                            result -= entry.getWeight();
                            if (result < 0) {
                                int slotIndex = random.nextBoundedInt(freeSlots.size());
                                int inventorySlot = freeSlots.remove(slotIndex);
                                Item item = Item.get(entry.getId(), entry.getMeta(), NukkitMath.randomRange(random, entry.getMinCount(), entry.getMaxCount()));
                                applyRandomEnchantment(item, entry.getEnchantments(), random);
                                inventory.setItem(inventorySlot, item);
                                break;
                            }
                        }
                    }
                });

                if (random.nextBoundedInt(15) == 0 && !freeSlots.isEmpty()) {
                    int inventorySlot = freeSlots.remove(random.nextBoundedInt(freeSlots.size()));
                    inventory.setItem(inventorySlot, Item.get(Item.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
