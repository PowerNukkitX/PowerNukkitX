package org.powernukkitx.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class MiscSettings extends OkaeriConfig {
    @Comment("pnx.settings.misc.shutdownmessage")
    String shutdownMessage = "Server closed";
    @Comment("pnx.settings.misc.installspark")
    boolean installSpark = false;
    @Comment("pnx.settings.misc.enableterra")
    boolean enableTerra = false;
    @Comment("pnx.settings.misc.bypassapicheck")
    boolean bypassAPICheck = false;
    @Comment("pnx.settings.misc.overrideserverauthblockbreaking")
    boolean overrideServerAuthBlockBreaking = false;
    @Comment("pnx.settings.misc.disablemetrics")
    boolean disableMetrics = true;
}