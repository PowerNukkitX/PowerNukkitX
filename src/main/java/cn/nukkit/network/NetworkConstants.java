package cn.nukkit.network;

import cn.nukkit.network.serializer.ClientboundDataStoreSerializerFixed;
import cn.nukkit.utils.SemVersion;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v1001.Bedrock_v1001;
import org.cloudburstmc.protocol.bedrock.data.PacketRecipient;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundDataStorePacket;

/**
 * @author Kaooot
 */
@UtilityClass
public class NetworkConstants {

    public final BedrockCodec CODEC = Bedrock_v1001.CODEC.toBuilder()
            .deregisterPacket(ClientboundDataStorePacket.class)
            .registerPacket(ClientboundDataStorePacket::new, ClientboundDataStoreSerializerFixed.INSTANCE, 330, PacketRecipient.CLIENT)
            .build();

    public int BLOCK_STATE_VERSION_NO_REVISION;

    static {
        final SemVersion semVer = SemVersion.fromString(CODEC.getMinecraftVersion());
        BLOCK_STATE_VERSION_NO_REVISION = (semVer.major() << 24) | (semVer.minor() << 16) | (semVer.patch() << 8);
    }
}