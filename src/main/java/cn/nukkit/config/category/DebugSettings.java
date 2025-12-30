package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class DebugSettings extends OkaeriConfig {
    @Comment("pnx.settings.debug.deprecatedverbose")
    boolean deprecatedVerbose = true;
    @Comment("pnx.settings.debug.level")
    String level = "INFO";
    @Comment("pnx.settings.debug.command")
    boolean command = false;
    @Comment("pnx.settings.debug.ignoredpackets")
    ArrayList<String> ignoredPackets = new ArrayList<>();
}