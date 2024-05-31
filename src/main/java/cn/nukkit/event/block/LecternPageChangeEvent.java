package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;


public class LecternPageChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final BlockEntityLectern lectern;
    private int newRawPage;
    /**
     * @deprecated 
     */
    

    public LecternPageChangeEvent(Player player, BlockEntityLectern lectern, int newPage) {
        super(lectern.getBlock());
        this.player = player;
        this.lectern = lectern;
        this.newRawPage = newPage;
    }

    public BlockEntityLectern getLectern() {
        return lectern;
    }
    /**
     * @deprecated 
     */
    

    public int getLeftPage() {
        return (newRawPage * 2) + 1;
    }
    /**
     * @deprecated 
     */
    

    public int getRightPage() {
        return getLeftPage() + 1;
    }
    /**
     * @deprecated 
     */
    

    public void setLeftPage(int newLeftPage) {
        this.newRawPage = (newLeftPage - 1) / 2;
    }
    /**
     * @deprecated 
     */
    

    public void setRightPage(int newRightPage) {
        this.setLeftPage(newRightPage - 1);
    }
    /**
     * @deprecated 
     */
    

    public int getNewRawPage() {
        return newRawPage;
    }
    /**
     * @deprecated 
     */
    

    public void setNewRawPage(int newRawPage) {
        this.newRawPage = newRawPage;
    }
    /**
     * @deprecated 
     */
    

    public int getMaxPage() {
        return lectern.getTotalPages();
    }

    public Player getPlayer() {
        return player;
    }
}
