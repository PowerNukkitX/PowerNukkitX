package org.powernukkitx.addons.parser;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockComposter;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.customitem.CustomItemDefinition;
import org.powernukkitx.item.customitem.ItemDataDriven;
import org.powernukkitx.item.customitem.ItemDataDrivenStorage;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
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
    void sendsClientComponentsInDedicatedServerSchema() {
        String id = "pnxtest:parsed_client_components";

        assertEquals(id, register(id, """
                {
                  "minecraft:rarity": "rare",
                  "minecraft:interact_button": "Use This",
                  "minecraft:liquid_clipped": true,
                  "minecraft:dyeable": { "default_color": "#175882" },
                  "minecraft:storage_item": { "max_slots": 64, "banned_items": ["minecraft:shulker_box"] },
                  "minecraft:cooldown": { "category": "spear", "duration": 1.05, "type": "attack" },
                  "minecraft:use_modifiers": { "use_duration": 2, "start_sound": "item.diamond_spear.use" }
                }
                """));

        CompoundTag components = ItemRegistry.getCustomItemDefinitionByIdStatic(id).nbt().getCompound("components");
        assertTrue(assertInstanceOf(ItemDataDriven.class, Registries.ITEM.get(id)).isDyeable());
        assertEquals("rare", components.getCompound("minecraft:rarity").getString("value"));
        assertEquals("Use This", components.getCompound("minecraft:interact_button").getString("interact_text"));
        assertTrue(components.getCompound("item_properties").getBoolean("liquid_clipped"));
        ListTag<IntTag> color = components.getCompound("minecraft:dyeable").getList("default_color", IntTag.class);
        assertEquals(0x17, (int) color.get(0).getData());
        assertEquals(0x82, (int) color.get(2).getData());
        assertEquals("minecraft:shulker_box", components.getCompound("minecraft:storage_item")
                .getList("banned_items", CompoundTag.class).get(0).getString("name"));
        assertEquals("attack", components.getCompound("minecraft:cooldown").getString("type"));
        assertEquals("if_first", components.getCompound("minecraft:use_modifiers").getString("start_using"));
        assertTrue(components.contains("item_tags"));
        assertTrue(tracker.isEmpty(), "client components must not be reported unsupported");
    }

    @Test
    void seedPlantsOnlyAtItsFace() {
        String id = "pnxtest:parsed_hanging_seed";

        assertEquals(id, register(id, """
                { "minecraft:seed": { "crop_result": "minecraft:wheat", "plant_at_face": "DOWN" },
                  "minecraft:durability_sensor": { "durability_thresholds": [ { "durability": 5, "particle_type": "wolfarmorcrack" } ] } }
                """));

        ItemDataDriven seed = assertInstanceOf(ItemDataDriven.class, Registries.ITEM.get(id));
        assertFalse(seed.isPiercingWeapon());
        assertTrue(seed.canPlantAt(BlockFace.DOWN));
        assertFalse(seed.canPlantAt(BlockFace.UP));
        assertTrue(tracker.isEmpty(), "durability_sensor must not be reported unsupported");
    }

    @Test
    void storageItemStoresWithinPackLimits() {
        String id = "pnxtest:parsed_pouch";

        assertEquals(id, register(id, """
                {
                  "minecraft:max_stack_size": 1,
                  "minecraft:storage_item": { "max_slots": 8, "allow_nested_storage_items": false, "banned_items": ["stick"] },
                  "minecraft:storage_weight_limit": { "max_weight_limit": 32 }
                }
                """));

        ItemDataDrivenStorage pouch = assertInstanceOf(ItemDataDrivenStorage.class, Registries.ITEM.get(id));
        assertEquals(8, pouch.getMaxStorageSlots());
        assertEquals(32, pouch.getMaxStorageWeight());
        assertTrue(pouch.canStore(Item.get(ItemID.APPLE)));
        assertFalse(pouch.canStore(Item.get(ItemID.BUNDLE)), "nested storage items are not allowed");
        assertFalse(pouch.canStore(Item.get(ItemID.STICK)), "banned names without a namespace are vanilla items");
    }

    @Test
    void readsPiercingWeapon() {
        String id = "pnxtest:parsed_spear";

        assertEquals(id, register(id, """
                { "minecraft:damage": 4, "minecraft:piercing_weapon": { "reach": { "min": 2.0, "max": 4.5 }, "hitbox_margin": 0.25 } }
                """));

        ItemDataDriven spear = assertInstanceOf(ItemDataDriven.class, Registries.ITEM.get(id));
        assertTrue(spear.isPiercingWeapon());
    }

    @Test
    void acceptsKineticWeapon() {
        String id = "pnxtest:parsed_lance";

        assertEquals(id, register(id, """
                { "minecraft:kinetic_weapon": { "delay": 10, "damage_multiplier": 1.2,
                  "damage_conditions": { "min_relative_speed": 4.6, "max_duration": 200 } } }
                """));

        assertInstanceOf(ItemDataDriven.class, Registries.ITEM.get(id));
        assertTrue(tracker.isEmpty(), "kinetic_weapon must not be reported unsupported");
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

    @Test
    void mapsFireResistantSeedAndLegacyUseDuration() {
        String id = "pnxtest:parsed_seed";

        assertEquals(id, register(id, """
                { "minecraft:fire_resistant": { "value": true },
                  "minecraft:seed": { "crop_result": "minecraft:wheat", "plant_at": ["farmland"] },
                  "minecraft:use_duration": 1.5 }
                """));

        Item item = Item.get(id);
        assertTrue(item.isLavaResistant());
        assertEquals("minecraft:wheat", item.getCustomDefinition().getBlockPlacerData().blockId());
        assertEquals(java.util.List.of("minecraft:farmland"), item.getCustomDefinition().getBlockPlacerData().useOn());
        assertTrue(tracker.isEmpty());
    }

    @Test
    void diggerTagRulesFollowTheirQuery() {
        String id = "pnxtest:all_tags_pickaxe";

        assertEquals(id, register(id, """
                { "minecraft:digger": { "destroy_speeds": [
                    { "block": { "tags": "query.all_tags('stone', 'pnxtest_never_present')" }, "speed": 7 } ] } }
                """));

        assertNull(Item.get(id).getDiggerSpeed(Block.get(BlockID.STONE)), "all_tags must not match a block missing one tag");
    }

    @Test
    void readsRecordAndDamageAbsorption() {
        String id = "pnxtest:parsed_record";

        assertEquals(id, register(id, """
                { "minecraft:record": { "sound_event": "record.cat", "duration": 185, "comparator_signal": 2 },
                  "minecraft:damage_absorption": { "absorbable_causes": ["entity_attack"] } }
                """));

        ItemDataDriven item = (ItemDataDriven) Item.get(id);
        assertEquals("record.cat", item.getRecord().soundEvent());
        assertTrue(item.absorbs(org.powernukkitx.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK));
        assertFalse(item.absorbs(org.powernukkitx.event.entity.EntityDamageEvent.DamageCause.FALL));
        assertTrue(tracker.isEmpty());
    }
}
