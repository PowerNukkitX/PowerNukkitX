package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedCompoundTagTest {
    @Test
    void defaultConstruction() {
        LinkedCompoundTag c = new LinkedCompoundTag();
        Assertions.assertTrue(c.isEmpty());
        Assertions.assertEquals(Tag.TAG_Compound, c.getId());
    }

    @Test
    void mapConstruction() {
        Map<String, Tag> map = new LinkedHashMap<>();
        map.put("x", new IntTag(1));
        LinkedCompoundTag c = new LinkedCompoundTag(map);
        Assertions.assertEquals(1, c.getInt("x"));
    }

    @Test
    void insertionOrderPreserved() {
        LinkedCompoundTag c = new LinkedCompoundTag();
        c.putInt("z", 1).putInt("a", 2).putInt("m", 3);
        Assertions.assertArrayEquals(new String[]{"z", "a", "m"}, c.getTags().keySet().toArray());
    }

    @Test
    void copyReturnsLinked() {
        LinkedCompoundTag c = new LinkedCompoundTag();
        c.putInt("x", 1);
        LinkedCompoundTag copy = c.copy();
        Assertions.assertInstanceOf(LinkedCompoundTag.class, copy);
        Assertions.assertEquals(1, copy.getInt("x"));
    }

    @Test
    void parseValue() {
        LinkedCompoundTag c = new LinkedCompoundTag();
        c.putInt("x", 5);
        Assertions.assertEquals(5, c.parseValue().get("x"));
    }
}
