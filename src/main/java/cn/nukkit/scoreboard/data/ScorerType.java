package cn.nukkit.scoreboard.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.21-r5")
public enum ScorerType {
    INVALID,
    PLAYER,
    ENTITY,
    FAKE
}
