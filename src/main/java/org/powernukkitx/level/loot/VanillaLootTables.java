package org.powernukkitx.level.loot;

import org.powernukkitx.block.Block;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.generator.object.RandomizableContainer;
import org.powernukkitx.utils.random.BedrockRandom;
import com.google.common.base.Preconditions;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Resolves vanilla container loot tables supported by the runtime.
 *
 * @author Curse
 */
public final class VanillaLootTables {
    public static final String STRONGHOLD_CORRIDOR = "loot_tables/chests/stronghold_corridor.json";
    public static final String STRONGHOLD_CROSSING = "loot_tables/chests/stronghold_crossing.json";
    public static final String STRONGHOLD_LIBRARY = "loot_tables/chests/stronghold_library.json";

    private static final RandomizableContainer STRONGHOLD_CORRIDOR_LOOT = new StrongholdCorridorLoot();
    private static final RandomizableContainer STRONGHOLD_CROSSING_LOOT = new StrongholdCrossingLoot();
    private static final RandomizableContainer STRONGHOLD_LIBRARY_LOOT = new StrongholdLibraryLoot();

    private VanillaLootTables() {
    }

    /**
     * Populates a container from a supported loot-table identifier.
     */
    public static boolean populate(String lootTable, Inventory inventory, int seed) {
        Preconditions.checkNotNull(lootTable);
        Preconditions.checkNotNull(inventory);

        RandomizableContainer table = switch (lootTable) {
            case STRONGHOLD_CORRIDOR -> STRONGHOLD_CORRIDOR_LOOT;
            case STRONGHOLD_CROSSING -> STRONGHOLD_CROSSING_LOOT;
            case STRONGHOLD_LIBRARY -> STRONGHOLD_LIBRARY_LOOT;
            default -> null;
        };
        if (table == null) return false;

        int resolvedSeed = seed == 0 ? ThreadLocalRandom.current().nextInt() : seed;
        table.create(inventory, new BedrockRandom(resolvedSeed));
        return true;
    }

    private static final class StrongholdCorridorLoot extends RandomizableContainer {
        private StrongholdCorridorLoot() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.ENDER_PEARL, 50))
                    .register(new ItemEntry(Item.EMERALD, 0, 3, 1, 15))
                    .register(new ItemEntry(Item.DIAMOND, 0, 3, 1, 15))
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 1, 50))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 3, 1, 25))
                    .register(new ItemEntry(Item.REDSTONE, 0, 9, 4, 25))
                    .register(new ItemEntry(Item.BREAD, 0, 3, 1, 75))
                    .register(new ItemEntry(Item.APPLE, 0, 3, 1, 75))
                    .register(new ItemEntry(Item.IRON_PICKAXE, 25))
                    .register(new ItemEntry(Item.IRON_SWORD, 25))
                    .register(new ItemEntry(Item.IRON_CHESTPLATE, 25))
                    .register(new ItemEntry(Item.IRON_HELMET, 25))
                    .register(new ItemEntry(Item.IRON_LEGGINGS, 25))
                    .register(new ItemEntry(Item.IRON_BOOTS, 25))
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 5))
                    .register(new ItemEntry(Item.LEATHER, 0, 5, 1, 5))
                    .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.GOLDEN_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.MUSIC_DISC_OTHERSIDE, 5))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 6, getTreasure()));

            this.pools.put(pool1.build(), new RollEntry(3, 2, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Block.AIR, 9))
                    .register(new ItemEntry(Item.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 1));

            this.pools.put(pool2.build(), new RollEntry(1, 1, pool2.getTotalWeight()));
        }
    }

    private static final class StrongholdCrossingLoot extends RandomizableContainer {
        private StrongholdCrossingLoot() {
            PoolBuilder pool = new PoolBuilder()
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 1, 50))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 3, 1, 25))
                    .register(new ItemEntry(Item.REDSTONE, 0, 9, 4, 25))
                    .register(new ItemEntry(Item.COAL, 0, 8, 3, 50))
                    .register(new ItemEntry(Item.BREAD, 0, 3, 1, 75))
                    .register(new ItemEntry(Item.APPLE, 0, 3, 1, 75))
                    .register(new ItemEntry(Item.IRON_PICKAXE, 5))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 6, getTreasure()))
                    .register(new ItemEntry(Item.INK_SAC, 0, 3, 1, 75));

            this.pools.put(pool.build(), new RollEntry(4, 1, pool.getTotalWeight()));
        }
    }

    private static final class StrongholdLibraryLoot extends RandomizableContainer {
        private StrongholdLibraryLoot() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.BOOK, 0, 3, 1, 100))
                    .register(new ItemEntry(Item.PAPER, 0, 7, 2, 100))
                    .register(new ItemEntry(Item.EMPTY_MAP, 5))
                    .register(new ItemEntry(Item.COMPASS, 5))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 60, getTreasure()));

            this.pools.put(pool1.build(), new RollEntry(10, 2, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 1));

            this.pools.put(pool2.build(), new RollEntry(1, 1, pool2.getTotalWeight()));
        }
    }

    private static Enchantment[] getTreasure() {
        return new Enchantment[]{
                Enchantment.getEnchantment(Enchantment.ID_BINDING_CURSE),
                Enchantment.getEnchantment(Enchantment.ID_VANISHING_CURSE),
                Enchantment.getEnchantment(Enchantment.ID_FROST_WALKER),
                Enchantment.getEnchantment(Enchantment.ID_MENDING)
        };
    }
}
