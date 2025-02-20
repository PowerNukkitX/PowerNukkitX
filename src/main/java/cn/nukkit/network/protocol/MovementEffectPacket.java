package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.MovementEffectType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MovementEffectPacket extends DataPacket {
    public long targetRuntimeID;
    public MovementEffectType effectType;
    public int effectDuration;
    public long tick;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarLong(this.targetRuntimeID);
        byteBuf.writeUnsignedVarInt(this.effectType.getId());
        byteBuf.writeUnsignedVarInt(this.effectDuration);
        byteBuf.writeUnsignedVarLong(this.tick);
    }

    @Override
    public int pid() {
        return ProtocolInfo.MOVEMENT_EFFECT_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

}
