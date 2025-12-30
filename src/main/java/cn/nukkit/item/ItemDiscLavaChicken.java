package cn.nukkit.item;


public class ItemDiscLavaChicken extends ItemMusicDisc {

    public ItemDiscLavaChicken() {
        super(MUSIC_DISC_LAVA_CHICKEN);
    }

    @Override
    public String getSoundId() {
        return "record.lava_chicken";
    }

}
