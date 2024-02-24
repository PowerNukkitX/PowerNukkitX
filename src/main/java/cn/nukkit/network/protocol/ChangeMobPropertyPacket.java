package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

/**
 * Server-bound packet to change the properties of a mob.
 *
 * @since v503
 */
public class ChangeMobPropertyPacket extends DataPacket {
    private long uniqueEntityId;
    private String property;
    private boolean boolValue;
    private String stringValue;
    private int intValue;
    private float floatValue;

    @Override
    public int pid() {
        return ProtocolInfo.CHANGE_MOB_PROPERTY_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.uniqueEntityId = byteBuf.readLong();
        this.property = byteBuf.readString();
        this.boolValue = byteBuf.readBoolean();
        this.stringValue = byteBuf.readString();
        this.intValue = byteBuf.readVarInt();
        this.floatValue = byteBuf.readFloatLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeLong(this.uniqueEntityId);
        byteBuf.writeString(this.property);
        byteBuf.writeBoolean(this.boolValue);
        byteBuf.writeString(this.stringValue);
        byteBuf.writeVarInt(this.intValue);
        byteBuf.writeFloatLE(this.floatValue);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
