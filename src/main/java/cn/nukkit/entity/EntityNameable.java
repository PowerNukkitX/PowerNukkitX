package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * An entity which can be named by name tags.
 */

public interface EntityNameable {
    String getNameTag();

    void setNameTag(String nameTag);

    boolean isNameTagVisible();

    void setNameTagVisible(boolean visible);


    boolean isPersistent();


    void setPersistent(boolean persistent);

    default boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG) {
            if (!player.isSpectator()) {
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
}
