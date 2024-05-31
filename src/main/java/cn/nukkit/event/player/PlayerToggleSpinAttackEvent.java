package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @author GoodLucky777
 */
public class PlayerToggleSpinAttackEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final boolean isSpinAttacking;
    /**
     * @deprecated 
     */
    

    public PlayerToggleSpinAttackEvent(Player player, boolean isSpinAttacking) {
        this.player = player;
        this.isSpinAttacking = isSpinAttacking;
    }
    /**
     * @deprecated 
     */
    

    public boolean isSpinAttacking() {
        return this.isSpinAttacking;
    }
}
