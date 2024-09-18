package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

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

    @Override
    public int pid() {
        return ProtocolInfo.SERVERBOUND_DIAGNOSTICS_PACKET;
    }

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
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        // server bound
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
