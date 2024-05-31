package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.AbilityType;
import cn.nukkit.network.protocol.types.PlayerAbility;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestAbilityPacket extends DataPacket {
    public static final PlayerAbility[] ABILITIES = UpdateAbilitiesPacket.VALID_FLAGS;
    public static final AbilityType[] ABILITY_TYPES = AbilityType.values();

    public PlayerAbility ability;
    public AbilityType type;
    public boolean boolValue;
    public float floatValue;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.ability = ABILITIES[byteBuf.readVarInt()];
        this.type = ABILITY_TYPES[byteBuf.readByte()];
        this.boolValue = byteBuf.readBoolean();
        this.floatValue = byteBuf.readFloatLE();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.REQUEST_ABILITY_PACKET;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
