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
    public int getMaxLevel() {
        return 3;
    }
}
