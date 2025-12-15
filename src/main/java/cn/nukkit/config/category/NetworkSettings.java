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
public class NetworkSettings extends OkaeriConfig {
    @Comment("pnx.settings.network.queryplugins")
    boolean queryPlugins = true;
    @Comment("pnx.settings.network.compressionlevel")
    int compressionLevel = 4;
    @Comment("pnx.settings.network.zlibprovider")
    int zlibProvider = 3;
    @Comment("pnx.settings.network.snappy")
    boolean snappy = false;
    @Comment("pnx.settings.network.compressionbuffersize")
    int compressionBufferSize = 1048576;
    @Comment("pnx.settings.network.maxdecompresssize")
    int maxDecompressSize = 268435456;
    @Comment("pnx.settings.network.packetlimit")
    int packetLimit = 8000;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.network.query")
    boolean enableQuery = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.network.encryption")
    boolean networkEncryption = true;
    @Comment("pnx.settings.network.logintime")
    boolean checkLoginTime = false;
    @Comment("pnx.settings.network.pacing.enabled")
    boolean pacingEnabled = true;
    @Comment("pnx.settings.network.pacing.flushintervalmillis")
    int pacingFlushIntervalMillis = 3;
    @Comment("pnx.settings.network.pacing.maxbytespersecond")
    int pacingMaxBytesPerSecond = 8388608;
}