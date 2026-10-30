package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author CreeperFace
 */
public abstract class ItemMusicDisc extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    protected ItemMusicDisc(String id) {
        this(id, DEFINITION);
    }

    protected ItemMusicDisc(String id, ItemDefinition definition) {
        super(id, definition);
    }

    public abstract String getSoundId();

    /**
     * Returns the vanilla playback duration used by jukebox listeners.
     * Custom records can override this method; zero leaves playback active until stopped.
     * @return playback duration in game ticks, or zero when unknown
     */
    public int getDurationTicks() {
        int seconds = switch (getSoundId()) {
            case "record.13", "record.5" -> 178;
            case "record.cat", "record.chirp" -> 185;
            case "record.blocks" -> 345;
            case "record.far" -> 174;
            case "record.mall" -> 197;
            case "record.mellohi" -> 96;
            case "record.stal" -> 150;
            case "record.strad" -> 188;
            case "record.ward" -> 251;
            case "record.11" -> 71;
            case "record.wait" -> 238;
            case "record.pigstep" -> 149;
            case "record.otherside" -> 195;
            case "record.relic" -> 218;
            case "record.creator" -> 176;
            case "record.creator_music_box" -> 73;
            case "record.precipice" -> 299;
            case "record.tears" -> 175;
            case "record.bounce", "record.lava_chicken" -> 134;
            default -> 0;
        };
        return seconds * 20;
    }
}
