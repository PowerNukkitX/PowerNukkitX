package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.ClientInputLocksFlag;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@Builder
@Getter
@Setter
@ToString(doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientInputLocksPacket extends DataPacket {

    private int lockComponentData;
    public Vector3f serverPosition;

    public void setFlags(Set<ClientInputLocksFlag> flags) {
        this.lockComponentData = ClientInputLocksFlag.toBitSet(flags);
    }

    public Set<ClientInputLocksFlag> getActiveFlags() {
        return ClientInputLocksFlag.fromBitSet(this.lockComponentData);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.lockComponentData = byteBuf.readUnsignedVarInt();
        this.serverPosition = byteBuf.readVector3f();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(lockComponentData);
        byteBuf.writeVector3f(serverPosition);
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_CLIENT_INPUT_LOCKS;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}