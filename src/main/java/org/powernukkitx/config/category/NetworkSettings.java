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

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#packetLimit()} */
    @Deprecated
    public int packetLimit() {
        return this.rakNetSettings.packetLimit();
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#packetLimit(int)} */
    @Deprecated
    public NetworkSettings packetLimit(int packetLimit) {
        this.rakNetSettings.packetLimit(packetLimit);
        return this;
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#autoFlush()} */
    @Deprecated
    public boolean autoFlush() {
        return this.rakNetSettings.autoFlush();
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#autoFlush(boolean)} */
    @Deprecated
    public NetworkSettings autoFlush(boolean autoFlush) {
        this.rakNetSettings.autoFlush(autoFlush);
        return this;
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#flushInterval()} */
    @Deprecated
    public int flushInterval() {
        return this.rakNetSettings.flushInterval();
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#flushInterval(int)} */
    @Deprecated
    public NetworkSettings flushInterval(int flushInterval) {
        this.rakNetSettings.flushInterval(flushInterval);
        return this;
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#maxQueuedBytes()} */
    @Deprecated
    public int maxQueuedBytes() {
        return this.rakNetSettings.maxQueuedBytes();
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#maxQueuedBytes(int)} */
    @Deprecated
    public NetworkSettings maxQueuedBytes(int maxQueuedBytes) {
        this.rakNetSettings.maxQueuedBytes(maxQueuedBytes);
        return this;
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#cookieMode()} */
    @Deprecated
    public String cookieMode() {
        return this.rakNetSettings.cookieMode();
    }

    /** @deprecated use {@link #rakNetSettings()} and {@link RakNetSettings#cookieMode(String)} */
    @Deprecated
    public NetworkSettings cookieMode(String cookieMode) {
        this.rakNetSettings.cookieMode(cookieMode);
        return this;
    }

    public TransportType resolvedTransport() {
        try {
            return TransportType.valueOf(this.transport.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (RuntimeException invalid) {
            return TransportType.RAKNET;
        }
    }
}
