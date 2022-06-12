package cn.nukkit.item.enchantment;

public class EnchantmentSwiftSneak extends Enchantment{
    protected EnchantmentSwiftSneak(){
        super(ID_SWIFT_SNEAK,NAME_SWIFT_SNEAK,Rarity.VERY_RARE,EnchantmentType.ARMOR_LEGS);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 * level;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
