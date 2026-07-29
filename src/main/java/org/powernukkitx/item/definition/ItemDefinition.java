package org.powernukkitx.item.definition;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
public class ItemDefinition {
    boolean canTakeDamage;
    boolean unbreakable;
    boolean lavaResistant;
    boolean fertilizer;
    boolean applyEnchantments;
    boolean canBeActivated;
    boolean canBreakShield;
    boolean noDamageOnAttack;
    boolean noDamageOnBreak;
    boolean shouldDespawn;
    boolean edible;
    boolean consumable;
    boolean requiresHunger;
    boolean tool;
    boolean sword;
    boolean spear;
    boolean axe;
    boolean pickaxe;
    boolean shovel;
    boolean hoe;
    boolean shears;
    boolean shield;
    boolean bow;
    boolean crossbow;
    boolean trident;
    boolean mace;
    boolean armor;
    boolean helmet;
    boolean chestplate;
    boolean leggings;
    boolean boots;

    int maxStackSize;
    int maxDurability;
    int damageChanceMin;
    int damageChanceMax;
    int tier;
    int enchantAbility;
    int attackDamage;
    int armorPoints;
    int toughness;
    int nutrition;
    int usingTicks;

    float saturation;
    float knockbackResistance;
    float useDuration;
    float movementModifier;
}
