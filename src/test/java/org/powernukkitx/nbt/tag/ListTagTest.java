package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ListTagTest {
    @Test
    void addAndGetAndSize() {
        ListTag<IntTag> list = new ListTag<>();
        list.add(new IntTag(1)).add(new IntTag(2));
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(1, list.get(0).data);
        Assertions.assertEquals(Tag.TAG_Int, list.type);
    }

    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_List, new ListTag<>().getId());
    }

    @Test
    void constructWithType() {
        ListTag<IntTag> list = new ListTag<>(Tag.TAG_Int);
        Assertions.assertEquals(Tag.TAG_Int, list.type);
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void constructFromCollection() {
        ListTag<IntTag> list = new ListTag<>(List.of(new IntTag(5), new IntTag(6)));
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(Tag.TAG_Int, list.type);
    }

    @Test
    void constructFromEmptyCollectionThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ListTag<>(List.of()));
    }

    @Test
    void addAll() {
        ListTag<IntTag> list = new ListTag<>();
        list.addAll(List.of(new IntTag(1), new IntTag(2), new IntTag(3)));
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void addAtIndexAppendAndReplace() {
        ListTag<IntTag> list = new ListTag<>();
        list.add(new IntTag(1));
        list.add(1, new IntTag(2));
        Assertions.assertEquals(2, list.size());
        list.add(0, new IntTag(9));
        Assertions.assertEquals(9, list.get(0).data);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void removeByTagAndIndex() {
        IntTag a = new IntTag(1);
        IntTag b = new IntTag(2);
        ListTag<IntTag> list = new ListTag<>();
        list.add(a).add(b);
        list.remove(a);
        Assertions.assertEquals(1, list.size());
        list.remove(0);
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void removeAll() {
        IntTag a = new IntTag(1);
        IntTag b = new IntTag(2);
        ListTag<IntTag> list = new ListTag<>();
        list.add(a).add(b);
        list.removeAll(List.of(a, b));
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void getAllAndSetAll() {
        ListTag<IntTag> list = new ListTag<>();
        list.setAll(List.of(new IntTag(7), new IntTag(8)));
        List<IntTag> all = list.getAll();
        Assertions.assertEquals(2, all.size());
        Assertions.assertEquals(7, all.get(0).data);
    }

    @Test
    void parseValue() {
        ListTag<IntTag> list = new ListTag<>();
        list.add(new IntTag(3)).add(new IntTag(4));
        List<Object> parsed = list.parseValue();
        Assertions.assertEquals(List.of(3, 4), parsed);
    }

    @Test
    void snbtEmpty() {
        Assertions.assertEquals("[]", new ListTag<>().toSNBT());
        Assertions.assertEquals("[]", new ListTag<>().toSNBT(2));
    }

    @Test
    void snbtNumbers() {
        ListTag<IntTag> list = new ListTag<>();
        list.add(new IntTag(1)).add(new IntTag(2));
        Assertions.assertEquals("[1,2]", list.toSNBT());
        Assertions.assertEquals("[1, 2]", list.toSNBT(2));
    }

    @Test
    void copyIsDeep() {
        ListTag<IntTag> list = new ListTag<>();
        list.add(new IntTag(1));
        Tag copy = list.copy();
        Assertions.assertEquals(list, copy);
        Assertions.assertNotSame(list, copy);
    }

    @Test
    void equalsAndHashCode() {
        ListTag<IntTag> a = new ListTag<>();
        a.add(new IntTag(1));
        ListTag<IntTag> b = new ListTag<>();
        b.add(new IntTag(1));
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        b.add(new IntTag(2));
        Assertions.assertNotEquals(a, b);
    }

    @Test
    void toStringContainsTypeName() {
        ListTag<IntTag> list = new ListTag<>();
        list.add(new IntTag(1));
        Assertions.assertTrue(list.toString().contains("TAG_Int"));
    }
}
