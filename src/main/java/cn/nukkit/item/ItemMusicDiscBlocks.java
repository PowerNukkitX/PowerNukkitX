package cn.nukkit.item;

public class ItemMusicDiscBlocks extends ItemMusicDisc {
    public ItemMusicDiscBlocks() {
        super(MUSIC_DISC_BLOCKS);
    }

    @Override
    public String getSoundId() {
        return "record.blocks";
    }
}