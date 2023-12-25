package cn.nukkit.item;

public class ItemMusicDiscWait extends ItemMusicDisc {
    public ItemMusicDiscWait() {
        super(MUSIC_DISC_WAIT);
    }

    @Override
    public String getSoundId() {
        return "record.wait";
    }
}