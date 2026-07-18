package org.powernukkitx.event.weather;

import org.powernukkitx.entity.weather.EntityLightningStrike;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.event.level.WeatherEvent;
import org.powernukkitx.level.Level;

/**
 * @author funcraft (Nukkit Project)
 */
public class LightningStrikeEvent extends WeatherEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityLightningStrike bolt;

    public LightningStrikeEvent(Level level, final EntityLightningStrike bolt) {
        super(level);
        this.bolt = bolt;
    }

    /**
     * Gets the bolt which is striking the earth.
     * @return lightning entity
     */
    public EntityLightningStrike getLightning() {
        return bolt;
    }

}
