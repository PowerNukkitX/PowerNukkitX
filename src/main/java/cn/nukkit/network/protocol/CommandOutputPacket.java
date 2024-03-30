package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.CommandOriginData;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import cn.nukkit.network.protocol.types.CommandOutputType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommandOutputPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.COMMAND_OUTPUT_PACKET;

    public final List<CommandOutputMessage> messages = new ObjectArrayList<>();
    public CommandOriginData commandOriginData;
    public CommandOutputType type;
    public int successCount;
    public String data;


    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        //non
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUnsignedVarInt(this.commandOriginData.type.ordinal());
        byteBuf.writeUUID(this.commandOriginData.uuid);
        byteBuf.writeString(this.commandOriginData.requestId);// unknown
        if (this.commandOriginData.type == CommandOriginData.Origin.DEV_CONSOLE || this.commandOriginData.type == CommandOriginData.Origin.TEST) {
            byteBuf.writeVarLong(this.commandOriginData.getVarLong().orElse(-1));// unknown
        }

        byteBuf.writeByte((byte) this.type.ordinal());
        byteBuf.writeUnsignedVarInt(this.successCount);

        byteBuf.writeUnsignedVarInt(messages.size());
        for (var msg : messages) {
            byteBuf.writeBoolean(msg.isInternal());
            byteBuf.writeString(msg.getMessageId());
            byteBuf.writeUnsignedVarInt(msg.getParameters().length);
            for (var param : msg.getParameters()) {
                byteBuf.writeString(param);
            }
        }
        if (this.type == CommandOutputType.DATA_SET) {
            byteBuf.writeString(this.data);// unknown
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
