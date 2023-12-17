package cn.nukkit.item;


public class ItemShelterPotterySherd extends ItemPotterySherd {
    public ItemShelterPotterySherd() {
        this(0, 1);
    }

    public ItemShelterPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemShelterPotterySherd(Integer meta, int count) {
        super(SHELTER_POTTERY_SHERD, meta, count, "Shelter Pottery Sherd");
    }
}
