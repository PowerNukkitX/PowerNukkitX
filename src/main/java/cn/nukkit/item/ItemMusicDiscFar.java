package cn.nukkit.item;

public class ItemMusicDiscFar extends ItemMusicDisc {
    public ItemMusicDiscFar() {
        super(MUSIC_DISC_FAR);
    }

    @Override
    public String getSoundId() {
        return "record.far";
    }
}