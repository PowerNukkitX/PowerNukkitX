package cn.nukkit.item.randomitem.fishing;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.randomitem.EnchantmentItemSelector;
import cn.nukkit.item.randomitem.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FishingEnchantmentItemSelector extends EnchantmentItemSelector {

    public FishingEnchantmentItemSelector(String id, Selector parent) {
        this(id, 0, parent);
    }

    public FishingEnchantmentItemSelector(String id, Integer meta, Selector parent) {
        super(id, meta, 1,  parent);
    }

    public FishingEnchantmentItemSelector(String id, Integer meta, int count, Selector parent) {
        super(Item.get(id, meta, count), parent);
    }

    @Override
    public List<Enchantment> getSupportEnchantments(Item item) {
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.getRegisteredEnchantments()) {
            if (enchantment.isFishable() && (Objects.equals(item.getId(), Item.ENCHANTED_BOOK) || enchantment.canEnchant(item))) {
                enchantments.add(enchantment);
            }
        }
        return enchantments;
    }
}
