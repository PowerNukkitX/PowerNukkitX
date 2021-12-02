package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

@PowerNukkitOnly
public class ComposterEmptyEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private Item drop;
    private Item itemUsed;
    private int newLevel;
    private Vector3 motion;

    @PowerNukkitOnly
    public ComposterEmptyEvent(Block block, Player player, Item itemUsed, Item drop, int newLevel) {
        super(block);
        this.player = player;
        this.drop = drop;
        this.itemUsed = itemUsed;
        this.newLevel = Math.max(0, Math.min(newLevel, 8));
    }

    @PowerNukkitOnly
    public Player getPlayer() {
        return player;
    }

    @PowerNukkitOnly
    public Item getDrop() {
        return drop.clone();
    }

    @PowerNukkitOnly
    public void setDrop(Item drop) {
        if (drop == null) {
            drop = Item.get(Item.AIR);
        } else {
            drop = drop.clone();
        }
        this.drop = drop;
    }

    @PowerNukkitOnly
    public Item getItemUsed() {
        return itemUsed;
    }

    @PowerNukkitOnly
    public void setItemUsed(Item itemUsed) {
        this.itemUsed = itemUsed;
    }

    @PowerNukkitOnly
    public int getNewLevel() {
        return newLevel;
    }

    @PowerNukkitOnly
    public void setNewLevel(int newLevel) {
        this.newLevel = Math.max(0, Math.min(newLevel, 8));
    }

    @PowerNukkitOnly
    public Vector3 getMotion() {
        return motion;
    }

    @PowerNukkitOnly
    public void setMotion(Vector3 motion) {
        this.motion = motion;
    }

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }

}
