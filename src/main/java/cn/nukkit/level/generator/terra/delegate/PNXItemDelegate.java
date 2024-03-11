package cn.nukkit.level.generator.terra.delegate;

import com.dfsek.terra.api.inventory.Item;
import com.dfsek.terra.api.inventory.ItemStack;

public record PNXItemDelegate(cn.nukkit.item.Item innerItem) implements Item {
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
    public cn.nukkit.item.Item getHandle() {
        return innerItem;
    }
}
