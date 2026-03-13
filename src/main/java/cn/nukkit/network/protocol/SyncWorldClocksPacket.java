package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.clock.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SyncWorldClocksPacket extends DataPacket {
    public ClockPayloadData payloadData;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        final ClockPayloadData.Type type = ClockPayloadData.Type.from(byteBuf.readUnsignedVarInt());
        switch (type) {
            case SYNC_STATE_DATA:
                setPayloadData(readSyncStateData(byteBuf));
                break;
            case INITIALIZE_REGISTRY_DATA:
                setPayloadData(readInitializeRegistryData(byteBuf));
                break;
            case ADD_TIME_MARKER_DATA:
                setPayloadData(readAddTimeMarkerData(byteBuf));
                break;
            case REMOVE_TIME_MARKER_DATA:
                setPayloadData(readRemoveTimeMarkerData(byteBuf));
                break;
            default:
                throw new IllegalStateException("Read unknown ClockPayloadData.Type");
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(payloadData.getType().ordinal());
        switch (payloadData.getType()) {
            case SYNC_STATE_DATA:
                writeSyncStateData(byteBuf, (SyncStateData) payloadData);
                break;
            case INITIALIZE_REGISTRY_DATA:
                writeInitializeRegistryData(byteBuf, (InitializeRegistryData) payloadData);
                break;
            case ADD_TIME_MARKER_DATA:
                writeAddTimeMarkerData(byteBuf, (AddTimeMarkerData) payloadData);
                break;
            case REMOVE_TIME_MARKER_DATA:
                writeRemoveTimeMarkerData(byteBuf, (RemoveTimeMarkerData) payloadData);
                break;
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.SYNC_WORLD_CLOCKS_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    private void writeSyncStateData(HandleByteBuf byteBuf, SyncStateData data) {
        byteBuf.writeArray(data.getClockData(), this::writeSyncWorldClockStateData);
    }

    private SyncStateData readSyncStateData(HandleByteBuf byteBuf) {
        final SyncStateData data = new SyncStateData();
        byteBuf.readArray(data.getClockData(), this::readSyncWorldClockStateData);
        return data;
    }

    private void writeInitializeRegistryData(HandleByteBuf byteBuf, InitializeRegistryData data) {
        byteBuf.writeArray(data.getClockData(), this::writeWorldClockData);
    }

    private InitializeRegistryData readInitializeRegistryData(HandleByteBuf byteBuf) {
        final InitializeRegistryData data = new InitializeRegistryData();
        byteBuf.readArray(data.getClockData(), this::readWorldClockData);
        return data;
    }

    private void writeAddTimeMarkerData(HandleByteBuf byteBuf, AddTimeMarkerData data) {
        byteBuf.writeUnsignedVarLong(data.getClockId());
        byteBuf.writeArray(data.getTimeMarkers(), this::writeTimeMarkerData);
    }

    private AddTimeMarkerData readAddTimeMarkerData(HandleByteBuf byteBuf) {
        final AddTimeMarkerData data = new AddTimeMarkerData();
        data.setClockId(byteBuf.readUnsignedVarLong());
        byteBuf.readArray(data.getTimeMarkers(), this::readTimeMarkerData);
        return data;
    }

    private void writeRemoveTimeMarkerData(HandleByteBuf byteBuf, RemoveTimeMarkerData data) {
        byteBuf.writeVarLong(data.getClockId());
        byteBuf.writeArray(data.getTimeMarkerIds(), HandleByteBuf::writeUnsignedVarLong);
    }

    private RemoveTimeMarkerData readRemoveTimeMarkerData(HandleByteBuf byteBuf) {
        final RemoveTimeMarkerData data = new RemoveTimeMarkerData();
        data.setClockId(byteBuf.readVarLong());
        byteBuf.readArray(data.getTimeMarkerIds(), HandleByteBuf::readUnsignedVarLong);
        return data;
    }

    private void writeSyncWorldClockStateData(HandleByteBuf byteBuf, SyncWorldClockStateData data) {
        byteBuf.writeUnsignedVarLong(data.getClockId());
        byteBuf.writeVarInt(data.getTime());
        byteBuf.writeBoolean(data.isPaused());
    }

    private SyncWorldClockStateData readSyncWorldClockStateData(HandleByteBuf byteBuf) {
        final SyncWorldClockStateData data = new SyncWorldClockStateData();
        data.setClockId(byteBuf.readUnsignedVarLong());
        data.setTime(byteBuf.readVarInt());
        data.setPaused(byteBuf.readBoolean());
        return data;
    }

    private void writeWorldClockData(HandleByteBuf byteBuf, WorldClockData data) {
        byteBuf.writeUnsignedVarLong(data.getId());
        byteBuf.writeString(data.getName());
        byteBuf.writeVarInt(data.getTime());
        byteBuf.writeBoolean(data.isPaused());
        byteBuf.writeArray(data.getTimeMarkers(), this::writeTimeMarkerData);
    }

    private WorldClockData readWorldClockData(HandleByteBuf byteBuf) {
        final WorldClockData data = new WorldClockData();
        data.setId(byteBuf.readUnsignedVarLong());
        data.setName(byteBuf.readString());
        data.setTime(byteBuf.readVarInt());
        data.setPaused(byteBuf.readBoolean());
        byteBuf.readArray(data.getTimeMarkers(), this::readTimeMarkerData);
        return data;
    }

    private void writeTimeMarkerData(HandleByteBuf byteBuf, TimeMarkerData data) {
        byteBuf.writeUnsignedVarLong(data.getId());
        byteBuf.writeString(data.getName());
        byteBuf.writeVarInt(data.getTime());
        byteBuf.writeNotNull(data.getPeriod(), byteBuf::writeIntLE);
    }

    private TimeMarkerData readTimeMarkerData(HandleByteBuf byteBuf) {
        final TimeMarkerData data = new TimeMarkerData();
        data.setId(byteBuf.readUnsignedVarLong());
        data.setName(byteBuf.readString());
        data.setTime(byteBuf.readVarInt());
        data.setPeriod(byteBuf.readOptional(null, byteBuf::readIntLE));
        return data;
    }
}
