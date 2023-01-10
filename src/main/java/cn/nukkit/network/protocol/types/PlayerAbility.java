package cn.nukkit.network.protocol.types;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.Arrays;
import java.util.List;

@Since("1.19.30-r1")
@PowerNukkitXOnly
public enum PlayerAbility {
    BUILD(0B0000_0001),
    MINE(0B0000_0010),
    DOORS_AND_SWITCHES(0B0000_0100),
    OPEN_CONTAINERS(0B0000_1000),
    ATTACK_PLAYERS(0B0001_0000),
    ATTACK_MOBS(0B0010_0000),
    OPERATOR_COMMANDS(0B0100_0000),
    TELEPORT(0B1000_0000),

    INVULNERABLE,
    FLYING,
    MAY_FLY,
    INSTABUILD,
    LIGHTNING,
    FLY_SPEED,
    WALK_SPEED,
    MUTED,
    WORLD_BUILDER,
    NO_CLIP;

    public static final List<PlayerAbility> VALUES = Arrays.asList(values());

    //用于RequestPermissionsPacket的特征位
    public final int bit;
    PlayerAbility() {
        this(0);
    }

    PlayerAbility(int bit) {
        this.bit = bit;
    }
}
