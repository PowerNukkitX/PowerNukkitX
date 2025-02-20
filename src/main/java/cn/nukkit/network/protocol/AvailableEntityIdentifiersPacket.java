package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.registry.Registries;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class AvailableEntityIdentifiersPacket extends DataPacket {

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBytes(Registries.ENTITY.getTag());
    }

    @Override
    public int pid() {
        return ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


