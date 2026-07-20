package org.powernukkitx.event.potion;

import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.entity.item.EntitySplashPotion;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

/**
 * @author Snake1999
 * @since 2016/1/12
 */
public class PotionCollideEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntitySplashPotion thrownPotion;

    public PotionCollideEvent(PotionType potion, EntitySplashPotion thrownPotion) {
        super(potion);
        this.thrownPotion = thrownPotion;
    }

    public EntitySplashPotion getThrownPotion() {
        return thrownPotion;
    }
}
