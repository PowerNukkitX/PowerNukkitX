package cn.nukkit.item;

/**
 * ItemFish
 */
public class ItemCod extends ItemEdible {
    public ItemCod() {
        super(COD);
    }

    public ItemCod(Integer meta) {
        this(meta, 1);
    }

    public ItemCod(Integer meta, int count) {
        super(COD, meta, count);
    }

    protected ItemCod(String id, Integer meta, int count) {
        super(id, meta, count);
    }
}