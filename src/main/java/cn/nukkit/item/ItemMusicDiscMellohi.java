package cn.nukkit.item;

public class ItemMusicDiscMellohi extends ItemMusicDisc {
    public ItemMusicDiscMellohi() {
        super(MUSIC_DISC_MELLOHI);
    }

    @Override
    public String getSoundId() {
        return "record.mellohi";
    }
}