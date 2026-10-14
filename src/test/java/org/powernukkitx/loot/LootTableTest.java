package org.powernukkitx.loot;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers rolling a loot table. A seeded {@link Random} keeps the outcomes reproducible,
 * so the assertions are about what the table can produce rather than about a particular
 * sequence of random numbers.
 */
class LootTableTest {

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
    }

    private static LootTable.Entry item(String id, int weight, int min, int max) {
        return new LootTable.Entry(LootTable.EntryType.ITEM, id, weight,
                new LootTable.NumberRange(min, max), null, null, null, List.of());
    }

    private static LootTable single(LootTable.Entry... entries) {
        return new LootTable(List.of(
                new LootTable.Pool(LootTable.NumberRange.ONE, List.of(), List.of(entries))));
    }

    @Test
    void rollsSingleItemPool() {
        LootTable table = single(item(ItemID.DIAMOND, 1, 2, 2));

        List<Item> loot = table.generate(new Random(1));

        assertEquals(1, loot.size());
        assertEquals(ItemID.DIAMOND, loot.getFirst().getId());
        assertEquals(2, loot.getFirst().getCount());
    }

    @Test
    void samplesCountWithinRange() {
        LootTable table = single(item(ItemID.STICK, 1, 1, 4));
        Random random = new Random(42);

        for (int i = 0; i < 200; i++) {
            List<Item> loot = table.generate(random);
            assertEquals(1, loot.size());
            int count = loot.getFirst().getCount();
            assertTrue(count >= 1 && count <= 4, "count out of range: " + count);
        }
    }

    @Test
    void appliesRollsPerPool() {
        LootTable table = new LootTable(List.of(
                new LootTable.Pool(new LootTable.NumberRange(3, 3), List.of(), List.of(item(ItemID.STICK, 1, 1, 1)))));

        assertEquals(3, table.generate(new Random(7)).size());
    }

    @Test
    void skipsPoolBelowRandomChance() {
        LootTable never = new LootTable(List.of(
                new LootTable.Pool(LootTable.NumberRange.ONE, List.of(new LootTable.RandomChance(0.0f)), List.of(item(ItemID.DIAMOND, 1, 1, 1)))));

        assertTrue(never.generate(new Random(3)).isEmpty());
    }

    @Test
    void emptyEntryYieldsNothing() {
        LootTable table = single(LootTable.Entry.empty());

        assertTrue(table.generate(new Random(5)).isEmpty());
    }

    @Test
    void weightingFavoursHeavierEntry() {
        LootTable table = single(
                item(ItemID.DIAMOND, 1, 1, 1),
                item(ItemID.STICK, 99, 1, 1));

        int sticks = 0;
        Random random = new Random(11);
        for (int i = 0; i < 500; i++) {
            List<Item> loot = table.generate(random);
            if (!loot.isEmpty() && ItemID.STICK.equals(loot.getFirst().getId())) {
                sticks++;
            }
        }

        assertTrue(sticks > 400, "heavier entry should dominate, got " + sticks + "/500");
    }

    @Test
    void skipsUnknownItemWithoutFailing() {
        LootTable table = single(item("pnxtest:definitely_not_an_item", 1, 1, 1));

        assertTrue(table.generate(new Random(2)).isEmpty());
    }

    @Test
    void resolvesNestedTableThroughRegistry() {
        LootTableRegistry.register("pnxtest/nested_inner", single(item(ItemID.EMERALD, 1, 1, 1)));
        LootTable outer = single(new LootTable.Entry(LootTable.EntryType.LOOT_TABLE,
                "pnxtest/nested_inner", 1, LootTable.NumberRange.ONE, null, null, null, List.of()));

        List<Item> loot = outer.generate(new Random(9));

        assertEquals(1, loot.size());
        assertEquals(ItemID.EMERALD, loot.getFirst().getId());
    }

    @Test
    void skipsMissingNestedTable() {
        LootTable outer = single(new LootTable.Entry(LootTable.EntryType.LOOT_TABLE,
                "pnxtest/does_not_exist", 1, LootTable.NumberRange.ONE, null, null, null, List.of()));

        assertTrue(outer.generate(new Random(9)).isEmpty());
    }

    @Test
    void stopsOnSelfReferencingTable() {
        String path = "pnxtest/recursive";
        LootTable recursive = single(new LootTable.Entry(LootTable.EntryType.LOOT_TABLE,
                path, 1, LootTable.NumberRange.ONE, null, null, null, List.of()));
        LootTableRegistry.register(path, recursive);

        assertTrue(recursive.generate(new Random(4)).isEmpty(), "a cycle must terminate, not overflow");
    }

    @Test
    void appliesDisplayNameAndLore() {
        LootTable table = single(new LootTable.Entry(LootTable.EntryType.ITEM, ItemID.DIAMOND, 1,
                LootTable.NumberRange.ONE, null, "Shiny", List.of("line one", "line two"), List.of()));

        Item generated = table.generate(new Random(6)).getFirst();

        assertEquals("Shiny", generated.getCustomName());
        assertEquals(2, generated.getLore().length);
        assertEquals("line one", generated.getLore()[0]);
    }

    @Test
    void inclusiveRangeHandlesInvertedAndFixedBounds() {
        Random random = new Random(8);
        assertEquals(5, new LootTable.NumberRange(5, 5).sample(random));
        assertEquals(5, new LootTable.NumberRange(5, 2).sample(random));
    }

    @Test
    void recordsRejectMutationOfTheirLists() {
        LootTable table = single(item(ItemID.STICK, 1, 1, 1));

        assertThrows(UnsupportedOperationException.class, () -> table.pools().clear());
        assertThrows(UnsupportedOperationException.class, () -> table.pools().getFirst().entries().clear());
    }

    @Test
    void registryLookupToleratesPathSpellings() {
        LootTable table = single(item(ItemID.STICK, 1, 1, 1));
        LootTableRegistry.register("loot_tables/pnxtest/spelling.json", table);

        assertNotNull(LootTableRegistry.get("pnxtest/spelling"));
        assertNotNull(LootTableRegistry.get("loot_tables/pnxtest/spelling"));
        assertNotNull(LootTableRegistry.get("pnxtest/spelling.json"));
        assertNotNull(LootTableRegistry.get("minecraft:loot_tables/pnxtest/spelling.json"));
        assertNotNull(LootTableRegistry.get("PNXTest/Spelling"));
        assertNotNull(LootTableRegistry.get("loot_tables\\pnxtest\\spelling.json"));
        assertTrue(LootTableRegistry.contains("pnxtest/spelling"));
        assertFalse(LootTableRegistry.contains("pnxtest/other"));
    }

    private static LootTable.Entry conditioned(String id, int weight, LootTable.Condition... conditions) {
        return new LootTable.Entry(LootTable.EntryType.ITEM, id, weight,
                LootTable.NumberRange.ONE, null, null, null, List.of(conditions));
    }

    private static LootTable.MatchTool needsTag(String tag) {
        return new LootTable.MatchTool(null, List.of(tag), List.of(), List.of(), false);
    }

    @Test
    void excludesEntriesWhoseConditionFailsBeforeWeighting() {
        LootTable table = single(
                conditioned(ItemID.DIAMOND, 5000, needsTag("minecraft:is_shears")),
                conditioned(ItemID.STICK, 1));

        for (int i = 0; i < 50; i++) {
            List<Item> loot = table.generate(new Random(i));
            assertEquals(1, loot.size());
            assertEquals(ItemID.STICK, loot.getFirst().getId(), "a failed condition must remove the entry, whatever its weight");
        }
    }

    @Test
    void matchToolHoldsForToolCarryingTheTag() {
        LootTable table = single(conditioned(ItemID.DIAMOND, 1, needsTag("minecraft:is_shears")));

        assertEquals(1, table.generate(new Random(1), Item.get(ItemID.SHEARS)).size());
        assertTrue(table.generate(new Random(1), Item.get(ItemID.STICK)).isEmpty());
        assertTrue(table.generate(new Random(1), null).isEmpty(), "an empty hand never matches a tool requirement");
    }

    @Test
    void matchToolChecksItemAndExcludedTags() {
        LootTable.MatchTool exactShears = new LootTable.MatchTool(ItemID.SHEARS, List.of(), List.of(), List.of(), false);
        LootTable.MatchTool notShears = new LootTable.MatchTool(null, List.of(), List.of(), List.of("minecraft:is_shears"), false);
        Random random = new Random(3);

        assertTrue(exactShears.test(random, Item.get(ItemID.SHEARS)));
        assertFalse(exactShears.test(random, Item.get(ItemID.STICK)));
        assertFalse(notShears.test(random, Item.get(ItemID.SHEARS)));
        assertTrue(notShears.test(random, Item.get(ItemID.STICK)));
        assertTrue(notShears.test(random, null));
    }

    @Test
    void unsatisfiableMatchToolNeverHolds() {
        LootTable.MatchTool unsatisfiable = new LootTable.MatchTool(null, List.of(), List.of(), List.of(), true);

        assertFalse(unsatisfiable.test(new Random(4), Item.get(ItemID.SHEARS)));
    }
}
