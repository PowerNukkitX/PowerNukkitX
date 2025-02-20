package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.handler.codec.EncoderException;
import lombok.*;

import java.io.IOException;
import java.nio.ByteOrder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LevelEventGenericPacket extends DataPacket {
    public int eventId;
    public CompoundTag tag;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(eventId);
        try {
            byteBuf.writeBytes(NBTIO.writeValue(tag, ByteOrder.LITTLE_ENDIAN, true));
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.LEVEL_EVENT_GENERIC_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
