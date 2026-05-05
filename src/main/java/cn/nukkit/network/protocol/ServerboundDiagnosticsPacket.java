package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.EntityDiagnosticTimingInfo;
import cn.nukkit.network.protocol.types.MemoryCategoryCounter;
import cn.nukkit.network.protocol.types.SystemDiagnosticTimingInfo;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
    public List<EntityDiagnosticTimingInfo> entityDiagnostics = new ObjectArrayList<>();
    public List<SystemDiagnosticTimingInfo> systemDiagnostics = new ObjectArrayList<>();

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
        byteBuf.readArray(this.entityDiagnostics, this::readEntityDiagnostics);
        byteBuf.readArray(this.systemDiagnostics, this::readSystemDiagnostics);
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

    protected EntityDiagnosticTimingInfo readEntityDiagnostics(HandleByteBuf buffer) {
        final EntityDiagnosticTimingInfo entityDiagnostics = new EntityDiagnosticTimingInfo();
        entityDiagnostics.setDisplayName(buffer.readString());
        entityDiagnostics.setEntity(buffer.readString());
        entityDiagnostics.setTimeInNS(buffer.readLongLE());
        entityDiagnostics.setPercentOfTotal(buffer.readUnsignedByte());
        return entityDiagnostics;
    }

    protected SystemDiagnosticTimingInfo readSystemDiagnostics(HandleByteBuf buffer) {
        final SystemDiagnosticTimingInfo systemDiagnostics = new SystemDiagnosticTimingInfo();
        systemDiagnostics.setDisplayName(buffer.readString());
        systemDiagnostics.setSystemIndex(buffer.readLongLE());
        systemDiagnostics.setTimeInNS(buffer.readLongLE());
        systemDiagnostics.setPercentOfTotal(buffer.readUnsignedByte());
        return systemDiagnostics;
    }
}
