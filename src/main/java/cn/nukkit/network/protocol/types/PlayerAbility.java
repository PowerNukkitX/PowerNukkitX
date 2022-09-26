package cn.nukkit.network.protocol.types;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.Arrays;
import java.util.List;

@Since("1.19.30-r1")
@PowerNukkitXOnly
public enum PlayerAbility {
    BUILD,
    MINE,
    DOORS_AND_SWITCHES,
    OPEN_CONTAINERS,
    ATTACK_PLAYERS,
    ATTACK_MOBS,
    OPERATOR_COMMANDS,
    TELEPORT,
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
}
