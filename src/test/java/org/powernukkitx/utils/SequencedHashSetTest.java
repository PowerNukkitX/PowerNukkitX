package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class SequencedHashSetTest {
    @Test
    void addAssignsSequentialIndices() {
        SequencedHashSet<String> set = new SequencedHashSet<>();
        Assertions.assertTrue(set.add("a"));
        Assertions.assertTrue(set.add("b"));
        Assertions.assertTrue(set.add("c"));
        Assertions.assertEquals(0, set.indexOf("a"));
        Assertions.assertEquals(1, set.indexOf("b"));
        Assertions.assertEquals(2, set.indexOf("c"));
        Assertions.assertEquals("a", set.get(0));
        Assertions.assertEquals("b", set.get(1));
        Assertions.assertEquals("c", set.get(2));
    }

    @Test
    void duplicateAddReturnsFalse() {
        SequencedHashSet<String> set = new SequencedHashSet<>();
        Assertions.assertTrue(set.add("x"));
        Assertions.assertFalse(set.add("x"));
        Assertions.assertEquals(1, set.size());
    }

    @Test
    void sizeContainsEmpty() {
        SequencedHashSet<String> set = new SequencedHashSet<>();
        Assertions.assertTrue(set.isEmpty());
        set.add("v");
        Assertions.assertFalse(set.isEmpty());
        Assertions.assertEquals(1, set.size());
        Assertions.assertTrue(set.contains("v"));
        Assertions.assertFalse(set.contains("nope"));
    }

    @Test
    void addAllAndContainsAll() {
        SequencedHashSet<String> set = new SequencedHashSet<>();
        List<String> items = Arrays.asList("a", "b", "c");
        Assertions.assertTrue(set.addAll(items));
        Assertions.assertTrue(set.containsAll(items));
        Assertions.assertEquals(3, set.size());
    }

    @Test
    void indexOfLastIndexOfSame() {
        SequencedHashSet<String> set = new SequencedHashSet<>();
        set.add("a");
        set.add("b");
        Assertions.assertEquals(set.indexOf("b"), set.lastIndexOf("b"));
    }

    @Test
    void unsupportedOperationsThrow() {
        SequencedHashSet<String> set = new SequencedHashSet<>();
        set.add("a");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> set.remove("a"));
        Assertions.assertThrows(UnsupportedOperationException.class, set::clear);
        Assertions.assertThrows(UnsupportedOperationException.class, set::listIterator);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> set.remove(0));
    }

    @Test
    void iteratorAndToArray() {
        SequencedHashSet<String> set = new SequencedHashSet<>();
        set.add("a");
        set.add("b");
        Assertions.assertTrue(set.iterator().hasNext());
        Object[] arr = set.toArray();
        Assertions.assertEquals(2, arr.length);
    }
}
