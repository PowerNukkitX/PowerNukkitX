package cn.nukkit.item;

public class ItemMusicDiscMall extends ItemMusicDisc {
    public ItemMusicDiscMall() {
        super(MUSIC_DISC_MALL);
    }

    @Override
    public String getSoundId() {
        return "record.mall";
    }
}