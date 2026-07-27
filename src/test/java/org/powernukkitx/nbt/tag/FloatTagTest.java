package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FloatTagTest {
    @Test
    void constructionFromFloat() {
        FloatTag tag = new FloatTag(1.5f);
        Assertions.assertEquals(1.5f, tag.data);
        Assertions.assertEquals(Float.valueOf(1.5f), tag.getData());
        Assertions.assertEquals(Float.valueOf(1.5f), tag.parseValue());
    }

    @Test
    void constructionFromDouble() {
        FloatTag tag = new FloatTag(2.5d);
        Assertions.assertEquals(2.5f, tag.data);
    }

    @Test
    void setData() {
        FloatTag tag = new FloatTag(0f);
        tag.setData(3.25f);
        Assertions.assertEquals(3.25f, tag.data);
        tag.setData(null);
        Assertions.assertEquals(0f, tag.data);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Float, new FloatTag(0f).getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("1.5f", new FloatTag(1.5f).toSNBT());
        Assertions.assertEquals("1.5f", new FloatTag(1.5f).toSNBT(2));
    }

    @Test
    void equalsAndCopy() {
        FloatTag a = new FloatTag(4.0f);
        Assertions.assertEquals(a, new FloatTag(4.0f));
        Assertions.assertNotEquals(a, new FloatTag(5.0f));
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
    }
}
