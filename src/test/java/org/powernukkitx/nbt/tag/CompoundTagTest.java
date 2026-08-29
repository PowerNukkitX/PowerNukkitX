package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompoundTagTest {
    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_Compound, new CompoundTag().getId());
    }

    @Test
    void putAndGetNumbers() {
        CompoundTag c = new CompoundTag();
        c.putByte("b", 1).putShort("s", 2).putInt("i", 3)
                .putLong("l", 4L).putFloat("f", 5f).putDouble("d", 6d);
        Assertions.assertEquals((byte) 1, c.getByte("b"));
        Assertions.assertEquals((short) 2, c.getShort("s"));
        Assertions.assertEquals(3, c.getInt("i"));
        Assertions.assertEquals(4L, c.getLong("l"));
        Assertions.assertEquals(5f, c.getFloat("f"));
        Assertions.assertEquals(6d, c.getDouble("d"));
    }

    @Test
    void putBytePrimitiveOverload() {
        CompoundTag c = new CompoundTag();
        c.putByte("b", (byte) 9);
        Assertions.assertEquals((byte) 9, c.getByte("b"));
    }

    @Test
    void getDefaultsWhenMissing() {
        CompoundTag c = new CompoundTag();
        Assertions.assertEquals((byte) 0, c.getByte("x"));
        Assertions.assertEquals(0, c.getInt("x"));
        Assertions.assertEquals("", c.getString("x"));
        Assertions.assertEquals(42, c.getInt("x", 42));
    }

    @Test
    void putAndGetString() {
        CompoundTag c = new CompoundTag();
        c.putString("k", "v");
        Assertions.assertEquals("v", c.getString("k"));
    }

    @Test
    void getStringOfNumber() {
        CompoundTag c = new CompoundTag();
        c.putInt("n", 5);
        Assertions.assertEquals("5", c.getString("n"));
    }

    @Test
    void putAndGetBoolean() {
        CompoundTag c = new CompoundTag();
        c.putBoolean("t", true).putBoolean("f", false);
        Assertions.assertTrue(c.getBoolean("t"));
        Assertions.assertFalse(c.getBoolean("f"));
        Assertions.assertFalse(c.getBoolean("missing"));
    }

    @Test
    void putAndGetArrays() {
        CompoundTag c = new CompoundTag();
        c.putByteArray("ba", new byte[]{1, 2});
        c.putIntArray("ia", new int[]{3, 4});
        Assertions.assertArrayEquals(new byte[]{1, 2}, c.getByteArray("ba"));
        Assertions.assertArrayEquals(new int[]{3, 4}, c.getIntArray("ia"));
        Assertions.assertEquals(0, c.getByteArray("x").length);
        Assertions.assertEquals(0, c.getIntArray("x").length);
    }

    @Test
    void putAndGetCompound() {
        CompoundTag inner = new CompoundTag().putInt("v", 1);
        CompoundTag c = new CompoundTag().putCompound("c", inner);
        Assertions.assertEquals(1, c.getCompound("c").getInt("v"));
        Assertions.assertTrue(c.getCompound("missing").isEmpty());
    }

    @Test
    void putAndGetList() {
        ListTag<IntTag> list = new ListTag<>();
        list.add(new IntTag(1));
        CompoundTag c = new CompoundTag().putList("l", list);
        Assertions.assertEquals(1, c.getList("l").size());
        Assertions.assertEquals(0, c.getList("missing").size());
    }

    @Test
    void containsChecks() {
        CompoundTag c = new CompoundTag();
        c.putInt("i", 1).putString("s", "x").putByte("b", 1)
                .putShort("sh", 2).putDouble("d", 1d).putFloat("f", 1f)
                .putByteArray("ba", new byte[]{1}).putIntArray("ia", new int[]{1});
        c.putCompound("cp", new CompoundTag());
        c.putList("l", new ListTag<>(Tag.TAG_Int));
        Assertions.assertTrue(c.contains("i"));
        Assertions.assertTrue(c.exist("i"));
        Assertions.assertTrue(c.containsInt("i"));
        Assertions.assertTrue(c.containsString("s"));
        Assertions.assertTrue(c.containsByte("b"));
        Assertions.assertTrue(c.containsShort("sh"));
        Assertions.assertTrue(c.containsDouble("d"));
        Assertions.assertTrue(c.containsFloat("f"));
        Assertions.assertTrue(c.containsByteArray("ba"));
        Assertions.assertTrue(c.containsIntArray("ia"));
        Assertions.assertTrue(c.containsCompound("cp"));
        Assertions.assertTrue(c.containsList("l"));
        Assertions.assertTrue(c.containsList("l", Tag.TAG_Int));
        Assertions.assertTrue(c.containsNumber("i"));
        Assertions.assertFalse(c.containsInt("s"));
    }

    @Test
    void removeAndRemoveAndGet() {
        CompoundTag c = new CompoundTag().putInt("a", 1).putInt("b", 2);
        c.remove("a");
        Assertions.assertFalse(c.contains("a"));
        IntTag removed = c.removeAndGet("b");
        Assertions.assertEquals(2, removed.data);
        Assertions.assertFalse(c.contains("b"));
    }

    @Test
    void putAllAndGetTags() {
        CompoundTag src = new CompoundTag().putInt("x", 1).putInt("y", 2);
        CompoundTag dst = new CompoundTag();
        dst.putAll(src);
        Assertions.assertEquals(2, dst.getTags().size());
        Assertions.assertEquals(1, dst.getInt("x"));
    }

    @Test
    void isEmptyAndGetAllTags() {
        CompoundTag c = new CompoundTag();
        Assertions.assertTrue(c.isEmpty());
        c.putInt("x", 1);
        Assertions.assertFalse(c.isEmpty());
        Assertions.assertEquals(1, c.getAllTags().size());
    }

    @Test
    void parseValue() {
        CompoundTag c = new CompoundTag().putInt("x", 1).putString("y", "z");
        var parsed = c.parseValue();
        Assertions.assertEquals(1, parsed.get("x"));
        Assertions.assertEquals("z", parsed.get("y"));
    }

    @Test
    void getEntrySetUnmodifiable() {
        CompoundTag c = new CompoundTag().putInt("x", 1);
        Assertions.assertEquals(1, c.getEntrySet().size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> c.getEntrySet().clear());
    }

    @Test
    void copyIsDeepAndEqual() {
        CompoundTag c = new CompoundTag().putInt("x", 1);
        CompoundTag copy = c.copy();
        Assertions.assertEquals(c, copy);
        Assertions.assertNotSame(c, copy);
        copy.putInt("x", 99);
        Assertions.assertEquals(1, c.getInt("x"));
    }

    @Test
    void equalsAndHashCode() {
        CompoundTag a = new CompoundTag().putInt("x", 1);
        CompoundTag b = new CompoundTag().putInt("x", 1);
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, new CompoundTag().putInt("x", 2));
    }

    @Test
    void snbt() {
        CompoundTag c = new CompoundTag().putInt("x", 1);
        Assertions.assertEquals("{\"x\":1}", c.toSNBT());
        Assertions.assertTrue(c.toSNBT(2).contains("\"x\": 1"));
    }

    @Test
    void toStringContainsEntries() {
        CompoundTag c = new CompoundTag().putInt("x", 1);
        Assertions.assertTrue(c.toString().contains("1 entries"));
    }

    @Test
    void crossNumberTypeGet() {
        CompoundTag c = new CompoundTag().putDouble("d", 3.9d);
        Assertions.assertEquals(3, c.getInt("d"));
        Assertions.assertEquals(3L, c.getLong("d"));
    }
}
