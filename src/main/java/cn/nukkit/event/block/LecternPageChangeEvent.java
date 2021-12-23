package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitOnly
public class LecternPageChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BlockEntityLectern lectern;
    private int newRawPage;

    @PowerNukkitOnly
    public LecternPageChangeEvent(Player player, BlockEntityLectern lectern, int newPage) {
        super(lectern.getBlock());
        this.player = player;
        this.lectern = lectern;
        this.newRawPage = newPage;
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
    public int getLeftPage() {
        return (newRawPage * 2) + 1;
    }

    @PowerNukkitOnly
    public int getRightPage() {
        return getLeftPage() + 1;
    }

    @PowerNukkitOnly
    public void setLeftPage(int newLeftPage) {
        this.newRawPage = (newLeftPage - 1) / 2;
    }

    @PowerNukkitOnly
    public void setRightPage(int newRightPage) {
        this.setLeftPage(newRightPage - 1);
    }

    @PowerNukkitOnly
    public int getNewRawPage() {
        return newRawPage;
    }

    @PowerNukkitOnly
    public void setNewRawPage(int newRawPage) {
        this.newRawPage = newRawPage;
    }

    @PowerNukkitOnly
    public int getMaxPage() {
        return lectern.getTotalPages();
    }

    @PowerNukkitOnly
    public Player getPlayer() {
        return player;
    }
}
