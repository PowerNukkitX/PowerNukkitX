package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.IMemoryStorage;

/**
 * This interface abstracts a sensor<br>
 * The sensor is used to collect environmental information and write a memory {@link org.powernukkitx.entity.ai.memory.MemoryType} to the memory storage {@link IMemoryStorage}
 */


public interface ISensor {

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
