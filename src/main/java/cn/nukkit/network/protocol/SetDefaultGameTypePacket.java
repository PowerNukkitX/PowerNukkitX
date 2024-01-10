package cn.nukkit.network.protocol;


public class SetDefaultGameTypePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET;

    public int gamemode;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.gamemode = getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.gamemode);
    }
}
