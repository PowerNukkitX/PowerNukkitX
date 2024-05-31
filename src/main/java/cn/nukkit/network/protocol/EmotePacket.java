package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmotePacket extends DataPacket {
    public static final int $1 = ProtocolInfo.EMOTE_PACKET;


    public long runtimeId;

    public String $2 = "";

    public String $3 = "";

    public String emoteID;

    public byte flags;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.runtimeId = byteBuf.readEntityRuntimeId();
        this.emoteID = byteBuf.readString();
        this.xuid = byteBuf.readString();
        this.platformId = byteBuf.readString();
        this.flags = (byte) byteBuf.readByte();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeEntityRuntimeId(this.runtimeId);
        byteBuf.writeString(this.emoteID);
        byteBuf.writeString(this.xuid);
        byteBuf.writeString(this.platformId);
        byteBuf.writeByte(flags);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
