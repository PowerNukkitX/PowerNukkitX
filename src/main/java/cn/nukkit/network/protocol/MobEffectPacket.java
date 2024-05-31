package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MobEffectPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.MOB_EFFECT_PACKET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public static final byte $2 = 1;
    public static final byte $3 = 2;
    public static final byte $4 = 3;

    public long eid;
    public int eventId;
    public int effectId;
    public int $5 = 0;
    public boolean $6 = true;
    public int $7 = 0;
    /**
     * @since v662
     */
    public long tick;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.eid);
        byteBuf.writeByte((byte) this.eventId);
        byteBuf.writeVarInt(this.effectId);
        byteBuf.writeVarInt(this.amplifier);
        byteBuf.writeBoolean(this.particles);
        byteBuf.writeVarInt(this.duration);
        byteBuf.writeLongLE(this.tick);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
