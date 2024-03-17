package cn.nukkit.level.generator.terra.handles;

import cn.nukkit.level.generator.terra.PNXAdapter;
import cn.nukkit.level.generator.terra.delegate.PNXEnchantmentDelegate;
import com.dfsek.terra.api.handle.ItemHandle;
import com.dfsek.terra.api.inventory.Item;
import com.dfsek.terra.api.inventory.item.Enchantment;

import java.util.Set;
import java.util.stream.Collectors;

public class PNXItemHandle implements ItemHandle {
    @Override
    public Item createItem(String s) {
        return PNXAdapter.adapt(cn.nukkit.item.Item.get(s));
    }

    @Override
    public Enchantment getEnchantment(String s) {
        if (s.startsWith("minecraft:")) s = s.substring(10);
        return new PNXEnchantmentDelegate(cn.nukkit.item.enchantment.Enchantment.getEnchantment(s));
    }

    @Override
    public Set<Enchantment> getEnchantments() {
        return cn.nukkit.item.enchantment.Enchantment.getRegisteredEnchantments().stream()
                .map(PNXEnchantmentDelegate::new)
                .collect(Collectors.toSet());
    }
}
