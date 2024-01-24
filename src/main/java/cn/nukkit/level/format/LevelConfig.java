package cn.nukkit.level.format;

import cn.nukkit.level.DimensionData;

import java.util.Map;

public record LevelConfig(String format, Map<Integer, GeneratorConfig> generators) {
    public record GeneratorConfig(String name, long seed, DimensionData dimensionData, Map<String, Object> preset) {
    }
}
