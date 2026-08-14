package org.powernukkitx.network;

import org.powernukkitx.utils.SemVersion;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v2168.Bedrock_v2168;
import org.cloudburstmc.protocol.bedrock.packet.CameraInstructionPacket;
import org.powernukkitx.network.protocol.fix.CameraInstructionSerializerV2168Fix;

/**
 * @author Kaooot
 */
@UtilityClass
public class NetworkConstants {

    // Bedrock_v2168.CODEC still registers CameraInstructionSerializer_v924, whose FOV easeType
    // write is a byte ordinal; 1.26.10+ clients read a string and disconnect on the mismatch
    // (see CameraInstructionSerializerV2168Fix). Patched here until bedrock-codec ships a fixed serializer.
    public final BedrockCodec CODEC = Bedrock_v2168.CODEC.toBuilder()
            .updateSerializer(CameraInstructionPacket.class, CameraInstructionSerializerV2168Fix.INSTANCE)
            .build();

    public int BLOCK_STATE_VERSION_NO_REVISION;

    static {
        final SemVersion semVer = SemVersion.fromString(CODEC.getMinecraftVersion());
        BLOCK_STATE_VERSION_NO_REVISION = (semVer.major() << 24) | (semVer.minor() << 16) | (semVer.patch() << 8);
    }
}
