package cn.nukkit.config.category;

import cn.nukkit.config.category.network.BotnetSettings;
import cn.nukkit.config.category.network.RateLimitSettings;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
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
    @Comment("pnx.settings.network.query")
    boolean enableQuery = true;
    @Comment("pnx.settings.network.encryption")
    boolean networkEncryption = true;
    @Comment("pnx.settings.network.logintime")
    boolean checkLoginTime = false;
    @Comment("pnx.settings.network.autoflush")
    boolean autoFlush = true;
    @Comment("pnx.settings.network.flushinterval")
    int flushInterval = 10;
    @Comment("pnx.settings.network.maxqueuedbytes")
    int maxQueuedBytes = 67108864;
    @Comment("pnx.settings.network.cookiemode")
    String cookieMode = "ACTIVE";

    @Comment("pnx.settings.network.ratelimit")
    @CustomKey("rate-limit")
    private RateLimitSettings rateLimitSettings = new RateLimitSettings();

    @Comment("pnx.settings.network.ratelimit")
    @CustomKey("botnet")
    private BotnetSettings botnetSettings = new BotnetSettings();
}