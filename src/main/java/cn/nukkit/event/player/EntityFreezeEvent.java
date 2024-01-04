package cn.nukkit.event.player;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityEvent;
import lombok.Getter;


public class EntityFreezeEvent extends EntityEvent implements Cancellable {
    private final Entity entity;

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public EntityFreezeEvent(Entity human) {
        this.entity = human;
    }
}
