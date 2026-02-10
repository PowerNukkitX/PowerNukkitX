package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientboundTextureShiftPacket extends DataPacket {
    public static final int ACTION_INVALID = 0;
    public static final int ACTION_INITIALIZE = 1;
    public static final int ACTION_START = 2;
    public static final int ACTION_SET_ENABLED = 3;
    public static final int ACTION_SYNC = 4;

    public int action = ACTION_INVALID;
    public String collectionName = "";
    public String fromStep = "";
    public String toStep = "";
    public String[] allSteps = new String[0];
    public long currentLengthInTicks;
    public long totalLengthInTicks;
    public boolean enabled;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        action = byteBuf.readByte();
        collectionName = byteBuf.readString();
        fromStep = byteBuf.readString();
        toStep = byteBuf.readString();
        int count = (int) byteBuf.readUnsignedVarInt();
        allSteps = new String[count];
        for (int i = 0; i < count; i++) {
            allSteps[i] = byteBuf.readString();
        }
        currentLengthInTicks = byteBuf.readUnsignedVarLong();
        totalLengthInTicks = byteBuf.readUnsignedVarLong();
        enabled = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) action);
        byteBuf.writeString(collectionName);
        byteBuf.writeString(fromStep);
        byteBuf.writeString(toStep);
        byteBuf.writeUnsignedVarInt(allSteps.length);
        for (String step : allSteps) {
            byteBuf.writeString(step);
        }
        byteBuf.writeUnsignedVarLong(currentLengthInTicks);
        byteBuf.writeUnsignedVarLong(totalLengthInTicks);
        byteBuf.writeBoolean(enabled);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_TEXTURE_SHIFT;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
