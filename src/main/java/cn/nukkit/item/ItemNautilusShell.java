package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("FUTURE")
public class ItemNautilusShell extends Item {

    @Since("FUTURE")
    public ItemNautilusShell() {
        this(0, 1);
    }

    @Since("FUTURE")
    public ItemNautilusShell(Integer meta) {
        this(meta, 1);
    }

    @Since("FUTURE")
    public ItemNautilusShell(Integer meta, int count) {
        super(NAUTILUS_SHELL, meta, count, "Nautilus Shell");
    }
}
