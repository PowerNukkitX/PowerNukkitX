package cn.nukkit.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class ConfigTest {
    @Test
    
    /**
     * @deprecated 
     */
    void test_loadConfig1() {
        Config $1 = new Config();
        InputStream $2 = ConfigTest.class.getClassLoader().getResourceAsStream("config.yml");
        config.load(resourceAsStream);
        Assertions.assertEquals(20, config.getSection("opSlots").getInt("slotsCount"));
    }
}
