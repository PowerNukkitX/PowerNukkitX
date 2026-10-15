package org.powernukkitx.block.customblock;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.addons.parser.BehaviorBlockParser;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.PrecipitationBehavior;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.math.BlockFace;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers what behaviour pack block components and traits change on the server, through the parser, so the
 * JSON forms the official reference documents are what is exercised.
 */
class BlockDataDrivenBehaviorTest {

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

    private BlockDataDriven.Spec parse(String identifier, String description, String components, String permutations) {
        BehaviorBlockParser.ParsedBlock parsed = parser.parseDefinition("TestPack", "block.json", JsonParser.parseString("""
                { "format_version": "1.26.50", "minecraft:block": {
                  "description": { "identifier": "%s"%s },
                  "components": %s, "permutations": %s } }
                """.formatted(identifier, description, components, permutations)).getAsJsonObject());
        assertNotNull(parsed);
        return parsed.spec();
    }

    private static BlockState state(BlockDataDriven.Spec spec, Map<String, Object> values) {
        for (BlockState state : spec.properties().getSpecialValueMap().values()) {
            boolean matches = true;
            for (BlockPropertyType.BlockPropertyValue<?, ?, ?> value : state.getBlockPropertyValues()) {
                Object wanted = values.get(value.getPropertyType().getName());
                if (wanted != null && !wanted.equals(value.getValue())) {
                    matches = false;
                }
            }
            if (matches) {
                return state;
            }
        }
        throw new AssertionError("no state " + values);
    }

    @Test
    void rotationOffsetTurnsCounterClockwise() {
        assertEquals("east", BlockDataDriven.turn(BlockFace.SOUTH, 90));
        assertEquals("south", BlockDataDriven.turn(BlockFace.SOUTH, 0));
        assertEquals("north", BlockDataDriven.turn(BlockFace.SOUTH, 180));
        assertEquals("west", BlockDataDriven.turn(BlockFace.SOUTH, 270));
    }

    @Test
    void producesRedstoneOnConnectedFaces() {
        BlockDataDriven block = new BlockDataDriven(null, parse("pnxtest:power_block", "",
                "{ \"minecraft:redstone_producer\": { \"power\": 12, \"connected_faces\": [\"north\"], \"strongly_powered_face\": \"north\" } }",
                "[]"));

        assertTrue(block.isPowerSource());
        assertEquals(12, block.getWeakPower(BlockFace.SOUTH), "a receiver to the north queries with the south face");
        assertEquals(0, block.getWeakPower(BlockFace.NORTH));
        assertEquals(12, block.getStrongPower(BlockFace.SOUTH));
    }

    @Test
    void permutationRedstoneProducerAppliesPerState() {
        BlockDataDriven.Spec spec = parse("pnxtest:switch_block", ", \"states\": { \"pnxtest:on\": [false, true] }", "{}",
                "[ { \"condition\": \"q.block_state('pnxtest:on')\", \"components\": { \"minecraft:redstone_producer\": { \"power\": 15 } } } ]");

        assertFalse(new BlockDataDriven(state(spec, Map.of("pnxtest:on", false)), spec).isPowerSource());
        assertEquals(15, new BlockDataDriven(state(spec, Map.of("pnxtest:on", true)), spec).getWeakPower(BlockFace.UP));
    }

    @Test
    void readsServerSideComponents() {
        BlockDataDriven block = new BlockDataDriven(null, parse("pnxtest:many_components", "", """
                { "minecraft:redstone_conductivity": { "redstone_conductor": false },
                  "minecraft:liquid_detection": { "detection_rules": [ { "liquid_type": "water", "can_contain_liquid": true, "on_liquid_touches": "popped" } ] },
                  "minecraft:movable": { "movement_type": "popped" },
                  "minecraft:replaceable": {},
                  "minecraft:precipitation_interactions": { "precipitation_behavior": "none" },
                  "minecraft:chest_obstruction": { "obstruction_rule": "never" },
                  "minecraft:flower_pottable": {},
                  "minecraft:block_light_emission": 0.5 }
                """, "[]"));

        assertFalse(block.isNormalBlock());
        assertTrue(block.canBeFlowedInto());
        assertEquals(1, block.getWaterloggingLevel());
        assertTrue(CustomBlockComponentBehavior.breaksWhenMoved(block));
        assertTrue(block.canBeReplaced());
        assertEquals(PrecipitationBehavior.NONE, block.getPrecipitationBehavior());
        assertEquals(BlockDataDriven.ChestObstruction.NEVER, block.getChestObstruction());
        assertTrue(block.isPotBlockState());
        assertEquals(8, block.getLightLevel());
        assertTrue(tracker.isEmpty());
    }

    @Test
    void itemSpecificSpeedReplacesDestroyTime() {
        BlockDataDriven block = new BlockDataDriven(null, parse("pnxtest:hard_block", "", """
                { "minecraft:destructible_by_mining": { "seconds_to_destroy": 10,
                    "item_specific_speeds": [ { "item": "minecraft:diamond_pickaxe", "destroy_speed": 2 } ] } }
                """, "[]"));

        BlockDataDriven faster = new BlockDataDriven(null, parse("pnxtest:soft_block", "",
                "{ \"minecraft:destructible_by_mining\": { \"seconds_to_destroy\": 2 } }", "[]"));
        BlockDataDriven plain = new BlockDataDriven(null, parse("pnxtest:plain_hard_block", "",
                "{ \"minecraft:destructible_by_mining\": { \"seconds_to_destroy\": 10 } }", "[]"));
        Item pickaxe = Item.get(ItemID.DIAMOND_PICKAXE);
        Item stick = Item.get(ItemID.STICK);

        assertEquals(faster.calculateBreakTimeNotInAir(pickaxe, null), block.calculateBreakTimeNotInAir(pickaxe, null), 1.0e-6,
                "with the listed item the block breaks as if its destroy time were 2 seconds");
        assertEquals(plain.calculateBreakTimeNotInAir(stick, null), block.calculateBreakTimeNotInAir(stick, null), 1.0e-6);
    }

    @Test
    void stairSupportFollowsHalfAndFacing() {
        BlockDataDriven.Spec spec = parse("pnxtest:custom_stairs", """
                , "traits": { "minecraft:placement_direction": { "enabled_states": ["minecraft:cardinal_direction"] },
                  "minecraft:placement_position": { "enabled_states": ["minecraft:vertical_half"] } }
                """, "{ \"minecraft:support\": { \"shape\": \"stair\" } }", "[]");
        BlockDataDriven stairs = new BlockDataDriven(state(spec,
                Map.of("minecraft:cardinal_direction", "north", "minecraft:vertical_half", "bottom")), spec);

        assertTrue(stairs.isSolid(BlockFace.DOWN));
        assertFalse(stairs.isSolid(BlockFace.UP));
        assertTrue(stairs.isSolid(BlockFace.NORTH));
        assertFalse(stairs.isSolid(BlockFace.SOUTH));
    }

    @Test
    void connectionRuleRejectsNeighbours() {
        BlockDataDriven block = new BlockDataDriven(null, parse("pnxtest:no_connections", "",
                "{ \"minecraft:connection_rule\": { \"accepts_connections_from\": \"only_fences\", \"enabled_directions\": [\"north\"] } }", "[]"));
        Block north = Block.get(BlockID.STONE);
        north.z = -1;
        Block east = Block.get(BlockID.STONE);
        east.x = 1;

        assertTrue(block.acceptsConnectionFrom(north, true));
        assertFalse(block.acceptsConnectionFrom(north, false), "only fences may connect");
        assertFalse(block.acceptsConnectionFrom(east, true), "east is not an enabled direction");
    }

    @Test
    void declaresTraitStates() {
        BlockDataDriven.Spec multi = parse("pnxtest:tall_lamp", """
                , "traits": { "minecraft:multi_block": { "enabled_states": ["minecraft:multi_block_part"], "parts": 3, "direction": "up" } }
                """, "{ \"minecraft:movable\": { \"movement_type\": \"popped\" } }", "[]");
        BlockDataDriven.Spec connected = parse("pnxtest:connected_block", """
                , "traits": { "minecraft:connection": { "enabled_states": ["minecraft:cardinal_connections"] } }
                """, "{}", "[]");
        BlockDataDriven.Spec corners = parse("pnxtest:corner_stairs", """
                , "traits": { "minecraft:placement_direction": { "enabled_states": ["minecraft:corner_and_cardinal_direction"] } }
                """, "{}", "[]");

        assertEquals(3, multi.behavior().multiBlock().parts());
        assertEquals(3, multi.properties().getSpecialValueMap().size(), "one state per part");
        assertTrue(connected.behavior().cardinalConnections());
        assertEquals(16, connected.properties().getSpecialValueMap().size());
        assertNotNull(corners.behavior().cornerWith());
        assertTrue(corners.behavior().traitPlacement().cardinalDirection());
        assertEquals(20, corners.properties().getSpecialValueMap().size());
        assertTrue(tracker.isEmpty());
    }
}
