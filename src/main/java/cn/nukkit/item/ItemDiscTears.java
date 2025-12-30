package cn.nukkit.item;

public class ItemDiscTears extends ItemMusicDisc {

    public ItemDiscTears() {
        super(MUSIC_DISC_TEARS);
    }

    @Override
    public String getSoundId() {
        return "record.tears";
    }
}
