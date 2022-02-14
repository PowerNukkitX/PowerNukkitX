package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.dfsek.terra.api.inventory.Item;
import com.dfsek.terra.api.inventory.ItemStack;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public record PNXItemDelegate(cn.nukkit.item.Item innerItem) implements Item {
    @Override
    public ItemStack newItemStack(int i) {
        return null;
    }

    @Override
    public double getMaxDurability() {
        return innerItem.getMaxDurability();
    }

    @Override
    public cn.nukkit.item.Item getHandle() {
        return innerItem;
    }
}
