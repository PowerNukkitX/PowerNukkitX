package cn.nukkit.utils;

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
}
