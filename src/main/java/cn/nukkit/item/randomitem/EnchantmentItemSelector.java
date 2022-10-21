package cn.nukkit.item.randomitem;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LT_Name
 */
@PowerNukkitXOnly
@Since("1.19.31-r2")
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
        List<Enchantment> enchantments = getSupportEnchantments(item);
        if (!enchantments.isEmpty()) {
            Random random = ThreadLocalRandom.current();
            Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
            enchantment.setLevel(Utils.rand(1, enchantment.getMaxLevel()));
            item.addEnchantment(enchantment);
        }
    }

    /**
     * 根据物品获得支持的附魔
     *
     * @param item 物品
     * @return 支持的附魔
     */
    public List<Enchantment> getSupportEnchantments(Item item) {
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.getRegisteredEnchantments()) {
            if (enchantment.canEnchant(item)) {
                enchantments.add(enchantment);
            }
        }
        return enchantments;
    }

}
