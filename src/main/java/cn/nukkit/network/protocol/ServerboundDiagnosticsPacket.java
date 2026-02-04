package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.MemoryCategoryCounter;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerboundDiagnosticsPacket extends DataPacket {
    public float avgFps;
    public float avgServerSimTickTimeMS;
    public float avgClientSimTickTimeMS;
    public float avgBeginFrameTimeMS;
    public float avgInputTimeMS;
    public float avgRenderTimeMS;
    public float avgEndFrameTimeMS;
    public float avgRemainderTimePercent;
    public float avgUnaccountedTimePercent;
    public List<MemoryCategoryCounter> memoryCategoryValues = new ArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.avgFps = byteBuf.readFloatLE();
        this.avgServerSimTickTimeMS = byteBuf.readFloatLE();
        this.avgClientSimTickTimeMS = byteBuf.readFloatLE();
        this.avgBeginFrameTimeMS = byteBuf.readFloatLE();
        this.avgInputTimeMS = byteBuf.readFloatLE();
        this.avgRenderTimeMS = byteBuf.readFloatLE();
        this.avgEndFrameTimeMS = byteBuf.readFloatLE();
        this.avgRemainderTimePercent = byteBuf.readFloatLE();
        this.avgUnaccountedTimePercent = byteBuf.readFloatLE();

        memoryCategoryValues = Arrays.stream(byteBuf.readArray(MemoryCategoryCounter.class, (buf) ->
                new MemoryCategoryCounter(MemoryCategoryCounter.Category.values()[buf.readUnsignedByte()], byteBuf.readLongLE())
        )).toList();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.SERVERBOUND_DIAGNOSTICS_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
