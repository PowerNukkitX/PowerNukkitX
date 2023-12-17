package cn.nukkit.item;

import cn.nukkit.api.Since;


public class ItemLead extends Item {


    public ItemLead() {
        this(0, 1);
    }


    public ItemLead(Integer meta) {
        this(meta, 1);
    }


    public ItemLead(Integer meta, int count) {
        super(LEAD, meta, count, "Lead");
    }
    
    // TODO: Add Functionality
}
