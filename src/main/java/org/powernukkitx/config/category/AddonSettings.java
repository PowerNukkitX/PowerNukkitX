package org.powernukkitx.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class AddonSettings extends OkaeriConfig {
    @Comment("pnx.settings.addon.enabled")
    boolean enabled = true;
}
