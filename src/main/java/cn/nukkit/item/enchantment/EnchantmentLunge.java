package cn.nukkit.item.enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentLunge extends Enchantment {
    protected EnchantmentLunge() {
        super(ID_LUNGE, "lunge", Rarity.RARE, EnchantmentType.SPEAR);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return level + 8 * level + 6;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return getMinEnchantAbility(level) + 45 + level;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
