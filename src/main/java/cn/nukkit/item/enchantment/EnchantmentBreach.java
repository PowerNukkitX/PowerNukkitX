package cn.nukkit.item.enchantment;

import cn.nukkit.event.entity.EntityDamageByEntityEvent;

import static cn.nukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR;
import static cn.nukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR_ENCHANTMENTS;

public class EnchantmentBreach extends Enchantment {
    protected EnchantmentBreach() {
        super(ID_BREACH, "breach", Rarity.RARE, EnchantmentType.MACE);
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
        return 4;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        final int id = enchantment.getId();
        return id != ID_DAMAGE_ALL && id != ID_DENSITY && id != ID_DAMAGE_SMITE && id != ID_DAMAGE_ARTHROPODS;
    }

    public float getArmorEfficiencyReduction() {
        return (100 - (getLevel() * 15)) / 100f;
    }

    @Override
    public void doAttack(EntityDamageByEntityEvent event) {
        float reduction = getArmorEfficiencyReduction();
        event.setDamage(event.getDamage(ARMOR) * reduction, ARMOR);
        event.setDamage(event.getDamage(ARMOR_ENCHANTMENTS) * reduction, ARMOR_ENCHANTMENTS);
    }
}
