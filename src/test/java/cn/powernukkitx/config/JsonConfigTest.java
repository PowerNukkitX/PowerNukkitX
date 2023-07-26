package cn.powernukkitx.config;

import cn.nukkit.utils.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonConfigTest {
    @Test
    public void testJson() throws IOException {
        var jsonConfig = new Config(Config.JSON);
        var file = new File("./src/test/resources/cn/powernukkitx/test.json");
        try (var fis = new FileInputStream(file)) {
            jsonConfig.load(fis);
            Assertions.assertEquals(
                    "{a2=a3, b2={a3=a4}}", jsonConfig.getSection("a0.b1").toString());
            Assertions.assertEquals(
                    "{a1=[a2, b2, c2], b1={a2=a3, b2={a3=a4}}}",
                    jsonConfig.getSection("a0").toString());
            Assertions.assertEquals("[a2, b2, c2]", jsonConfig.getList("a0.a1").toString());
            Assertions.assertEquals("a4", jsonConfig.getString("a0.b1.b2.a3"));
        }
    }
}
