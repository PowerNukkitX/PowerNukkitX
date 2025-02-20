package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddPaintingPacket extends DataPacket {
    public long entityUniqueId;
    public long entityRuntimeId;
    public float x;
    public float y;
    public float z;
    public int direction;
    public String title;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityUniqueId(this.entityUniqueId);
        byteBuf.writeEntityRuntimeId(this.entityRuntimeId);

        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVarInt(this.direction);
        byteBuf.writeString(this.title);
    }

    @Override
    public int pid() {
        return ProtocolInfo.ADD_PAINTING_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
