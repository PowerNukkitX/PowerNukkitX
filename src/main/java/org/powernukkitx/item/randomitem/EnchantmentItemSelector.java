package org.powernukkitx.item.randomitem;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentHelper;
import org.powernukkitx.utils.random.NukkitRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author LT_Name
 */


public class EnchantmentItemSelector extends ConstantItemSelector {
    /**
     * Highest enchanting cost a loot roll can draw. {@link NukkitRandom#nextInt(int)} is inclusive, so this
     * yields the vanilla range of 0 to 29.
     */
    private static final int MAX_ENCHANT_COST = 29;

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
        // Vanilla rolls a random enchanting cost and runs the enchanting table algorithm on it,
        // instead of picking one enchantment and one level uniformly.
        NukkitRandom random = new NukkitRandom();
        List<Enchantment> enchantments = EnchantmentHelper.selectEnchantments(random, item, random.nextInt(MAX_ENCHANT_COST));
        for (Enchantment enchantment : enchantments) {
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
