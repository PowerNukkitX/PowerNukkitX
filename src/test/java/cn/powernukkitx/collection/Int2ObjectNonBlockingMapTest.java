package cn.powernukkitx.collection;

import cn.nukkit.utils.collection.nb.Int2ObjectNonBlockingMap;
import cn.nukkit.utils.collection.nb.Int2ObjectNonBlockingMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Int2ObjectNonBlockingMapTest {
    @Test
    public void testPut() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals("1", map.get(1));
        Assertions.assertEquals("2", map.get(2));
        Assertions.assertEquals("3", map.get(3));
    }

    @Test
    public void testParPut() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        IntStream.rangeClosed(1, 10000).parallel().forEach(i -> map.put(i, String.valueOf(i)));
        Assertions.assertEquals(10000, map.size());
        IntStream.rangeClosed(1, 10000).parallel().forEach(i -> Assertions.assertEquals(String.valueOf(i), map.get(i)));
    }

    @Test
    public void testParUpdate() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        IntStream.rangeClosed(1, 10000).parallel().forEach(i -> map.put(i, String.valueOf(i)));
        Assertions.assertEquals(10000, map.size());
        IntStream.rangeClosed(1, 10000).parallel().forEach(i -> map.put(i, String.valueOf(i + 1)));
        Assertions.assertEquals(10000, map.size());
        IntStream.rangeClosed(1, 10000).parallel().forEach(i -> Assertions.assertEquals(String.valueOf(i + 1), map.get(i)));
    }

    @Test
    public void testParRemove() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        IntStream.rangeClosed(1, 10000).parallel().forEach(i -> map.put(i, String.valueOf(i)));
        Assertions.assertEquals(10000, map.size());
        IntStream.rangeClosed(1, 10000).parallel().forEach(map::remove);
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void testRemove() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals("1", map.remove(1));
        Assertions.assertEquals("2", map.remove(2));
        Assertions.assertEquals("3", map.remove(3));
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void testClear() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        Assertions.assertEquals(3, map.size());
        map.clear();
        //noinspection ConstantValue
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void testContainsKey() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        Assertions.assertEquals(3, map.size());
        Assertions.assertFalse(map.containsKey(0));
        Assertions.assertTrue(map.containsKey(1));
        Assertions.assertTrue(map.containsKey(2));
        Assertions.assertTrue(map.containsKey(3));
        Assertions.assertFalse(map.containsKey(4));
    }

    @Test
    public void testContainsValue() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        Assertions.assertEquals(3, map.size());
        Assertions.assertFalse(map.containsValue("0"));
        Assertions.assertTrue(map.containsValue("1"));
        Assertions.assertTrue(map.containsValue("2"));
        Assertions.assertTrue(map.containsValue("3"));
        Assertions.assertFalse(map.containsValue("4"));
    }

    @Test
    public void testParWriteRead() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        IntStream.rangeClosed(1, 16).parallel().forEach(i -> {
            for (int j = 0; j < 10000; j++) {
                map.put(j, String.valueOf(j));
                Assertions.assertEquals(String.valueOf(j), map.get(j));
            }
        });
    }

    @Test
    public void testParEntryIter() {
        Int2ObjectNonBlockingMap<String> map = new Int2ObjectNonBlockingMap<>();
        IntStream.rangeClosed(1, 10000).parallel().forEach(i -> map.put(i, String.valueOf(i)));
        Assertions.assertEquals(10000, map.size());
        AtomicBoolean found = new AtomicBoolean(false);
        map.fastEntrySet().parallelStream().forEach(entry -> {
            if (entry.getIntKey() == 1 && entry.getValue().equals("1")) {
                found.set(true);
            }
        });
        Assertions.assertTrue(found.get());
    }
}
