package org.powernukkitx.level.vibration;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Storage schema for vibration listeners used by
 * Sculk Sensor, Calibrated Sculk Sensor, Sculk Shrieker and Warden.
 *
 * <pre>
 * VibrationListener: {
 * event: INT,              // optional
 * pending: {               // optional
 * distance: FLOAT,
 * source: LONG,
 * projectile: LONG,
 * vibration: INT,
 * x: INT,
 * y: INT,
 * z: INT
 * },
 * selector: COMPOUND,
 * ticks: INT               // optional
 * }
 * </pre>
 *
 * @author Curse
 */
public final class VibrationListenerStorage {

    /**
     * Public constant used by this API.
     */
    public static final String TAG_VIBRATION_LISTENER = "VibrationListener";

    /**
     * Public constant used by this API.
     */
    public static final String TAG_EVENT = "event";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_PENDING = "pending";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_SELECTOR = "selector";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_TICKS = "ticks";

    /**
     * Public constant used by this API.
     */
    public static final String TAG_PENDING_DISTANCE = "distance";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_PENDING_SOURCE = "source";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_PENDING_PROJECTILE = "projectile";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_PENDING_VIBRATION = "vibration";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_PENDING_X = "x";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_PENDING_Y = "y";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_PENDING_Z = "z";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_SELECTOR_TICK = "tick";
    /**
     * Public constant used by this API.
     */
    public static final String TAG_SELECTOR_CONTEXT = "context";

    private VibrationListenerStorage() {
    }

    /**
     * Creates initial listener data.
     * @return the requested value
     */
    public static CompoundTag createInitialData() {
        return new CompoundTag().putCompound(TAG_VIBRATION_LISTENER, createIdle());
    }

    /**
     * Creates idle listener data.
     * @return the requested value
     */
    public static CompoundTag createIdle() {
        return new CompoundTag().putCompound(TAG_SELECTOR, new CompoundTag());
    }

    /**
     * Returns the listener compound.
     *
     * @param nbt value for this API
     * @return the requested value
     */
    public static CompoundTag getListener(CompoundTag nbt) {
        if (!nbt.containsCompound(TAG_VIBRATION_LISTENER)) {
            throw new IllegalStateException("Missing VibrationListener compound");
        }

        CompoundTag listener = nbt.getCompound(TAG_VIBRATION_LISTENER);
        if (!listener.containsCompound(TAG_SELECTOR)) {
            throw new IllegalStateException("Missing VibrationListener selector");
        }
        return listener;
    }

    /**
     * Returns whether a vibration is scheduled.
     *
     * @param nbt value for this API
     * @return the requested value
     */
    public static boolean hasScheduledVibration(CompoundTag nbt) {
        CompoundTag listener = getListener(nbt);
        return listener.containsCompound(TAG_PENDING) || listener.getCompound(TAG_SELECTOR).containsCompound(TAG_SELECTOR_CONTEXT);
    }
}
