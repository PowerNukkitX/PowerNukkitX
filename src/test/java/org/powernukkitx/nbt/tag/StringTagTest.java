package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringTagTest {
    @Test
    void construction() {
        StringTag tag = new StringTag("hello");
        Assertions.assertEquals("hello", tag.data);
        Assertions.assertEquals("hello", tag.parseValue());
    }

    @Test
    void nullThrows() {
        Assertions.assertThrows(NullPointerException.class, () -> new StringTag(null));
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_String, new StringTag("x").getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("\"abc\"", new StringTag("abc").toSNBT());
        Assertions.assertEquals("\"abc\"", new StringTag("abc").toSNBT(4));
    }

    @Test
    void toStringContainsData() {
        Assertions.assertTrue(new StringTag("yo").toString().contains("yo"));
    }

    @Test
    void equalsAndCopy() {
        StringTag a = new StringTag("k");
        Assertions.assertEquals(a, new StringTag("k"));
        Assertions.assertNotEquals(a, new StringTag("j"));
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
    }

    @Test
    void hashCodeConsistent() {
        Assertions.assertEquals(new StringTag("q").hashCode(), new StringTag("q").hashCode());
    }
}
