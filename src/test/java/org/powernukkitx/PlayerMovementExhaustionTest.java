package org.powernukkitx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerMovementExhaustionTest {

    private static final double EPSILON = 1.0e-7;

    @Test
    void usesLinearHorizontalDistanceOnGround() {
        assertEquals(0.2, exhaustion(2.0, 0.0, 0.0, false, false, true, true, false), EPSILON);
        assertEquals(0.05, exhaustion(0.5, 0.0, 0.0, false, false, true, true, false), EPSILON);
        assertEquals(0.02, exhaustion(2.0, 0.0, 0.0, false, false, true, false, false), EPSILON);
    }

    @Test
    void usesThreeDimensionalDistanceOnlyWhenSubmerged() {
        assertEquals(0.03, exhaustion(0.0, 2.0, 0.0, true, true, false, false, false), EPSILON);
        assertEquals(0.0, exhaustion(0.0, 2.0, 0.0, false, true, false, false, false), EPSILON);
        assertEquals(0.03, exhaustion(2.0, 0.0, 0.0, false, true, false, false, false), EPSILON);
    }

    @Test
    void roundsDistanceToCentimetresBeforeApplyingExhaustion() {
        assertEquals(0.005, exhaustion(0.03, 0.0, 0.04, false, false, true, true, false), EPSILON);
        assertEquals(0.0, exhaustion(0.004, 0.0, 0.0, false, false, true, true, false), EPSILON);
    }

    @Test
    void ignoresAirborneAndVehicleMovement() {
        assertEquals(0.0, exhaustion(2.0, 0.0, 0.0, false, false, false, true, false), EPSILON);
        assertEquals(0.0, exhaustion(2.0, 0.0, 0.0, true, true, true, true, true), EPSILON);
    }

    private static double exhaustion(double deltaX, double deltaY, double deltaZ,
                                     boolean underWater, boolean inWater, boolean onGround,
                                     boolean sprinting, boolean riding) {
        return Player.calculateMovementExhaustion(
                deltaX, deltaY, deltaZ, underWater, inWater, onGround, sprinting, riding
        );
    }
}
