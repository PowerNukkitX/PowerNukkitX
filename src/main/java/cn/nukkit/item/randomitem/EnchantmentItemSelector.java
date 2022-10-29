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
@Since("1.19.40-r3")
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
        //TODO 贴近原版附魔概率
        List<Enchantment> enchantments = getSupportEnchantments(item);
        if (!enchantments.isEmpty()) {
            Random random = ThreadLocalRandom.current();
            Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
            if (random.nextDouble() < 0.3) { //减少高等级附魔概率
                enchantment.setLevel(Utils.rand(1, enchantment.getMaxLevel()));
            }
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
