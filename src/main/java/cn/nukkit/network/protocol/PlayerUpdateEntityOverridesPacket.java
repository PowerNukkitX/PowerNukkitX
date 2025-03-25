package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.UpdateType;

public class PlayerUpdateEntityOverridesPacket extends DataPacket {

    public long targetID;
    public int propertyIndex;
    public UpdateType updateType;
    public Number value;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.targetID = byteBuf.readLong();
        this.propertyIndex = byteBuf.readUnsignedVarInt();
        this.updateType = UpdateType.VALUES[byteBuf.readByte()];
        if(updateType.equals(UpdateType.SET_INT)) {
            this.value = byteBuf.readIntLE();
        } else if(updateType.equals(UpdateType.SET_FLOAT)) {
            this.value = byteBuf.readFloatLE();
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeLong(targetID);
        byteBuf.writeUnsignedVarInt(propertyIndex);
        byteBuf.writeByte(updateType.ordinal());
        if(updateType.equals(UpdateType.SET_INT)) {
            byteBuf.writeIntLE(value.intValue());
        } else if(updateType.equals(UpdateType.SET_FLOAT)) {
            byteBuf.writeFloatLE(value.floatValue());
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_UPDATE_ENTITY_OVERRIDES_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
