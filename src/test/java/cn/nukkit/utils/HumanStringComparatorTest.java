package cn.nukkit.utils;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-09-25
 */
class HumanStringComparatorTest {
    private final HumanStringComparator comparator = HumanStringComparator.getInstance();

    @SuppressWarnings("EqualsWithItself")
    @Test
    void pillars() {
        assertEquals(-1, comparator.compare("minecraft:basalt;pillar_axis=x", "minecraft:basalt;pillar_axis=y"));
        assertEquals(0, comparator.compare("minecraft:basalt;pillar_axis=y", "minecraft:basalt;pillar_axis=y"));
        assertEquals(1, comparator.compare("minecraft:basalt;pillar_axis=z", "minecraft:basalt;pillar_axis=y"));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    void compare() {
        assertNegative(comparator.compare("-9", "9"));
        assertNegative(comparator.compare("32", "325_0"));
        assertNegative(comparator.compare("33", "325_0"));
        assertNegative(comparator.compare("325", "325_0"));
        assertZero(comparator.compare("325_0", "325_0"));
        assertPositive(comparator.compare("326", "325_0"));
    }

    @Test
    void coralFan() {
        assertNegative(comparator.compare(
                "minecraft:coral_fan_hang;coral_hang_type_bit=0;coral_direction=0;dead_bit=0",
                "minecraft:coral_fan_hang3;coral_hang_type_bit=1;coral_direction=3;dead_bit=1"
        ));
        assertNegative(comparator.compare(
                "minecraft:coral_fan_hang2;coral_hang_type_bit=0;coral_direction=0;dead_bit=0",
                "minecraft:coral_fan_hang3;coral_hang_type_bit=1;coral_direction=3;dead_bit=1"
        ));
    }

    private void assertNegative(int actual) {
        if (actual >= 0) {
            throw new AssertionFailedError("Expected a negative value, got " + actual, -1, actual);
        }
    }

    private void assertPositive(int actual) {
        if (actual <= 0) {
            throw new AssertionFailedError("Expected a positive value, got " + actual, 1, actual);
        }
    }

    private void assertZero(int actual) {
        assertEquals(0 , actual);
    }
}
