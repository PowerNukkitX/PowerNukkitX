package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@PowerNukkitXOnly
@Since("1.19.50-r2")
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InvalidDescriptor implements ItemDescriptor {

    public static final InvalidDescriptor INSTANCE = new InvalidDescriptor();

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.INVALID;
    }

    @Override
    public Item toItem() {
        throw new UnsupportedOperationException();
    }
}
