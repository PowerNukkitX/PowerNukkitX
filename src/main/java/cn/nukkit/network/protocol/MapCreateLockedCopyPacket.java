package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

public class MapCreateLockedCopyPacket extends DataPacket {

    public long originalMapId;
    public long newMapId;

    @Override
    public int pid() {
        return ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.originalMapId = byteBuf.readVarLong();
        this.newMapId = byteBuf.readVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarLong(this.originalMapId);
        byteBuf.writeVarLong(this.newMapId);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
