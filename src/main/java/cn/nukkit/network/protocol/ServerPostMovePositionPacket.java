package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;

public class ServerPostMovePositionPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SERVER_POST_MOVE_POSITION;

    public Vector3f position;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.position = this.getVector3f();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVector3f(this.position);
    }
}
