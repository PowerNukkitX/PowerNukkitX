package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DoubleTagTest {
    @Test
    void construction() {
        DoubleTag tag = new DoubleTag(3.14d);
        Assertions.assertEquals(3.14d, tag.data);
        Assertions.assertEquals(Double.valueOf(3.14d), tag.getData());
        Assertions.assertEquals(Double.valueOf(3.14d), tag.parseValue());
    }

    @Test
    void setData() {
        DoubleTag tag = new DoubleTag(0d);
        tag.setData(6.28d);
        Assertions.assertEquals(6.28d, tag.data);
        tag.setData(null);
        Assertions.assertEquals(0d, tag.data);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Double, new DoubleTag(0d).getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("2.5d", new DoubleTag(2.5d).toSNBT());
        Assertions.assertEquals("2.5d", new DoubleTag(2.5d).toSNBT(2));
    }

    @Test
    void equalsAndCopy() {
        DoubleTag a = new DoubleTag(1.0d);
        Assertions.assertEquals(a, new DoubleTag(1.0d));
        Assertions.assertNotEquals(a, new DoubleTag(2.0d));
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
    }
}
