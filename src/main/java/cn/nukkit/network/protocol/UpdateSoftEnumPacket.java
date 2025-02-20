package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSoftEnumPacket extends DataPacket {
    public List<String> values = List.of();
    public String name = "";
    public Type type = Type.SET;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(name);
        byteBuf.writeUnsignedVarInt(values.size());

        for (String value : values) {
            byteBuf.writeString(value);
        }
        byteBuf.writeByte((byte) type.ordinal());
    }

    public enum Type {
        ADD,
        REMOVE,
        SET
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
