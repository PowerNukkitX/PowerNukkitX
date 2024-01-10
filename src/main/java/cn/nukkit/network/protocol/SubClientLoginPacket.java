package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SubClientLoginPacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.SUB_CLIENT_LOGIN_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }
}
