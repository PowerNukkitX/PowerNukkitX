package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

import javax.annotation.Nullable;

/**
 * 实现这个接口的实体可以被驯服
 *
 * @author BeYkeRYkt (Nukkit Project)
 */
public interface EntityOwnable extends EntityComponent {
    @Nullable
    default String getOwnerName() {
        return getMemoryStorage().get(CoreMemoryTypes.OWNER_NAME);
    }

    default void setOwnerName(@Nullable String playerName) {
        getMemoryStorage().put(CoreMemoryTypes.OWNER_NAME, playerName);
    }

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
}
