package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public class LecternPlaceBookEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BlockEntityLectern lectern;
    private Item book;

    @PowerNukkitOnly
    public LecternPlaceBookEvent(Player player, BlockEntityLectern lectern, Item book) {
        super(lectern.getBlock());
        this.player = player;
        this.lectern = lectern;
        this.book = book;
    }

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }

    @PowerNukkitOnly
    public BlockEntityLectern getLectern() {
        return lectern;
    }

    @PowerNukkitOnly
    public Player getPlayer() {
        return player;
    }

    @PowerNukkitOnly
    public Item getBook() {
        return book.clone();
    }

    @PowerNukkitOnly
    public void setBook(Item book) {
        this.book = book;
    }
}
