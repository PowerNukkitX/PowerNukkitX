package cn.nukkit.config.category.network;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class RateLimitSettings extends OkaeriConfig {
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
}
