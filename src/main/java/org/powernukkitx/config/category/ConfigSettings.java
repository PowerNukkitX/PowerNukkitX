package org.powernukkitx.config.category;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class ConfigSettings extends OkaeriConfig {
    String version = "3.0.0";

}
