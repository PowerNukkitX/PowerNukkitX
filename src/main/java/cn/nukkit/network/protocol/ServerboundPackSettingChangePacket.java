package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

import java.util.UUID;

public class ServerboundPackSettingChangePacket extends DataPacket {
    private UUID packId;
    private byte dataType; // confirm type
    private String name;
    private boolean boolValue = false;
    private float floatValue = 0F;
    private String strValue = "";

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.packId = byteBuf.readUUID();
        this.dataType = byteBuf.readByte();
        this.name = byteBuf.readString();

        this.boolValue = byteBuf.readBoolean();
        this.floatValue = byteBuf.readFloat();
        this.strValue = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUUID(this.packId);
        byteBuf.writeByte(this.dataType);
        byteBuf.writeString(this.name);

        byteBuf.writeBoolean(this.boolValue);
        byteBuf.writeFloat(this.floatValue);
        byteBuf.writeString(this.strValue);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SERVERBOUND_PACK_SETTING_CHANGE_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {

    }
}
