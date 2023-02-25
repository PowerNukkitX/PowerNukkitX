package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.LongEntityData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
@Deprecated
@DeprecationDetails(since = "1.19.30-r1", reason = "统一接口定义", replaceWith = "replace to EntityTamable")
public interface EntityOwnable extends EntityComponent {
    @Nullable
    default String getOwnerName() {
        return getMemoryStorage().get(CoreMemoryTypes.OWNER_NAME);
    }

    default void setOwnerName(@NotNull String playerName) {
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
            if (owner != null)
                asEntity().setDataProperty(new LongEntityData(Entity.DATA_OWNER_EID, owner.getId()));
        }
        return owner;
    }
}
