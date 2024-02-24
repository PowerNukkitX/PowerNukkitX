package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;


@ToString
public class AnvilDamagePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.ANVIL_DAMAGE_PACKET;
    public int damage;
    public int x;
    public int y;
    public int z;

    @Override
    public int pid() {
        return ProtocolInfo.ANVIL_DAMAGE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.damage = byteBuf.readByte();
        BlockVector3 vec = byteBuf.readBlockVector3();
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
