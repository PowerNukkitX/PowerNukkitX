package org.powernukkitx.item;

public class ItemMusicDiscBounce extends ItemMusicDisc {

    public ItemMusicDiscBounce() {
        super(MUSIC_DISC_BOUNCE);
    }

    @Override
    public String getSoundId() {
        return "record.bounce";
    }
}
