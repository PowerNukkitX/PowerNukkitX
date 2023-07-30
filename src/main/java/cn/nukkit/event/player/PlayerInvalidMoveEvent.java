package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.player.Player;

/**
 * call when a player moves wrongly
 *
 * @author WilliamGao
 */
public class PlayerInvalidMoveEvent extends PlayerEvent implements Cancellable {

    private boolean revert;

    public PlayerInvalidMoveEvent(Player player, boolean revert) {
        this.player = player;
        this.revert = revert;
    }

    public boolean isRevert() {
        return this.revert;
    }

    /**
     * @deprecated If you just simply want to disable the movement check, please use {@link Player#setCheckMovement(boolean)} instead.
     * @param revert revert movement
     */
    @Deprecated
    public void setRevert(boolean revert) {
        this.revert = revert;
    }
}
