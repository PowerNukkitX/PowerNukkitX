package org.powernukkitx.item.randomitem;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LT_Name
 */


public class EnchantmentItemSelector extends ConstantItemSelector {
    public EnchantmentItemSelector(String id, Selector parent) {
        this(id, 0, parent);
    }

    public EnchantmentItemSelector(String id, Integer meta, Selector parent) {
        this(id, meta, 1,  parent);
    }

    public EnchantmentItemSelector(String id, Integer meta, int count, Selector parent) {
        this(Item.get(id, meta, count), parent);
    }

    public EnchantmentItemSelector(Item item, Selector parent) {
        super(item, parent);
        //TODO align with vanilla enchantment probabilities
        List<Enchantment> enchantments = getSupportEnchantments(item);
        if (!enchantments.isEmpty()) {
            Random random = ThreadLocalRandom.current();
            Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
            if (random.nextDouble() < 0.3) { //reduce the probability of high-level enchantments
                enchantment.setLevel(Utils.rand(1, enchantment.getMaxLevel()));
            }
            item.addEnchantment(enchantment);
        }
    }

    /**
     * Gets the enchantments supported by the given item
     *
     * @param item the item
     * @return the supported enchantments
     */
    public List<Enchantment> getSupportEnchantments(Item item) {
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.getRegisteredEnchantments()) {
            if (Objects.equals(item.getId(), Item.ENCHANTED_BOOK) || enchantment.canEnchant(item)) {
                enchantments.add(enchantment);
            }
        }
        return enchantments;
    }

}
