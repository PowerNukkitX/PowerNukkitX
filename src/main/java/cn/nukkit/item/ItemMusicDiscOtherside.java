package cn.nukkit.item;

public class ItemMusicDiscOtherside extends ItemMusicDisc {
    public ItemMusicDiscOtherside() {
        super(MUSIC_DISC_OTHERSIDE);
    }

    @Override
    public String getSoundId() {
        return "record.otherside";
    }
}