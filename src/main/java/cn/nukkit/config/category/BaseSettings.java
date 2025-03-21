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
public class BaseSettings extends OkaeriConfig {
    @Comment("pnx.settings.base.language")
    String language = "eng";
    @Comment("pnx.settings.base.forcetranslate")
    boolean forceServerTranslate = false;
    @Comment("pnx.settings.base.safespawn")
    boolean safeSpawn = true;
    @Comment("pnx.settings.base.waterdogpe")
    boolean waterdogpe = false;
    @Comment("pnx.settings.base.autosave")
    boolean autoSave = true;
    int autosaveDelay = 6000;
    @Comment("pnx.settings.base.saveunknownblock")
    boolean saveUnknownBlock = true;

    @Comment("pnx.settings.base.motd")
    String motd = "PowerNukkitX Server";
    @CustomKey("sub-motd")
    @Comment("pnx.settings.base.submotd")
    String subMotd = "powernukkitx.org";

    String ip = "0.0.0.0";
    int port = 19132;
    int maxPlayers = 20;
    String levelName = "world";
    String levelSeed = String.valueOf(System.currentTimeMillis());
    boolean allowList = false;
    boolean xboxAuth = true;
}