package cn.nukkit.level.format;

import cn.nukkit.level.DimensionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class LevelConfig {
    String format = "leveldb";
    boolean enable = true;
    Map<Integer, GeneratorConfig> generators;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Accessors(fluent = true)
    public static class GeneratorConfig {
        String name;
        long seed = randomSeed();
        boolean enableAntiXray = false;
        AntiXrayMode antiXrayMode = AntiXrayMode.LOW;
        boolean preDeobfuscate = true;
        DimensionData dimensionData;
        Map<String, Object> preset;

        public static Long randomSeed() {
            return ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
        }
    }

    public enum AntiXrayMode {
        LOW,
        MEDIUM,
        HIGH
    }
}
