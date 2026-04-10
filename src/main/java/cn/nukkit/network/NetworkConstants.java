package cn.nukkit.network;

import cn.nukkit.utils.SemVersion;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v944.Bedrock_v944;

/**
 * @author Kaooot
 */
@UtilityClass
public class NetworkConstants {

    public final BedrockCodec CODEC = Bedrock_v944.CODEC;

    public int BLOCK_STATE_VERSION_NO_REVISION;

    static {
        final SemVersion semVer = SemVersion.fromString(CODEC.getMinecraftVersion());
        BLOCK_STATE_VERSION_NO_REVISION = (semVer.major() << 24) | (semVer.minor() << 16) | (semVer.patch() << 8);
    }
}