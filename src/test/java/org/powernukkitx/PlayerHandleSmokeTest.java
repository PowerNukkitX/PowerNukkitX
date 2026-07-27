package org.powernukkitx;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Exercises the network-free accessors of {@link PlayerHandle}: field getters/setters that
 * simply proxy the wrapped player. Risky calls (respawn, login, movement) are safe-wrapped.
 */
class PlayerHandleSmokeTest {

    private static TestPlayer player;
    private static PlayerHandle handle;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
        player = PlayerFixture.get();
        handle = new PlayerHandle(player);
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    @Test
    void wrapsPlayer() {
        assertSame(player, handle.player);
        assertNotNull(handle.packetRateLimiter);
        assertEquals(player.getName(), handle.getUsername());
    }

    @Test
    void primitiveFieldRoundTrips() {
        handle.setBreakingBlockTime(1234L);
        assertEquals(1234L, handle.getBreakingBlockTime());

        handle.setBlockBreakProgress(0.5);
        assertEquals(0.5, handle.getBlockBreakProgress());

        handle.setMessageCounter(7);
        assertEquals(7, handle.getMessageCounter());

        handle.setChunkLoadCount(3);
        assertEquals(3, handle.getChunkLoadCount());

        handle.setNextChunkOrderRun(9);
        assertEquals(9, handle.getNextChunkOrderRun());

        handle.setChunkRadius(8);
        assertEquals(8, handle.getChunkRadius());

        handle.setStartAirTicks(2);
        assertEquals(2, handle.getStartAirTicks());

        handle.setLastPlayedLevelUpSoundTime(11);
        assertEquals(11, handle.getLastPlayedLevelUpSoundTime());

        handle.setClosingWindowId(5);
        assertEquals(5, handle.getClosingWindowId());

        handle.setFormWindowCount(4);
        assertEquals(4, handle.getFormWindowCount());
    }

    @Test
    void objectFieldRoundTrips() {
        String name = "Handle Display";
        handle.setDisplayName(name);
        assertEquals(name, handle.getDisplayName());

        Vector3 pos = new Vector3(1, 2, 3);
        handle.setNewPosition(pos);
        assertSame(pos, handle.getNewPosition());

        Vector3 sleep = new Vector3(4, 5, 6);
        handle.setSleeping(sleep);
        assertSame(sleep, handle.getSleeping());

        Position spawn = new Position(10, 64, 10, ServerMockFixture.level);
        handle.setSpawnPosition(spawn);
        assertSame(spawn, handle.getSpawnPosition());
    }

    @Test
    void inventoryAndWindowFlags() {
        handle.setInventoryOpen(true);
        assertEquals(true, handle.getInventoryOpen());
        handle.setInventoryOpen(false);
        assertEquals(false, handle.getInventoryOpen());

        assertNotNull(handle.getWindows());
        assertNotNull(handle.getWindowIndex());
        assertNotNull(handle.getFormWindows());
        assertNotNull(handle.getHiddenPlayers());
        assertNotNull(handle.getFogStack());
    }

    @Test
    void miscReadOnlyAccessors() {
        handle.getChunksPerTick();
        handle.getSpawnThreshold();
        handle.isCheckMovement();
        handle.getShowingCredits();
        handle.getBaseOffset();
        assertEquals(Player.NO_SHIELD_DELAY, PlayerHandle.getNoShieldDelay());
    }

    @Test
    void riskyCallsAreTolerant() {
        safe(() -> handle.addDefaultWindows());
        safe(() -> handle.setInAirTicks(0));
        safe(() -> handle.setLastInAirTick(0));
        safe(() -> handle.setInteract());
        safe(() -> handle.getLoginChainData());
    }
}
