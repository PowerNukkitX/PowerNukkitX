package cn.nukkit.item;

import cn.nukkit.api.Since;

/**
 * @author LT_Name
 */
@Since("FUTURE")
public class ItemSpyglass extends Item {

    @Since("FUTURE")
    public ItemSpyglass() {
        this(0, 1);
    }

    @Since("FUTURE")
    public ItemSpyglass(Integer meta) {
        this(meta, 1);
    }

    @Since("FUTURE")
    public ItemSpyglass(Integer meta, int count) {
        super(SPYGLASS, meta, count, "Spyglass");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
