package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class LevelSettings extends OkaeriConfig {
    @Comment("pnx.settings.level.levelthread")
    boolean levelThread = false;
    @Comment("pnx.settings.level.autotickrate")
    boolean autoTickRate = true;
    @Comment("pnx.settings.level.autotickratelimit")
    int autoTickRateLimit = 20;
    @Comment("pnx.settings.level.basetickrate")
    int baseTickRate = 1;
    @Comment("pnx.settings.level.alwaystickplayers")
    boolean alwaysTickPlayers = false;
    @Comment("pnx.settings.level.chunkunloaddelay")
    int chunkUnloadDelay = 15000;
    @Comment("pnx.settings.level.entityspawncap")
    int entitySpawnCap = 512;
    @Comment("pnx.settings.level.fieldofview")
    int fieldOfView = 100;
}