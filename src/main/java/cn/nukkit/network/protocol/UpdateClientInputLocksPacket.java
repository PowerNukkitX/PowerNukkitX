package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true)
public class UpdateClientInputLocksPacket extends DataPacket {
    private int lockComponentData;
    private Vector3f serverPosition;


    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_CLIENT_INPUT_LOCKS;
    }

    @Override
    public void decode() {
        this.lockComponentData = this.getVarInt();
        this.serverPosition = this.getVector3f();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(lockComponentData);
        this.putVector3f(serverPosition);
    }
}
