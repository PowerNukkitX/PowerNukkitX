package cn.nukkit.item;

import cn.nukkit.block.Block;


public abstract class ItemHangingSign extends Item {
    /**
     * @deprecated 
     */
    
    public ItemHangingSign(String id) {
        super(id);
        this.block = Block.get(id);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 16;
    }
}
