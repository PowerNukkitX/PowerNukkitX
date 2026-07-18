package org.powernukkitx.event.potion;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

import java.util.List;

/**
 * @author Snake1999
 * @since 2016/1/12
 */
public class PotionApplyEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity entity;
    private List<Effect> applyEffects;

    public PotionApplyEvent(PotionType potion, List<Effect> applyEffects, Entity entity) {
        super(potion);
        this.applyEffects = applyEffects;
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public List<Effect> getApplyEffects() {
        return applyEffects;
    }

    public void setApplyEffect(List<Effect> applyEffects) {
        this.applyEffects = applyEffects;
    }
}
