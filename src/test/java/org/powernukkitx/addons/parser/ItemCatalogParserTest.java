package org.powernukkitx.addons.parser;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.registry.CreativeItemRegistry;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemCatalogParserTest {

    private static final List<String> ITEMS = List.of("pnxtest:catalog_a", "pnxtest:catalog_b", "pnxtest:catalog_loose");

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @AfterEach
    void cleanUp() {
        CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.removeAll(ITEMS);
        ITEMS.forEach(id -> {
            CreativeItemRegistry.ITEM_GROUP_MAP.remove(id);
            CreativeItemRegistry.ITEM_CATEGORY_MAP.remove(id);
            CreativeItemRegistry.ITEM_ORDER_MAP.remove(id);
        });
    }

    @Test
    void assignsGroupsLooseItemsAndOrder() {
        CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.addAll(ITEMS);
        AddonFeatureTracker tracker = new AddonFeatureTracker();

        int assigned = new ItemCatalogParser(tracker).parse("TestPack", JsonParser.parseString("""
                { "format_version": "1.21.60", "minecraft:crafting_items_catalog": { "categories": [
                  { "category_name": "construction", "groups": [
                    { "items": ["pnxtest:catalog_loose", "minecraft:stone"] },
                    { "group_identifier": { "name": "pnxtest:catalog_group", "icon": "pnxtest:catalog_b" },
                      "items": ["pnxtest:catalog_b", "pnxtest:catalog_a"] } ] } ] } }
                """).getAsJsonObject());

        assertEquals(3, assigned, "vanilla items keep their place");
        assertEquals(CreativeCategory.CONSTRUCTION, CreativeItemRegistry.ITEM_CATEGORY_MAP.get("pnxtest:catalog_loose"));
        assertNull(CreativeItemRegistry.ITEM_GROUP_MAP.get("pnxtest:catalog_loose"));
        assertEquals("pnxtest:catalog_group", CreativeItemRegistry.ITEM_GROUP_MAP.get("pnxtest:catalog_a"));
        assertFalse(CreativeItemRegistry.ITEM_CATEGORY_MAP.containsKey("pnxtest:catalog_a"));
        assertTrue(CreativeItemRegistry.ITEM_ORDER_MAP.get("pnxtest:catalog_loose") < CreativeItemRegistry.ITEM_ORDER_MAP.get("pnxtest:catalog_b"));
        assertTrue(CreativeItemRegistry.ITEM_ORDER_MAP.get("pnxtest:catalog_b") < CreativeItemRegistry.ITEM_ORDER_MAP.get("pnxtest:catalog_a"),
                "items keep the order the catalog lists them in");
        assertTrue(tracker.isEmpty());
    }
}
