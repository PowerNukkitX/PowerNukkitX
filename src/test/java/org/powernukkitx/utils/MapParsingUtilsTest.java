package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MapParsingUtilsTest {
    private static final Function<String, RuntimeException> ERR = f -> new IllegalArgumentException("bad field: " + f);

    @Test
    void stringObjectMap() {
        Map<String, Object> in = new LinkedHashMap<>();
        in.put("a", 1);
        in.put("b", "x");
        Map<String, Object> out = MapParsingUtils.stringObjectMap(in, "field", ERR);
        Assertions.assertEquals(1, out.get("a"));
        Assertions.assertEquals("x", out.get("b"));
    }

    @Test
    void stringObjectMapThrowsOnNonMap() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MapParsingUtils.stringObjectMap("notamap", "field", ERR));
    }

    @Test
    void stringObjectMapThrowsOnNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MapParsingUtils.stringObjectMap(null, "field", ERR));
    }

    @Test
    void stringObjectMapOrNullReturnsNull() {
        Assertions.assertNull(MapParsingUtils.stringObjectMapOrNull(null, "field", ERR));
    }

    @Test
    void stringObjectMapThrowsOnNonStringKey() {
        Map<Object, Object> in = new LinkedHashMap<>();
        in.put(42, "value");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MapParsingUtils.stringObjectMap(in, "field", ERR));
    }

    @Test
    void stringStringMap() {
        Map<String, String> in = new LinkedHashMap<>();
        in.put("k", "v");
        Map<String, String> out = MapParsingUtils.stringStringMap(in, "field", ERR);
        Assertions.assertEquals("v", out.get("k"));
    }

    @Test
    void stringStringMapThrowsOnNonStringValue() {
        Map<String, Object> in = new LinkedHashMap<>();
        in.put("k", 5);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MapParsingUtils.stringStringMap(in, "field", ERR));
    }

    @Test
    void stringStringMapOrNull() {
        Assertions.assertNull(MapParsingUtils.stringStringMapOrNull(null, "field", ERR));
    }

    @Test
    void stringList() {
        List<String> out = MapParsingUtils.stringList(Arrays.asList("a", "b"), "field", ERR);
        Assertions.assertEquals(Arrays.asList("a", "b"), out);
    }

    @Test
    void stringListThrowsOnNonString() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MapParsingUtils.stringList(Arrays.asList("a", 1), "field", ERR));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MapParsingUtils.stringList("notalist", "field", ERR));
    }

    @Test
    void stringObjectMapList() {
        Map<String, Object> m1 = new LinkedHashMap<>();
        m1.put("a", 1);
        List<Map<String, Object>> out = MapParsingUtils.stringObjectMapList(List.of(m1), "field", ERR);
        Assertions.assertEquals(1, out.size());
        Assertions.assertEquals(1, out.get(0).get("a"));
    }
}
