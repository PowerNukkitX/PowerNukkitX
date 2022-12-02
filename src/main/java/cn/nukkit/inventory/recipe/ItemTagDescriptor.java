package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.Value;

@PowerNukkitXOnly
@Since("1.19.50-r2")
@Value
public class ItemTagDescriptor implements ItemDescriptor {
    String itemTag;

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.ITEM_TAG;
    }

    @Override
    public Item toItem() {
        throw new UnsupportedOperationException();
    }
}
