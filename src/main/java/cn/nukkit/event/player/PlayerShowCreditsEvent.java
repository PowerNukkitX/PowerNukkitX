package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

/**
 * @author GoodLucky777
 */
public class PlayerShowCreditsEvent extends PlayerEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public PlayerShowCreditsEvent(Player player) {
        this.player = player;
    }
}
