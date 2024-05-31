package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NPCRequestPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.NPC_REQUEST_PACKET;
    public long entityRuntimeId;
    public RequestType $2 = RequestType.SET_SKIN;
    public String $3 = "";
    public int $4 = 0;
    public String $5 = "";

    public enum RequestType {
        SET_ACTIONS,
        EXECUTE_ACTION,
        EXECUTE_CLOSING_COMMANDS,
        SET_NAME,
        SET_SKIN,
        SET_INTERACTION_TEXT,
        EXECUTE_OPENING_COMMANDS
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.entityRuntimeId = byteBuf.readEntityRuntimeId();
        this.requestType = RequestType.values()[byteBuf.readByte()];
        this.data = byteBuf.readString();
        this.skinType = byteBuf.readByte();
        this.sceneName = byteBuf.readString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.entityRuntimeId);
        byteBuf.writeByte((byte) requestType.ordinal());
        byteBuf.writeString(this.data);
        byteBuf.writeByte((byte) this.skinType);
        byteBuf.writeString(this.sceneName);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
