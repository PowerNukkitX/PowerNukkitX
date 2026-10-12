package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class ConfigTest {
    @Test
    void test_loadConfig1() {
        Config config = new Config();
        InputStream resourceAsStream = ConfigTest.class.getClassLoader().getResourceAsStream("config.yml");
        config.load(resourceAsStream);
        Assertions.assertEquals(20, config.getSection("opSlots").getInt("slotsCount"));
    }

    @Test
    void test_removeNestedKey() {
        Config config = new Config(Config.YAML);
        config.set("first.second", "value");
        Assertions.assertTrue(config.exists("first.second"));

        config.remove("first.second");
        Assertions.assertFalse(config.exists("first.second"));
        Assertions.assertTrue(config.exists("first"));
    }
}
