package cn.nukkit.recipe.descriptor;

import cn.nukkit.item.Item;


public class DefaultDescriptor implements ItemDescriptor {
    private final Item item;
    /**
     * @deprecated 
     */
    

    public DefaultDescriptor(Item item) {
        this.item = item;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.DEFAULT;
    }

    @Override
    public Item toItem() {
        return item.clone();
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
        return this.item.getCount();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean match(Item item) {
        return this.item.equals(item, true, false);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return item.hashCode();
    }

    public Item getItem() {
        return this.item;
    }
    /**
     * @deprecated 
     */
    

    public String toString() {
        return "DefaultDescriptor(item=" + this.getItem() + ")";
    }
}