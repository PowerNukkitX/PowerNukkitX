package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
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
    int autosave = 6000;
    @Comment("pnx.settings.base.saveunknownblock")
    boolean saveUnknownBlock = true;
}