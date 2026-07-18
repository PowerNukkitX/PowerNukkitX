package org.powernukkitx.item;

public class ItemMusicDiscCat extends ItemMusicDisc {
    public ItemMusicDiscCat() {
        super(MUSIC_DISC_CAT);
    }

    @Override
    public String getSoundId() {
        return "record.cat";
    }
}