package org.powernukkitx.level;

import org.jetbrains.annotations.Nullable;

/**
 * Defines how a music track is repeated once it has been started.
 */
public enum MusicRepeatMode {
    PLAY_ONCE("play_once"),
    LOOP("loop");

    private final String name;

    MusicRepeatMode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isLooping() {
        return this == LOOP;
    }

    @Nullable
    public static MusicRepeatMode byName(String name) {
        for (MusicRepeatMode mode : values()) {
            if (mode.name.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }
}
