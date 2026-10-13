package org.powernukkitx.level.format.leveldb;

import com.google.common.base.Preconditions;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Codec for the {@code WorldClocks} global LevelDB value.
 *
 * @author Curse
 */
final class LevelDBWorldClocksCodec {
    static final String OVERWORLD_CLOCK_NAME = "minecraft:overworld";

    private static final String CLOCKS_TAG = "clocks";
    private static final String NAME_TAG = "Name";
    private static final String TIME_TAG = "Time";
    private static final String IS_PAUSED_TAG = "IsPaused";

    private LevelDBWorldClocksCodec() {
    }

    static CompoundTag encode(List<WorldClock> clocks) {
        Preconditions.checkNotNull(clocks, "clocks");

        ListTag<CompoundTag> clockTags = new ListTag<>();
        for (WorldClock clock : clocks) {
            Preconditions.checkNotNull(clock, "clock");
            clockTags.add(new CompoundTag()
                    .putString(NAME_TAG, clock.name())
                    .putInt(TIME_TAG, clock.time())
                    .putBoolean(IS_PAUSED_TAG, clock.paused()));
        }

        return new CompoundTag().putList(CLOCKS_TAG, clockTags);
    }

    static List<WorldClock> decode(CompoundTag tag) {
        Preconditions.checkNotNull(tag, "tag");

        ListTag<CompoundTag> clockTags = tag.getList(CLOCKS_TAG, CompoundTag.class);
        List<WorldClock> clocks = new ArrayList<>(clockTags.size());

        for (CompoundTag clockTag : clockTags.getAll()) {
            clocks.add(new WorldClock(
                    clockTag.getString(NAME_TAG),
                    clockTag.getInt(TIME_TAG),
                    clockTag.getBoolean(IS_PAUSED_TAG)
            ));
        }

        return clocks;
    }

    /**
     * Persisted world clock state.
     *
     * @param name clock name
     * @param time 32-bit clock time
     * @param paused persisted pause state
     *
     * @author Curse
     */
    record WorldClock(String name, int time, boolean paused) {
        WorldClock {
            Preconditions.checkNotNull(name, "name");
        }
    }
}
