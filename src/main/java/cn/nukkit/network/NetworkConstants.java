package cn.nukkit.network;

import cn.nukkit.network.serializer.ClientboundDataStoreSerializerFixed;
import cn.nukkit.utils.SemVersion;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v1001.Bedrock_v1001;
import org.cloudburstmc.protocol.bedrock.codec.v944.serializer.ServerboundDataDrivenScreenClosedSerializer_v944;
import org.cloudburstmc.protocol.bedrock.data.PacketRecipient;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundDataStorePacket;
import org.cloudburstmc.protocol.bedrock.packet.ServerboundDataDrivenScreenClosedPacket;

/**
 * @author Kaooot
 */
@UtilityClass
public class NetworkConstants {

    public final BedrockCodec CODEC = Bedrock_v1001.CODEC.toBuilder()
            // Fix: DataStorePropertyValueType must use getId() not ordinal() (STRING=4 not 3, TYPE=6 not 4)
            .deregisterPacket(ClientboundDataStorePacket.class)
            .registerPacket(ClientboundDataStorePacket::new, ClientboundDataStoreSerializerFixed.INSTANCE, 330, PacketRecipient.CLIENT)
            // Fix: ServerboundDataDrivenScreenClosedPacket is sent by client to server, not CLIENT
            .deregisterPacket(ServerboundDataDrivenScreenClosedPacket.class)
            .registerPacket(ServerboundDataDrivenScreenClosedPacket::new, ServerboundDataDrivenScreenClosedSerializer_v944.INSTANCE, 343, PacketRecipient.SERVER)
            .build();

    public int BLOCK_STATE_VERSION_NO_REVISION;

    static {
        final SemVersion semVer = SemVersion.fromString(CODEC.getMinecraftVersion());
        BLOCK_STATE_VERSION_NO_REVISION = (semVer.major() << 24) | (semVer.minor() << 16) | (semVer.patch() << 8);
    }
}