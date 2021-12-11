package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class ItemLead extends Item {

    @Since("1.4.0.0-PN")
    public ItemLead() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemLead(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemLead(Integer meta, int count) {
        super(LEAD, meta, count, "Lead");
    }
    
    // TODO: Add Functionality
}
