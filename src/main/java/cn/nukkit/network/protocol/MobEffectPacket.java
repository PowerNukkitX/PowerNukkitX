package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class MobEffectPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.MOB_EFFECT_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public static final byte EVENT_ADD = 1;
    public static final byte EVENT_MODIFY = 2;
    public static final byte EVENT_REMOVE = 3;

    public long eid;
    public int eventId;
    public int effectId;
    public int amplifier = 0;
    public boolean particles = true;
    public int duration = 0;

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
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
