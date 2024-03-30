package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString(exclude = "namedTag")
@NoArgsConstructor
@AllArgsConstructor
public class BlockEntityDataPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.BLOCK_ENTITY_DATA_PACKET;

    public int x;
    public int y;
    public int z;
    public CompoundTag namedTag;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        BlockVector3 v = byteBuf.readBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        try (ByteBufInputStream is = new ByteBufInputStream(byteBuf)) {
            this.namedTag = NBTIO.read(is, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
        try {
            byteBuf.writeBytes(NBTIO.write(namedTag, ByteOrder.LITTLE_ENDIAN, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
