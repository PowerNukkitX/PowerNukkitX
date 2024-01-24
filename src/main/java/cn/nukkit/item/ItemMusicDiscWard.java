package cn.nukkit.item;

public class ItemMusicDiscWard extends ItemMusicDisc {
    public ItemMusicDiscWard() {
        super(MUSIC_DISC_WARD);
    }

    @Override
    public String getSoundId() {
        return "record.ward";
    }
}