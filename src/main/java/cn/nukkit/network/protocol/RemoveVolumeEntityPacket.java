package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;

public class RemoveVolumeEntityPacket extends DataPacket {


    public static final int NETWORK_ID = ProtocolInfo.REMOVE_VOLUME_ENTITY_PACKET;

    private long id;
    /**
     * @since v503
     */
    private int dimension;


    public RemoveVolumeEntityPacket() {
        // Does nothing
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        id = byteBuf.readUnsignedVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt((int) id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
