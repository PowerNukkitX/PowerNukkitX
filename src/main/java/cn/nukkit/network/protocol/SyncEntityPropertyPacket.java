package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.nio.ByteOrder;


public class SyncEntityPropertyPacket extends DataPacket {


    public static final int NETWORK_ID = ProtocolInfo.SYNC_ENTITY_PROPERTY_PACKET;

    private CompoundTag data;


    public SyncEntityPropertyPacket() {
        // Does nothing
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        try (ByteBufInputStream stream = new ByteBufInputStream(byteBuf)){
            this.data = NBTIO.read(stream, ByteOrder.BIG_ENDIAN, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf){
        
        try {
            byteBuf.writeBytes(NBTIO.write(data, ByteOrder.BIG_ENDIAN, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CompoundTag getData() {
        return data;
    }

    public void setData(CompoundTag data) {
        this.data = data;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
