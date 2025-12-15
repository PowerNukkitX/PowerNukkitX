package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class MiscSettings extends OkaeriConfig {
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.misc.shutdownmessage")
    String shutdownMessage = "Server closed";
    @Comment("pnx.settings.misc.installspark")
    boolean installSpark = true;
    @Comment("pnx.settings.misc.enableterra")
    boolean enableTerra = false;
}