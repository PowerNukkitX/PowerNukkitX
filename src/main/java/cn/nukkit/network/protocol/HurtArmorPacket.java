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
public class HurtArmorPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.HURT_ARMOR_PACKET;


    public int cause;


    public int damage;


    public long armorSlots;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.cause = byteBuf.readVarInt();
        this.damage = byteBuf.readVarInt();
        this.armorSlots = byteBuf.readUnsignedVarLong();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(this.cause);
        byteBuf.writeVarInt(this.damage);
        byteBuf.writeUnsignedVarLong(this.armorSlots);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
