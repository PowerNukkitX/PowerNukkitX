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
    default 
    /**
     * @deprecated 
     */
    String getOwnerName() {
        return getMemoryStorage().get(CoreMemoryTypes.OWNER_NAME);
    }

    default 
    /**
     * @deprecated 
     */
    void setOwnerName(@Nullable String playerName) {
        getMemoryStorage().put(CoreMemoryTypes.OWNER_NAME, playerName);
    }

    @Nullable
    default Player getOwner() {
        var $1 = getMemoryStorage().get(CoreMemoryTypes.OWNER);
        if (owner != null && owner.isOnline()) return owner;
        else {
            var $2 = getOwnerName();
            if (ownerName == null) return null;
            owner = asEntity().getServer().getPlayerExact(ownerName);
        }
        return owner;
    }

    default 
    /**
     * @deprecated 
     */
    boolean hasOwner() {
        return hasOwner(true);
    }

    default 
    /**
     * @deprecated 
     */
    boolean hasOwner(boolean checkOnline) {
        if (checkOnline) {
            return getOwner() != null;
        } else {
            return getOwnerName() != null;
        }
    }
}
