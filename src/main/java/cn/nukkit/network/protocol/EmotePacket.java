package cn.nukkit.network.protocol;

import cn.nukkit.command.selector.args.impl.M;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.VarInt;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmotePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.EMOTE_PACKET;

    public long runtimeId;

    public String xuid = "";

    public String platformId = "";

    public String emoteID;

    public byte flags;

    public int emoteLengthTicks;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.runtimeId = byteBuf.readEntityRuntimeId();
        this.emoteID = byteBuf.readString();
        this.emoteLengthTicks= byteBuf.readUnsignedVarInt();
        this.xuid = byteBuf.readString();
        this.platformId = byteBuf.readString();
        this.flags = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.runtimeId);
        byteBuf.writeString(this.emoteID);
        byteBuf.writeUnsignedVarInt(this.emoteLengthTicks);
        byteBuf.writeString(this.xuid);
        byteBuf.writeString(this.platformId);
        byteBuf.writeByte(flags);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
