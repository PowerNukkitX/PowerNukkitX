package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BiomeDefinitionListPacket extends DataPacket {

    public String[] biomeStringList;
    public BiomeDefinition[] biomeDefinitionData;

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(biomeDefinitionData, v -> v.encode(byteBuf));
        byteBuf.writeArray(biomeStringList, byteBuf::writeString);
    }

    @Override
    public int pid() {
        return ProtocolInfo.BIOME_DEFINITION_LIST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
