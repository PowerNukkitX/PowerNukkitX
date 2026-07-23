package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntTagTest {
    @Test
    void construction() {
        IntTag tag = new IntTag(1234);
        Assertions.assertEquals(1234, tag.data);
        Assertions.assertEquals(Integer.valueOf(1234), tag.getData());
        Assertions.assertEquals(Integer.valueOf(1234), tag.parseValue());
    }

    @Test
    void setData() {
        IntTag tag = new IntTag();
        tag.setData(77);
        Assertions.assertEquals(77, tag.data);
        tag.setData(null);
        Assertions.assertEquals(0, tag.data);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Int, new IntTag().getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("42", new IntTag(42).toSNBT());
        Assertions.assertEquals("42", new IntTag(42).toSNBT(3));
    }

    @Test
    void equalsAndCopy() {
        IntTag a = new IntTag(9);
        Assertions.assertEquals(a, new IntTag(9));
        Assertions.assertNotEquals(a, new IntTag(10));
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
    }

    @Test
    void hashCodeConsistent() {
        Assertions.assertEquals(new IntTag(5).hashCode(), new IntTag(5).hashCode());
    }
}
