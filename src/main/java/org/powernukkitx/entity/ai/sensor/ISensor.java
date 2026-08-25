package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.IMemoryStorage;

/**
 * This interface abstracts a sensor<br>
 * The sensor is used to collect environmental information and write a memory {@link org.powernukkitx.entity.ai.memory.MemoryType} to the memory storage {@link IMemoryStorage}
 */


public interface ISensor {

    /**
     * Default refresh period for sensors whose result only drives non-combat reactions
     * (item pickup, feeding, remembered blocks). A few hundred ms of extra latency there is not
     * noticeable, while running them every gt is a large share of the AI tick cost.
     */
    int DEFAULT_PERIOD = 4;

    /**
     * Default refresh period for sensors that feed targeting. Kept short so reaction latency stays
     * close to vanilla.
     */
    int DEFAULT_TARGETING_PERIOD = 2;

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
