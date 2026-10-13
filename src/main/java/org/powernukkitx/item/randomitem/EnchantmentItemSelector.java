package org.powernukkitx.item.randomitem;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentHelper;
import org.powernukkitx.utils.random.NukkitRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

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
    }

    /**
     * Returns a copy of the loot item carrying a freshly rolled set of enchantments.
     * <p>
     * Vanilla rolls a random enchanting cost and runs the enchanting table algorithm on it, instead of picking
     * one enchantment and one level uniformly. The roll happens here and not in the constructor because a
     * selector is normally kept in a static field, which would hand out the very same enchantments for the
     * whole lifetime of the server.
     *
     * @return the enchanted item
     */
    @Override
    public Object select() {
        Item result = getItem().clone();
        NukkitRandom random = new NukkitRandom(ThreadLocalRandom.current().nextLong());
        List<Enchantment> enchantments = EnchantmentHelper.selectEnchantments(
                random, result, random.nextInt(MAX_ENCHANT_COST), getEnchantmentPool());
        for (Enchantment enchantment : enchantments) {
            result.addEnchantment(enchantment);
        }
        return result;
    }

    /**
     * Decides which enchantments a roll may draw from. Defaults to the ones an enchanting table gives out,
     * subclasses override it when their loot source draws from a different pool.
     *
     * @return the filter applied to the enchantment pool
     */
    protected Predicate<Enchantment> getEnchantmentPool() {
        return Enchantment::isObtainableFromEnchantingTable;
    }

    /**
     * Gets the enchantments supported by the given item
     *
     * @param item the item
     * @return the supported enchantments
     * @deprecated the roll no longer draws from this list, override {@link #getEnchantmentPool()} instead
     */
    @Deprecated
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
