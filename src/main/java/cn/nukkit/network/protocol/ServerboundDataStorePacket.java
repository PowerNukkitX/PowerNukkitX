package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.ddui.DataStoreUpdate;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ServerboundDataStorePacket extends DataPacket {

    private DataStoreUpdate update;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        setUpdate(byteBuf.readDataStoreUpdate());
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeDataStoreUpdate(getUpdate());
    }

    @Override
    public int pid() {
        return ProtocolInfo.SERVERBOUND_DATA_STORE_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
