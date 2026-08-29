package org.powernukkitx.config.category.gameplay;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class CdnPackSettings extends OkaeriConfig {
    @Comment("pnx.settings.gameplay.cdnPacks.url")
    String url = "";
    @Comment("pnx.settings.gameplay.cdnPacks.name")
    String name = "";
    @Comment("pnx.settings.gameplay.cdnPacks.uuid")
    String uuid = "";
    @Comment("pnx.settings.gameplay.cdnPacks.version")
    String version = "";
    @Comment("pnx.settings.gameplay.cdnPacks.size")
    int size = 0;
    @Comment("pnx.settings.gameplay.cdnPacks.contentKey")
    String contentKey = "";
    @Comment("pnx.settings.gameplay.cdnPacks.subPackName")
    String subPackName = "";
    @Comment("pnx.settings.gameplay.cdnPacks.addonPack")
    boolean addonPack = false;
    @Comment("pnx.settings.gameplay.cdnPacks.scripts")
    boolean scripts = false;
    @Comment("pnx.settings.gameplay.cdnPacks.rayTracing")
    boolean rayTracing = false;
}
