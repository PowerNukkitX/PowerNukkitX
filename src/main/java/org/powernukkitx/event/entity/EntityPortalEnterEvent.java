package org.powernukkitx.event.entity;

import org.jetbrains.annotations.Nullable;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.Level;

public class EntityPortalEnterEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final PortalType type;
    private Level destinationLevel;

    public EntityPortalEnterEvent(Entity entity, PortalType type) {
        this.entity = entity;
        this.type = type;

        Level level = entity == null ? null : entity.getLevel();
        if (level != null) {
            if (type == PortalType.NETHER) {
                this.destinationLevel = level.getNetherPortalDestination();
            } else if (type == PortalType.END) {
                this.destinationLevel = level.getEndPortalDestination();
            }
        }
    }

    public PortalType getPortalType() {
        return type;
    }

    /**
     * Returns the destination level selected for this portal transition.
     *
     * @return destination level, or {@code null} when no destination is available
     */
    public @Nullable Level getDestinationLevel() {
        return destinationLevel;
    }

    /**
     * Sets the destination level for this portal transition.
     *
     * @param destinationLevel destination level, or {@code null} to disable the transition
     */
    public void setDestinationLevel(@Nullable Level destinationLevel) {
        this.destinationLevel = destinationLevel;
    }

    public enum PortalType {
        NETHER,
        END
    }
}
