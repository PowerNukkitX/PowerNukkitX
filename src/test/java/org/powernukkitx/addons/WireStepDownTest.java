package org.powernukkitx.addons;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.parser.BehaviorBlockParser;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockRedstoneWire;
import org.powernukkitx.block.property.enums.RedstoneSide;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.powernukkitx.block.property.CommonBlockProperties.REDSTONE_EAST;
import static org.powernukkitx.block.property.CommonBlockProperties.REDSTONE_WEST;

/**
 * A wire on top of a pack block whose {@code minecraft:redstone_conductivity} disallows stepping down does
 * not run down its sides to a wire beside the block, from either end.
 */
class WireStepDownTest {

    private static final String ID = "pnxtest:wire_no_step";
    private static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        new BehaviorBlockParser(new AddonFeatureTracker()).parseAndRegister("TestPack", "block.json", JsonParser.parseString("""
                { "format_version": "1.26.50", "minecraft:block": { "description": { "identifier": "%s" },
                  "components": { "minecraft:redstone_conductivity": { "redstone_conductor": true,
                    "allows_wire_to_step_down": false } } } }
                """.formatted(ID)).getAsJsonObject());
    }

    @Test
    void wiresDoNotStepDownFromNoStepBlock() {
        BlockRedstoneWire[] wires = wiresAround(ID, 3100);

        assertEquals(RedstoneSide.NONE, wires[0].getPropertyValue(REDSTONE_EAST));
        assertEquals(RedstoneSide.NONE, wires[1].getPropertyValue(REDSTONE_WEST));
    }

    @Test
    void wiresStepDownFromStone() {
        BlockRedstoneWire[] wires = wiresAround(BlockID.STONE, 3110);

        assertEquals(RedstoneSide.SIDE, wires[0].getPropertyValue(REDSTONE_EAST));
        assertEquals(RedstoneSide.UP, wires[1].getPropertyValue(REDSTONE_WEST));
    }

    /**
     * Places a wire on top of the support block and a wire on the floor east of it, each with another wire
     * running north or south from it, so neither stretches across toward the other on its own.
     *
     * @return the configured upper and lower wire
     */
    private static BlockRedstoneWire[] wiresAround(String support, int x) {
        Vector3 base = new Vector3(x, 70, 3100);
        for (int dx = -1; dx <= 2; dx++) {
            level.setBlock(base.add(dx, -1, 0), Block.get(BlockID.STONE));
            level.setBlock(base.add(dx, 0, 0), Block.get(BlockID.AIR));
            level.setBlock(base.add(dx, 1, 0), Block.get(BlockID.AIR));
            level.setBlock(base.add(dx, 2, 0), Block.get(BlockID.AIR));
        }
        level.setBlock(base.north(), Block.get(BlockID.STONE));
        level.setBlock(base.north().up(), Block.get(BlockID.REDSTONE_WIRE));
        level.setBlock(base, Block.get(support));
        level.setBlock(base.up(), Block.get(BlockID.REDSTONE_WIRE));
        level.setBlock(base.east(), Block.get(BlockID.REDSTONE_WIRE));
        level.setBlock(base.east().south().down(), Block.get(BlockID.STONE));
        level.setBlock(base.east().south(), Block.get(BlockID.REDSTONE_WIRE));
        BlockRedstoneWire upper = (BlockRedstoneWire) level.getBlock(base.up());
        BlockRedstoneWire lower = (BlockRedstoneWire) level.getBlock(base.east());
        upper.autoConfigureState();
        lower.autoConfigureState();
        return new BlockRedstoneWire[]{upper, lower};
    }
}
