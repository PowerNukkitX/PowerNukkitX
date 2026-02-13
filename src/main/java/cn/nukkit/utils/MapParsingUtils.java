package cn.nukkit.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class MapParsingUtils {
    private MapParsingUtils() {
    }

    public static Map<String, Object> stringObjectMap(Object value, String field, Function<String, RuntimeException> error) {
        return stringObjectMap(value, field, error, false);
    }

    public static Map<String, Object> stringObjectMapOrNull(Object value, String field, Function<String, RuntimeException> error) {
        return stringObjectMap(value, field, error, true);
    }

    private static Map<String, Object> stringObjectMap(Object value, String field, Function<String, RuntimeException> error, boolean allowNull) {
        if (value == null) {
            if (allowNull) {
                return null;
            }
            throw error.apply(field);
        }
        if (!(value instanceof Map)) {
            throw error.apply(field);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw error.apply(field);
            }
            map.put((String) entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static Map<String, String> stringStringMap(Object value, String field, Function<String, RuntimeException> error) {
        return stringStringMap(value, field, error, false);
    }

    public static Map<String, String> stringStringMapOrNull(Object value, String field, Function<String, RuntimeException> error) {
        return stringStringMap(value, field, error, true);
    }

    private static Map<String, String> stringStringMap(Object value, String field, Function<String, RuntimeException> error, boolean allowNull) {
        if (value == null) {
            if (allowNull) {
                return null;
            }
            throw error.apply(field);
        }
        if (!(value instanceof Map)) {
            throw error.apply(field);
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw error.apply(field);
            }
            Object entryValue = entry.getValue();
            if (!(entryValue instanceof String)) {
                throw error.apply(field);
            }
            map.put((String) entry.getKey(), (String) entryValue);
        }
        return map;
    }

    public static List<String> stringList(Object value, String field, Function<String, RuntimeException> error) {
        if (!(value instanceof List)) {
            throw error.apply(field);
        }
        List<String> list = new ArrayList<>();
        for (Object entry : (List<?>) value) {
            if (!(entry instanceof String)) {
                throw error.apply(field);
            }
            list.add((String) entry);
        }
        return list;
    }

    public static List<Map<String, Object>> stringObjectMapList(Object value, String field, Function<String, RuntimeException> error) {
        if (!(value instanceof List)) {
            throw error.apply(field);
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object entry : (List<?>) value) {
            list.add(stringObjectMap(entry, field, error));
        }
        return list;
    }
}
