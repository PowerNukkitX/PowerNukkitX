package cn.nukkit.item.enchantment.spear;

/**
 * @author xRookieFight
 * @since 09/01/2026
 */
public class EnchantmentLunge extends EnchantmentSpear {
    public EnchantmentLunge() {
        super(ID_LUNGE, "lunge", Rarity.UNCOMMON);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
