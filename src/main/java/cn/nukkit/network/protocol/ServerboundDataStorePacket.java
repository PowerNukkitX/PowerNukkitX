package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

public class ServerboundDataStorePacket extends DataPacket {

    private String dataStoreName;
    private String property;
    private String path;
    private Object data;
    private int updateCount;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.dataStoreName = byteBuf.readString();
        this.property = byteBuf.readString();
        this.path = byteBuf.readString();
        int type = byteBuf.readUnsignedVarInt();
        this.data = switch (type) {
            case 0 -> byteBuf.readDoubleLE();
            case 1 -> byteBuf.readBoolean();
            case 2 -> byteBuf.readString();
            default -> throw new IllegalStateException("Invalid data store data type: " + type);
        };
        this.updateCount = (int) byteBuf.readUnsignedIntLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

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
