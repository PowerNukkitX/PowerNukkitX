package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.Data;
import lombok.Getter;

@Getter
public class ServerboundDataStorePacket extends DataPacket {

    private final Update update = new Update();

    @Data
    public static class Update {
        private String dataStoreName;
        private String property;
        private String path;
        private Object data;
        private int updateCount;
        private int pathUpdateCount;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.update.dataStoreName = byteBuf.readString();
        this.update.property = byteBuf.readString();
        this.update.path = byteBuf.readString();
        int type = byteBuf.readUnsignedVarInt();
        this.update.data = switch (type) {
            case 0 -> byteBuf.readDoubleLE();
            case 1 -> byteBuf.readBoolean();
            case 2 -> byteBuf.readString();
            default -> throw new IllegalStateException("Invalid data store data type: " + type);
        };
        this.update.updateCount = (int) byteBuf.readUnsignedIntLE();
        this.update.pathUpdateCount = (int) byteBuf.readUnsignedIntLE();
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
