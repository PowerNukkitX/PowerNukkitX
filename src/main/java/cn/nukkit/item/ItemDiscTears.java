package cn.nukkit.item;


public class ItemDiscTears extends Item {

    public ItemDiscTears() {
        this(0, 1);
    }

    public ItemDiscTears(Integer meta) {
        this(meta, 1);
    }

    public ItemDiscTears(Integer meta, int count) {
        super(MUSIC_DISC_TEARS, 0, count, "Disc Tears");
    }
}
