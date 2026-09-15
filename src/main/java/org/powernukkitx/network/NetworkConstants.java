package org.powernukkitx.network;

import org.powernukkitx.utils.SemVersion;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v2168.Bedrock_v2168;
import org.cloudburstmc.protocol.bedrock.codec.v2168.Bedrock_v2168_hotfix4;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Kaooot
 */
@UtilityClass
public class NetworkConstants {

    /**
     * 1.26.45 keeps the packet formats of the 1.26.40 release and only bumps the protocol version.
     */
    private final BedrockCodec CODEC_v2169 = Bedrock_v2168.CODEC.toBuilder()
        .minecraftVersion("1.26.45")
        .protocolVersion(2169)
        .build();

    /**
     * Every codec the server speaks, newest first. A hotfix keeps the protocol version of the
     * release it patches, so the game version a client reports in its login data is the only thing
     * telling those apart.
     */
    // TODO: temporary usage, we need to get rid out of this ASAP
    private final List<BedrockCodec> CODECS = List.of(
        CODEC_v2169,                    // 1.26.45
        Bedrock_v2168_hotfix4.CODEC,    // 1.26.44
        Bedrock_v2168.CODEC             // 1.26.40
    );

    public final BedrockCodec CODEC = CODECS.getFirst();

    /**
     * The newest release that actually changed the block and item data formats. 1.26.45 only bumps
     * the protocol version, so following {@link #CODEC} here would send every stored block and item
     * through the updaters for a revision that never changed them.
     */
    public final String DATA_FORMAT_VERSION = Bedrock_v2168_hotfix4.CODEC.getMinecraftVersion();

    public int BLOCK_STATE_VERSION_NO_REVISION;

    static {
        final SemVersion semVer = SemVersion.fromString(DATA_FORMAT_VERSION);
        BLOCK_STATE_VERSION_NO_REVISION = (semVer.major() << 24) | (semVer.minor() << 16) | (semVer.patch() << 8);
    }

    /**
     * Picks the newest codec speaking the given protocol version.
     *
     * @param protocolVersion the protocol version the client sent
     * @return the matching codec, or {@code null} when the protocol version is not supported
     */
    // TODO: temporary usage, drop this once every supported release shares one protocol version
    public @Nullable BedrockCodec codecForProtocolVersion(int protocolVersion) {
        for (BedrockCodec codec : CODECS) {
            if (codec.getProtocolVersion() == protocolVersion) {
                return codec;
            }
        }
        return null;
    }

    /**
     * Picks the newest codec of the given protocol version that is not ahead of the given game
     * version, so a client still on the release reads the release's packet formats instead of the
     * hotfix ones.
     *
     * @param protocolVersion the protocol version the client sent
     * @param gameVersion     the {@code GameVersion} the client sent in its login data
     * @return the matching codec, or the protocol version's newest codec when the game version
     * cannot be read
     * @throws IllegalArgumentException when the protocol version is not supported
     */
    // TODO: temporary usage, this only exists because hotfixes reuse the protocol version
    public BedrockCodec codecForGameVersion(int protocolVersion, @Nullable String gameVersion) {
        final BedrockCodec newest = codecForProtocolVersion(protocolVersion);
        if (newest == null) {
            throw new IllegalArgumentException("Unsupported protocol version " + protocolVersion);
        }
        if (gameVersion == null) {
            return newest;
        }
        final SemVersion clientVersion = SemVersion.fromString(gameVersion);
        for (BedrockCodec codec : CODECS) {
            if (codec.getProtocolVersion() != protocolVersion) {
                continue;
            }
            if (compare(SemVersion.fromString(codec.getMinecraftVersion()), clientVersion) <= 0) {
                return codec;
            }
        }
        return newest;
    }

    /**
     * Tells whether a client reporting the given protocol version is ahead of everything the server
     * speaks, which decides between the outdated server and the outdated client disconnect reason.
     */
    public boolean isServerOutdated(int protocolVersion) {
        return protocolVersion > CODECS.getFirst().getProtocolVersion();
    }

    private int compare(SemVersion left, SemVersion right) {
        int result = Integer.compare(left.major(), right.major());
        if (result == 0) {
            result = Integer.compare(left.minor(), right.minor());
        }
        if (result == 0) {
            result = Integer.compare(left.patch(), right.patch());
        }
        if (result == 0) {
            result = Integer.compare(left.revision(), right.revision());
        }
        return result;
    }
}
