package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShortTagTest {
    @Test
    void construction() {
        ShortTag tag = new ShortTag(300);
        Assertions.assertEquals((short) 300, tag.data);
        Assertions.assertEquals(Short.valueOf((short) 300), tag.getData());
        Assertions.assertEquals(Short.valueOf((short) 300), tag.parseValue());
    }

    @Test
    void setData() {
        ShortTag tag = new ShortTag();
        tag.setData((short) 42);
        Assertions.assertEquals((short) 42, tag.data);
        tag.setData(null);
        Assertions.assertEquals((short) 0, tag.data);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Short, new ShortTag().getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("9s", new ShortTag(9).toSNBT());
        Assertions.assertEquals("9s", new ShortTag(9).toSNBT(2));
    }

    @Test
    void toStringContainsData() {
        Assertions.assertTrue(new ShortTag(11).toString().contains("11"));
    }

    @Test
    void equalsAndCopy() {
        ShortTag a = new ShortTag(5);
        Assertions.assertEquals(a, new ShortTag(5));
        Assertions.assertNotEquals(a, new ShortTag(6));
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
    }
}
