package cn.nukkit.item;

public class ItemMusicDiscCreatorMusicBox extends ItemMusicDisc {
    public ItemMusicDiscCreatorMusicBox() {
        super(MUSIC_DISC_CREATOR_MUSIC_BOX);
    }

    @Override
    public String getSoundId() {
        return "record.creator_music_box";
    }
}