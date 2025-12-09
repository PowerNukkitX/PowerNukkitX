package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.CommandOriginData;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import cn.nukkit.network.protocol.types.CommandOutputType;
import cn.nukkit.utils.OptionalValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommandOutputPacket extends DataPacket {
    public final List<CommandOutputMessage> messages = new ObjectArrayList<>();
    public CommandOriginData commandOriginData;
    public CommandOutputType type;
    public int successCount;
    public String data;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString("player");
        byteBuf.writeUUID(this.commandOriginData.uuid);
        byteBuf.writeString(this.commandOriginData.requestId);// unknown
        byteBuf.writeLongLE(this.commandOriginData.getVarLong().orElse(-1));// unknown
        byteBuf.writeString(type.getNetworkname());

        byteBuf.writeIntLE(this.successCount);
        byteBuf.writeUnsignedVarInt(messages.size());
        for (var msg : messages) {
            byteBuf.writeString(msg.getMessageId());
            byteBuf.writeBoolean(msg.isInternal());
            byteBuf.writeUnsignedVarInt(msg.getParameters().length);
            for (var param : msg.getParameters()) {
                byteBuf.writeString(param);
            }
        }
        byteBuf.writeNotNull(this.data, byteBuf::writeString);
    }

    @Override
    public int pid() {
        return ProtocolInfo.COMMAND_OUTPUT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
