package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TreeMapCompoundTagTest {
    @Test
    void defaultConstruction() {
        TreeMapCompoundTag c = new TreeMapCompoundTag();
        Assertions.assertTrue(c.isEmpty());
        Assertions.assertEquals(Tag.TAG_Compound, c.getId());
    }

    @Test
    void mapConstruction() {
        Map<String, Tag> map = new HashMap<>();
        map.put("x", new IntTag(1));
        TreeMapCompoundTag c = new TreeMapCompoundTag(map);
        Assertions.assertEquals(1, c.getInt("x"));
    }

    @Test
    void keysSorted() {
        TreeMapCompoundTag c = new TreeMapCompoundTag();
        c.putInt("z", 1).putInt("a", 2).putInt("m", 3);
        Assertions.assertArrayEquals(new String[]{"a", "m", "z"}, c.getTags().keySet().toArray());
    }

    @Test
    void copyReturnsTreeMap() {
        TreeMapCompoundTag c = new TreeMapCompoundTag();
        c.putInt("x", 1);
        TreeMapCompoundTag copy = c.copy();
        Assertions.assertInstanceOf(TreeMapCompoundTag.class, copy);
        Assertions.assertEquals(1, copy.getInt("x"));
    }

    @Test
    void parseValueSorted() {
        TreeMapCompoundTag c = new TreeMapCompoundTag();
        c.putInt("b", 2).putInt("a", 1);
        Assertions.assertArrayEquals(new String[]{"a", "b"}, c.parseValue().keySet().toArray());
    }
}
