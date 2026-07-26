package org.powernukkitx.event.potion;

import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.event.Event;

/**
 * @author Snake1999
 * @since 2016/1/12
 */
public abstract class PotionEvent extends Event {

    private PotionType potion;

    public PotionEvent(PotionType potion) {
        this.potion = potion;
    }

    public PotionType getPotion() {
        return potion;
    }

    public void setPotion(PotionType potion) {
        this.potion = potion;
    }

}
