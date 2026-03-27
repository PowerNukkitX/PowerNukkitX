package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @deprecated Use {@link Entity#getComponentNameable()}, {@link Entity#isNameable()} and
 * {@link Entity#setNameTag(String)} instead.
 *
 * <p>
 * This interface is kept for backward compatibility only.
 * Naming (name tag) logic has been centralized in {@link Entity} to provide a
 * consistent API and interaction behavior across all entity types.
 * </p>
 *
 * <p>
 * Entity implementations that support name tags should override
 * {@link Entity#getComponentNameable()} (or {@link Entity#isNameable()}) instead of
 * implementing this interface.
 * 
 * Planned removal: after 6 months (>= 2026-08-19).
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public interface EntityNameable {
    /*
    String getNameTag();

    void setNameTag(String nameTag);

    boolean isNameTagVisible();

    void setNameTagVisible(boolean visible);


    boolean isPersistent();


    void setPersistent(boolean persistent);

    default boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (Objects.equals(item.getId(), Item.NAME_TAG)) {
            if (!player.isSpectator() && !(this instanceof Player)) {
                return playerApplyNameTag(player, item);
            }
        }
        return false;
    }

    default boolean playerApplyNameTag(@NotNull Player player, @NotNull Item item) {
        return playerApplyNameTag(player, item, true);
    }

    default boolean playerApplyNameTag(@NotNull Player player, @NotNull Item item, boolean consume) {
        if (item.hasCustomName()) {
            this.setNameTag(item.getCustomName());
            this.setNameTagVisible(true);

            if (consume && !player.isCreative()) {
                player.getInventory().removeItem(item);
            }
            // Set entity as persistent.
            return true;
        }
        return false;
    }
     */
}
