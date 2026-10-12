package org.powernukkitx.item.definition;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Constant per-item values backing the property getters of {@link org.powernukkitx.item.Item}.
 * <p>
 * Item classes declare a shared {@code DEFINITION} derived from {@link org.powernukkitx.item.Item#DEFAULT_DEFINITION}
 * (or a base class constant such as {@code ItemTool.TOOL}) with {@link #toBuilder()} and pass it to their
 * constructor. Overriding a getter still takes precedence over the definition.
 * <p>
 * Custom items are backed by {@link org.powernukkitx.item.customitem.CustomItemDefinition}, which extends this
 * class with values read from the item components.
 * <p>
 * {@code maxDurability} is {@code -1} for items without durability.
 */
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
