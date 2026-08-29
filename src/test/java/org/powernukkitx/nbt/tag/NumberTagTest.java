package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberTagTest {
    @Test
    void getDataViaConcretes() {
        NumberTag<Integer> i = new IntTag(5);
        Assertions.assertEquals(Integer.valueOf(5), i.getData());
        NumberTag<Long> l = new LongTag(6L);
        Assertions.assertEquals(Long.valueOf(6L), l.getData());
    }

    @Test
    void numberConversions() {
        NumberTag<Double> d = new DoubleTag(9.7d);
        Assertions.assertEquals(9, d.getData().intValue());
        Assertions.assertEquals(9L, d.getData().longValue());
        Assertions.assertEquals(9.7f, d.getData().floatValue());
        Assertions.assertEquals((byte) 9, d.getData().byteValue());
    }

    @Test
    void hashCodeIncludesData() {
        Assertions.assertNotEquals(new IntTag(1).hashCode(), new IntTag(2).hashCode());
        Assertions.assertEquals(new IntTag(3).hashCode(), new IntTag(3).hashCode());
    }

    @Test
    void setDataViaBase() {
        NumberTag<Integer> i = new IntTag();
        i.setData(11);
        Assertions.assertEquals(Integer.valueOf(11), i.getData());
    }
}
