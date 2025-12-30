package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class PerformanceSettings extends OkaeriConfig {
    @Comment("pnx.settings.performance.asyncworkers")
    String asyncWorkers = "auto";
    @Comment("pnx.settings.performance.freezearray.enable")
    boolean enable = true;
    int slots = 32;
    int defaultTemperature = 32;
    int freezingPoint = 0;
    int boilingPoint = 1024;
    int absoluteZero = -256;
    int melting = 16;
    int singleOperation = 1;
    int batchOperation = 32;
}