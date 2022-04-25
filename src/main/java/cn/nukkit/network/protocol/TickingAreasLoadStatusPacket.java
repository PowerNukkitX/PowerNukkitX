package cn.nukkit.network.protocol;

public class TickingAreasLoadStatusPacket extends DataPacket {
    boolean waitingForPreload;

    @Override
    public byte pid() {
        return ProtocolInfo.TICKING_AREAS_LOAD_STATUS_PACKET;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.putBoolean(this.waitingForPreload);
    }
}
