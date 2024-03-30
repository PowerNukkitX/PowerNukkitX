package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.handler.codec.EncoderException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteOrder;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LevelEventGenericPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.LEVEL_EVENT_GENERIC_PACKET;

    public int eventId;
    public CompoundTag tag;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
