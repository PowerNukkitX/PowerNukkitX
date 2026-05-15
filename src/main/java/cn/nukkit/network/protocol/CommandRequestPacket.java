package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.CommandOriginData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommandRequestPacket extends DataPacket {

    public String command;
    public CommandOriginData data;
    public boolean internal;
    /**
     * @since v567
     */
    public String version;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.command = byteBuf.readString();
        byteBuf.readString(); // hardcoded "player"
        UUID uuid = byteBuf.readUUID();
        CommandOriginData.Origin type = CommandOriginData.Origin.PLAYER;
        String requestId = byteBuf.readString();
        Long varLong = byteBuf.readLongLE();
        this.data = new CommandOriginData(type, uuid, requestId, varLong);
        this.internal = byteBuf.readBoolean();
        this.version = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.COMMAND_REQUEST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
