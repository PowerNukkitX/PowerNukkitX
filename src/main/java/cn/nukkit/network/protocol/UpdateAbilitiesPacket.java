package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.AbilityLayer;
import cn.nukkit.network.protocol.types.CommandPermission;
import cn.nukkit.network.protocol.types.PlayerAbility;
import cn.nukkit.network.protocol.types.PlayerPermission;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;


@ToString
public class UpdateAbilitiesPacket extends DataPacket {
    public static final PlayerAbility[] VALID_FLAGS = {
            PlayerAbility.BUILD,
            PlayerAbility.MINE,
            PlayerAbility.DOORS_AND_SWITCHES,
            PlayerAbility.OPEN_CONTAINERS,
            PlayerAbility.ATTACK_PLAYERS,
            PlayerAbility.ATTACK_MOBS,
            PlayerAbility.OPERATOR_COMMANDS,
            PlayerAbility.TELEPORT,
            PlayerAbility.INVULNERABLE,
            PlayerAbility.FLYING,
            PlayerAbility.MAY_FLY,
            PlayerAbility.INSTABUILD,
            PlayerAbility.LIGHTNING,
            PlayerAbility.FLY_SPEED,
            PlayerAbility.WALK_SPEED,
            PlayerAbility.MUTED,
            PlayerAbility.WORLD_BUILDER,
            PlayerAbility.NO_CLIP,
            PlayerAbility.PRIVILEGED_BUILDER
    };
    public static final EnumMap<PlayerAbility, Integer> FLAGS_TO_BITS = new EnumMap<>(PlayerAbility.class);

    static {
        for (int i = 0; i < VALID_FLAGS.length; i++) {
            FLAGS_TO_BITS.put(VALID_FLAGS[i], (1 << i));
        }
    }

    public long entityId;
    public PlayerPermission playerPermission;
    public CommandPermission commandPermission;
    public final List<AbilityLayer> abilityLayers = new ObjectArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeLongLE(this.entityId);
        byteBuf.writeUnsignedVarInt(this.playerPermission.ordinal());
        byteBuf.writeUnsignedVarInt(this.commandPermission.ordinal());
        byteBuf.writeArray(this.abilityLayers, this::writeAbilityLayer);
    }

    private void writeAbilityLayer(HandleByteBuf byteBuf, AbilityLayer abilityLayer) {
        byteBuf.writeShortLE(abilityLayer.getLayerType().ordinal());
        byteBuf.writeIntLE(getAbilitiesNumber(abilityLayer.getAbilitiesSet()));
        byteBuf.writeIntLE(getAbilitiesNumber(abilityLayer.getAbilityValues()));
        byteBuf.writeFloatLE(abilityLayer.getFlySpeed());
        byteBuf.writeFloatLE(abilityLayer.getWalkSpeed());
    }

    private static int getAbilitiesNumber(Set<PlayerAbility> abilities) {
        int number = 0;
        for (PlayerAbility ability : abilities) {
            number |= FLAGS_TO_BITS.getOrDefault(ability, 0);
        }
        return number;
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_ABILITIES_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
