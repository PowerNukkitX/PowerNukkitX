package org.powernukkitx.event.block;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntityLectern;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;


public class LecternPageChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final BlockEntityLectern lectern;
    private int newRawPage;

    public LecternPageChangeEvent(Player player, BlockEntityLectern lectern, int newPage) {
        super(lectern.getBlock());
        this.player = player;
        this.lectern = lectern;
        this.newRawPage = newPage;
    }

    public BlockEntityLectern getLectern() {
        return lectern;
    }

    public int getLeftPage() {
        return (newRawPage * 2) + 1;
    }

    public int getRightPage() {
        return getLeftPage() + 1;
    }

    public void setLeftPage(int newLeftPage) {
        this.newRawPage = (newLeftPage - 1) / 2;
    }

    public void setRightPage(int newRightPage) {
        this.setLeftPage(newRightPage - 1);
    }

    public int getNewRawPage() {
        return newRawPage;
    }

    public void setNewRawPage(int newRawPage) {
        this.newRawPage = newRawPage;
    }

    public int getMaxPage() {
        return lectern.getTotalPages();
    }

    public Player getPlayer() {
        return player;
    }
}
