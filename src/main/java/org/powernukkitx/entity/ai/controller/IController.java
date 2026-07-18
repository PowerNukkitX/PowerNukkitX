package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.entity.EntityIntelligent;

/**
 * The controller is used to control the behavior of the entity, such as the specific implementation of moving, jumping, attacking, etc.<br>
 * For different entities, different controllers can be provided to achieve special implementations of the above behaviors.
 */


public interface IController {
    /**
     * Implement behavior
     *
     * @param entity the target entity
     * @return whether the behavior was implemented successfully
     */
    boolean control(EntityIntelligent entity);
}
