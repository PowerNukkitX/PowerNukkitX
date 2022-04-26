package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;

public class PlayerEffectUpdateEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private Effect updateEffect;

    public PlayerEffectUpdateEvent(Player player, Effect effect) {
        this.player = player;
        this.updateEffect = effect;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Effect getUpdateEffect() {
        return updateEffect;
    }
}
