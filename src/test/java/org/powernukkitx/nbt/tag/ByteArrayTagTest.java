package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteArrayTagTest {
    @Test
    void construction() {
        byte[] arr = {1, 2, 3};
        ByteArrayTag tag = new ByteArrayTag(arr);
        Assertions.assertArrayEquals(arr, tag.getData());
        Assertions.assertArrayEquals(arr, tag.parseValue());
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Byte_Array, new ByteArrayTag(new byte[0]).getId());
    }

    @Test
    void snbt() {
        ByteArrayTag tag = new ByteArrayTag(new byte[]{1, 2});
        Assertions.assertEquals("[B;1b,2b]", tag.toSNBT());
        Assertions.assertEquals("[B; 1b, 2b]", tag.toSNBT(2));
    }

    @Test
    void equalsAndHashCode() {
        ByteArrayTag a = new ByteArrayTag(new byte[]{4, 5});
        ByteArrayTag b = new ByteArrayTag(new byte[]{4, 5});
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, new ByteArrayTag(new byte[]{4, 6}));
    }

    @Test
    void copyIsDeep() {
        byte[] arr = {7, 8};
        ByteArrayTag a = new ByteArrayTag(arr);
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
        Assertions.assertNotSame(arr, ((ByteArrayTag) copy).data);
    }
}
