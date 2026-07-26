package org.powernukkitx.entity;

import org.powernukkitx.entity.ai.memory.IMemoryStorage;

/**
 * The interface that inherits this interface is an entity component<br>
 * The implementation of the entity component uses the default method to carry the logic, and the related values are stored in memory
 */


public interface EntityComponent {
    IMemoryStorage getMemoryStorage();

    default Entity asEntity() {
        return getMemoryStorage().getEntity();
    }
}
