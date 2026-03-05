package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

import javax.annotation.Nullable;

/**
 * @deprecated Since 2.0.0 (2026-02-19).
 * EntityOwnable handling was moved to {@link Entity}.
 * Use {@link Entity#getTameable()} and {@link Entity#isTameable()} for the core, others methods still exist in {@link Entity}.
 *
 * Planned removal: after 6 months (>= 2026-08-26).
 * @author BeYkeRYkt (Nukkit Project)
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public interface EntityOwnable extends EntityComponent {
    /*
    @Nullable
    default String getOwnerName() {
        return getMemoryStorage().get(CoreMemoryTypes.OWNER_NAME);
    }
     */

    /*
    default void setOwnerName(@Nullable String playerName) {
        getMemoryStorage().put(CoreMemoryTypes.OWNER_NAME, playerName);
    }
     */

    /*
    @Nullable
    default Player getOwner() {
        var owner = getMemoryStorage().get(CoreMemoryTypes.OWNER);
        if (owner != null && owner.isOnline()) return owner;
        else {
            var ownerName = getOwnerName();
            if (ownerName == null) return null;
            owner = asEntity().getServer().getPlayerExact(ownerName);
        }
        return owner;
    }

    default boolean hasOwner() {
        return hasOwner(true);
    }

    default boolean hasOwner(boolean checkOnline) {
        if (checkOnline) {
            return getOwner() != null;
        } else {
            return getOwnerName() != null;
        }
    }
     */
}
