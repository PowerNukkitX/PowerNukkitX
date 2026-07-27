package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Rail.Orientation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RailOrientationTest {

    @Test
    void metadataRoundTripsThroughByMetadata() {
        for (Orientation o : Orientation.values()) {
            assertEquals(o, Orientation.byMetadata(o.metadata()));
        }
    }

    @Test
    void byMetadataOutOfRangeFallsBackToZero() {
        assertEquals(Orientation.byMetadata(0), Orientation.byMetadata(-1));
        assertEquals(Orientation.byMetadata(0), Orientation.byMetadata(999));
    }

    @Test
    void straightSelectsAxisOrientation() {
        assertEquals(Orientation.STRAIGHT_NORTH_SOUTH, Orientation.straight(BlockFace.NORTH));
        assertEquals(Orientation.STRAIGHT_NORTH_SOUTH, Orientation.straight(BlockFace.SOUTH));
        assertEquals(Orientation.STRAIGHT_EAST_WEST, Orientation.straight(BlockFace.EAST));
        assertEquals(Orientation.STRAIGHT_EAST_WEST, Orientation.straight(BlockFace.WEST));
    }

    @Test
    void ascendingSelectsPerFace() {
        assertEquals(Orientation.ASCENDING_NORTH, Orientation.ascending(BlockFace.NORTH));
        assertEquals(Orientation.ASCENDING_SOUTH, Orientation.ascending(BlockFace.SOUTH));
        assertEquals(Orientation.ASCENDING_EAST, Orientation.ascending(BlockFace.EAST));
        assertEquals(Orientation.ASCENDING_WEST, Orientation.ascending(BlockFace.WEST));
    }

    @Test
    void curvedMatchesBothConnectingFaces() {
        assertEquals(Orientation.CURVED_SOUTH_EAST, Orientation.curved(BlockFace.SOUTH, BlockFace.EAST));
        assertEquals(Orientation.CURVED_NORTH_WEST, Orientation.curved(BlockFace.NORTH, BlockFace.WEST));
    }

    @Test
    void stateFlagsAreConsistent() {
        assertTrue(Orientation.STRAIGHT_NORTH_SOUTH.isStraight());
        assertFalse(Orientation.STRAIGHT_NORTH_SOUTH.isCurved());

        assertTrue(Orientation.ASCENDING_EAST.isAscending());
        assertTrue(Orientation.CURVED_SOUTH_EAST.isCurved());
    }

    @Test
    void connectingDirectionsAndAscendingDirection() {
        assertTrue(Orientation.STRAIGHT_NORTH_SOUTH.hasConnectingDirections(BlockFace.NORTH, BlockFace.SOUTH));
        assertFalse(Orientation.STRAIGHT_NORTH_SOUTH.hasConnectingDirections(BlockFace.EAST));

        assertEquals(BlockFace.EAST, Orientation.ASCENDING_EAST.ascendingDirection().orElse(null));
        assertTrue(Orientation.STRAIGHT_NORTH_SOUTH.ascendingDirection().isEmpty());
    }
}
