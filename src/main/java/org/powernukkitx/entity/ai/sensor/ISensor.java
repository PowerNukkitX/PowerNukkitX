package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.IMemoryStorage;

/**
 * This interface abstracts a sensor<br>
 * The sensor is used to collect environmental information and write a memory {@link org.powernukkitx.entity.ai.memory.MemoryType} to the memory storage {@link IMemoryStorage}
 */


public interface ISensor {

    /**
     * Refresh period for sensors that feed targeting. 1 keeps vanilla reaction time. Raising it
     * saves AI ticks but delays every reaction by that many gt.
     */
    int DEFAULT_TARGETING_PERIOD = 1;

    /**
     * @param entity the target entity
     */
    void sense(EntityIntelligent entity);

    /**
     * Returns the refresh period of this sensor, a small refresh period will make the sensor be called more frequently
     *
     * @return the refresh period
     */
    default int getPeriod() {
        return 1;
    }
}
