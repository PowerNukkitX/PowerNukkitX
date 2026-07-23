package org.powernukkitx.utils;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonaPieceTintTest {

    @Test
    void colorsAreCopiedIntoImmutableList() {
        PersonaPieceTint tint = new PersonaPieceTint("eyes", Arrays.asList("#fff", "#000"));
        assertEquals("eyes", tint.pieceType);
        assertEquals(ImmutableList.of("#fff", "#000"), tint.colors);
    }

    @Test
    void colorsListIsImmutable() {
        PersonaPieceTint tint = new PersonaPieceTint("eyes", Arrays.asList("#fff"));
        assertThrows(UnsupportedOperationException.class, () -> tint.colors.add("#111"));
    }

    @Test
    void equalsAndHashCode() {
        PersonaPieceTint a = new PersonaPieceTint("eyes", Arrays.asList("#fff"));
        PersonaPieceTint b = new PersonaPieceTint("eyes", Arrays.asList("#fff"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        PersonaPieceTint c = new PersonaPieceTint("hair", Arrays.asList("#fff"));
        assertNotEquals(a, c);
    }
}
