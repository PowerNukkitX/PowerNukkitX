package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberConversionsTest {
    @Test
    void floorHandlesNegatives() {
        Assertions.assertEquals(1, NumberConversions.floor(1.9));
        Assertions.assertEquals(-2, NumberConversions.floor(-1.1));
        Assertions.assertEquals(5, NumberConversions.floor(5.0));
    }

    @Test
    void ceilHandlesNegatives() {
        Assertions.assertEquals(2, NumberConversions.ceil(1.1));
        Assertions.assertEquals(-1, NumberConversions.ceil(-1.9));
        Assertions.assertEquals(5, NumberConversions.ceil(5.0));
    }

    @Test
    void round() {
        Assertions.assertEquals(2, NumberConversions.round(1.5));
        Assertions.assertEquals(1, NumberConversions.round(1.4));
        Assertions.assertEquals(-1, NumberConversions.round(-1.4));
    }

    @Test
    void square() {
        Assertions.assertEquals(9.0, NumberConversions.square(3.0));
        Assertions.assertEquals(6.25, NumberConversions.square(2.5));
    }

    @Test
    void toIntFromNumberAndString() {
        Assertions.assertEquals(7, NumberConversions.toInt(7L));
        Assertions.assertEquals(42, NumberConversions.toInt("42"));
        Assertions.assertEquals(0, NumberConversions.toInt("nope"));
        Assertions.assertEquals(0, NumberConversions.toInt(null));
    }

    @Test
    void toFloatToDoubleToLongToShortToByte() {
        Assertions.assertEquals(1.5f, NumberConversions.toFloat("1.5"));
        Assertions.assertEquals(2.5d, NumberConversions.toDouble(2.5));
        Assertions.assertEquals(9L, NumberConversions.toLong("9"));
        Assertions.assertEquals((short) 3, NumberConversions.toShort("3"));
        Assertions.assertEquals((byte) 4, NumberConversions.toByte("4"));
    }

    @Test
    void badStringsReturnZero() {
        Assertions.assertEquals(0f, NumberConversions.toFloat("x"));
        Assertions.assertEquals(0d, NumberConversions.toDouble("x"));
        Assertions.assertEquals(0L, NumberConversions.toLong("x"));
        Assertions.assertEquals((short) 0, NumberConversions.toShort("x"));
        Assertions.assertEquals((byte) 0, NumberConversions.toByte("x"));
    }
}
