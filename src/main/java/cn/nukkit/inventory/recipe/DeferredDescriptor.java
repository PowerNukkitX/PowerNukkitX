package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.Value;

@PowerNukkitXOnly
@Since("Future")
@Value
public class DeferredDescriptor implements ItemDescriptor {
    String fullName;
    int auxValue;
    int count;

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.DEFERRED;
    }

    @Override
    public Item toItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemDescriptor clone() throws CloneNotSupportedException {
        return (ItemDescriptor) super.clone();
    }
}
