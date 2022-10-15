package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.item.Item;

@PowerNukkitXOnly
@Since("1.19.31-r1")
public class PlayerChangeArmorStandEvent extends PlayerEvent implements Cancellable {
    private final Entity armorStand;
    private Item item;
    private final int slot;

    public PlayerChangeArmorStandEvent(Player player, Entity armorStand, Item item, int slot) {
        this.player = player;
        this.armorStand = armorStand;
        this.item = item;
        this.slot = slot;
    }

    public Entity getArmorStand() {
        return armorStand;
    }

    public int getSlot() {
        return slot;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
