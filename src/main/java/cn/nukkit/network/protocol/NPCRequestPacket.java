package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NPCRequestPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.NPC_REQUEST_PACKET;
    public long entityRuntimeId;
    public RequestType requestType = RequestType.SET_SKIN;
    public String data = "";
    public int skinType = 0;
    public String sceneName = "";

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
    public int pid() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.entityRuntimeId = byteBuf.readEntityRuntimeId();
        this.requestType = RequestType.values()[byteBuf.readByte()];
        this.data = byteBuf.readString();
        this.skinType = byteBuf.readByte();
        this.sceneName = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.entityRuntimeId);
        byteBuf.writeByte((byte) requestType.ordinal());
        byteBuf.writeString(this.data);
        byteBuf.writeByte((byte) this.skinType);
        byteBuf.writeString(this.sceneName);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
