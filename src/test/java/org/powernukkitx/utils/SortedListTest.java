package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortedListTest {

    private SortedList<Integer> naturalList() {
        return new SortedList<>(Comparator.naturalOrder());
    }

    @Test
    void newListIsEmpty() {
        var list = naturalList();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addKeepsElementsSorted() {
        var list = naturalList();
        list.add(5);
        list.add(1);
        list.add(3);
        list.add(2);
        list.add(4);
        assertEquals(5, list.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, list.get(i));
        }
    }

    @Test
    void addAllowsDuplicates() {
        var list = naturalList();
        list.add(2);
        list.add(2);
        list.add(1);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(2, list.get(2));
    }

    @Test
    void containsFindsPresentAndRejectsAbsent() {
        var list = naturalList();
        list.add(10);
        list.add(20);
        assertTrue(list.contains(10));
        assertTrue(list.contains(20));
        assertFalse(list.contains(15));
    }

    @Test
    void removeByIndexReturnsRemovedAndShrinks() {
        var list = naturalList();
        list.add(3);
        list.add(1);
        list.add(2);
        Integer removed = list.remove(0);
        assertEquals(1, removed);
        assertEquals(2, list.size());
        assertEquals(2, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    void removeByValue() {
        var list = naturalList();
        list.add(7);
        list.add(8);
        assertTrue(list.remove(Integer.valueOf(7)));
        assertFalse(list.remove(Integer.valueOf(99)));
        assertEquals(1, list.size());
        assertEquals(8, list.get(0));
    }

    @Test
    void clearEmptiesList() {
        var list = naturalList();
        list.add(1);
        list.add(2);
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    void iteratorTraversesInSortedOrder() {
        var list = naturalList();
        list.add(30);
        list.add(10);
        list.add(20);
        StringBuilder sb = new StringBuilder();
        for (Integer v : list) {
            sb.append(v).append(',');
        }
        assertEquals("10,20,30,", sb.toString());
    }

    @Test
    void iteratorThrowsOnConcurrentModification() {
        var list = naturalList();
        list.add(1);
        list.add(2);
        Iterator<Integer> it = list.iterator();
        it.next();
        list.add(3);
        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    void toArrayReflectsSortedContent() {
        var list = naturalList();
        list.add(3);
        list.add(1);
        list.add(2);
        assertArrayEquals(new Object[]{1, 2, 3}, list.toArray());
    }

    @Test
    void reverseComparatorOrdersDescending() {
        var list = new SortedList<>(Comparator.<Integer>reverseOrder());
        list.add(1);
        list.add(3);
        list.add(2);
        assertEquals(3, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(1, list.get(2));
    }
}
