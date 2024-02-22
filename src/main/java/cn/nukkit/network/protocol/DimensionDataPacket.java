package cn.nukkit.network.protocol;


public class DimensionDataPacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.DIMENSION_DATA_PACKET;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {

    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
