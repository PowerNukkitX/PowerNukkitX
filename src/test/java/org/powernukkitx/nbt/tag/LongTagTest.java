package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongTagTest {
    @Test
    void construction() {
        LongTag tag = new LongTag(9000000000L);
        Assertions.assertEquals(9000000000L, tag.data);
        Assertions.assertEquals(Long.valueOf(9000000000L), tag.getData());
        Assertions.assertEquals(Long.valueOf(9000000000L), tag.parseValue());
    }

    @Test
    void setData() {
        LongTag tag = new LongTag();
        tag.setData(55L);
        Assertions.assertEquals(55L, tag.data);
        tag.setData(null);
        Assertions.assertEquals(0L, tag.data);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Long, new LongTag().getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("42L", new LongTag(42).toSNBT());
        Assertions.assertEquals("42L", new LongTag(42).toSNBT(1));
    }

    @Test
    void equalsAndCopy() {
        LongTag a = new LongTag(7);
        Assertions.assertEquals(a, new LongTag(7));
        Assertions.assertNotEquals(a, new LongTag(8));
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
    }
}
