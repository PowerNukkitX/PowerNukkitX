package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * Deprecated since v594
 */
@Deprecated(since = "1.20.10-r1")
@ToString
public class ScriptCustomEventPacket extends DataPacket {

    public String eventName;
    public byte[] eventData;

    @Override
    public int pid() {
        return ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.eventName = byteBuf.readString();
        this.eventData = byteBuf.readByteArray();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeString(this.eventName);
        byteBuf.writeByteArray(this.eventData);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
