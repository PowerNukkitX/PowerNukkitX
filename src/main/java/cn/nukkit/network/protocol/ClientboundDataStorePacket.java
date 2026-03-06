package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.ddui.DataStoreChange;
import cn.nukkit.network.protocol.types.ddui.DataStoreChangeInfo;
import cn.nukkit.network.protocol.types.ddui.DataStorePropertyValue;
import cn.nukkit.network.protocol.types.ddui.DataStoreRemoval;
import cn.nukkit.network.protocol.types.ddui.DataStoreUpdate;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ClientboundDataStorePacket extends DataPacket {

    private List<DataStoreChangeInfo> updates = new ObjectArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        byteBuf.readArray(getUpdates(), this::readDataStoreChangeInfo);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(getUpdates(), this::writeDataStoreChangeInfo);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_DATA_STORE_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    protected void writeDataStoreChangeInfo(HandleByteBuf buffer,  DataStoreChangeInfo info) {
        buffer.writeUnsignedVarInt(info.getChangeType().ordinal());
        switch (info.getChangeType()) {
            case UPDATE:
                buffer.writeDataStoreUpdate((DataStoreUpdate) info);
                break;
            case CHANGE:
                this.writeDataStoreChange(buffer, (DataStoreChange) info);
                break;
            case REMOVAL:
                this.writeDataStoreRemoval(buffer, (DataStoreRemoval) info);
                break;
        }
    }

    protected DataStoreChangeInfo readDataStoreChangeInfo(HandleByteBuf buffer) {
        final DataStoreChangeInfo.Type changeType = DataStoreChangeInfo.Type.from(buffer.readUnsignedVarInt());
        return switch (changeType) {
            case UPDATE -> buffer.readDataStoreUpdate();
            case CHANGE -> this.readDataStoreChange(buffer);
            case REMOVAL -> this.readDataStoreRemoval(buffer);
        };
    }

    protected void writeDataStoreChange(HandleByteBuf buffer, DataStoreChange change) {
        buffer.writeString(change.getDataStoreName());
        buffer.writeString(change.getProperty());
        buffer.writeIntLE(change.getUpdateCount());
        buffer.writeIntLE(change.getTheNewPropertyValue().getType().ordinal());
        this.writeTheNewPropertyValue(buffer, change.getTheNewPropertyValue());
    }

    protected void writeTheNewPropertyValue(HandleByteBuf buffer, DataStorePropertyValue value) {
        switch (value.getType()) {
            case NONE:
                break;
            case BOOL:
                buffer.writeBoolean((boolean) value.getValue());
                break;
            case INT64:
                buffer.writeLongLE((long) value.getValue());
                break;
            case STRING:
                buffer.writeString((String) value.getValue());
                break;
            case TYPE:
                final Map<String, DataStorePropertyValue> map = (Map<String, DataStorePropertyValue>) value.getValue();
                buffer.writeUnsignedVarInt(map.size());
                for (Map.Entry<String, DataStorePropertyValue> entry : map.entrySet()) {
                    buffer.writeString(entry.getKey());
                    buffer.writeIntLE(entry.getValue().getType().ordinal());
                    this.writeTheNewPropertyValue(buffer, entry.getValue());
                }
                break;
        }
    }

    protected DataStoreChange readDataStoreChange(HandleByteBuf buffer) {
        final DataStorePropertyValue.Type valueType = DataStorePropertyValue.Type.from(buffer.readIntLE());
        return new DataStoreChange(
                buffer.readString(),
                buffer.readString(),
                buffer.readIntLE(),
                this.readTheNewPropertyValue(buffer, valueType));
    }

    protected DataStorePropertyValue readTheNewPropertyValue(HandleByteBuf buffer, DataStorePropertyValue.Type type) {
        switch (type) {
            case NONE:
                return null;
            case BOOL:
                return new DataStorePropertyValue(type, buffer.readBoolean());
            case INT64:
                return new DataStorePropertyValue(type, buffer.readLongLE());
            case STRING:
                return new DataStorePropertyValue(type, buffer.readString());
            case TYPE:
                final int length = buffer.readUnsignedVarInt();
                final Map<String, DataStorePropertyValue> map = new HashMap<>();
                for (int i = 0; i < length; i++) {
                    final String key = buffer.readString();
                    final DataStorePropertyValue.Type valueType = DataStorePropertyValue.Type.from(buffer.readIntLE());
                    map.put(key, this.readTheNewPropertyValue(buffer, valueType));
                }
                return new DataStorePropertyValue(type, map);
            default:
                throw new IllegalStateException("Read invalid DataStorePropertyValueType");
        }
    }

    protected void writeDataStoreRemoval(HandleByteBuf buffer, DataStoreRemoval removal) {
        buffer.writeString(removal.getDataStoreName());
    }

    protected DataStoreRemoval readDataStoreRemoval(HandleByteBuf buffer) {
        final DataStoreRemoval removal = new DataStoreRemoval();
        removal.setDataStoreName(buffer.readString());
        return removal;
    }
}
