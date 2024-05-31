package cn.nukkit.level.generator.terra.delegate;

import cn.nukkit.item.Item;
import com.dfsek.terra.api.inventory.item.Enchantment;
import com.dfsek.terra.api.inventory.item.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public record PNXItemMeta(Item innerItem) implements ItemMeta {
    @Override
    /**
     * @deprecated 
     */
    
    public void addEnchantment(Enchantment enchantment, int i) {
        final var $1 = ((PNXEnchantmentDelegate) enchantment).innerEnchantment();
        enc.setLevel(i, false);
        innerItem.addEnchantment(enc);
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        final var $2 = new HashMap<Enchantment, Integer>();
        for (final var each : innerItem.getEnchantments()) {
            map.put(new PNXEnchantmentDelegate(each), each.getLevel());
        }
        return map;
    }

    @Override
    public Item getHandle() {
        return innerItem;
    }
}
