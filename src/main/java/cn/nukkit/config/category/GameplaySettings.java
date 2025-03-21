package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class GameplaySettings extends OkaeriConfig {
    @Comment("pnx.settings.gameplay.enablecommandblocks")
    boolean enableCommandBlocks = true;
    @Comment("pnx.settings.gameplay.allowbeta")
    boolean allowBeta = false;
    @Comment("pnx.settings.gameplay.enableredstone")
    boolean enableRedstone = true;
    @Comment("pnx.settings.gameplay.tickRedstone")
    boolean tickRedstone = true;
}