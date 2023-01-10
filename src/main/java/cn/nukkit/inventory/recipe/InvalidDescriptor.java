package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

@PowerNukkitXOnly
@Since("1.19.50-r2")
public class InvalidDescriptor implements ItemDescriptor {
    int count = 0;

    public static final InvalidDescriptor INSTANCE = new InvalidDescriptor();

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
    public int getCount() {
        return count;
    }

    public String toString() {
        return "InvalidDescriptor(count=" + this.getCount() + ")";
    }
}
