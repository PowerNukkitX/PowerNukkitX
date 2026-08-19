package org.powernukkitx.player;

import org.powernukkitx.PlayerFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.event.player.PlayerTeleportEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTeleportMovementTest {

    private static final Vector3 ORIGIN = new Vector3(0.5, 80, 0.5);
    private static final Vector3 DESTINATION = new Vector3(40.5, 80, 40.5);

    static TestPlayer player;
    static Level level;

    @BeforeAll
    static void boot() {

        player = PlayerFixture.newPlayer();
        level = player.getLevel();
    }

    @BeforeEach
    void teleportAway() {
        player.loggedIn = true;
        player.spawned = true;
        player.setHealth(20);

        player.temporalVector = new Vector3();
        player.setPosition(ORIGIN);

        Assertions.assertTrue(player.teleport(
                Location.fromObject(DESTINATION, level),
                PlayerTeleportEvent.TeleportCause.ENDER_PEARL));
    }

    @Test
    void movesSentBeforeTheTeleportWasAppliedAreDropped() {
        player.offerMovementTask(Location.fromObject(ORIGIN.add(0.2, 0, 0), level));

        assertNextPositionIs(DESTINATION);
    }

    @Test
    void theFirstStepAfterLandingIsAcceptedImmediately() {
        Vector3 afterLanding = DESTINATION.add(0.8, 0, 0);

        player.offerMovementTask(Location.fromObject(afterLanding, level));

        assertNextPositionIs(afterLanding);
    }

    @Test
    void movementKeepsFlowingOnceTheTeleportIsConfirmed() {
        player.offerMovementTask(Location.fromObject(DESTINATION.add(0.8, 0, 0), level));

        Vector3 walkedOff = DESTINATION.add(6, 0, 0);
        player.offerMovementTask(Location.fromObject(walkedOff, level));

        assertNextPositionIs(walkedOff);
    }

    private static void assertNextPositionIs(Vector3 expected) {
        Position next = player.getNextPosition();
        Assertions.assertEquals(expected.getX(), next.getX(), 1e-6);
        Assertions.assertEquals(expected.getY(), next.getY(), 1e-6);
        Assertions.assertEquals(expected.getZ(), next.getZ(), 1e-6);
    }
}
