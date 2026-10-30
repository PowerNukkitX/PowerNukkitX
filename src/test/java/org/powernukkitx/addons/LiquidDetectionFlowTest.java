package org.powernukkitx.addons;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.parser.BehaviorBlockParser;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.level.Level;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Water flowing next to a pack block whose {@code minecraft:liquid_detection} lets it wash away replaces the
 * block, whether it is broken without drops or popped.
 */
class LiquidDetectionFlowTest {

    static Level level;
    static boolean liquidFlowBefore;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        liquidFlowBefore = level.getGameplaySettings().enableLiquidFlow();
        level.getGameplaySettings().enableLiquidFlow(true);
    }

    @AfterAll
    static void restore() {
        level.getGameplaySettings().enableLiquidFlow(liquidFlowBefore);
    }

    @Test
    void brokenBlockIsReplacedByWater() {
        assertWashedAway("pnxtest:washes_broken", "broken", 2000);
    }

    @Test
    void poppedBlockIsReplacedByWater() {
        assertWashedAway("pnxtest:washes_popped", "popped", 2020);
    }

    private static void assertWashedAway(String id, String touches, int x) {
        new BehaviorBlockParser(new AddonFeatureTracker()).parseAndRegister("TestPack", "block.json", JsonParser.parseString("""
                { "format_version": "1.26.50", "minecraft:block": { "description": { "identifier": "%s" },
                  "components": { "minecraft:collision_box": false,
                    "minecraft:liquid_detection": { "detection_rules": [ { "on_liquid_touches": "%s" } ] } } } }
                """.formatted(id, touches)).getAsJsonObject());
        int y = 70, z = 2000;
        for (int dx = -6; dx <= 7; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                level.setBlock(x + dx, y - 1, z + dz, Block.get(BlockID.STONE), false, false);
            }
        }
        level.setBlock(x, y, z, Block.get(id), false, false);
        level.setBlock(x + 1, y, z, Block.get(BlockID.WATER), false, false);

        ((BlockLiquid) level.getBlock(x + 1, y, z)).onUpdate(Level.BLOCK_UPDATE_SCHEDULED);

        assertInstanceOf(BlockLiquid.class, level.getBlock(x, y, z), id + " should have been washed away");
    }
}
