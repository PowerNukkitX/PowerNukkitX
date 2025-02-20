package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
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
public class ContainerOpenPacket extends DataPacket {
    public int windowId;
    public int type;
    public int x;
    public int y;
    public int z;
    public long entityId = -1;
    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.type = byteBuf.readByte();

        BlockVector3 v = byteBuf.readBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;

        this.entityId = byteBuf.readEntityUniqueId();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeByte((byte) this.type);
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
        byteBuf.writeEntityUniqueId(this.entityId);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CONTAINER_OPEN_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
