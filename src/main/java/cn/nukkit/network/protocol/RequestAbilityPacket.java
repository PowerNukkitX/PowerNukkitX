package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.types.AbilityType;
import cn.nukkit.network.protocol.types.PlayerAbility;
import lombok.ToString;

@Since("1.19.30-r1")
@PowerNukkitXOnly
@ToString
public class RequestAbilityPacket extends DataPacket {
    public static final PlayerAbility[] ABILITIES = UpdateAbilitiesPacket.VALID_FLAGS;
    public static final AbilityType[] ABILITY_TYPES = AbilityType.values();

    public PlayerAbility ability;
    public AbilityType type;
    public boolean boolValue;
    public float floatValue;

    @Override
    public void decode() {
        this.ability = ABILITIES[this.getVarInt()];
        this.type = ABILITY_TYPES[this.getByte()];
        this.boolValue = this.getBoolean();
        this.floatValue = this.getLFloat();
    }

    @Override
    public void encode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte pid() {
        return ProtocolInfo.REQUEST_ABILITY_PACKET;
    }
}
