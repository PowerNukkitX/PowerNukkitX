package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class NetworkSettings extends OkaeriConfig {
    @Comment("pnx.settings.network.queryplugins")
    boolean queryPlugins = true;
    @Comment("pnx.settings.network.compressionlevel")
    int compressionLevel = 7;
    @Comment("pnx.settings.network.zlibprovider")
    int zlibProvider = 3;
    @Comment("pnx.settings.network.snappy")
    boolean snappy = false;
    @Comment("pnx.settings.network.compressionbuffersize")
    int compressionBufferSize = 1048576;
    @Comment("pnx.settings.network.maxdecompresssize")
    int maxDecompressSize = 536870912;
    @Comment("pnx.settings.network.packetlimit")
    int packetLimit = 240;
    @Comment("pnx.settings.network.query")
    boolean enableQuery = true;
    @Comment("pnx.settings.network.encryption")
    boolean networkEncryption = true;
    @Comment("pnx.settings.network.logintime")
    boolean checkLoginTime = false;
}