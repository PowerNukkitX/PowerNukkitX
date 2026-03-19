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
    @Comment("pnx.settings.network.pacing.enabled")
    boolean pacingEnabled = true;
    @Comment("pnx.settings.network.pacing.flushintervalmillis")
    int pacingFlushIntervalMillis = 3;
    @Comment("pnx.settings.network.pacing.maxbytespersecond")
    int pacingMaxBytesPerSecond = 8388608;

    @Comment("pnx.settings.network.ratelimit.enabled")
    boolean rateLimitEnabled = true;
    @Comment("pnx.settings.network.ratelimit.maxinboundpersecond")
    int maxInboundPacketsPerSecond = 1500;
    @Comment("pnx.settings.network.ratelimit.maxpacketspertick")
    int maxPacketsPerTick = 500;
    @Comment("pnx.settings.network.ratelimit.maxcommandsperplayer")
    int maxCommandsPerSecondPerPlayer = 10;
    @Comment("pnx.settings.network.ratelimit.maxchatperplayer")
    int maxChatPerSecondPerPlayer = 2;
    @Comment("pnx.settings.network.ratelimit.maxformresponsesperplayer")
    int maxFormResponsesPerSecondPerPlayer = 20;
    @Comment("pnx.settings.network.ratelimit.maxmovementperplayer")
    int maxMovementPacketsPerSecondPerPlayer = 40;

    @Comment("pnx.settings.network.botnet.enabled")
    boolean botnetDetectionEnabled = false;
    @Comment("pnx.settings.network.botnet.suspiciousthreshold")
    int botnetSuspiciousThreshold = 300;
    @Comment("pnx.settings.network.botnet.minsuspiciousips")
    int botnetMinSuspiciousIps = 3;
    @Comment("pnx.settings.network.botnet.autoblock")
    boolean botnetAutoBlock = true;
    @Comment("pnx.settings.network.botnet.autoblockdurationseconds")
    int botnetAutoBlockDurationSeconds = 60;
    @Comment("pnx.settings.network.botnet.minscore")
    int botnetMinScore = 2;
}