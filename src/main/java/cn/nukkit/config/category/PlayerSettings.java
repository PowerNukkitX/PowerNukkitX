package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class PlayerSettings extends OkaeriConfig {
    @Comment("pnx.settings.player.saveplayerdata")
    boolean savePlayerData = true;
    @Comment("pnx.settings.player.skinchangecooldown")
    int skinChangeCooldown = 30;
    @Comment("pnx.settings.player.forceskintrusted")
    boolean forceSkinTrusted = false;
    @Comment("pnx.settings.player.checkmovement")
    boolean checkMovement = true;
    @Comment("pnx.settings.player.spawnradius")
    int spawnRadius = 16;
    @Comment("pnx.settings.player.entitycap")
    int entityCap = 50;
}
