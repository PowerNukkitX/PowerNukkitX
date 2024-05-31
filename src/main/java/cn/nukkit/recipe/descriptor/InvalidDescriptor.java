package cn.nukkit.recipe.descriptor;

import cn.nukkit.item.Item;


public class InvalidDescriptor implements ItemDescriptor {
    int $1 = 0;

    public static final InvalidDescriptor $2 = new InvalidDescriptor();

    
    /**
     * @deprecated 
     */
    private InvalidDescriptor() {
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.INVALID;
    }

    @Override
    public Item toItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemDescriptor clone() throws CloneNotSupportedException {
        return (ItemDescriptor) super.clone();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getCount() {
        return count;
    }
    /**
     * @deprecated 
     */
    

    public String toString() {
        return "InvalidDescriptor(count=" + this.getCount() + ")";
    }
}
