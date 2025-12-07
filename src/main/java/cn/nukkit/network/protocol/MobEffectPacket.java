package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MobEffectPacket extends DataPacket {
    public static final byte EVENT_ADD = 1;
    public static final byte EVENT_MODIFY = 2;
    public static final byte EVENT_REMOVE = 3;
    public long eid;

    public int eventId;
    public int effectId;
    public int amplifier = 0;
    public boolean particles = true;
    public int duration = 0;
    /**
     * @since v662
     */
    public long tick;
    /**
     * @since v897
     */
    private boolean ambient;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.eid);
        byteBuf.writeByte((byte) this.eventId);
        byteBuf.writeVarInt(this.effectId);
        byteBuf.writeVarInt(this.amplifier);
        byteBuf.writeBoolean(this.particles);
        byteBuf.writeVarInt(this.duration);
        byteBuf.writeUnsignedVarLong(this.tick);
        byteBuf.writeBoolean(ambient);
    }

    @Override
    public int pid() {
        return ProtocolInfo.MOB_EFFECT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
