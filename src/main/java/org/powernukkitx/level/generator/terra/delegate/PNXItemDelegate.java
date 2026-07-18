package org.powernukkitx.level.generator.terra.delegate;

import com.dfsek.terra.api.inventory.Item;
import com.dfsek.terra.api.inventory.ItemStack;

public record PNXItemDelegate(org.powernukkitx.item.Item innerItem) implements Item {
    @Override
    public ItemStack newItemStack(int i) {
        final var tmp = innerItem.clone();
        tmp.setCount(i);
        return new PNXItemStack(tmp);
    }

    @Override
    public double getMaxDurability() {
        return innerItem.getMaxDurability();
    }

    @Override
    public org.powernukkitx.item.Item getHandle() {
        return innerItem;
    }
}
