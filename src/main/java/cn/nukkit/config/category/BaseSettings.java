package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class BaseSettings extends OkaeriConfig {
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.ip")
    String ip = "0.0.0.0";
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.port")
    int port = 19132;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.maxplayers")
    int maxPlayers = 20;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.defaultlevel")
    String defaultLevelName = "world";
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.allowlist")
    boolean allowList = false;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.motd")
    String motd = "PowerNukkitX Server";
    @Exclude // Configured in server.properties
    @CustomKey("sub-motd")
    @Comment("pnx.settings.base.submotd")
    String subMotd = "powernukkitx.org";
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.language")
    String language = "eng";
    @Comment("pnx.settings.base.forcetranslate")
    boolean forceServerTranslate = false;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.safespawn")
    boolean safeSpawn = true;
    @Comment("pnx.settings.base.waterdogpe")
    boolean waterdogpe = false;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.autosave")
    boolean autoSave = true;
    int autosaveDelay = 6000;
    @Comment("pnx.settings.base.saveunknownblock")
    boolean saveUnknownBlock = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.base.xboxauth")
    boolean xboxAuth = true;
}