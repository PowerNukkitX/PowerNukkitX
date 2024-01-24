package cn.nukkit.item;

public class ItemMusicDiscStal extends ItemMusicDisc {
    public ItemMusicDiscStal() {
        super(MUSIC_DISC_STAL);
    }

    @Override
    public String getSoundId() {
        return "record.stal";
    }
}