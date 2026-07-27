package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class VectorMathTest {

    @Test
    void getDirection2D() {
        Vector2 v = VectorMath.getDirection2D(0);
        Assertions.assertEquals(1, v.x, 1e-6);
        Assertions.assertEquals(0, v.y, 1e-6);
        Vector2 v2 = VectorMath.getDirection2D(Math.PI / 2);
        Assertions.assertEquals(0, v2.x, 1e-6);
        Assertions.assertEquals(1, v2.y, 1e-6);
    }

    @Test
    void calculateAxis() {
        Vector3 base = new Vector3(0, 0, 0);
        Assertions.assertEquals(BlockFace.Axis.X, VectorMath.calculateAxis(base, new Vector3(1, 0, 0)));
        Assertions.assertEquals(BlockFace.Axis.Z, VectorMath.calculateAxis(base, new Vector3(0, 0, 1)));
        Assertions.assertEquals(BlockFace.Axis.Y, VectorMath.calculateAxis(base, new Vector3(0, 1, 0)));
    }

    @Test
    void calculateFace() {
        Vector3 base = new Vector3(0, 0, 0);
        Assertions.assertEquals(BlockFace.EAST, VectorMath.calculateFace(base, new Vector3(1, 0, 0)));
        Assertions.assertEquals(BlockFace.WEST, VectorMath.calculateFace(base, new Vector3(-1, 0, 0)));
        Assertions.assertEquals(BlockFace.SOUTH, VectorMath.calculateFace(base, new Vector3(0, 0, 1)));
        Assertions.assertEquals(BlockFace.NORTH, VectorMath.calculateFace(base, new Vector3(0, 0, -1)));
        Assertions.assertEquals(BlockFace.UP, VectorMath.calculateFace(base, new Vector3(0, 1, 0)));
        Assertions.assertEquals(BlockFace.DOWN, VectorMath.calculateFace(base, new Vector3(0, -1, 0)));
    }

    @Test
    void getPassByVector3Straight() {
        List<Vector3> result = VectorMath.getPassByVector3(new Vector3(0.5, 0.5, 0.5), new Vector3(3.5, 0.5, 0.5));
        Assertions.assertNotNull(result);
        for (Vector3 v : result) {
            Assertions.assertEquals(0, v.y, 1e-6);
            Assertions.assertEquals(0, v.z, 1e-6);
        }

        List<Vector3> diagonal = VectorMath.getPassByVector3(new Vector3(0, 0, 0), new Vector3(3, 3, 3));
        Assertions.assertFalse(diagonal.isEmpty());
        for (Vector3 v : diagonal) {
            Assertions.assertEquals(v.x, Math.floor(v.x), 1e-6);
            Assertions.assertEquals(v.y, Math.floor(v.y), 1e-6);
            Assertions.assertEquals(v.z, Math.floor(v.z), 1e-6);
        }
    }

    @Test
    void getPassByVector3SameThrows() {
        Vector3 v = new Vector3(1, 1, 1);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> VectorMath.getPassByVector3(v, new Vector3(1, 1, 1)));
    }
}
