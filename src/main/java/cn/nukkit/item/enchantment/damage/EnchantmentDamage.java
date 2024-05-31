package cn.nukkit.item.enchantment.damage;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentDamage extends Enchantment {

    public enum TYPE {
        ALL,
        SMITE,
        ARTHROPODS
    }
    protected final TYPE damageType;

    
    /**
     * @deprecated 
     */
    protected EnchantmentDamage(int id, String name, Rarity rarity, TYPE type) {
        super(id, name, rarity, EnchantmentType.SWORD);
        this.damageType = type;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentDamage);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canEnchant(Item item) {
        return item.isAxe() || super.canEnchant(item);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "%enchantment.damage." + this.name;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isMajor() {
        return true;
    }

}
