package org.powernukkitx.utils.collection.nb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RangeUtilTest {

    @Test
    void returnsValueWhenNonNegative() {
        assertEquals(0, RangeUtil.checkPositiveOrZero(0, "n"));
        assertEquals(42, RangeUtil.checkPositiveOrZero(42, "n"));
    }

    @Test
    void throwsOnNegativeWithNameInMessage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> RangeUtil.checkPositiveOrZero(-1, "count"));
        assertEquals("count: -1 (expected: >= 0)", ex.getMessage());
    }
}
