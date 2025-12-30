package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCookie extends ItemFood {

    public ItemCookie() {
        this(0, 1);
    }

    public ItemCookie(Integer meta) {
        this(meta, 1);
    }

    public ItemCookie(Integer meta, int count) {
        super(COOKIE, meta, count, "Cookie");
    }

    @Override
    public int getNutrition() {
        return 2;
    }

    @Override
    public float getSaturation() {
        return 0.4F;
    }
}
