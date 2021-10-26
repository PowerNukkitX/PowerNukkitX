package cn.nukkit.utils;

import cn.nukkit.test.ThrowingFunction;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import io.netty.util.internal.EmptyArrays;
import joptsimple.internal.Strings;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    Config config;

    String json;

    @BeforeEach
    void setUp() {
        config = new Config();
    }

    @Test
    void loadAndSaveAsJson() throws IOException {
        config = new Config(Config.JSON);
        json = "{\"a\":5}";
        var gson = new GsonBuilder().disableHtmlEscaping()
                .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                    if (src == src.longValue())
                        return new JsonPrimitive(src.longValue());
                    return new JsonPrimitive(src);
                }).create();

        boolean result = use(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), (in) -> config.loadAsJson(in, gson));
        assertTrue(result);
        assertEquals(5.0, config.get("a"));
        File temp = Files.createTempFile("ConfigTest_", ".json").toFile();
        try {
            temp.deleteOnExit();
            assertTrue(config.saveAsJson(temp, false, gson));
            var lines = Files.readAllLines(temp.toPath());
            assertEquals(1, lines.size());
            assertEquals(json, lines.get(0));

            assertTrue(config.save(temp, false));
            String content = Strings.join(Files.readAllLines(temp.toPath()), "\n");
            assertEquals("{\n  \"a\": 5.0\n}", content);
        } finally {
            //noinspection ResultOfMethodCallIgnored
            temp.delete();
        }

        // Set config.correct to false
        temp = Files.createTempFile("ConfigTest_", ".zzz").toFile();
        try {
            temp.deleteOnExit();
            config = new Config(-2);
            assertThrows(IllegalStateException.class, ()-> config.save());
            config.load(temp.getAbsolutePath());

            config.set("a", 5);
            assertFalse(config.saveAsJson(temp, false, gson));
            assertFalse(config.save(temp));
            assertFalse(config.loadAsJson(null, gson));
            assertFalse(config.loadAsJson(new ByteArrayInputStream(EmptyArrays.EMPTY_BYTES), gson));
        } finally {
            //noinspection ResultOfMethodCallIgnored
            temp.delete();
        }
    }

    <T extends AutoCloseable, R> R use(T obj, ThrowingFunction<T, R> consumer) {
        try(T resource = obj) {
            return consumer.apply(resource);
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }
}
