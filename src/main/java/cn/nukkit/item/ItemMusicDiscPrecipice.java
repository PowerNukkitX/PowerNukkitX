package cn.nukkit.item;

public class ItemMusicDiscPrecipice extends ItemMusicDisc {
    public ItemMusicDiscPrecipice() {
        super(MUSIC_DISC_PRECIPICE);
    }

    @Override
    public String getSoundId() {
        return "record.precipice";
    }
}