package cn.nukkit.level;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.LevelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-12-15
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class LocationTest {
    public static final double X = 2;
    public static final double Y = 64;
    public static final double Z = 3;
    public static final float PITCH = 30f;
    public static final float YAW = 40f;
    public static final float HEAD_YAW = 50f;

    @MockLevel
    Level level;

    @BeforeEach
    void setUp() {
        level.setBlock(new Vector3(X, Y, Z), Block.get(BlockID.STONE), true, false);
    }

    @Test
    void constructorLevel() {
        Location location = new Location(X, Y, Z, level);
        assertEquals(X, location.x);
        assertEquals(Y, location.y);
        assertEquals(Z, location.z);
        assertSame(level, location.level);
        assertEquals(0, location.yaw);
        assertEquals(0, location.pitch);
        assertEquals(0, location.headYaw);
    }

    @Test
    void constructorYawPitchLevel() {
        Location location = new Location(X, Y, Z, YAW, PITCH, level);
        assertEquals(X, location.x);
        assertEquals(Y, location.y);
        assertEquals(Z, location.z);
        assertSame(level, location.level);
        assertEquals(YAW, location.yaw);
        assertEquals(PITCH, location.pitch);
        assertEquals(0, location.headYaw);
    }

    @Test
    void constructorYawPitchHeadYaw() {
        Location location = new Location(X, Y, Z, YAW, PITCH, HEAD_YAW);
        assertEquals(X, location.x);
        assertEquals(Y, location.y);
        assertEquals(Z, location.z);
        assertNull(location.level);
        assertEquals(YAW, location.yaw);
        assertEquals(PITCH, location.pitch);
        assertEquals(HEAD_YAW, location.headYaw);
    }

    @Test
    void constructorYawPitchHeadYawLevel() {
        Location location = new Location(X, Y, Z, YAW, PITCH, HEAD_YAW, level);
        assertEquals(X, location.x);
        assertEquals(Y, location.y);
        assertEquals(Z, location.z);
        assertSame(level, location.level);
        assertEquals(YAW, location.yaw);
        assertEquals(PITCH, location.pitch);
        assertEquals(HEAD_YAW, location.headYaw);
    }

    @Test
    void fromObjectWithEverything() {
        Location location = Location.fromObject(new Vector3(X, Y, Z), level, YAW, PITCH, HEAD_YAW);
        assertEquals(X, location.x);
        assertEquals(Y, location.y);
        assertEquals(Z, location.z);
        assertSame(level, location.level);
        assertEquals(YAW, location.yaw);
        assertEquals(PITCH, location.pitch);
        assertEquals(HEAD_YAW, location.headYaw);
    }

    @Test
    void fromObjectWithoutLevel() {
        Location location = Location.fromObject(new Vector3(X, Y, Z), null, YAW, PITCH, HEAD_YAW);
        assertEquals(X, location.x);
        assertEquals(Y, location.y);
        assertEquals(Z, location.z);
        assertNull(location.getLevel());
        assertEquals(YAW, location.yaw);
        assertEquals(PITCH, location.pitch);
        assertEquals(HEAD_YAW, location.headYaw);
        assertEquals("Location (level=null, x=" + X + ", y=" + Y + ", z=" + Z + ", yaw=" + YAW + ", pitch=" + PITCH + ", headYaw=" + HEAD_YAW + ")", location.toString());
    }

    @Test
    void fromObjectWithLevelFromPosition() {
        Location location = Location.fromObject(new Position(X, Y, Z, level), null, YAW, PITCH, HEAD_YAW);
        assertEquals(X, location.x);
        assertEquals(Y, location.y);
        assertEquals(Z, location.z);
        assertSame(level, location.getLevel());
        assertEquals(YAW, location.yaw);
        assertEquals(PITCH, location.pitch);
        assertEquals(HEAD_YAW, location.headYaw);
        assertEquals(HEAD_YAW, location.getHeadYaw());
        assertEquals("Location (level=" + level.getName() + ", x=" + X + ", y=" + Y + ", z=" + Z + ", yaw=" + YAW + ", pitch=" + PITCH + ", headYaw=" + HEAD_YAW + ")", location.toString());
    }

    @Test
    void getLocationValid() {
        Location location = new Location(X, Y, Z, YAW, PITCH, HEAD_YAW, level);
        Location other = location.getLocation();
        assertNotSame(location, other);
        assertEquals(location, other);
    }

    @Test
    void getLocationNotValid() {
        Location location = new Location(X, Y, Z, YAW, PITCH, HEAD_YAW, null);
        assertThrows(LevelException.class, location::getLocation);
    }

    @Test
    void add() {
        Location location = new Location(X, Y, Z, YAW, PITCH, HEAD_YAW, level);
        assertEquals(new Location(X + 1, Y, Z, YAW, PITCH, HEAD_YAW, level), location.add(1));
        assertEquals(new Location(X + 1, Y + 2, Z, YAW, PITCH, HEAD_YAW, level), location.add(1, 2));
        assertEquals(new Location(X + 1, Y + 2, Z + 3, YAW, PITCH, HEAD_YAW, level), location.add(1, 2, 3));
        assertEquals(new Location(X + 1, Y + 2, Z + 3, YAW, PITCH, HEAD_YAW, level), location.add(new Vector3(1, 2, 3)));
    }

    @Test
    void subtract() {
        Location location = new Location(X, Y, Z, YAW, PITCH, HEAD_YAW, level);
        assertEquals(new Location(X - 1, Y, Z, YAW, PITCH, HEAD_YAW, level), location.subtract(1));
        assertEquals(new Location(X - 1, Y - 2, Z, YAW, PITCH, HEAD_YAW, level), location.subtract(1, 2));
        assertEquals(new Location(X - 1, Y - 2, Z - 3, YAW, PITCH, HEAD_YAW, level), location.subtract(1, 2, 3));
        assertEquals(new Location(X - 1, Y - 2, Z - 3, YAW, PITCH, HEAD_YAW, level), location.subtract(new Vector3(1, 2, 3)));
    }

    @Test
    void multiply() {
        Location location = new Location(X, Y, Z, YAW, PITCH, HEAD_YAW, level);
        assertEquals(new Location(X * 4, Y * 4, Z * 4, YAW, PITCH, HEAD_YAW, level), location.multiply(4));
    }

    @Test
    void divide() {
        Location location = new Location(X, Y, Z, YAW, PITCH, HEAD_YAW, level);
        assertEquals(new Location(X / 2, Y / 2, Z / 2, YAW, PITCH, HEAD_YAW, level), location.divide(2));
    }

    @Test
    void ceil() {
        Location location = new Location(X + 0.8, Y + 0.8, Z + 0.8, YAW + 0.8, PITCH + 0.8, HEAD_YAW + 0.8, level);
        assertEquals(new Location(X + 1, Y + 1, Z + 1, YAW + 0.8, PITCH + 0.8, HEAD_YAW + 0.8, level), location.ceil());
    }

    @Test
    void floor() {
        Location location = new Location(X + 0.8, Y + 0.8, Z + 0.8, YAW + 0.8, PITCH + 0.8, HEAD_YAW + 0.8, level);
        assertEquals(new Location(X, Y, Z, YAW + 0.8, PITCH + 0.8, HEAD_YAW + 0.8, level), location.floor());
    }

    @Test
    void round() {
        Location location = new Location(X + 0.8, Y + 0.8, Z + 0.8, YAW + 0.8, PITCH + 0.8, HEAD_YAW + 0.8, level);
        assertEquals(new Location(X + 1, Y + 1, Z + 1, YAW + 0.8, PITCH + 0.8, HEAD_YAW + 0.8, level), location.round());
    }

    @Test
    void abs() {
        Location location = new Location(X + 0.8, Y + 0.8, Z + 0.8, YAW + 0.8, PITCH + 0.8, HEAD_YAW + 0.8, level);
        assertEquals(new Location(X, Y, Z, YAW + 0.8, PITCH + 0.8, HEAD_YAW + 0.8, level), location.abs());
    }
}
