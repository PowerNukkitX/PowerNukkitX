package org.powernukkitx.addons.parser;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemSuspiciousStew;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.loot.LootTable;
import org.powernukkitx.loot.LootTableRegistry;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.ItemHelper;

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
    void fillsContainerFromAnotherTable() {
        LootTableRegistry.register("loot_tables/pnxtest/box_contents.json", parse(LEAVES));
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:undyed_shulker_box",
                  "functions": [ { "function": "fill_container", "loot_table": "loot_tables/pnxtest/box_contents.json" } ] } ] } ] }
                """);

        Item box = table.generate(new Random(3)).getFirst();
        ListTag<CompoundTag> items = box.getNbt().getList("Items", CompoundTag.class);
        assertEquals(1, items.size());
        assertEquals(ItemID.STICK, ItemHelper.read(items.get(0)).getId());
        assertTrue(tracker.isEmpty());
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
                  "conditions": [ { "condition": "match_tool", "pnxtest_unknown_predicate": 1 } ] } ] } ] }
                """);

        assertTrue(table.generate(new Random(1), Item.get(ItemID.SHEARS)).isEmpty());
        assertFalse(tracker.isEmpty());
    }

    @Test
    void reportsUnsupportedFunctionWithoutLosingEntry() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:diamond",
                  "functions": [ { "function": "exploration_map" } ] } ] } ] }
                """);

        assertEquals(1, table.generate(new Random(1)).size());
        assertFalse(tracker.isEmpty());
    }

    @Test
    void matchToolChecksEnchantments() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:diamond",
                  "conditions": [ { "condition": "match_tool",
                    "enchantments": [ { "enchantment": "silk_touch", "levels": { "range_min": 1 } } ] } ] } ] } ] }
                """);
        Item silkTouch = Item.get(ItemID.DIAMOND_PICKAXE);
        silkTouch.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_SILK_TOUCH));

        assertTrue(table.generate(new Random(1), Item.get(ItemID.DIAMOND_PICKAXE)).isEmpty());
        assertEquals(ItemID.DIAMOND, table.generate(new Random(1), silkTouch).getFirst().getId());
        assertTrue(tracker.isEmpty());
    }

    @Test
    void lootingRaisesChanceAndCount() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1,
                  "conditions": [ { "condition": "random_chance_with_looting", "chance": 0.0, "looting_multiplier": 1.0 } ],
                  "entries": [ { "type": "item", "name": "minecraft:stick",
                    "functions": [ { "function": "looting_enchant", "count": { "min": 2, "max": 2 } } ] } ] } ] }
                """);
        Item sword = Item.get(ItemID.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_LOOTING).setLevel(3, false));

        assertTrue(table.generate(new Random(1)).isEmpty(), "without looting the chance is zero");
        assertEquals(7, table.generate(new Random(1), sword).getFirst().getCount());
    }

    @Test
    void specificEnchantsTurnBookIntoEnchantedBook() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:book",
                  "functions": [ { "function": "specific_enchants", "enchants": [ { "id": "sharpness", "level": [2, 2] } ] } ] } ] } ] }
                """);

        Item book = table.generate(new Random(1)).getFirst();
        assertEquals(ItemID.ENCHANTED_BOOK, book.getId());
        assertEquals(2, book.getEnchantmentLevel(Enchantment.ID_DAMAGE_ALL));
        assertTrue(tracker.isEmpty());
    }

    @Test
    void setDamageIsTheDurabilityLeft() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:diamond_sword",
                  "functions": [ { "function": "set_damage", "damage": 0.25 } ] } ] } ] }
                """);

        Item sword = table.generate(new Random(1)).getFirst();
        assertEquals(Math.round(sword.getMaxDurability() * 0.75), sword.getDamage());
    }

    @Test
    void readsSpawnEggBottleAndToolDurability() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:pig_spawn_egg",
                    "functions": [ { "function": "set_actor_id", "id": "minecraft:wolf" } ] } ] },
                  { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:ominous_bottle",
                    "functions": [ { "function": "set_ominous_bottle_amplifier", "amplifier": { "min": 3, "max": 3 } } ] } ] },
                  { "rolls": 1, "conditions": [ { "condition": "match_tool", "durability": { "range_min": 100 } } ],
                    "entries": [ { "type": "item", "name": "minecraft:stick" } ] } ] }
                """);
        Item worn = Item.get(ItemID.DIAMOND_PICKAXE);
        worn.setDamage(worn.getMaxDurability() - 10);

        java.util.List<Item> loot = table.generate(new Random(1), worn);
        assertEquals("minecraft:wolf_spawn_egg", loot.get(0).getId());
        assertEquals(3, loot.get(1).getDamage());
        assertEquals(2, loot.size(), "a tool with 10 durability left fails range_min 100");
        assertEquals(3, table.generate(new Random(1), Item.get(ItemID.DIAMOND_PICKAXE)).size());
        assertTrue(tracker.isEmpty());
    }

    @Test
    void furnaceSmeltLeavesBlockDropsAlone() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:raw_iron",
                  "functions": [ { "function": "furnace_smelt" } ] } ] } ] }
                """);

        assertEquals(ItemID.RAW_IRON, table.generate(new Random(1)).getFirst().getId());
        assertTrue(tracker.isEmpty());
    }

    @Test
    void explosionDecayThinsStacksOnlyForExplosions() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:stick",
                  "functions": [ { "function": "set_count", "count": 64 }, { "function": "explosion_decay" } ] } ] } ] }
                """);

        assertEquals(64, table.generate(new Random(1)).getFirst().getCount());
        int kept = table.generate(new Random(1), null, 4).getFirst().getCount();
        assertTrue(kept > 0 && kept < 64, "about a quarter of the stack survives a radius 4 explosion, got " + kept);
        assertTrue(table.generate(new Random(1), null, 1.0e9).isEmpty(), "an emptied stack is not dropped");
        assertTrue(tracker.isEmpty());
    }

    @Test
    void stewEffectSetsTheStewGivingIt() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:suspicious_stew",
                  "functions": [ { "function": "set_stew_effect", "effects": [ { "id": 20 }, { "id": 99 } ] } ] } ] } ] }
                """);

        Item stew = table.generate(new Random(1)).getFirst();
        assertEquals(20, ItemSuspiciousStew.getEffect(stew.getDamage()).getId(), "wither is the only listed effect a stew gives");
        assertTrue(tracker.isEmpty());
    }

    @Test
    void randomDifficultyChanceUsesDefaultWhenUnlisted() {
        LootTable always = parse("""
                { "pools": [ { "rolls": 1, "conditions": [ { "condition": "random_difficulty_chance", "default_chance": 1.0 } ],
                  "entries": [ { "type": "item", "name": "minecraft:stick" } ] } ] }
                """);

        assertEquals(1, always.generate(new Random(1)).size());
        assertTrue(tracker.isEmpty());
    }

    @Test
    void tiersPickByPosition() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "tiers": { "initial_range": 1, "bonus_rolls": 3, "bonus_chance": 1.0 },
                  "entries": [ { "type": "item", "name": "minecraft:stick" }, { "type": "item", "name": "minecraft:diamond" },
                               { "type": "item", "name": "minecraft:diamond_hoe" } ] } ] }
                """);

        assertEquals(ItemID.DIAMOND_HOE, table.generate(new Random(1)).getFirst().getId(), "every bonus roll succeeds, capped at the last tier");
    }

    @Test
    void appliesBannerBookAndTrim() {
        LootTable table = parse("""
                { "pools": [ { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:written_book",
                    "functions": [ { "function": "set_book_contents", "author": "Steve", "title": "Notes", "pages": ["one", "two"] } ] } ] },
                  { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:diamond_chestplate",
                    "functions": [ { "function": "set_armor_trim", "material": "netherite", "pattern": "wayfinder" } ] } ] },
                  { "rolls": 1, "entries": [ { "type": "item", "name": "minecraft:banner",
                    "functions": [ { "function": "set_banner_details", "type": 0, "base_color": "silver",
                      "patterns": [ { "color": "black", "pattern": "gradient" } ] } ] } ] } ] }
                """);

        java.util.List<Item> loot = table.generate(new Random(1));
        assertEquals("Steve", ((org.powernukkitx.item.ItemWrittenBook) loot.get(0)).getAuthor());
        assertTrue(org.powernukkitx.network.protocol.types.ArmorTrim.has(loot.get(1)));
        assertEquals(1, ((org.powernukkitx.item.ItemBanner) loot.get(2)).getPatternsSize());
        assertTrue(tracker.isEmpty());
    }
}
