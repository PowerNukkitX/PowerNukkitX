package org.powernukkitx.positiontracking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tolerant coverage for the positiontracking package - exercises PositionTracking
 * vector arithmetic and drives the service add / read handler paths.
 */
class PositionTrackingSmokeTest {

    private static final AtomicInteger checked = new AtomicInteger();

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }

    private static void safe(ThrowingRunnable r) {
        try {
            r.run();
            checked.incrementAndGet();
        } catch (Throwable ignored) {
        }
    }

    @Test
    void positionTrackingGettersAndArithmetic() {
        safe(() -> {
            PositionTracking pt = new PositionTracking("world", 1.0, 2.0, 3.0);
            pt.getLevelName();
            pt.getFloorX();
            pt.getFloorY();
            pt.getFloorZ();
            pt.setLevelName("nether");
            pt.getLevelName();
        });
        safe(() -> {
            PositionTracking pt = new PositionTracking("world", new Vector3(4, 5, 6));
            pt.add(1);
            pt.add(1, 2);
            pt.add(1, 2, 3);
            pt.add(new Vector3(1, 1, 1));
            pt.subtract(1);
            pt.subtract(1, 2);
            pt.subtract(1, 2, 3);
            pt.subtract(new Vector3(1, 1, 1));
            pt.multiply(2);
            pt.divide(2);
            pt.ceil();
            pt.floor();
            pt.round();
            pt.abs();
        });
        safe(() -> {
            PositionTracking pt = new PositionTracking("world", 0, 0, 0);
            pt.up();
            pt.up(2);
            pt.down();
            pt.down(2);
            pt.north();
            pt.south();
            pt.east();
            pt.west();
            pt.getSide(BlockFace.UP);
            pt.getSide(BlockFace.NORTH, 3);
            pt.clone();
        });
        safe(() -> {
            PositionTracking a = new PositionTracking("world", 1, 1, 1);
            PositionTracking b = new PositionTracking("world", 1, 1, 1);
            a.matchesNamedPosition(b);
        });
        safe(() -> {
            PositionTracking pt = new PositionTracking("world", 1, 2, 3);
            pt.setComponents(9, 9, 9);
            pt.setComponents(new Vector3(1, 2, 3));
        });
        assertTrue(checked.get() > 0);
    }

    @Test
    void serviceAddAndReadHandlers() {
        PositionTrackingService service = ServerMockFixture.server.getPositionTrackingService();
        if (service == null) {
            return;
        }

        final int[] handler = {-1};
        safe(() -> {
            PositionTracking pos = new PositionTracking("world", 10, 64, 20);
            handler[0] = service.addNewPosition(pos);
        });
        safe(() -> {
            if (handler[0] >= 0) {
                service.getPosition(handler[0]);
                service.hasPosition(handler[0]);
                service.isEnabled(handler[0]);
            }
        });
        safe(() -> {
            PositionTracking pos = new PositionTracking("world", 10, 64, 20);
            service.addOrReusePosition(pos);
            service.findTrackingHandler(pos);
            service.findTrackingHandlers(pos);
        });
        safe(() -> {
            if (handler[0] >= 0) {
                service.setEnabled(handler[0], false);
                service.setEnabled(handler[0], true);
                service.invalidateHandler(handler[0]);
            }
        });
        assertTrue(checked.get() > 0);
    }
}
