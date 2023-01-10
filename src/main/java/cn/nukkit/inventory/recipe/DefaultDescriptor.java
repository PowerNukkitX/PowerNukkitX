package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.item.Item;

@PowerNukkitXOnly
@Since("1.19.50-r2")
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
        return CraftingManager.getFullItemHash(item);
    }

    public Item getItem() {
        return this.item;
    }

    public String toString() {
        return "DefaultDescriptor(item=" + this.getItem() + ")";
    }
}