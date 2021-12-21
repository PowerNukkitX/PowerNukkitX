package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("FUTURE")
public class ItemFireworkStar extends Item {

    @Since("FUTURE")
    public ItemFireworkStar() {
        this(0, 1);
    }

    @Since("FUTURE")
    public ItemFireworkStar(Integer meta) {
        this(meta, 1);
    }

    @Since("FUTURE")
    public ItemFireworkStar(Integer meta, int count) {
        super(FIREWORKSCHARGE, meta, count, "Firework Star");
    }
}
