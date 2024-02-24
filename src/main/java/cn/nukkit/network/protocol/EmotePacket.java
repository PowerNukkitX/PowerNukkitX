package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class EmotePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.EMOTE_PACKET;


    public long runtimeId;

    public String xuid = "";

    public String platformId = "";

    public String emoteID;

    public byte flags;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.runtimeId = byteBuf.readEntityRuntimeId();
        this.emoteID = byteBuf.readString();
        this.xuid = byteBuf.readString();
        this.platformId = byteBuf.readString();
        this.flags = (byte) byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeEntityRuntimeId(this.runtimeId);
        byteBuf.writeString(this.emoteID);
        byteBuf.writeString(this.xuid);
        byteBuf.writeString(this.platformId);
        byteBuf.writeByte(flags);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
