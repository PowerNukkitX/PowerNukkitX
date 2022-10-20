package cn.nukkit.item.randomitem;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LT_Name
 */
public class EnchantmentItemSelector extends ConstantItemSelector {
    public EnchantmentItemSelector(int id, Selector parent) {
        this(id, 0, parent);
    }

    public EnchantmentItemSelector(int id, Integer meta, Selector parent) {
        this(id, meta, 1,  parent);
    }

    public EnchantmentItemSelector(int id, Integer meta, int count, Selector parent) {
        this(Item.get(id, meta, count), parent);
    }

    public EnchantmentItemSelector(Item item, Selector parent) {
        super(item, parent);
        //TODO 这里可能是30级附魔台的概率
        ArrayList<Enchantment> enchantments = new ArrayList<>(Enchantment.getRegisteredEnchantments());
        Random random = ThreadLocalRandom.current();
        Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
        enchantment.setLevel(Utils.rand(1, enchantment.getMaxLevel()));
        item.addEnchantment(enchantment);
    }

}
