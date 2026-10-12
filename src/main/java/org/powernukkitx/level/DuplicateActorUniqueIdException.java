package org.powernukkitx.level;

import org.powernukkitx.utils.LevelException;

/**
 * Signals that actor materialization attempted to register a persistent ActorUniqueID already owned by another live
 * actor.
 *
 * @author Curse
 */
public final class DuplicateActorUniqueIdException extends LevelException {

    /**
     * Creates a duplicate persistent ActorUniqueID failure.
     *
     * @param uniqueId conflicting ActorUniqueID
     */
    public DuplicateActorUniqueIdException(long uniqueId) {
        super("Duplicate persistent ActorUniqueID " + uniqueId);
    }
}
