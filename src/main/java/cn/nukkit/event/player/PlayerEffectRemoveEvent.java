package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;

@PowerNukkitXOnly
public class PlayerEffectRemoveEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private Effect removeEffect;

    public PlayerEffectRemoveEvent(Player player, Effect effect) {
        this.player = player;
        this.removeEffect = effect;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Effect getRemoveEffect() {
        return removeEffect;
    }
}
