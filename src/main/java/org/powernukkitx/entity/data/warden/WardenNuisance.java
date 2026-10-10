package org.powernukkitx.entity.data.warden;

import org.powernukkitx.nbt.tag.CompoundTag;


/**
 * Stores one anger target used by the warden nuisance system. It keeps the actor unique ID, anger amount, and priority
 * and supports NBT serialization.
 *
 * @author Curse
 */
public final class WardenNuisance {

    private final long actorId;
    private int anger;
    private final byte priority;

    /**
     * Creates a new WardenNuisance instance.
     *
     * @param actorId value for this API
     * @param anger value for this API
     * @param priority value for this API
     */
    public WardenNuisance(long actorId, int anger, byte priority) {
        this.actorId = actorId;
        this.anger = anger;
        this.priority = priority;
    }

    /**
     * Returns the actor unique ID.
     * @return the requested value
     */
    public long actorId() {
        return actorId;
    }

    /**
     * Returns the anger value.
     * @return the requested value
     */
    public int anger() {
        return anger;
    }

    /**
     * Sets the anger value.
     *
     * @param anger value for this API
     */
    public void setAnger(int anger) {
        this.anger = anger;
    }

    /**
     * Returns the priority value.
     * @return the requested value
     */
    public byte priority() {
        return priority;
    }

    /**
     * Creates an instance from NBT data.
     *
     * @param tag value for this API
     * @return the requested value
     */
    public static WardenNuisance fromTag(CompoundTag tag) {
        return new WardenNuisance(tag.getLong("ActorId"), tag.getInt("Anger"), tag.getByte("Priority"));
    }

    /**
     * Writes this instance to NBT data.
     * @return the requested value
     */
    public CompoundTag toTag() {
        return new CompoundTag()
                .putLong("ActorId", actorId)
                .putInt("Anger", anger)
                .putByte("Priority", priority);
    }
}
