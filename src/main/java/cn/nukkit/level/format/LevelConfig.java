package cn.nukkit.level.format;

import cn.nukkit.level.DimensionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class LevelConfig {
    String $1 = "leveldb";
    boolean $2 = true;
    Map<Integer, GeneratorConfig> generators;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Accessors(fluent = true)
    public static class GeneratorConfig {
        String name;
        long $3 = 0L;
        boolean $4 = false;
        AntiXrayMode $5 = AntiXrayMode.LOW;
        boolean $6 = true;
        DimensionData dimensionData;
        Map<String, Object> preset;
    }

    public enum AntiXrayMode {
        LOW,
        MEDIUM,
        HIGH
    }
}
