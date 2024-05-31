package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

import java.util.Random;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentDurability extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentDurability() {
        super(ID_DURABILITY, "durability", Rarity.UNCOMMON, EnchantmentType.BREAKABLE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canEnchant(Item item) {
        return item.getMaxDurability() >= 0 || super.canEnchant(item);
    }
    /**
     * @deprecated 
     */
    

    public static boolean negateDamage(Item item, int level, Random random) {
        return !(item.isArmor() && random.nextFloat() < 0.6f) && random.nextInt(level + 1) > 0;
    }

}
