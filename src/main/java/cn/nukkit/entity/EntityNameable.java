package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * An entity which can be named by name tags.
 */

public interface EntityNameable {
    ("The Entity implementations are not PowerNukkit only")
    String getNameTag();

    ("The Entity implementations are not PowerNukkit only")
    void setNameTag(String nameTag);

    ("The Entity implementations are not PowerNukkit only")
    boolean isNameTagVisible();

    ("The Entity implementations are not PowerNukkit only")
    void setNameTagVisible(boolean visible);


    boolean isPersistent();


    void setPersistent(boolean persistent);

    ("The Entity implementations are not PowerNukkit only")
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


    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN",
            reason = "New implementation needs a player instance, using this method may allow players to name unexpected entities",
            by = "PowerNukkit", replaceWith = "playerApplyNameTag(Player, Item)")
    default boolean applyNameTag(Item item) {
        if (item.hasCustomName()) {
            this.setNameTag(item.getCustomName());
            this.setNameTagVisible(true);
            return true;
        } else {
            return false;
        }
    }
}
