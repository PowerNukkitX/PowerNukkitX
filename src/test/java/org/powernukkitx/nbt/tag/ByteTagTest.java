package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteTagTest {
    @Test
    void construction() {
        ByteTag tag = new ByteTag((byte) 5);
        Assertions.assertEquals((byte) 5, tag.data);
        Assertions.assertEquals(Byte.valueOf((byte) 5), tag.getData());
        Assertions.assertEquals(Byte.valueOf((byte) 5), tag.parseValue());
    }

    @Test
    void defaultConstruction() {
        Assertions.assertEquals((byte) 0, new ByteTag().data);
    }

    @Test
    void setData() {
        ByteTag tag = new ByteTag();
        tag.setData((byte) 12);
        Assertions.assertEquals((byte) 12, tag.data);
        tag.setData(null);
        Assertions.assertEquals((byte) 0, tag.data);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Byte, new ByteTag().getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("7b", new ByteTag((byte) 7).toSNBT());
        Assertions.assertEquals("7b", new ByteTag((byte) 7).toSNBT(2));
    }

    @Test
    void toStringHex() {
        Assertions.assertTrue(new ByteTag((byte) 1).toString().contains("0x01"));
        Assertions.assertTrue(new ByteTag((byte) 0x1f).toString().contains("0x1f"));
    }

    @Test
    void equalsAndCopy() {
        ByteTag a = new ByteTag((byte) 3);
        Assertions.assertEquals(a, new ByteTag((byte) 3));
        Assertions.assertNotEquals(a, new ByteTag((byte) 4));
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
    }

    @Test
    void hashCodeConsistent() {
        Assertions.assertEquals(new ByteTag((byte) 9).hashCode(), new ByteTag((byte) 9).hashCode());
    }
}
