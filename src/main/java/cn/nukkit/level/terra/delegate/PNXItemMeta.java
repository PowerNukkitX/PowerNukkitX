package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import com.dfsek.terra.api.inventory.item.Enchantment;
import com.dfsek.terra.api.inventory.item.ItemMeta;

import java.util.HashMap;
import java.util.Map;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public record PNXItemMeta(cn.nukkit.item.Item innerItem) implements ItemMeta {
    @Override
    public void addEnchantment(Enchantment enchantment, int i) {
        final var enc = ((PNXEnchantmentDelegate) enchantment).innerEnchantment();
        enc.setLevel(i, false);
        innerItem.addEnchantment(enc);
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        final var map = new HashMap<Enchantment, Integer>();
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
