package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntArrayTagTest {
    @Test
    void defaultEmpty() {
        Assertions.assertEquals(0, new IntArrayTag().getData().length);
    }

    @Test
    void construction() {
        int[] arr = {10, 20, 30};
        IntArrayTag tag = new IntArrayTag(arr);
        Assertions.assertArrayEquals(arr, tag.getData());
        Assertions.assertArrayEquals(arr, tag.parseValue());
    }

    @Test
    void setData() {
        IntArrayTag tag = new IntArrayTag();
        tag.setData(new int[]{1});
        Assertions.assertArrayEquals(new int[]{1}, tag.data);
        tag.setData(null);
        Assertions.assertEquals(0, tag.data.length);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Int_Array, new IntArrayTag().getId());
    }

    @Test
    void snbt() {
        Assertions.assertEquals("[I;1, 2]", new IntArrayTag(new int[]{1, 2}).toSNBT());
        Assertions.assertEquals("[I;1, 2]", new IntArrayTag(new int[]{1, 2}).toSNBT(2));
    }

    @Test
    void equalsAndHashCode() {
        IntArrayTag a = new IntArrayTag(new int[]{1, 2});
        IntArrayTag b = new IntArrayTag(new int[]{1, 2});
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, new IntArrayTag(new int[]{1, 3}));
    }

    @Test
    void copyIsDeep() {
        int[] arr = {5, 6};
        IntArrayTag a = new IntArrayTag(arr);
        Tag copy = a.copy();
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(arr, ((IntArrayTag) copy).data);
    }
}
