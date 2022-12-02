package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

@PowerNukkitXOnly
@Since("1.19.50-r2")
public interface ItemDescriptor {

    ItemDescriptorType getType();

    Item toItem();
}
