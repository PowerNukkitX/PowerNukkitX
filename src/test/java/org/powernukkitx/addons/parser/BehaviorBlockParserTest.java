package org.powernukkitx.addons.parser;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.customblock.BlockDataDriven;
import org.powernukkitx.block.customblock.PlacementFilter;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.loot.LootTable;
import org.powernukkitx.loot.LootTableRegistry;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.BlockColor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers parsing behaviour pack block documents, including the definition shapes that once made
 * clients fail on join. Registering cases use their own identifiers, because the block registry is
 * process-wide and a registration cannot be undone.
 */
class BehaviorBlockParserTest {

    private AddonFeatureTracker tracker;
    private BehaviorBlockParser parser;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @BeforeEach
    void setUp() {
        tracker = new AddonFeatureTracker();
        parser = new BehaviorBlockParser(tracker);
    }

    private static JsonObject json(String text) {
        return JsonParser.parseString(text).getAsJsonObject();
    }

    private static JsonObject block(String identifier, String description, String components, String permutations) {
        return json("""
                {
                  "format_version": "1.21.0",
                  "minecraft:block": {
                    "description": { "identifier": "%s"%s },
                    "components": %s,
                    "permutations": %s
                  }
                }
                """.formatted(identifier, description, components, permutations));
    }

    private BlockDataDriven.Spec parse(JsonObject root) {
        BehaviorBlockParser.ParsedBlock parsed = parser.parseDefinition("TestPack", "block.json", root);
        assertNotNull(parsed, "document should have parsed");
        return parsed.spec();
    }

    private static CompoundTag components(BlockDataDriven.Spec spec) {
        return spec.definition().nbt().getCompound("components");
    }

    private static BlockState stateWith(BlockDataDriven.Spec spec, String property, Object value) {
        for (BlockState state : spec.properties().getSpecialValueMap().values()) {
            for (BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue : state.getBlockPropertyValues()) {
                if (propertyValue.getPropertyType().getName().equals(property) && propertyValue.getValue().equals(value)) {
                    return state;
                }
            }
        }
        throw new AssertionError("no state with " + property + "=" + value);
    }

    @Test
    void declaresTraitStatesSoConditionsCanQueryThem() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:trait_block",
                ", \"traits\": { \"minecraft:placement_direction\": { \"enabled_states\": [\"minecraft:cardinal_direction\"] } }",
                "{}",
                "[ { \"condition\": \"q.block_state('minecraft:cardinal_direction') == 'north'\", \"components\": { \"minecraft:friction\": 0.2 } } ]"));

        assertTrue(spec.properties().getPropertyTypeSet().stream()
                .anyMatch(type -> type.getName().equals("minecraft:cardinal_direction")));
        CompoundTag nbt = spec.definition().nbt();
        assertFalse(nbt.contains("properties"), "the client adds trait states itself and rejects them twice");
        CompoundTag trait = nbt.getList("traits", CompoundTag.class).get(0);
        assertEquals("minecraft:placement_direction", trait.getString("name"));
        assertEquals(1, trait.getCompound("enabled_states").getByte("cardinal_direction"));
        assertEquals(0, trait.getCompound("enabled_states").getByte("facing_direction"));
        assertEquals(13, nbt.getInt("molangVersion"));
    }

    @Test
    void sendsMultiBlockTraitAndKeepsOwnStates() {
        CompoundTag nbt = parse(block("pnxtest:tall_block",
                ", \"states\": { \"pnxtest:lit\": [false, true] }, \"traits\": { \"minecraft:multi_block\": "
                        + "{ \"enabled_states\": [\"minecraft:multi_block_part\"], \"parts\": 3, \"direction\": \"up\" } }",
                "{}", "[]")).definition().nbt();

        CompoundTag trait = nbt.getList("traits", CompoundTag.class).get(0);
        assertEquals(3, trait.getByte("part_count"));
        assertEquals(BlockFace.UP.getIndex(), trait.getByte("direction"));
        ListTag<CompoundTag> properties = nbt.getList("properties", CompoundTag.class);
        assertEquals(1, properties.size());
        assertEquals("pnxtest:lit", properties.get(0).getString("name"));
    }

    @Test
    void sendsCornerAndConnectionTraitsInsteadOfTheirStates() {
        CompoundTag nbt = parse(block("pnxtest:corner_fence",
                ", \"traits\": { \"minecraft:placement_direction\": { \"enabled_states\": [\"minecraft:corner_and_cardinal_direction\"], "
                        + "\"blocks_to_corner_with\": [\"pnxtest:corner_fence\"] }, "
                        + "\"minecraft:connection\": { \"enabled_states\": [\"minecraft:cardinal_connections\"] } }",
                "{}", "[]")).definition().nbt();

        assertFalse(nbt.contains("properties"));
        ListTag<CompoundTag> traits = nbt.getList("traits", CompoundTag.class);
        CompoundTag direction = traits.get(0);
        assertEquals(1, direction.getCompound("enabled_states").getByte("corner_and_cardinal_direction"));
        assertEquals("pnxtest:corner_fence", direction.getList("blocks_to_corner_with", CompoundTag.class).get(0).getString("name"));
        CompoundTag connection = traits.get(1);
        assertEquals("minecraft:connection", connection.getString("name"));
        assertEquals(1, connection.getInt("enabled_states"));
    }

    @Test
    void sendsClientComponentsInDedicatedServerSchema() {
        CompoundTag components = components(parse(block("pnxtest:schema_block", "",
                "{ \"minecraft:liquid_detection\": { \"detection_rules\": [ { \"can_contain_liquid\": true, "
                        + "\"on_liquid_touches\": \"blocking\", \"stops_liquid_flowing_from_direction\": [\"up\", \"north\"] } ] },"
                        + "\"minecraft:redstone_conductivity\": { \"redstone_conductor\": true },"
                        + "\"minecraft:placement_filter\": { \"conditions\": [ { \"allowed_faces\": [\"side\"] } ] },"
                        + "\"minecraft:material_instances\": { \"*\": { \"texture\": \"stone\", \"face_dimming\": false, "
                        + "\"isotropic\": true, \"ambient_occlusion\": 0.5 } },"
                        + "\"minecraft:transformation\": { \"rotation\": [0, 90, 0], \"rotation_pivot\": [0, 0.5, 0] } }",
                "[]")));

        CompoundTag rule = components.getCompound("minecraft:liquid_detection").getList("detectionRules", CompoundTag.class).get(0);
        assertEquals(1, rule.getByte("canContainLiquid"));
        assertEquals("water", rule.getString("liquidType"));
        assertEquals((1 << BlockFace.UP.getIndex()) | (1 << BlockFace.NORTH.getIndex()), rule.getByte("stopsLiquidFromDirection"));
        assertEquals(1, components.getCompound("minecraft:redstone_conductivity").getByte("redstoneConductor"));
        assertEquals(1, components.getCompound("minecraft:redstone_conductivity").getByte("allowsWireToStepDown"));
        assertEquals(0x3c, components.getCompound("minecraft:placement_filter")
                .getList("conditions", CompoundTag.class).get(0).getByte("allowed_faces"));
        CompoundTag material = components.getCompound("minecraft:material_instances").getCompound("materials").getCompound("*");
        assertEquals(0x6, material.getByte("packed_bools"));
        assertEquals(0.5f, material.getFloat("ambient_occlusion"));
        assertEquals(0.5f, components.getCompound("minecraft:transformation").getFloat("RYP"));
    }

    @Test
    void readsMapColorTintMethod() {
        BlockDataDriven.Spec tinted = parse(block("pnxtest:tinted_map", "",
                "{ \"minecraft:map_color\": { \"color\": \"#ba8524\", \"tint_method\": \"default_foliage\" } }", "[]"));
        BlockDataDriven.Spec plain = parse(block("pnxtest:plain_map", "", "{ \"minecraft:map_color\": \"#ba8524\" }", "[]"));

        assertEquals(BlockColor.Tint.DEFAULT_FOLIAGE, tinted.behavior().mapTint());
        assertEquals(BlockColor.Tint.NONE, plain.behavior().mapTint());
    }

    @Test
    void sendsDisabledCollisionBoxWithEmptyBoxList() {
        CompoundTag box = components(parse(block("pnxtest:no_collision", "", "{ \"minecraft:collision_box\": false }", "[]")))
                .getCompound("minecraft:collision_box");

        assertFalse(box.getBoolean("enabled"));
        assertEquals(0, box.getList("boxes", CompoundTag.class).size(), "the dedicated server sends an empty box list");
    }

    @Test
    void sendsDisabledSelectionBoxInSelectionSchema() {
        CompoundTag box = components(parse(block("pnxtest:no_selection", "", "{ \"minecraft:selection_box\": false }", "[]")))
                .getCompound("minecraft:selection_box");

        assertFalse(box.getBoolean("enabled"));
        assertEquals(3, box.getList("origin").size());
        assertEquals(3, box.getList("size").size());
        assertFalse(box.contains("boxes"), "selection boxes must not use the collision box schema");
    }

    @Test
    void dropsPermutationsWithNothingToSend() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:tag_only_permutation",
                ", \"states\": { \"pnxtest:open\": [false, true] }",
                "{}",
                "[ { \"condition\": \"q.block_state('pnxtest:open')\", \"components\": { \"tag:pnxtest_open\": {} } } ]"));

        assertFalse(spec.definition().nbt().contains("permutations"), "an empty components compound must not be sent");
    }

    @Test
    void sendsPermutationGeometryInFullSchema() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:permutation_geometry",
                ", \"states\": { \"pnxtest:tall\": [false, true] }",
                "{}",
                "[ { \"condition\": \"q.block_state('pnxtest:tall')\", \"components\": { \"minecraft:geometry\": \"geometry.tall\" } } ]"));

        CompoundTag geometry = spec.definition().nbt().getList("permutations", CompoundTag.class).get(0)
                .getCompound("components").getCompound("minecraft:geometry");
        assertEquals("geometry.tall", geometry.getString("identifier"));
        assertTrue(geometry.getTags().size() > 2, "the permutation geometry must carry the builder's full default schema");
    }

    @Test
    void resolvesConditionalTagsPerState() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:conditional_tags",
                ", \"states\": { \"pnxtest:color\": [\"red\", \"blue\"] }",
                "{ \"tag:pnxtest_base\": {} }",
                "[ { \"condition\": \"q.block_state('pnxtest:color') == 'blue'\", \"components\": { \"tag:pnxtest_blue\": {} } } ]"));

        BlockDataDriven red = new BlockDataDriven(stateWith(spec, "pnxtest:color", "red"), spec);
        BlockDataDriven blue = new BlockDataDriven(stateWith(spec, "pnxtest:color", "blue"), spec);

        assertTrue(red.hasTag("pnxtest_base"));
        assertFalse(red.hasTag("pnxtest_blue"));
        assertTrue(blue.hasTag("pnxtest_base"));
        assertTrue(blue.hasTag("pnxtest_blue"));
        assertEquals(2, blue.getTags().length);
    }

    @Test
    void registersBlockWithServerSideGameplayValues() {
        String id = "pnxtest:registered_block";

        assertEquals(id, parser.parseAndRegister("TestPack", "block.json", block(id, "",
                "{ \"minecraft:destructible_by_mining\": { \"seconds_to_destroy\": 2.5 }, \"minecraft:friction\": 0.7, \"minecraft:light_emission\": 9 }",
                "[]")));

        Block block = Registries.BLOCK.get(id);
        assertInstanceOf(BlockDataDriven.class, block);
        assertEquals(2.5, block.getHardness(), 1.0e-6);
        assertEquals(0.7, block.getFrictionFactor(), 1.0e-6);
        assertEquals(9, block.getLightLevel());
    }

    @Test
    void skipsSecondDefinitionOfSameIdentifier() {
        String id = "pnxtest:registered_twice";

        assertEquals(id, parser.parseAndRegister("TestPack", "a.json", block(id, "", "{}", "[]")));
        assertNull(parser.parseAndRegister("TestPack", "b.json", block(id, "", "{}", "[]")));
        assertFalse(tracker.isEmpty());
    }

    @Test
    void skipsVanillaOverride() {
        assertNull(parser.parseDefinition("TestPack", "block.json", block("minecraft:stone", "", "{}", "[]")));
        assertFalse(tracker.isEmpty());
    }

    @Test
    void skipsBlockWithTooManyStateCombinations() {
        StringBuilder states = new StringBuilder(", \"states\": {");
        for (int i = 0; i < 17; i++) {
            states.append(i == 0 ? "" : ",").append("\"pnxtest:bit").append(i).append("\": [false, true]");
        }
        states.append('}');

        assertNull(parser.parseDefinition("TestPack", "block.json", block("pnxtest:too_many_states", states.toString(), "{}", "[]")));
    }

    @Test
    void recordsUnsupportedComponentsWithoutFailingTheBlock() {
        assertNotNull(parser.parseDefinition("TestPack", "block.json",
                block("pnxtest:unknown_component", "", "{ \"minecraft:totally_made_up\": {} }", "[]")));
        assertFalse(tracker.isEmpty());
    }

    private static LootTable singleItemTable(String item) {
        LootTable.Entry entry = new LootTable.Entry(LootTable.EntryType.ITEM, item, 1, LootTable.NumberRange.ONE,
                null, null, null, List.of());
        return new LootTable(List.of(new LootTable.Pool(LootTable.NumberRange.ONE, List.of(), List.of(entry))));
    }

    @Test
    void dropsFromLootTableChosenPerState() {
        LootTableRegistry.register("loot_tables/pnxtest/base.json", singleItemTable(ItemID.STICK));
        LootTableRegistry.register("loot_tables/pnxtest/ripe.json", singleItemTable(ItemID.DIAMOND));
        BlockDataDriven.Spec spec = parse(block("pnxtest:loot_block",
                ", \"states\": { \"pnxtest:ripe\": [false, true] }",
                "{ \"minecraft:loot\": \"loot_tables/pnxtest/base.json\" }",
                "[ { \"condition\": \"q.block_state('pnxtest:ripe')\", \"components\": { \"minecraft:loot\": { \"table\": \"loot_tables/pnxtest/ripe.json\" } } } ]"));

        Item[] unripe = new BlockDataDriven(stateWith(spec, "pnxtest:ripe", false), spec).getDrops(Item.AIR);
        Item[] ripe = new BlockDataDriven(stateWith(spec, "pnxtest:ripe", true), spec).getDrops(Item.AIR);

        assertEquals(1, unripe.length);
        assertEquals(ItemID.STICK, unripe[0].getId());
        assertEquals(1, ripe.length);
        assertEquals(ItemID.DIAMOND, ripe[0].getId());
        assertFalse(spec.definition().nbt().contains("permutations"), "loot is server-side only and must not reach the client");
    }

    @Test
    void dropsNothingWhenNamedLootTableIsMissing() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:missing_loot", "",
                "{ \"minecraft:loot\": \"loot_tables/pnxtest/does_not_exist.json\" }", "[]"));

        assertEquals(0, new BlockDataDriven(spec.properties().getDefaultState(), spec).getDrops(Item.AIR).length);
        assertTrue(tracker.isEmpty());
    }

    @Test
    void appliesPermutationGameplayValuesPerState() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:per_state_lamp",
                ", \"states\": { \"pnxtest:lit\": [false, true] }",
                "{ \"minecraft:light_emission\": 0, \"minecraft:friction\": 0.4 }",
                "[ { \"condition\": \"q.block_state('pnxtest:lit')\", \"components\": { \"minecraft:light_emission\": 13, "
                        + "\"minecraft:friction\": 0.8, \"minecraft:destructible_by_mining\": { \"seconds_to_destroy\": 5 } } } ]"));

        BlockDataDriven unlit = new BlockDataDriven(stateWith(spec, "pnxtest:lit", false), spec);
        BlockDataDriven lit = new BlockDataDriven(stateWith(spec, "pnxtest:lit", true), spec);

        assertEquals(0, unlit.getLightLevel());
        assertEquals(13, lit.getLightLevel());
        assertEquals(0.8, lit.getFrictionFactor(), 1.0e-6);
        assertEquals(5.0, lit.getHardness(), 1.0e-6);

        unlit.setPropertyValue(stateWith(spec, "pnxtest:lit", true).getBlockPropertyValues().getFirst());
        assertEquals(13, unlit.getLightLevel(), "changing the state in place must switch to that state's values");
    }

    @Test
    void rotatesCollisionBoxWithPermutationTransformation() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:rotated_box",
                ", \"states\": { \"pnxtest:turned\": [false, true] }",
                "{ \"minecraft:collision_box\": { \"origin\": [-8, 0, -8], \"size\": [16, 8, 8] } }",
                "[ { \"condition\": \"q.block_state('pnxtest:turned')\", \"components\": { "
                        + "\"minecraft:transformation\": { \"rotation\": [0, 90, 0] } } } ]"));

        AxisAlignedBB north = new BlockDataDriven(stateWith(spec, "pnxtest:turned", false), spec).getCollisionBoxes()[0];
        AxisAlignedBB west = new BlockDataDriven(stateWith(spec, "pnxtest:turned", true), spec).getCollisionBoxes()[0];

        assertEquals(0.0, north.getMinZ(), 1.0e-6);
        assertEquals(0.5, north.getMaxZ(), 1.0e-6);
        assertEquals(0.5, north.getMaxY(), 1.0e-6);
        assertEquals(0.0, west.getMinX(), 1.0e-6);
        assertEquals(0.5, west.getMaxX(), 1.0e-6);
        assertEquals(1.0, west.getMaxZ(), 1.0e-6);
    }

    @Test
    void readsLiquidFlowRules() {
        BlockDataDriven held = new BlockDataDriven(null, parse(block("pnxtest:water_holder", "",
                "{ \"minecraft:liquid_detection\": { \"detection_rules\": [ { \"can_contain_liquid\": true, "
                        + "\"stops_liquid_flowing_from_direction\": [\"down\", \"north\"] } ] } }", "[]")));
        BlockDataDriven through = new BlockDataDriven(null, parse(block("pnxtest:water_through", "",
                "{ \"minecraft:liquid_detection\": { \"detection_rules\": [ { \"on_liquid_touches\": \"no_reaction\", "
                        + "\"stops_liquid_flowing_from_direction\": [\"east\"] } ] } }", "[]")));
        BlockDataDriven washed = new BlockDataDriven(null, parse(block("pnxtest:water_broken", "",
                "{ \"minecraft:liquid_detection\": { \"detection_rules\": [ { \"on_liquid_touches\": \"broken\" } ] } }", "[]")));

        assertTrue(held.stopsLiquidFlowingOut(BlockFace.NORTH));
        assertFalse(held.stopsLiquidFlowingOut(BlockFace.SOUTH));
        assertFalse(held.stopsLiquidFlowingIn(BlockFace.NORTH), "only no_reaction blocks keep water out by face");
        assertTrue(through.stopsLiquidFlowingIn(BlockFace.EAST));
        assertFalse(through.stopsLiquidFlowingIn(BlockFace.WEST));
        assertTrue(washed.isBrokenByLiquidWithoutDrops());
        assertFalse(held.isBrokenByLiquidWithoutDrops());
    }

    @Test
    void propagatingConsumerConductsRedstone() {
        BlockDataDriven propagating = new BlockDataDriven(null, parse(block("pnxtest:consumer_on", "",
                "{ \"minecraft:redstone_consumer\": { \"propagates_power\": true } }", "[]")));
        BlockDataDriven blocking = new BlockDataDriven(null, parse(block("pnxtest:consumer_off", "",
                "{ \"minecraft:redstone_consumer\": { \"min_power\": 3 } }", "[]")));

        BlockDataDriven noStep = new BlockDataDriven(null, parse(block("pnxtest:no_step", "",
                "{ \"minecraft:redstone_conductivity\": { \"allows_wire_to_step_down\": false } }", "[]")));

        assertFalse(noStep.allowsWireToStepDown());
        assertTrue(propagating.allowsWireToStepDown());
        assertTrue(propagating.isNormalBlock());
        assertFalse(blocking.isNormalBlock());
        assertTrue(tracker.isEmpty(), "redstone_consumer must not be reported unsupported");
    }

    @Test
    void scalesCollisionBoxAboutScalePivot() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:squashed_box", "",
                "{ \"minecraft:collision_box\": { \"origin\": [-8, 0, -8], \"size\": [16, 16, 16] }, "
                        + "\"minecraft:transformation\": { \"scale\": [1, 0.5, 1], \"scale_pivot\": [0, -0.5, 0] } }", "[]"));

        AxisAlignedBB box = new BlockDataDriven(null, spec).getCollisionBoxes()[0];

        assertEquals(0.0, box.getMinY(), 1.0e-6);
        assertEquals(0.5, box.getMaxY(), 1.0e-6);
    }

    @Test
    void disabledCollisionBoxLeavesNothingToCollideWith() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:ghost_block", "", "{ \"minecraft:collision_box\": false }", "[]"));
        BlockDataDriven ghost = new BlockDataDriven(null, spec);

        assertEquals(0, ghost.getCollisionBoxes().length);
        assertNull(ghost.getBoundingBox());
        assertFalse(ghost.isSolid());
    }

    @Test
    void readsFlammableValues() {
        BlockDataDriven flammable = new BlockDataDriven(null, parse(block("pnxtest:flammable_block", "",
                "{ \"minecraft:flammable\": { \"catch_chance_modifier\": 30, \"destroy_chance_modifier\": 60 } }", "[]")));
        BlockDataDriven planks = new BlockDataDriven(null, parse(block("pnxtest:flammable_default", "",
                "{ \"minecraft:flammable\": true }", "[]")));

        assertEquals(30, flammable.getBurnChance());
        assertEquals(60, flammable.getBurnAbility());
        assertEquals(5, planks.getBurnChance());
        assertEquals(20, planks.getBurnAbility());
    }

    @Test
    void parsesPlacementFilterAndTraits() {
        BlockDataDriven.Spec spec = parse(block("pnxtest:filtered_block",
                ", \"traits\": { \"minecraft:placement_direction\": { \"enabled_states\": [\"minecraft:cardinal_direction\"], "
                        + "\"y_rotation_offset\": 180 }, \"minecraft:placement_position\": { \"enabled_states\": [\"minecraft:vertical_half\"] } }",
                "{ \"minecraft:placement_filter\": { \"conditions\": [ { \"allowed_faces\": [\"up\"], "
                        + "\"block_filter\": [\"minecraft:stone\", { \"name\": \"dirt\" }] } ] } }",
                "[]"));

        PlacementFilter filter = spec.behavior().placementFilter();
        assertNotNull(filter);
        assertTrue(filter.allowsPlacement(Block.get(BlockID.STONE), BlockFace.UP));
        assertTrue(filter.allowsPlacement(Block.get(BlockID.DIRT), BlockFace.UP));
        assertFalse(filter.allowsPlacement(Block.get(BlockID.STONE), BlockFace.NORTH));
        assertFalse(filter.allowsPlacement(Block.get(BlockID.OAK_PLANKS), BlockFace.UP));
        assertTrue(spec.behavior().traitPlacement().cardinalDirection());
        assertEquals(180, spec.behavior().traitPlacement().yRotationOffset());
        assertTrue(spec.behavior().traitPlacement().verticalHalf());
        assertTrue(tracker.isEmpty());
    }
}
