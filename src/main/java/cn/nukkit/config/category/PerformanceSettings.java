package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class PerformanceSettings extends OkaeriConfig {
    @Comment("pnx.settings.performance.asyncworkers")
    String asyncWorkers = "auto";
    @Comment("pnx.settings.performance.registrycache.enable")
    boolean registryCacheEnabled = false;
    @Comment("pnx.settings.performance.registrycache.path")
    String registryCachePath = "path/to/your/registry_cache.bin";
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
    /// Maximum allowed recursion depth when reading or writing NBT.
    /// 
    /// PowerNukkitX previously hardcoded this value to 16.
    /// However, modern Bedrock worlds and items (bundles, nested containers,
    /// migrated BDS worlds, etc.) can contain significantly deeper NBT structures.
    ///
    /// This setting allows server operators to increase the recursion depth
    /// to support those cases without modifying server source code.
    ///
    /// Increasing this value slightly increases recursion allowance but has
    /// negligible performance impact under normal conditions.
    @Comment("Maximum allowed recursion depth for NBT reading and writing. Increase if loading worlds with deeply nested containers.")
    @CustomKey("nbt-max-depth")
    private int nbtMaxDepth = 16;
}