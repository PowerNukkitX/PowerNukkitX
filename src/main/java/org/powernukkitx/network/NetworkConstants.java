package org.powernukkitx.network;

import org.cloudburstmc.protocol.bedrock.codec.v2192.Bedrock_v2192;
import org.powernukkitx.utils.SemVersion;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;

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

}
