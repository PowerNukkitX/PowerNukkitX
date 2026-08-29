package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongArrayTagTest {
    @Test
    void defaultEmpty() {
        Assertions.assertEquals(0, new LongArrayTag().getData().length);
    }

    @Test
    void construction() {
        long[] arr = {10L, 20L};
        LongArrayTag tag = new LongArrayTag(arr);
        Assertions.assertArrayEquals(arr, tag.getData());
        Assertions.assertArrayEquals(arr, tag.parseValue());
    }

    @Test
    void setData() {
        LongArrayTag tag = new LongArrayTag();
        tag.setData(new long[]{9L});
        Assertions.assertArrayEquals(new long[]{9L}, tag.data);
        tag.setData(null);
        Assertions.assertEquals(0, tag.data.length);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Long_Array, new LongArrayTag().getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("[I;1, 2]", new LongArrayTag(new long[]{1L, 2L}).toSNBT());
        Assertions.assertEquals("[I;1, 2]", new LongArrayTag(new long[]{1L, 2L}).toSNBT(2));
    }

    @Test
    void equalsAndHashCode() {
        LongArrayTag a = new LongArrayTag(new long[]{1L, 2L});
        LongArrayTag b = new LongArrayTag(new long[]{1L, 2L});
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, new LongArrayTag(new long[]{1L, 3L}));
    }

    @Test
    void copyIsDeep() {
        long[] arr = {5L, 6L};
        LongArrayTag a = new LongArrayTag(arr);
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(arr, ((LongArrayTag) copy).data);
    }
}
