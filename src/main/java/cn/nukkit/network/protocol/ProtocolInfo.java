package cn.nukkit.network.protocol;

import cn.nukkit.utils.SemVersion;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v924.Bedrock_v924;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX &amp; iNevet (Nukkit Project)
 */
public interface ProtocolInfo {
    /**
     * Actual Minecraft: PE protocol version
     */
    BedrockCodec CURRENT_CODEC = Bedrock_v924.CODEC;
    int CURRENT_PROTOCOL = dynamic(CURRENT_CODEC.getProtocolVersion());

    String MINECRAFT_VERSION_NETWORK = dynamic(CURRENT_CODEC.getMinecraftVersion());


    SemVersion MINECRAFT_SEMVERSION = new SemVersion(1, 26, 0, 0, 0);

    int BLOCK_STATE_VERSION_NO_REVISION = (MINECRAFT_SEMVERSION.major() << 24) | //major
            (MINECRAFT_SEMVERSION.minor() << 16) | //minor
            (MINECRAFT_SEMVERSION.patch() << 8); //patch

    String MINECRAFT_VERSION = 'v' + MINECRAFT_VERSION_NETWORK;

}
