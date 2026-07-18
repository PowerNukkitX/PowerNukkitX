package org.powernukkitx.event.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;


public class ComposterFillEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final Item item;
    private final int chance;
    private boolean success;

    public ComposterFillEvent(Block block, Player player, Item item, int chance, boolean success) {
        super(block);
        this.player = player;
        this.item = item;
        this.chance = chance;
        this.success = success;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public int getChance() {
        return chance;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
