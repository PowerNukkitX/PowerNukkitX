package org.powernukkitx.network;

import org.powernukkitx.utils.SemVersion;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v2168.Bedrock_v2168;
import org.cloudburstmc.protocol.bedrock.codec.v2168.Bedrock_v2168_hotfix4;

import java.util.List;

/**
 * @author Kaooot
 */
@UtilityClass
public class NetworkConstants {

    public final BedrockCodec CODEC = Bedrock_v2168_hotfix4.CODEC;

    /**
     * Every codec the server speaks, newest first. A hotfix keeps the protocol version of the
     * release it patches, so the game version a client reports in its login data is the only thing
     * telling these apart.
     */
    // TODO: pretty temporary, we need to get rid out of this ASAP
    private final List<BedrockCodec> CODECS = List.of(
        Bedrock_v2168_hotfix4.CODEC,
        Bedrock_v2168.CODEC
    );

    public int BLOCK_STATE_VERSION_NO_REVISION;

    static {
        final SemVersion semVer = SemVersion.fromString(CODEC.getMinecraftVersion());
        BLOCK_STATE_VERSION_NO_REVISION = (semVer.major() << 24) | (semVer.minor() << 16) | (semVer.patch() << 8);
    }

    /**
     * Picks the newest codec that is not ahead of the given game version, so a client still on the
     * release reads the release's packet formats instead of the hotfix ones.
     *
     * @param gameVersion the {@code GameVersion} the client sent in its login data
     * @return the matching codec, or {@link #CODEC} when the version cannot be read
     */
    public BedrockCodec codecForGameVersion(String gameVersion) {
        if (gameVersion == null) {
            return CODEC;
        }
        final SemVersion clientVersion = SemVersion.fromString(gameVersion);
        for (BedrockCodec codec : CODECS) {
            if (compare(SemVersion.fromString(codec.getMinecraftVersion()), clientVersion) <= 0) {
                return codec;
            }
        }
        return CODEC;
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
