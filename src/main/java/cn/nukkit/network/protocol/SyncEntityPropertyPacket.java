package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;


@ToString
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SyncEntityPropertyPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SYNC_ENTITY_PROPERTY_PACKET;

    public CompoundTag data;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        try (ByteBufInputStream stream = new ByteBufInputStream(byteBuf)) {
            this.data = NBTIO.read(stream, ByteOrder.BIG_ENDIAN, true);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        try {
            byteBuf.writeBytes(NBTIO.write(data, ByteOrder.BIG_ENDIAN, true));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
