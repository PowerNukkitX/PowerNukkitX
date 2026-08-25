package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockTurtleEgg;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The block sensor has to hand the closest matching block to the memory storage - it drives, among
 * others, how zombies pick the turtle egg they walk to.
 */
public class BlockSensorTest {

    private static Level level;
    private static EntityIntelligent zombie;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        level.getChunk(0, 0, true);
        var stone = Registries.BLOCKSTATE.getAllState().stream()
                .filter(state -> state.getIdentifier().equals("minecraft:stone")).findFirst().orElseThrow();
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                level.setBlock(new Vector3(x, 63, z), stone.toBlock());
            }
        }
        zombie = (EntityIntelligent) Entity.createEntity("minecraft:zombie", new Position(0.5, 64, 0.5, level));
    }

    @Test
    void picksTheClosestMatchingBlock() {
        var far = new Vector3(6, 64, 0);
        var near = new Vector3(2, 64, 0);
        level.setBlock(far, Block.get(BlockTurtleEgg.TURTLE_EGG));
        level.setBlock(near, Block.get(BlockTurtleEgg.TURTLE_EGG));
        try {
            new BlockSensor(BlockTurtleEgg.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2).sense(zombie);

            Block found = zombie.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK);
            Assertions.assertNotNull(found, "the sensor found no turtle egg");
            Assertions.assertEquals(near.getFloorX(), found.getFloorX(), "the sensor did not pick the closest egg");
            Assertions.assertEquals(near.getFloorZ(), found.getFloorZ(), "the sensor did not pick the closest egg");
        } finally {
            level.setBlock(far, Block.get(Block.AIR));
            level.setBlock(near, Block.get(Block.AIR));
            zombie.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_BLOCK);
        }
    }

    @Test
    void clearsTheMemoryWhenNothingMatches() {
        new BlockSensor(BlockTurtleEgg.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2).sense(zombie);

        Assertions.assertNull(zombie.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK));
    }
}
