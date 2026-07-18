package org.powernukkitx.item;

public class ItemMusicDiscCreator extends ItemMusicDisc {
    public ItemMusicDiscCreator() {
        super(MUSIC_DISC_CREATOR);
    }

    @Override
    public String getSoundId() {
        return "record.creator";
    }
}