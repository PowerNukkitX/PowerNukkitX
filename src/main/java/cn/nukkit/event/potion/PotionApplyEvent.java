package cn.nukkit.event.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

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
