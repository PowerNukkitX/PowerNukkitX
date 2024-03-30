package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.registry.Registries;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class BiomeDefinitionListPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.BIOME_DEFINITION_LIST_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBytes(Registries.BIOME.getBiomeDefinitionListPacketData());
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
