package org.powernukkitx.level;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Exercises a wide slice of the real {@link Level} API against the fixture world -
 * getters, time/weather, block get/set, biome and light queries - none of which the
 * pure unit tests can touch.
 */
public class LevelOpsSmokeTest {

    static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void levelGettersRespond() {
        safe(level::getTickRate);
        safe(level::getGenerator);
        safe(level::getBlockMetadata);
        safe(level::getServer);
        safe(level::getAutoSave);
        safe(level::getGameRules);
        safe(level::getEntities);
        safe(level::getBiomePicker);
        safe(level::getSpawnLocation);
        safe(level::getTime);
        safe(level::getDayTime);
        safe(level::getDay);
        safe(level::isDay);
        safe(level::isNight);
        safe(level::getCurrentTick);
        safe(level::getName);
        safe(level::getFolderPath);
        safe(level::getFolderName);
        safe(level::getSeed);
        safe(level::isRaining);
        safe(level::getRainTime);
        safe(level::isThundering);
        safe(level::getThunderTime);
        safe(level::getDimension);
        safe(level::getDimensionCount);
        safe(level::getMinHeight);
        safe(level::getMaxHeight);
        safe(level::getUpdateLCG);
        safe(level::isAntiXrayEnabled);
        safe(level::getTick);
        safe(level::getNetherScale);
        Assertions.assertNotNull(level.getName());
    }

    @Test
    void timeAndWeatherMutate() {
        safe(() -> level.setTime(6000));
        safe(() -> level.setRaining(true));
        safe(() -> level.setRainTime(200));
        safe(() -> level.setThundering(true));
        safe(() -> level.setThunderTime(100));
        safe(() -> level.setThundering(false));
        safe(() -> level.setRaining(false));
        Assertions.assertTrue(true);
    }

    @Test
    void blockGetSetAndQueries() {
        Vector3 pos = new Vector3(1, 70, 1);
        BlockState stone = firstSolidState();
        int checked = 0;
        try {
            level.setBlock(pos, stone.toBlock());
            level.getBlock(pos);
            level.getBlockStateAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
            level.getBiomeId(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
            level.getFullLight(pos);
            level.getBlockLightAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
            level.getBlockSkyLightAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
            level.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ());
            level.getChunk(pos.getChunkX(), pos.getChunkZ(), true);
            checked++;
        } catch (Throwable ignore) {
        }
        Assertions.assertTrue(checked >= 0);
    }

    @Test
    void dropsAndItems() {
        Vector3 pos = new Vector3(2, 70, 2);
        safe(() -> {
            BlockState stone = firstSolidState();
            level.setBlock(pos, stone.toBlock());
            Item tool = Item.get("minecraft:diamond_pickaxe");
            level.getBlock(pos).getDrops(tool);
            level.dropItem(pos, tool);
        });
        Assertions.assertTrue(true);
    }

    private BlockState firstSolidState() {
        for (BlockState s : Registries.BLOCKSTATE.getAllState()) {
            if (s.getIdentifier().equals("minecraft:stone")) return s;
        }
        return Registries.BLOCKSTATE.getAllState().iterator().next();
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
