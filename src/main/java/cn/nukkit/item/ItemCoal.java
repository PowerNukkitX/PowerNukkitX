package cn.nukkit.item;

import cn.nukkit.api.DeprecationDetails;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCoal extends Item {

    public ItemCoal() {
        this(0, 1);
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Charcoal now have it's own id",
        replaceWith = "ItemCoal() or ItemCharcoal()")
    public ItemCoal(Integer meta) {
        this(meta, 1);
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Charcoal now have it's own id",
            replaceWith = "ItemCoal() or ItemCharcoal()")
    public ItemCoal(Integer meta, int count) {
        super(COAL, meta, count, "Coal");
        if (this.aux == 1) {
            this.name = "Charcoal";
        }
    }


    protected ItemCoal(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.4.0.0-PN",
            reason = "Charcoal  now it's own ids, and its implementation extends ItemCoal, " +
                    "so you may get 0 as meta result even though you have a charcoal.",
            replaceWith = "isCharcoal()"
    )
    @Override
    public int getAux() {
        return super.getAux();
    }


    public boolean isCharcoal() {
        return getId() == COAL && super.getAux() == 1;
    }
}
