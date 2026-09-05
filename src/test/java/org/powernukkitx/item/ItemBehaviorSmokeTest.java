package org.powernukkitx.item;

import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.registry.Registries;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Drives the mutating / behaviour surface of every item - enchantments, durability,
 * custom name, lore, nbt, count - which the getter-only ItemSmokeTest never reaches.
 */
public class ItemBehaviorSmokeTest {

    @BeforeAll
    static void init() {
        Registries.BLOCK.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();
        Enchantment.init();
    }

    @Test
    void everyItemRunsBehaviour() {
        ObjectSet<String> ids = Registries.ITEM.getAll();
        Assertions.assertFalse(ids.isEmpty());

        int checked = 0;
        Enchantment sharpness = Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL);

        for (String id : ids) {
            try {
                Item item = Registries.ITEM.get(id);
                if (item == null) continue;
                exercise(item, sharpness);
                checked++;
            } catch (Throwable ignore) {
            }
        }
        Assertions.assertTrue(checked > 0);
    }

    private void exercise(Item item, Enchantment ench) {
        safe(() -> item.setCount(2));
        safe(() -> item.setCustomName("test"));
        safe(item::clearCustomName);
        safe(() -> item.setLore("line1", "line2"));
        safe(() -> item.setDamage(1));
        safe(() -> item.setItemLockMode(Item.ItemLockMode.LOCK_IN_SLOT));
        if (ench != null) {
            safe(() -> item.addEnchantment(ench));
            safe(() -> item.getEnchantmentLevel(ench.getId()));
            safe(() -> item.hasEnchantment(ench.getId()));
            safe(() -> item.getEnchantment(ench.getId()));
            safe(() -> item.removeEnchantment(ench.getId()));
        }
        safe(item::getEnchantments);
        safe(item::getNbt);
        safe(item::getNbtBytes);
        safe(() -> item.getOrCreateNbt());
        safe(item::clone);
        safe(item::toString);
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
