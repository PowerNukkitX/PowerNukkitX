package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;
import org.cloudburstmc.protocol.bedrock.data.BookEditAction;

public class PlayerEditBookEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item oldBook;
    private final BookEditAction action;
    private Item newBook;

    public PlayerEditBookEvent(Player player, Item oldBook, Item newBook, BookEditAction action) {
        this.player = player;
        this.oldBook = oldBook;
        this.newBook = newBook;
        this.action = action;
    }

    public BookEditAction getAction() {
        return this.action;
    }

    public Item getOldBook() {
        return this.oldBook;
    }

    public Item getNewBook() {
        return this.newBook;
    }

    public void setNewBook(Item book) {
        this.newBook = book;
    }
}
