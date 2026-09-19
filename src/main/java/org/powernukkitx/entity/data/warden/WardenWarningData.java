package org.powernukkitx.entity.data.warden;

import org.powernukkitx.nbt.tag.CompoundTag;

public class WardenWarningData {
    public static final int MAX_LEVEL = 4;
    public static final int INCREASE_COOLDOWN = 200;
    public static final int DECREASE_TIMER = 12000;

    public int warningLevel;
    public int increaseCooldown;
    public int decreaseTimer;

    /**
     * Loads the persisted Warden warning state.
     *
     * @param nbt persisted player data
     */
    public void load(CompoundTag nbt) {
        warningLevel = Math.max(0, Math.min(MAX_LEVEL, nbt.getInt("WardenThreatLevel")));
        increaseCooldown = Math.max(0, nbt.getInt("WardenThreatLevelIncreaseCooldown"));
        decreaseTimer = Math.max(0, nbt.getInt("WardenThreatDecreaseTimer"));
    }

    /**
     * Saves the current Warden warning state.
     *
     * @param nbt target player data
     */
    public void save(CompoundTag nbt) {
        nbt.putInt("WardenThreatLevel", warningLevel);
        nbt.putInt("WardenThreatLevelIncreaseCooldown", increaseCooldown);
        nbt.putInt("WardenThreatDecreaseTimer", decreaseTimer);
    }

    /**
     * Advances warning cooldown and decay timers.
     *
     * @param tickDiff elapsed ticks
     */
    public void tick(int tickDiff) {
        if (increaseCooldown > 0) increaseCooldown = Math.max(0, increaseCooldown - tickDiff);
        if (warningLevel == 0) return;
        decreaseTimer -= tickDiff;
        while (decreaseTimer <= 0 && warningLevel > 0) {
            warningLevel--;
            decreaseTimer += DECREASE_TIMER;
        }
        if (warningLevel == 0) decreaseTimer = 0;
    }

    /**
     * Attempts to increase the warning level.
     *
     * @return resulting warning level
     */
    public int increase() {
        if (increaseCooldown > 0) return warningLevel;
        warningLevel = Math.min(MAX_LEVEL, warningLevel + 1);
        increaseCooldown = INCREASE_COOLDOWN;
        decreaseTimer = DECREASE_TIMER;
        return warningLevel;
    }
}
