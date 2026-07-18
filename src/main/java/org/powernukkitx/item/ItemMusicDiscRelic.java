package org.powernukkitx.item;

public class ItemMusicDiscRelic extends ItemMusicDisc {
    public ItemMusicDiscRelic() {
        super(MUSIC_DISC_RELIC);
    }

    @Override
    public String getSoundId() {
        return "record.relic";
    }
}