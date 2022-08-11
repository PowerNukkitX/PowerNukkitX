package cn.nukkit.level.format.powerWorld;

import cn.nukkit.level.generator.Normal;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class GenerateTest {
    @SneakyThrows
    @Test
    public void generate() {
        PowerWorld.generate("target/testPowerWorld", "TestPowerWorld", 1145141919810L, Normal.class, Collections.emptyMap());
    }
}
