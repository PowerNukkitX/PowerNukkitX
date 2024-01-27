package cn.nukkit.recipe.descriptor;

import cn.nukkit.item.Item;


public class DefaultDescriptor implements ItemDescriptor {
    private final Item item;

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
    public int getCount() {
        return this.item.getCount();
    }

    @Override
    public boolean match(Item item) {
        return this.item.equals(item, true, false);
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    public Item getItem() {
        return this.item;
    }

    public String toString() {
        return "DefaultDescriptor(item=" + this.getItem() + ")";
    }
}