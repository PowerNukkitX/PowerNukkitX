package cn.nukkit.item.randomitem;

import cn.nukkit.item.Item;

/**
 * @author Snake1999
 * @since 2016/1/15
 */
public class ConstantItemSelector extends Selector {

    protected final Item item;
    /**
     * @deprecated 
     */
    

    public ConstantItemSelector(String id, Selector parent) {
        this(id, 0, parent);
    }
    /**
     * @deprecated 
     */
    

    public ConstantItemSelector(String id, Integer meta, Selector parent) {
        this(id, meta, 1, parent);
    }
    /**
     * @deprecated 
     */
    

    public ConstantItemSelector(String id, Integer meta, int count, Selector parent) {
        this(Item.get(id, meta, count), parent);
    }
    /**
     * @deprecated 
     */
    

    public ConstantItemSelector(Item item, Selector parent) {
        super(parent);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public Object select() {
        return getItem();
    }
}
