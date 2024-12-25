package cn.nukkit.item.enchantment;

import cn.nukkit.entity.Entity;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentDensity extends Enchantment {
    protected EnchantmentDensity() {
        super(ID_DENSITY, "density", Rarity.RARE, EnchantmentType.MACE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        final int id = enchantment.getId();
        return id != ID_DAMAGE_ALL && id != ID_BREACH && id != ID_DAMAGE_SMITE && id != ID_DAMAGE_ARTHROPODS;
    }

    @Override
    public double getDamageBonus(Entity target, Entity damager) {
        double height = damager.highestPosition - damager.y;
        if(height >= 1.5f) {
            return height * 0.5f * getLevel();
        }
        return 0;
    }
}
