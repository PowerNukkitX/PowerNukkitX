package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class EntityTransformEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity transformed;

    public EntityTransformEvent(@NotNull Entity entity, @NotNull Entity transformed) {
        this.entity = entity;
        this.transformed = transformed;
    }
}
