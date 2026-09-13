package org.powernukkitx.config.category;

import org.powernukkitx.config.category.network.BotnetSettings;
import org.powernukkitx.config.category.network.NetherNetSettings;
import org.powernukkitx.config.category.network.RakNetSettings;
import org.powernukkitx.config.category.network.RateLimitSettings;
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
    /**
     * Which transport carries Bedrock sessions. Exactly one is bound.
     */
    public enum TransportType {
        /**
         * @deprecated
         * The classic UDP transport every client.
         */
        @Deprecated RAKNET,
        /**
         * WebRTC data channels reached through a signalling endpoint.
         */
        NETHERNET
    }

    @Comment("pnx.settings.network.transport")
    String transport = "raknet";

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
    @Comment("pnx.settings.network.query")
    boolean enableQuery = true;
    @Comment("pnx.settings.network.encryption")
    boolean networkEncryption = true;
    @Comment("pnx.settings.network.logintime")
    boolean checkLoginTime = false;

    @Comment("pnx.settings.network.raknet")
    @CustomKey("raknet")
    private RakNetSettings rakNetSettings = new RakNetSettings();

    @Comment("pnx.settings.network.nethernet")
    @CustomKey("nethernet")
    private NetherNetSettings netherNetSettings = new NetherNetSettings();

    @Comment("pnx.settings.network.ratelimit")
    @CustomKey("rate-limit")
    private RateLimitSettings rateLimitSettings = new RateLimitSettings();

    @Comment("pnx.settings.network.ratelimit")
    @CustomKey("botnet")
    private BotnetSettings botnetSettings = new BotnetSettings();

    public TransportType resolvedTransport() {
        try {
            return TransportType.valueOf(this.transport.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (RuntimeException invalid) {
            return TransportType.RAKNET;
        }
    }
}
