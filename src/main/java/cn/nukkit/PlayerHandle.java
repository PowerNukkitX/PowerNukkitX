package cn.nukkit;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;

/**
 * A PlayerHandle is used to access a player's protected data.
 */
@SuppressWarnings("ClassCanBeRecord")
@Since("1.19.80-r2")
@PowerNukkitXOnly
public final class PlayerHandle {
    public final @NotNull Player player;

    public PlayerHandle(@NotNull Player player) {
        this.player = player;
    }
}
