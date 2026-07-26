package org.powernukkitx.event.block;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntityLectern;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;


public class LecternDropBookEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    private final Player player;
    private final BlockEntityLectern lectern;
    private Item book;

    public LecternDropBookEvent(Player player, BlockEntityLectern lectern, Item book) {
        super(lectern.getBlock());
        this.player = player;
        this.lectern = lectern;
        this.book = book;
    }

    public BlockEntityLectern getLectern() {
        return lectern;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getBook() {
        return book.clone();
    }

    public void setBook(Item book) {
        this.book = book;
    }
}
