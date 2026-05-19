package cn.nukkit.level.generator.object.structures.jigsaw.ancientcity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockSculkShrieker;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.JigsawStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.random.RandomSourceProvider;
import cn.nukkit.utils.random.Xoroshiro128;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.block.BlockID.CANDLE;
import static cn.nukkit.block.BlockID.PACKED_ICE;
import static cn.nukkit.block.BlockID.SCULK;
import static cn.nukkit.block.BlockID.SCULK_CATALYST;
import static cn.nukkit.block.BlockID.SCULK_SENSOR;
import static cn.nukkit.block.BlockID.SOUL_TORCH;
import static cn.nukkit.block.property.CommonBlockProperties.CAN_SUMMON;

public class AncientCityStructure extends JigsawStructure {

    public static final String START = "ancient_city/city_center";

    private static final BlockState SCULK_SHRIEKER = BlockSculkShrieker.PROPERTIES.getBlockState(
            CAN_SUMMON.createValue(true)
    );

    private static final int BEARD_KERNEL_RADIUS = 12;
    private static final int BEARD_KERNEL_SIZE = BEARD_KERNEL_RADIUS * 2;
    private static final double BEARD_CARVE_THRESHOLD = -0.03;
    private static final double[] BEARD_KERNEL = createBeardKernel();
    private static final AncientCityChestPopulator CITY_CHEST = new AncientCityChestPopulator();
    private static final AncientCityIceBoxPopulator ICE_BOX_CHEST = new AncientCityIceBoxPopulator();

    private static final StructurePoolCollection COLLECTION;

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put(START, pool(
                START,
                entry("ancient_city/city_center/city_center_1", 1),
                entry("ancient_city/city_center/city_center_2", 1),
                entry("ancient_city/city_center/city_center_3", 1)
        ));

        COLLECTION.put("ancient_city/city/entrance", pool(
                "ancient_city/city/entrance",
                entry("ancient_city/city/entrance/entrance_connector", 1),
                entry("ancient_city/city/entrance/entrance_path_1", 1),
                entry("ancient_city/city/entrance/entrance_path_2", 1),
                entry("ancient_city/city/entrance/entrance_path_3", 1),
                entry("ancient_city/city/entrance/entrance_path_4", 1),
                entry("ancient_city/city/entrance/entrance_path_5", 1)
        ));

        COLLECTION.put("ancient_city/structures", pool(
                "ancient_city/structures",
                entry("empty", 7),
                entry("ancient_city/structures/barracks", 4),
                entry("ancient_city/structures/chamber_1", 4),
                entry("ancient_city/structures/chamber_2", 4),
                entry("ancient_city/structures/chamber_3", 4),
                entry("ancient_city/structures/sauna_1", 4),
                entry("ancient_city/structures/small_statue", 4),
                entry("ancient_city/structures/large_ruin_1", 1),
                entry("ancient_city/structures/tall_ruin_1", 1),
                entry("ancient_city/structures/tall_ruin_2", 1),
                entry("ancient_city/structures/tall_ruin_3", 2),
                entry("ancient_city/structures/tall_ruin_4", 2),
                entry("ancient_city/structures/camp_1", 1),
                entry("ancient_city/structures/camp_2", 1),
                entry("ancient_city/structures/camp_3", 1),
                entry("ancient_city/structures/medium_ruin_1", 1),
                entry("ancient_city/structures/medium_ruin_2", 1),
                entry("ancient_city/structures/small_ruin_1", 1),
                entry("ancient_city/structures/small_ruin_2", 1),
                entry("ancient_city/structures/large_pillar_1", 1),
                entry("ancient_city/structures/medium_pillar_1", 1),
                entry("ancient_city/structures/ice_box_1", 1)
        ));

        COLLECTION.put("ancient_city/sculk", pool(
                "ancient_city/sculk",
                entry("empty", 7)
        ));

        COLLECTION.put("ancient_city/walls", pool(
                "ancient_city/walls",
                entry("ancient_city/walls/intact_corner_wall_1", 1),
                entry("ancient_city/walls/intact_intersection_wall_1", 1),
                entry("ancient_city/walls/intact_lshape_wall_1", 1),
                entry("ancient_city/walls/intact_horizontal_wall_1", 1),
                entry("ancient_city/walls/intact_horizontal_wall_2", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_1", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_2", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_3", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_4", 4),
                entry("ancient_city/walls/intact_horizontal_wall_passage_1", 3),
                entry("ancient_city/walls/ruined_corner_wall_1", 1),
                entry("ancient_city/walls/ruined_corner_wall_2", 1),
                entry("ancient_city/walls/ruined_horizontal_wall_stairs_1", 2),
                entry("ancient_city/walls/ruined_horizontal_wall_stairs_2", 2),
                entry("ancient_city/walls/ruined_horizontal_wall_stairs_3", 3),
                entry("ancient_city/walls/ruined_horizontal_wall_stairs_4", 3)
        ));

        COLLECTION.put("ancient_city/walls/no_corners", pool(
                "ancient_city/walls/no_corners",
                entry("ancient_city/walls/intact_horizontal_wall_1", 1),
                entry("ancient_city/walls/intact_horizontal_wall_2", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_1", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_2", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_3", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_4", 1),
                entry("ancient_city/walls/intact_horizontal_wall_stairs_5", 1),
                entry("ancient_city/walls/intact_horizontal_wall_bridge", 1)
        ));

        COLLECTION.put("ancient_city/city_center/walls", pool(
                "ancient_city/city_center/walls",
                entry("ancient_city/city_center/walls/bottom_1", 1),
                entry("ancient_city/city_center/walls/bottom_2", 1),
                entry("ancient_city/city_center/walls/bottom_left_corner", 1),
                entry("ancient_city/city_center/walls/bottom_right_corner_1", 1),
                entry("ancient_city/city_center/walls/bottom_right_corner_2", 1),
                entry("ancient_city/city_center/walls/left", 1),
                entry("ancient_city/city_center/walls/right", 1),
                entry("ancient_city/city_center/walls/top", 1),
                entry("ancient_city/city_center/walls/top_left_corner", 1),
                entry("ancient_city/city_center/walls/top_right_corner", 1)
        ));
    }

    @Override
    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        Level level = blockManager.getLevel();
        for(Block block : blockManager.getBlocks()) {
            if(block.getId().equals(Block.JIGSAW)) blockManager.setBlockStateAt(block, SCULK_SHRIEKER);
        }

        RandomizableContainer loot = structureName.contains("ice_box") ? ICE_BOX_CHEST : CITY_CHEST;
        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockChest chest) {
                blockManager.addHook(() -> {
                    loot.create(chest.getOrCreateBlockEntity().getInventory(), createRandom(level, block.asBlockVector3()));
                });
            }
        }
    }


    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    public String getEntryPool() {
        return START;
    }

    @Override
    protected int getMaxDepth() {
        return 7;
    }

    @Override
    protected void postProcessStructure(StructureHelper helper, List<BoundingBox> occupiedBoxes) {
        applyBeardBoxTerrainAdjustment(helper, occupiedBoxes);
        helper.applySubChunkUpdate();
    }

    private void applyBeardBoxTerrainAdjustment(StructureHelper helper, List<BoundingBox> occupiedBoxes) {
        BlockVector3 origin = helper.getOrigin();
        int minHeight = helper.getMinHeight();
        int maxHeight = helper.getMaxHeight() - 1;
        List<BoundingBox> absoluteBoxes = new ArrayList<>(occupiedBoxes.size());

        for (BoundingBox relativeBox : occupiedBoxes) {
            absoluteBoxes.add(relativeBox.moved(origin.getX(), origin.getY(), origin.getZ()));
        }

        for (BoundingBox box : absoluteBoxes) {
            int minX = box.x0 - BEARD_KERNEL_RADIUS;
            int maxX = box.x1 + BEARD_KERNEL_RADIUS;
            int minY = Math.max(minHeight, box.y0 - BEARD_KERNEL_RADIUS);
            int maxY = Math.min(maxHeight, box.y1 + BEARD_KERNEL_RADIUS);
            int minZ = box.z0 - BEARD_KERNEL_RADIUS;
            int maxZ = box.z1 + BEARD_KERNEL_RADIUS;

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    int dx = Math.max(0, Math.max(box.x0 - x, x - box.x1));
                    int dz = Math.max(0, Math.max(box.z0 - z, z - box.z1));
                    if (dx >= BEARD_KERNEL_RADIUS || dz >= BEARD_KERNEL_RADIUS) {
                        continue;
                    }

                    for (int y = minY; y <= maxY; y++) {
                        BlockVector3 position = new BlockVector3(x, y, z);
                        if (helper.isCached(position)) {
                            continue;
                        }

                        int dy = Math.max(0, Math.max(box.y0 - y, y - box.y1));
                        if (dy >= BEARD_KERNEL_RADIUS) {
                            continue;
                        }

                        int yToGround = y - box.y0;
                        if (getBeardContribution(dx, dy, dz, yToGround) * 0.8 < BEARD_CARVE_THRESHOLD) {
                            helper.setBlockStateAt(x - origin.getX(), y - origin.getY(), z - origin.getZ(), BlockAir.STATE);
                        }
                    }
                }
            }
        }
    }

    private static double getBeardContribution(int dx, int dy, int dz, int yToGround) {
        int xi = dx + BEARD_KERNEL_RADIUS;
        int yi = dy + BEARD_KERNEL_RADIUS;
        int zi = dz + BEARD_KERNEL_RADIUS;
        if (!isInKernelRange(xi) || !isInKernelRange(yi) || !isInKernelRange(zi)) {
            return 0.0;
        }

        double dyWithOffset = yToGround + 0.5;
        double distanceSqr = dx * (double) dx + dyWithOffset * dyWithOffset + dz * (double) dz;
        double value = -dyWithOffset / Math.sqrt(distanceSqr / 2.0) / 2.0;
        return value * BEARD_KERNEL[zi * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE + xi * BEARD_KERNEL_SIZE + yi];
    }

    private static boolean isInKernelRange(int index) {
        return index >= 0 && index < BEARD_KERNEL_SIZE;
    }

    private static double[] createBeardKernel() {
        double[] kernel = new double[BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE];
        for (int zi = 0; zi < BEARD_KERNEL_SIZE; zi++) {
            for (int xi = 0; xi < BEARD_KERNEL_SIZE; xi++) {
                for (int yi = 0; yi < BEARD_KERNEL_SIZE; yi++) {
                    int dx = xi - BEARD_KERNEL_RADIUS;
                    double dy = yi - BEARD_KERNEL_RADIUS + 0.5;
                    int dz = zi - BEARD_KERNEL_RADIUS;
                    double distanceSqr = dx * (double) dx + dy * dy + dz * (double) dz;
                    kernel[zi * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE + xi * BEARD_KERNEL_SIZE + yi] = Math.exp(-distanceSqr / 16.0);
                }
            }
        }
        return kernel;
    }

    protected RandomSourceProvider createRandom(Level level, BlockVector3 pos) {
        long seed = level.getSeed();
        seed ^= 0x9E3779B97F4A7C15L * pos.getX();
        seed ^= 0xC2B2AE3D27D4EB4FL * pos.getY();
        seed ^= 0x165667B19E3779F9L * pos.getZ();
        return new Xoroshiro128(seed);
    }

    private static StructurePool pool(String name, StructurePool.Entry... entries) {
        return new StructurePool(name, entries);
    }

    private static StructurePool.Entry entry(String structureName, int weight) {
        return new StructurePool.Entry(structureName, weight);
    }

    protected static class AncientCityChestPopulator extends RandomizableContainer {

        private final List<SpecialItemEntry> primaryPool;
        private final List<SpecialItemEntry> trimPool;
        private final int primaryWeight;
        private final int trimWeight;

        protected AncientCityChestPopulator() {
            primaryPool = List.of(
                    item(Item.ENCHANTED_GOLDEN_APPLE, 1, 2, 1),
                    item(Item.MUSIC_DISC_OTHERSIDE, 1),
                    item(Item.COMPASS, 1, 1, 2),
                    item(SCULK_CATALYST, 1, 2, 2),
                    item(Item.NAME_TAG, 2),
                    damagedEnchanted(Item.DIAMOND_HOE, 1, 1, 2, 30, 50, true),
                    item(Item.LEAD, 1, 1, 2),
                    item(Item.DIAMOND_HORSE_ARMOR, 1, 1, 2),
                    item(Item.LEATHER, 1, 5, 2),
                    item(Item.MUSIC_DISC_13, 2),
                    item(Item.MUSIC_DISC_CAT, 2),
                    enchanted(Item.DIAMOND_LEGGINGS, 1, 1, 2, 30, 50, true),
                    swiftSneakBook(1, 3, 3),
                    item(SCULK, 4, 10, 3),
                    item(SCULK_SENSOR, 1, 3, 3),
                    item(CANDLE, 1, 4, 3),
                    item(Item.AMETHYST_SHARD, 1, 15, 3),
                    item(Item.EXPERIENCE_BOTTLE, 1, 3, 3),
                    item(Item.GLOW_BERRIES, 1, 15, 3),
                    enchanted(Item.IRON_LEGGINGS, 1, 1, 3, 20, 39, true),
                    item(Item.ECHO_SHARD, 1, 3, 4),
                    item(Item.DISC_FRAGMENT_5, 1, 3, 4),
                    potion(Item.POTION, PotionType.REGENERATION_STRONG.id(), 1, 3, 5),
                    randomEnchantedBook(5),
                    item(Item.BOOK, 3, 10, 5),
                    item(Item.BONE, 1, 15, 5),
                    item(SOUL_TORCH, 1, 15, 5),
                    item(Item.COAL, 6, 15, 7)
            );
            primaryWeight = totalWeight(primaryPool);

            trimPool = List.of(
                    empty(75),
                    item(Item.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, 4),
                    item(Item.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
            );
            trimWeight = totalWeight(trimPool);
        }

        @Override
        public void create(cn.nukkit.inventory.Inventory inventory, RandomSourceProvider random) {
            populatePool(inventory, random, primaryPool, primaryWeight, 5, 10);
            populatePool(inventory, random, trimPool, trimWeight, 1, 1);
        }
    }

    protected static class AncientCityIceBoxPopulator extends RandomizableContainer {

        private final List<SpecialItemEntry> pool;
        private final int totalWeight;

        protected AncientCityIceBoxPopulator() {
            pool = List.of(
                    suspiciousStew(2, 6, 1),
                    item(Item.GOLDEN_CARROT, 1, 10, 1),
                    item(Item.BAKED_POTATO, 1, 10, 1),
                    item(PACKED_ICE, 2, 6, 2),
                    item(Item.SNOWBALL, 2, 6, 4)
            );
            totalWeight = totalWeight(pool);
        }

        @Override
        public void create(cn.nukkit.inventory.Inventory inventory, RandomSourceProvider random) {
            populatePool(inventory, random, pool, totalWeight, 4, 10);
        }
    }

    private static void populatePool(cn.nukkit.inventory.Inventory inventory, RandomSourceProvider random, List<SpecialItemEntry> entries, int totalWeight, int minRolls, int maxRolls) {
        try {
            int rolls = NukkitMath.randomRange(random, minRolls, maxRolls);
            for (int i = 0; i < rolls; i++) {
                int result = random.nextBoundedInt(totalWeight);
                for (SpecialItemEntry entry : entries) {
                    result -= entry.weight();
                    if (result < 0) {
                        Item item = entry.create(random);
                        if (!item.isNull()) {
                            inventory.setItem(random.nextBoundedInt(inventory.getSize()), item);
                        }
                        break;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static int totalWeight(List<SpecialItemEntry> entries) {
        int total = 0;
        for (SpecialItemEntry entry : entries) {
            total += entry.weight();
        }
        return total;
    }

    private static SpecialItemEntry empty(int weight) {
        return new SpecialItemEntry(weight, random -> Item.get("minecraft:air"));
    }

    private static SpecialItemEntry item(String id, int weight) {
        return item(id, 1, 1, weight);
    }

    private static SpecialItemEntry item(String id, int minCount, int maxCount, int weight) {
        return new SpecialItemEntry(weight, random -> Item.get(id, 0, NukkitMath.randomRange(random, minCount, maxCount)));
    }

    private static SpecialItemEntry potion(String id, int potionMeta, int minCount, int maxCount, int weight) {
        return new SpecialItemEntry(weight, random -> Item.get(id, potionMeta, NukkitMath.randomRange(random, minCount, maxCount)));
    }

    private static SpecialItemEntry suspiciousStew(int minCount, int maxCount, int weight) {
        return new SpecialItemEntry(weight, random -> {
            Item item = Item.get(Item.SUSPICIOUS_STEW, random.nextBoolean() ? 0 : 3, NukkitMath.randomRange(random, minCount, maxCount));
            return item;
        });
    }

    private static SpecialItemEntry randomEnchantedBook(int weight) {
        return new SpecialItemEntry(weight, random -> {
            Item item = Item.get(Item.ENCHANTED_BOOK);
            Enchantment.addRandomEnchantments(item, 1, false);
            return item;
        });
    }

    private static SpecialItemEntry swiftSneakBook(int minLevel, int maxLevel, int weight) {
        return new SpecialItemEntry(weight, random -> {
            Item item = Item.get(Item.ENCHANTED_BOOK);
            Enchantment enchantment = Enchantment.getEnchantment(new Identifier("minecraft", Enchantment.NAME_SWIFT_SNEAK));
            enchantment.setLevel(NukkitMath.randomRange(random, minLevel, maxLevel));
            item.addEnchantment(enchantment);
            return item;
        });
    }

    private static SpecialItemEntry enchanted(String id, int minCount, int maxCount, int weight, int minLevel, int maxLevel, boolean treasure) {
        return new SpecialItemEntry(weight, random -> {
            Item item = Item.get(id, 0, NukkitMath.randomRange(random, minCount, maxCount));
            applyEnchantWithLevels(item, random, minLevel, maxLevel, treasure);
            return item;
        });
    }

    private static SpecialItemEntry damagedEnchanted(String id, int minCount, int maxCount, int weight, int minLevel, int maxLevel, boolean treasure) {
        return new SpecialItemEntry(weight, random -> {
            Item item = Item.get(id, 0, NukkitMath.randomRange(random, minCount, maxCount));
            int maxDurability = item.getMaxDurability();
            if (maxDurability > 0) {
                item.setDamage(NukkitMath.randomRange(random, (int) Math.floor(maxDurability * 0.8f), maxDurability - 1));
            }
            applyEnchantWithLevels(item, random, minLevel, maxLevel, treasure);
            return item;
        });
    }

    private static void applyEnchantWithLevels(Item item, RandomSourceProvider random, int minLevel, int maxLevel, boolean treasure) {
        List<Enchantment> candidates = new java.util.ArrayList<>();
        for (Enchantment enchantment : Enchantment.getRegisteredEnchantments()) {
            if (enchantment == null || !enchantment.canEnchant(item)) {
                continue;
            }
            if (!treasure && !enchantment.isObtainableFromEnchantingTable()) {
                continue;
            }
            candidates.add(enchantment);
        }
        if (candidates.isEmpty()) {
            return;
        }

        Enchantment template = candidates.get(random.nextBoundedInt(candidates.size()));
        Enchantment enchantment = template.getIdentifier() == null
                ? Enchantment.getEnchantment(template.getId())
                : Enchantment.getEnchantment(template.getIdentifier());
        int targetLevel = NukkitMath.randomRange(random, minLevel, maxLevel);
        int appliedLevel = Math.max(enchantment.getMinLevel(), Math.min(enchantment.getMaxLevel(), Math.max(1, targetLevel / 15)));
        enchantment.setLevel(appliedLevel);
        item.addEnchantment(enchantment);
    }

    private record SpecialItemEntry(int weight, SpecialItemFactory factory) {
        Item create(RandomSourceProvider random) {
            return factory.create(random);
        }
    }

    @FunctionalInterface
    private interface SpecialItemFactory {
        Item create(RandomSourceProvider random);
    }
}
