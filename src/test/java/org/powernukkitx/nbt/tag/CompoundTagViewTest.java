package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompoundTagViewTest {
    private CompoundTag backing() {
        return new CompoundTag()
                .putInt("i", 1)
                .putString("s", "v")
                .putByteArray("ba", new byte[]{1})
                .putIntArray("ia", new int[]{2})
                .putCompound("cp", new CompoundTag().putInt("x", 9))
                .putList("l", new ListTag<>(Tag.TAG_Int));
    }

    @Test
    void readsDelegate() {
        CompoundTagView v = new CompoundTagView(backing());
        Assertions.assertEquals(1, v.getInt("i"));
        Assertions.assertEquals("v", v.getString("s"));
        Assertions.assertTrue(v.contains("i"));
        Assertions.assertTrue(v.exist("s"));
        Assertions.assertEquals(9, v.getCompound("cp").getInt("x"));
        Assertions.assertEquals(Tag.TAG_Compound, v.getId());
        Assertions.assertFalse(v.isEmpty());
    }

    @Test
    void containsDelegated() {
        CompoundTagView v = new CompoundTagView(backing());
        Assertions.assertTrue(v.containsInt("i"));
        Assertions.assertTrue(v.containsString("s"));
        Assertions.assertTrue(v.containsByteArray("ba"));
        Assertions.assertTrue(v.containsIntArray("ia"));
        Assertions.assertTrue(v.containsCompound("cp"));
        Assertions.assertTrue(v.containsList("l"));
        Assertions.assertTrue(v.containsNumber("i"));
    }

    @Test
    void mutationsThrow() {
        CompoundTagView v = new CompoundTagView(backing());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> v.putInt("z", 1));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> v.put("z", new IntTag(1)));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> v.putString("z", "q"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> v.remove("i"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> v.removeAndGet("i"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> v.putBoolean("z", true));
    }

    @Test
    void tagsUnmodifiable() {
        CompoundTagView v = new CompoundTagView(backing());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> v.getTags().clear());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> v.getAllTags().clear());
    }

    @Test
    void copyReturnsLinked() {
        CompoundTagView v = new CompoundTagView(backing());
        LinkedCompoundTag copy = v.copy();
        Assertions.assertEquals(1, copy.getInt("i"));
    }

    @Test
    void equalsAndHashCodeDelegate() {
        CompoundTag b = backing();
        CompoundTagView v = new CompoundTagView(b);
        Assertions.assertEquals(b.hashCode(), v.hashCode());
        Assertions.assertTrue(v.equals(b));
    }

    @Test
    void snbtDelegated() {
        CompoundTag b = new CompoundTag().putInt("i", 1);
        CompoundTagView v = new CompoundTagView(b);
        Assertions.assertEquals(b.toSNBT(), v.toSNBT());
        Assertions.assertEquals(b.toString(), v.toString());
    }
}
