package org.powernukkitx.addons.parser;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockComposter;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.customitem.CustomItemDefinition;
import org.powernukkitx.item.customitem.ItemDataDriven;
import org.powernukkitx.registry.ItemRegistry;
import org.powernukkitx.registry.Registries;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers parsing behaviour pack item documents. Each case registers under its own
 * identifier, because the item registry is process-wide and a registration cannot be
 * undone.
 */
class BehaviorItemParserTest {

    private AddonFeatureTracker tracker;
    private BehaviorItemParser parser;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @BeforeEach
    void setUp() {
        tracker = new AddonFeatureTracker();
        parser = new BehaviorItemParser(tracker);
    }

    private static JsonObject json(String text) {
        return JsonParser.parseString(text).getAsJsonObject();
    }

    private String register(String identifier, String components) {
        return parser.parseAndRegister("TestPack", identifier + ".json", json("""
                {
                  "format_version": "1.20.30",
                  "minecraft:item": {
                    "description": {
                      "identifier": "%s",
                      "menu_category": { "category": "equipment", "group": "itemGroup.name.sword" }
                    },
                    "components": %s
                  }
                }
                """.formatted(identifier, components)));
    }

    @Test
    void registersItemWithMappedComponents() {
        String id = "pnxtest:parsed_sword";

        assertEquals(id, register(id, """
                {
                  "minecraft:icon": { "textures": { "default": "parsed_sword" } },
                  "minecraft:display_name": { "value": "Parsed Sword" },
                  "minecraft:max_stack_size": 1,
                  "minecraft:hand_equipped": true,
                  "minecraft:damage": 7,
                  "minecraft:durability": { "max_durability": 250 }
                }
                """));

        Item item = Registries.ITEM.get(id);
        assertNotNull(item, "parsed item did not resolve from the registry");
        assertEquals(id, item.getId());
        assertEquals(1, item.getMaxStackSize());
        assertEquals(250, item.getMaxDurability());

        CustomItemDefinition definition = ItemRegistry.getCustomItemDefinitionByIdStatic(id);
        assertNotNull(definition);
        assertEquals(id, definition.identifier());
    }

    @Test
    void mapsFoodComponent() {
        String id = "pnxtest:parsed_food";

        assertEquals(id, register(id, """
                {
                  "minecraft:food": { "nutrition": 6, "saturation_modifier": 0.8, "can_always_eat": true },
                  "minecraft:use_animation": "eat"
                }
                """));

        Item item = Registries.ITEM.get(id);
        assertNotNull(item);
        assertTrue(item.isEdible(), "an item with minecraft:food must be edible");
    }

    @Test
    void recordsUnsupportedComponentsWithoutFailingTheItem() {
        String id = "pnxtest:parsed_unknown_component";

        assertEquals(id, register(id, """
                { "minecraft:max_stack_size": 16, "minecraft:totally_made_up": { "x": 1 } }
                """));

        assertNotNull(Registries.ITEM.get(id), "an unknown component must not lose the item");
        assertFalse(tracker.isEmpty(), "the unknown component should have been recorded");
    }

    @Test
    void skipsVanillaOverride() {
        assertNull(register("minecraft:diamond_sword", "{}"));
        assertFalse(tracker.isEmpty());
    }

    @Test
    void skipsSecondDefinitionOfSameIdentifier() {
        String id = "pnxtest:parsed_duplicate";

        assertEquals(id, register(id, "{}"));
        assertNull(register(id, "{}"), "the same identifier must not register twice");
        assertFalse(tracker.isEmpty());
    }

    @Test
    void skipsDocumentWithoutItemElement() {
        assertNull(parser.parseAndRegister("TestPack", "broken.json", json("{ \"format_version\": \"1.20.30\" }")));
    }

    @Test
    void skipsDocumentWithoutIdentifier() {
        assertNull(parser.parseAndRegister("TestPack", "broken.json",
                json("{ \"minecraft:item\": { \"description\": {} } }")));
    }

    private static final String REPLACED_BLOCK = """
            { "format_version": "1.21.0", "minecraft:block": { "description": { "identifier": "%s" }, "components": {} } }
            """;

    @Test
    void replacesBlockItemWhenDeclared() {
        String id = "pnxtest:replaced_block_item";
        assertEquals(id, new BehaviorBlockParser(tracker).parseAndRegister("TestPack", "block.json", json(REPLACED_BLOCK.formatted(id))));
        int blockItemRuntimeId = Registries.ITEM_RUNTIMEID.getInt(id);

        assertEquals(id, register(id, """
                { "minecraft:food": { "nutrition": 4 },
                  "minecraft:block_placer": { "block": "%s", "replace_block_item": true } }
                """.formatted(id)));

        assertInstanceOf(ItemDataDriven.class, Item.get(id));
        assertInstanceOf(ItemDataDriven.class, Registries.BLOCK.get(id).toItem(), "the block must drop the replacing item");
        assertEquals(blockItemRuntimeId, Registries.ITEM_RUNTIMEID.getInt(id), "the item keeps the block item's runtime id");
        assertTrue(tracker.isEmpty());
    }

    @Test
    void skipsItemReusingBlockIdentifierWithoutReplacing() {
        String id = "pnxtest:block_owned_id";
        new BehaviorBlockParser(tracker).parseAndRegister("TestPack", "block.json", json(REPLACED_BLOCK.formatted(id)));

        assertNull(register(id, "{}"));
        assertFalse(tracker.isEmpty());
    }

    @Test
    void mapsDiggerAndCompostable() {
        String id = "pnxtest:parsed_pickaxe";

        assertEquals(id, register(id, """
                { "minecraft:digger": { "use_efficiency": true, "destroy_speeds": [
                    { "block": "minecraft:stone", "speed": 9 },
                    { "block": { "tags": "query.any_tag('pnxtest_never_present')" }, "speed": 4 } ] },
                  "minecraft:compostable": { "composting_chance": 65 } }
                """));

        Item item = Item.get(id);
        assertEquals(9, item.getDiggerSpeed(Block.get(BlockID.STONE)));
        assertNull(item.getDiggerSpeed(Block.get(BlockID.DIRT)));
        assertEquals(65, BlockComposter.getChance(item));
        assertTrue(tracker.isEmpty());
    }
}
