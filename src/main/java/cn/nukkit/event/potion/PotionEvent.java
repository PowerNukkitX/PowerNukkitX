package cn.nukkit.event.potion;

import cn.nukkit.event.Event;
import cn.nukkit.entity.effect.PotionType;

/**
 * @author Snake1999
 * @since 2016/1/12
 */
public abstract class PotionEvent extends Event {

    private PotionType potion;
    /**
     * @deprecated 
     */
    

    public PotionEvent(PotionType potion) {
        this.potion = potion;
    }

    public PotionType getPotion() {
        return potion;
    }
    /**
     * @deprecated 
     */
    

    public void setPotion(PotionType potion) {
        this.potion = potion;
    }

}
