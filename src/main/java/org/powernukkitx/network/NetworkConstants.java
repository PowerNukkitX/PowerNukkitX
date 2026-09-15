package org.powernukkitx.network;

import org.cloudburstmc.protocol.bedrock.codec.v2192.Bedrock_v2192;
import org.powernukkitx.utils.SemVersion;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kaooot
 */
@UtilityClass
public class NetworkConstants {

    public final BedrockCodec CODEC = Bedrock_v2192.CODEC;

    public final String DATA_FORMAT_VERSION = CODEC.getMinecraftVersion();

    public int BLOCK_STATE_VERSION_NO_REVISION;

    static {
        final SemVersion semVer = SemVersion.fromString(DATA_FORMAT_VERSION);
        BLOCK_STATE_VERSION_NO_REVISION = (semVer.major() << 24) | (semVer.minor() << 16) | (semVer.patch() << 8);
    }

    /**
     * Looks up the codec for a protocol version.
     *
     * @param protocolVersion the protocol version sent by the client
     * @return the current codec, or {@code null} when the protocol version is unsupported
     * @deprecated PowerNukkitX now supports only the current protocol; use {@link #CODEC} when
     * the client's protocol has already been validated
     */
    @Deprecated
    public @Nullable BedrockCodec codecForProtocolVersion(int protocolVersion) {
        return protocolVersion == CODEC.getProtocolVersion() ? CODEC : null;
    }

    /**
     * Looks up the codec for a protocol and game version.
     *
     * @param protocolVersion the protocol version sent by the client
     * @param gameVersion the game version sent by the client; ignored for the single supported codec
     * @return the current codec
     * @throws IllegalArgumentException when the protocol version is unsupported
     * @deprecated PowerNukkitX now supports only the current protocol; use {@link #CODEC} when
     * the client's protocol has already been validated
     */
    @Deprecated
    public BedrockCodec codecForGameVersion(int protocolVersion, @Nullable String gameVersion) {
        final BedrockCodec codec = codecForProtocolVersion(protocolVersion);
        if (codec == null) {
            throw new IllegalArgumentException("Unsupported protocol version " + protocolVersion);
        }
        return codec;
    }

    /**
     * Indicates whether a client protocol is newer than the server protocol.
     *
     * @param protocolVersion the protocol version sent by the client
     * @return {@code true} when the client protocol is newer
     * @deprecated compare the client protocol version with {@link #CODEC} directly
     */
    @Deprecated
    public boolean isServerOutdated(int protocolVersion) {
        return protocolVersion > CODEC.getProtocolVersion();
    }

}
