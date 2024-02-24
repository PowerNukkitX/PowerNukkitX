package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class ContainerOpenPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CONTAINER_OPEN_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
