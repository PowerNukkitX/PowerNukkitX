package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordWait extends ItemRecord {

    public ItemRecordWait() {
        this(0, 1);
    }

    public ItemRecordWait(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordWait(Integer meta, int count) {
        super(RECORD_WAIT, meta, count);
        name = "Music Disc Wait";
    }

    @Override
    public String getSoundId() {
        return "record.wait";
    }
}
