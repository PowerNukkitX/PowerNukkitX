package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ServerToClientHandshakePacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.SERVER_TO_CLIENT_HANDSHAKE_PACKET;
    }

    public String jwt;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();

        this.putString(this.jwt);
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
