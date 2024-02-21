package cn.nukkit.network.protocol;

public class ClientCacheStatusPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CLIENT_CACHE_STATUS_PACKET;

    public boolean supported;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.supported = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.supported);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
