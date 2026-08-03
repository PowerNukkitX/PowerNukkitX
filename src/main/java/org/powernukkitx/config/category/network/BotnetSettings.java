package org.powernukkitx.config.category.network;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class BotnetSettings extends OkaeriConfig {
    @Comment("pnx.settings.network.botnet.enabled")
    boolean detectionEnabled = false;
    @Comment("pnx.settings.network.botnet.suspiciousthreshold")
    int suspiciousThreshold = 300;
    @Comment("pnx.settings.network.botnet.minsuspiciousips")
    int minSuspiciousIps = 3;
    @Comment("pnx.settings.network.botnet.autoblock")
    boolean autoBlock = true;
    @Comment("pnx.settings.network.botnet.autoblockdurationseconds")
    int autoBlockDurationSeconds = 60;
    @Comment("pnx.settings.network.botnet.minscore")
    int minScore = 2;

    @Comment("pnx.settings.network.botnet.maxConnectionsPerIP")
    int maxConnectionsPerIp = 10;
    @Comment("pnx.settings.network.botnet.connectionWindow")
    long connectionWindow = 5000;
    @Comment("pnx.settings.network.botnet.connectionLimiter")
    boolean connectionLimiter = true;
    @Comment("pnx.settings.network.botnet.blacklistTime")
    long blacklistTime = 3600;
}
