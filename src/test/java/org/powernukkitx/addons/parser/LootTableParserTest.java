package org.powernukkitx.addons.parser;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.loot.LootTable;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers parsing behaviour pack loot tables. The leaves case mirrors how real packs gate a drop
 * behind shears with an entry-level {@code match_tool} condition.
 */
class LootTableParserTest {

    private AddonFeatureTracker tracker;
    private LootTableParser parser;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @BeforeEach
    void setUp() {
        tracker = new AddonFeatureTracker();
        parser = new LootTableParser(tracker);
    }

    private LootTable parse(String json) {
        return parser.parse("TestPack", JsonParser.parseString(json).getAsJsonObject());
    }

    private static final String LEAVES = """
            { "pools": [ { "rolls": 1, "entries": [
              { "type": "item", "name": "minecraft:diamond", "weight": 5000,
                "conditions": [ { "condition": "match_tool", "minecraft:match_tool_filter_all": ["minecraft:is_shears"], "count": 1 } ] },
              { "type": "item", "name": "minecraft:stick", "weight": 1,
                "functions": [ { "function": "set_count", "count": 2 } ] }
            ] } ] }
            """;

    @Test
    void entryConditionGatesDropBehindTool() {
        LootTable table = parse(LEAVES);

        for (int i = 0; i < 30; i++) {
            List<Item> withHand = table.generate(new Random(i), null);
            assertEquals(ItemID.STICK, withHand.getFirst().getId(), "without shears the gated entry must be excluded");
            assertEquals(2, withHand.getFirst().getCount());
        }
        int diamonds = 0;
        for (int i = 0; i < 30; i++) {
            if (ItemID.DIAMOND.equals(table.generate(new Random(i), Item.get(ItemID.SHEARS)).getFirst().getId())) {
                diamonds++;
            }
        }
        assertTrue(diamonds > 25, "with shears the heavy gated entry should dominate, got " + diamonds);
        assertTrue(tracker.isEmpty());
    }

    @Test
    void readsPoolConditionsAndFunctions() {
        LootTable table = parse("""
                { "pools": [ { "rolls": { "min": 2, "max": 2 },
                  "conditions": [ { "condition": "random_chance", "chance": 1.0 } ],
                  "entries": [ { "type": "item", "name": "minecraft:diamond",
                    "functions": [ { "function": "set_lore", "lore": ["one", "two"] }, { "function": "set_name", "name": "Gem" } ] } ] } ] }
                """);

        List<Item> loot = table.generate(new Random(1));
        assertEquals(2, loot.size());
        assertEquals("Gem", loot.getFirst().getCustomName());
        assertEquals(2, loot.getFirst().getLore().length);
    }

    @Test
    void unknownConditionFailsClosed() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:diamond",
                  "conditions": [ { "condition": "killed_by_player_or_pets" } ] } ] } ] }
                """);

        assertTrue(table.generate(new Random(1), Item.get(ItemID.SHEARS)).isEmpty());
        assertFalse(tracker.isEmpty());
    }

    @Test
    void uncheckableToolRequirementFailsClosed() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:diamond",
                  "conditions": [ { "condition": "match_tool", "enchantments": [ { "enchantment": "silk_touch" } ] } ] } ] } ] }
                """);

        assertTrue(table.generate(new Random(1), Item.get(ItemID.SHEARS)).isEmpty());
        assertFalse(tracker.isEmpty());
    }

    @Test
    void reportsUnsupportedFunctionWithoutLosingEntry() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:diamond",
                  "functions": [ { "function": "enchant_randomly" } ] } ] } ] }
                """);

        assertEquals(1, table.generate(new Random(1)).size());
        assertFalse(tracker.isEmpty());
    }
}
