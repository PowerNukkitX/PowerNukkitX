package cn.nukkit.utils;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONUtilsTest {
    @Test
    void test_from_class() {
        JsonObject from = JSONUtils.from("""
                {
                    "a1": 123,
                    "a2": 22.2
                }
                """, JsonObject.class);
        assertEquals(123, from.get("a1").getAsInt());
        assertEquals(22.2, from.get("a2").getAsDouble());
    }

    @Test
    void test_toPretty() {
        Map<String, Object> data = new HashMap<>();
        data.put("a1", 123);
        data.put("a2", 22.2);
        assertEquals("""
                {
                  "a1": 123,
                  "a2": 22.2
                }""", JSONUtils.toPretty(data));
    }
}
