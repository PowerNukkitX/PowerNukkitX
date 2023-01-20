package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.CommandOriginData;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import cn.nukkit.network.protocol.types.CommandOutputType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import java.util.List;

@ToString
public class CommandOutputPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.COMMAND_OUTPUT_PACKET;

    public final List<CommandOutputMessage> messages = new ObjectArrayList<>();
    public CommandOriginData commandOriginData;
    public CommandOutputType type;
    public int successCount;
    public String data;


    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        //non
    }

    @Override
    public void encode() {
        this.reset();
        putUnsignedVarInt(this.commandOriginData.type.ordinal());
        putUUID(this.commandOriginData.uuid);
        putString(this.commandOriginData.requestId);// unknown
        if (this.commandOriginData.type == CommandOriginData.Origin.DEV_CONSOLE || this.commandOriginData.type == CommandOriginData.Origin.TEST) {
            putVarLong(this.commandOriginData.getVarLong().orElse(-1));// unknown
        }

        putByte((byte) this.type.ordinal());
        putUnsignedVarInt(this.successCount);

        this.putUnsignedVarInt(messages.size());
        for (var msg : messages) {
            this.putBoolean(msg.isInternal());
            this.putString(msg.getMessageId());
            this.putUnsignedVarInt(msg.getParameters().length);
            for (var param : msg.getParameters()) {
                this.putString(param);
            }
        }
        if (this.type == CommandOutputType.DATA_SET) {
            putString(this.data);// unknown
        }
    }
}
