package cn.nukkit.item.randomitem;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LT_Name
 */


public class EnchantmentItemSelector extends ConstantItemSelector {
    /**
     * @deprecated 
     */
    
    public EnchantmentItemSelector(String id, Selector parent) {
        this(id, 0, parent);
    }
    /**
     * @deprecated 
     */
    

    public EnchantmentItemSelector(String id, Integer meta, Selector parent) {
        this(id, meta, 1,  parent);
    }
    /**
     * @deprecated 
     */
    

    public EnchantmentItemSelector(String id, Integer meta, int count, Selector parent) {
        this(Item.get(id, meta, count), parent);
    }
    /**
     * @deprecated 
     */
    

    public EnchantmentItemSelector(Item item, Selector parent) {
        super(item, parent);
        //TODO 贴近原版附魔概率
        List<Enchantment> enchantments = getSupportEnchantments(item);
        if (!enchantments.isEmpty()) {
            Random $1 = ThreadLocalRandom.current();
            Enchantment $2 = enchantments.get(random.nextInt(enchantments.size()));
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
            if (Objects.equals(item.getId(), Item.ENCHANTED_BOOK) || enchantment.canEnchant(item)) {
                enchantments.add(enchantment);
            }
        }
        return enchantments;
    }

}
