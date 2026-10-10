package org.powernukkitx;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * A packet handler that moves the player to another level hands the player, and the rest of its
 * inbound queue, to that level's thread.
 */
@ExtendWith(GameMockExtension.class)
class PlayerLevelSwitchTest {
    private final List<BedrockPacket> handled = new ArrayList<>();

    private TestPlayer player;
    private Level level;
    private Level otherLevel;

    @BeforeEach
    void setUp(TestPlayer player, Level level) {
        this.player = player;
        this.level = level;
        this.otherLevel = mock(Level.class);
        // The first packet handled moves the player, as a /tp to another world would
        player.setInboundProcessor(packet -> {
            this.handled.add(packet);
            player.level = this.otherLevel;
        });
    }

    @AfterEach
    void tearDown() {
        setLastTickLevel(null);
        this.player.level = this.level;
        this.player.setInboundProcessor(null);
        this.player.inboundPackets.clear();
        this.player.clientMovements.clear();
        this.player.loggedIn = false;
        this.player.spawned = false;
    }

    @Test
    void drainStopsOnceAHandlerMovesThePlayerToAnotherLevel() {
        TextPacket first = new TextPacket();
        TextPacket second = new TextPacket();
        this.player.handlePacket(first);
        this.player.handlePacket(second);

        this.player.drainInboundPackets();

        assertEquals(List.of(first), this.handled);
        assertSame(second, this.player.inboundPackets.peek());
    }

    @Test
    void onUpdateStopsOnceAHandlerMovesThePlayerToAnotherLevel() {
        assumeTrue(this.player.isAlive());
        this.player.loggedIn = true;
        this.player.spawned = true;
        Location queuedMove = new Location(1, 100, 1, this.level);
        assertTrue(this.player.clientMovements.offer(queuedMove));
        this.player.handlePacket(new TextPacket());

        boolean keepTicking = this.player.onUpdate(this.player.lastUpdate + 1);

        assertTrue(keepTicking);
        assertEquals(1, this.handled.size());
        // Movement is left to the new level's thread instead of being applied by the old one
        assertSame(queuedMove, this.player.clientMovements.peek());
    }

    @Test
    void newLevelWaitsUntilThePreviousLevelHasFinishedItsTick() {
        this.player.loggedIn = true;
        this.player.spawned = true;
        this.player.handlePacket(new TextPacket());
        int lastUpdate = this.player.lastUpdate;
        Level previous = mock(Level.class);
        doReturn(true).when(previous).isTickedByAnotherThread();
        setLastTickLevel(previous);

        assertTrue(this.player.onUpdate(lastUpdate + 1));
        assertTrue(this.handled.isEmpty());
        assertEquals(lastUpdate, this.player.lastUpdate);

        // Previous level's tick is over, so the new level takes the player over
        doReturn(false).when(previous).isTickedByAnotherThread();
        assertTrue(this.player.onUpdate(lastUpdate + 1));
        assertEquals(1, this.handled.size());
    }

    @Test
    void levelReportsOnlyTicksRunningOnOtherThreads() {
        try {
            TestUtils.setField(Level.class, this.level, "tickingThread", Thread.currentThread());
            assertFalse(this.level.isTickedByAnotherThread());
            TestUtils.setField(Level.class, this.level, "tickingThread", new Thread(() -> {
            }));
            assertTrue(this.level.isTickedByAnotherThread());
        } finally {
            TestUtils.setField(Level.class, this.level, "tickingThread", null);
        }

        this.level.doTick(this.level.getTick() + 1);
        assertFalse(this.level.isTickedByAnotherThread());
        assertNull(TestUtils.getField(Level.class, this.level, "tickingThread"));
    }

    private void setLastTickLevel(Level level) {
        TestUtils.setField(Player.class, this.player, "lastTickLevel", level);
    }
}
