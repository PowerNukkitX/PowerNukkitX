package org.powernukkitx.addons;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.parser.BehaviorBlockParser;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;

import com.google.gson.JsonParser;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.ItemUsePredictedResult;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.ItemUseInventoryTransaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Clients predict a pack block's placement filter as failing, so the server places such blocks by the filter
 * alone, as the dedicated server does.
 */
class PlacementFilterUseTest {

    private static final String ID = "pnxtest:stone_only";
    private static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        new BehaviorBlockParser(new AddonFeatureTracker()).parseAndRegister("TestPack", "block.json", JsonParser.parseString("""
                { "format_version": "1.26.50", "minecraft:block": { "description": { "identifier": "%s" },
                  "components": { "minecraft:placement_filter": { "conditions": [
                    { "allowed_faces": ["up"], "block_filter": ["minecraft:stone"] } ] } } } }
                """.formatted(ID)).getAsJsonObject());
    }

    @Test
    void placesOnAcceptedSupportDespiteFailedPrediction() {
        assertEquals(ID, placeOn(BlockID.STONE, 3000).getId());
    }

    @Test
    void refusesUnacceptedSupport() {
        assertTrue(placeOn(BlockID.DIRT, 3004).isAir());
    }

    private static Block placeOn(String support, int x) {
        Vector3 below = new Vector3(x, 70, 3000);
        level.setBlock(below, Block.get(support));
        level.setBlock(below.up(), Block.get(BlockID.AIR));
        ItemUseInventoryTransaction data = new ItemUseInventoryTransaction();
        data.setClickPosition(Vector3f.from(0.5f, 1f, 0.5f));
        data.setClientInteractPrediction(ItemUsePredictedResult.FAILURE);
        level.useItemOn(below, Block.get(ID).toItem(), BlockFace.UP, data);
        return level.getBlock(below.up());
    }
}
