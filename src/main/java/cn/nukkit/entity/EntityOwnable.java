package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.entity.component.impl.EntityTameComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
@Deprecated
@DeprecationDetails(since = "1.19.30-r1", reason = "统一接口定义", replaceWith = "replace to EntityTamable")
public interface EntityOwnable {
    default String getOwnerName() {
        return getTameComponent().getOwnerName();
    }

    default void setOwnerName(@Nonnull String playerName) {
        getTameComponent().setOwnerName(playerName);
    }

    @Nullable
    default Player getOwner() {
        return getTameComponent().getOwner();
    }

    default EntityTameComponent getTameComponent() {
        return ((Entity) this).getComponentGroup().getComponent(EntityTameComponent.class);
    }
}
