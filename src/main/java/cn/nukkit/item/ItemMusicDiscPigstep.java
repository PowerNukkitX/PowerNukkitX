package cn.nukkit.item;

public class ItemMusicDiscPigstep extends ItemMusicDisc {
    public ItemMusicDiscPigstep() {
        super(MUSIC_DISC_PIGSTEP);
    }

    @Override
    public String getSoundId() {
        return "record.pigstep";
    }
}