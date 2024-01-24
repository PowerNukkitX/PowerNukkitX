package cn.nukkit.item;

public class ItemMusicDiscStrad extends ItemMusicDisc {
    public ItemMusicDiscStrad() {
        super(MUSIC_DISC_STRAD);
    }

    @Override
    public String getSoundId() {
        return "record.strad";
    }
}