package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.Getter;
import cn.nukkit.network.protocol.ServerboundDataStorePacket.Update;

@Getter
public class ClientboundDataStorePacket extends DataPacket {

    public static final int TYPE_UPDATE = 0;
    public static final int TYPE_CHANGE = 1;
    public static final int TYPE_REMOVAL = 2;

    private Update[] actions = new Update[0];

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int size = byteBuf.readUnsignedVarInt();
        this.actions = new Update[size];

        for (int i = 0; i < size; i++) {
            int type = byteBuf.readUnsignedVarInt();

            Update update = new Update();

            switch (type) {
                case TYPE_UPDATE -> {
                    update.setDataStoreName(byteBuf.readString());
                    update.setProperty(byteBuf.readString());
                    update.setPath(byteBuf.readString());

                    int valueType = byteBuf.readUnsignedVarInt();
                    update.setData(readValue(byteBuf, valueType));

                    update.setUpdateCount(byteBuf.readIntLE());
                    update.setPathUpdateCount(byteBuf.readIntLE());
                }
                case TYPE_CHANGE -> {
                    update.setDataStoreName(byteBuf.readString());
                    update.setProperty(byteBuf.readString());
                    update.setUpdateCount(byteBuf.readIntLE());

                    int valueType = byteBuf.readUnsignedVarInt();
                    update.setData(readValue(byteBuf, valueType));
                }
                case TYPE_REMOVAL -> update.setDataStoreName(byteBuf.readString());
                default -> throw new IllegalStateException("Unknown datastore action: " + type);
            }

            actions[i] = update;
        }
    }

    private Object readValue(HandleByteBuf buf, int type) {
        return switch (type) {
            case 0 -> buf.readDoubleLE();
            case 1 -> buf.readBoolean();
            case 2 -> buf.readString();
            default -> throw new IllegalStateException("Invalid datastore type: " + type);
        };
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(actions.length);

        for (Update update : actions) {

            int type;
            if (update.getProperty() == null && update.getData() == null) {
                type = TYPE_REMOVAL;
            } else if (update.getPath() == null || update.getPath().isEmpty()) {
                type = TYPE_CHANGE;
            } else {
                type = TYPE_UPDATE;
            }

            byteBuf.writeUnsignedVarInt(type);

            switch (type) {

                case TYPE_UPDATE -> {
                    byteBuf.writeString(update.getDataStoreName());
                    byteBuf.writeString(update.getProperty());
                    byteBuf.writeString(update.getPath());

                    writeValue(byteBuf, update.getData());

                    byteBuf.writeIntLE(update.getUpdateCount());
                    byteBuf.writeIntLE(update.getPathUpdateCount());
                }

                case TYPE_CHANGE -> {
                    byteBuf.writeString(update.getDataStoreName());
                    byteBuf.writeString(update.getProperty());
                    byteBuf.writeIntLE(update.getUpdateCount());

                    writeValue(byteBuf, update.getData());
                }

                case TYPE_REMOVAL -> {
                    byteBuf.writeString(update.getDataStoreName());
                }
            }
        }
    }

    private void writeValue(HandleByteBuf buf, Object value) {
        switch (value) {
            case Double d -> {
                buf.writeUnsignedVarInt(0);
                buf.writeDoubleLE(d);
            }
            case Boolean b -> {
                buf.writeUnsignedVarInt(1);
                buf.writeBoolean(b);
            }
            case String s -> {
                buf.writeUnsignedVarInt(2);
                buf.writeString(s);
            }
            default -> throw new IllegalStateException("Invalid datastore value type");
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_DATA_STORE_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
