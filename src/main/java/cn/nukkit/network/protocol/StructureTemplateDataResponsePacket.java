package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.*;
import lombok.*;

import java.nio.ByteOrder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StructureTemplateDataResponsePacket extends DataPacket {
    public String name;
    public boolean success;
    public CompoundTag data;
    public StructureTemplateResponseType responseType;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        name = byteBuf.readString();
        success = byteBuf.readBoolean();

        if (success) {
            data = byteBuf.readTag();
        }

        responseType = StructureTemplateResponseType.values()[byteBuf.readByte()];
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(name);
        byteBuf.writeBoolean(success);
        if (success) {
            byteBuf.writeTag(data);
        }
        byteBuf.writeByte(responseType.ordinal());
    }

    @Override
    public int pid() {
        return ProtocolInfo.STRUCTURE_DATA_RESPONSE;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
