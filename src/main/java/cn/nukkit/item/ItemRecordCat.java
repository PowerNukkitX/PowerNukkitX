package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordCat extends ItemRecord {

    public ItemRecordCat() {
        this(0, 1);
    }

    public ItemRecordCat(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordCat(Integer meta, int count) {
        super(RECORD_CAT, meta, count);
        name = "Music Disc Cat";
    }

    @Override
    public String getSoundId() {
        return "record.cat";
    }
}
