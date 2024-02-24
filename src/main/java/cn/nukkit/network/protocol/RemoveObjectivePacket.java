package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;

public class RemoveObjectivePacket extends DataPacket {

    public String objectiveName;

    @Override
    public int pid() {
        return ProtocolInfo.REMOVE_OBJECTIVE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        //only server -> client
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeString(this.objectiveName);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
