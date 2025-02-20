package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmotePacket extends DataPacket {
    public long runtimeId;
    public String xuid = "";
    public String platformId = "";
    public String emoteID;
    public byte flags;
    public int emoteDuration;

    @Override
    public int pid() {
        return ProtocolInfo.EMOTE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.runtimeId = byteBuf.readEntityRuntimeId();
        this.emoteID = byteBuf.readString();
        this.emoteDuration = byteBuf.readUnsignedVarInt();
        this.xuid = byteBuf.readString();
        this.platformId = byteBuf.readString();
        this.flags = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.runtimeId);
        byteBuf.writeString(this.emoteID);
        byteBuf.writeUnsignedVarInt(this.emoteDuration);
        byteBuf.writeString(this.xuid);
        byteBuf.writeString(this.platformId);
        byteBuf.writeByte(flags);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
