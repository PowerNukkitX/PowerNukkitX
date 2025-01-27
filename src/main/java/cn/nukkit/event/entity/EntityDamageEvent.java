package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.utils.EventException;
import com.google.common.collect.ImmutableMap;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityDamageEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final DamageCause cause;
    private final Map<DamageModifier, Float> modifiers;
    private final Map<DamageModifier, Float> originals;

    private boolean breakShield = false;
    private int attackCooldown = 10;
    private int ShieldBreakCoolDown = 100;

    private static Map<DamageModifier, Float> createDamageModifierMap(float baseDamage) {
        Map<DamageModifier, Float> modifiers = new EnumMap<>(DamageModifier.class);
        modifiers.put(DamageModifier.BASE, baseDamage);
        return modifiers;
    }

    public EntityDamageEvent(Entity entity, DamageCause cause, float damage) {
        this(entity, cause, createDamageModifierMap(damage));
    }

    public EntityDamageEvent(Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers) {
        this.entity = entity;
        this.cause = cause;
        this.modifiers = new EnumMap<>(modifiers);

        this.originals = ImmutableMap.copyOf(this.modifiers);

        if (!this.modifiers.containsKey(DamageModifier.BASE)) {
            throw new EventException("BASE Damage modifier missing");
        }

        if (entity.hasEffect(EffectType.RESISTANCE)) {
            this.setDamage((float) -(this.getDamage(DamageModifier.BASE) * 0.20 * entity.getEffect(EffectType.RESISTANCE).getLevel()), DamageModifier.RESISTANCE);
        }
    }

    public DamageCause getCause() {
        return cause;
    }

    public float getOriginalDamage() {
        return this.getOriginalDamage(DamageModifier.BASE);
    }

    public float getOriginalDamage(DamageModifier type) {
        if (this.originals.containsKey(type)) {
            return this.originals.get(type);
        }

        return 0;
    }

    public float getDamage() {
        return this.getDamage(DamageModifier.BASE);
    }

    public float getDamage(DamageModifier type) {
        if (this.modifiers.containsKey(type)) {
            return this.modifiers.get(type);
        }

        return 0;
    }

    public void setDamage(float damage) {
        this.setDamage(damage, DamageModifier.BASE);
    }

    public void setDamage(float damage, DamageModifier type) {
        this.modifiers.put(type, damage);
    }

    public boolean isApplicable(DamageModifier type) {
        return this.modifiers.containsKey(type);
    }

    public float getFinalDamage() {
        float damage = 0;
        for (Float d : this.modifiers.values()) {
            if (d != null) {
                damage += d;
            }
        }

        return damage;
    }

    public int getAttackCooldown() {
        return this.attackCooldown;
    }

    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public boolean canBeReducedByArmor() {
        return switch (this.cause) {
            case FIRE_TICK, SUFFOCATION, DROWNING, HUNGER, FALL, VOID, MAGIC, SUICIDE -> false;
            default -> true;
        };
    }

    public boolean isBreakShield() {
        return breakShield;
    }

    public void setBreakShield(boolean breakShield) {
        this.breakShield = breakShield;
    }

    public int getShieldBreakCoolDown() {
        return ShieldBreakCoolDown;
    }

    public void setShieldBreakCoolDown(int shieldBreakCoolDown) {
        ShieldBreakCoolDown = shieldBreakCoolDown;
    }

    public enum DamageModifier {
        /**
         * Raw amount of damage
         */
        BASE,
        /**
         * Damage reduction caused by wearing armor
         */
        ARMOR,
        /**
         * Additional damage caused by damager's Strength potion effect
         */
        STRENGTH,
        /**
         * Damage reduction caused by damager's Weakness potion effect
         */
        WEAKNESS,
        /**
         * Damage reduction caused by the Resistance potion effect
         */
        RESISTANCE,
        /**
         * Damage reduction caused by the Damage absorption effect
         */
        ABSORPTION,
        /**
         * Damage reduction caused by the armor enchantments worn.
         */
        ARMOR_ENCHANTMENTS
    }

    public enum DamageCause {
        /**
         * Damage caused by contact with a block such as a Cactus
         */
        CONTACT,
        /**
         * Damage caused by being attacked by another entity
         */
        ENTITY_ATTACK,
        /**
         * Damage caused by being hit by a projectile such as an Arrow
         */
        PROJECTILE,
        /**
         * Damage caused by being put in a block
         */
        SUFFOCATION,
        /**
         * Fall damage
         */
        FALL,
        /**
         * Damage caused by standing in fire
         */
        FIRE,
        /**
         * Burn damage
         */
        FIRE_TICK,
        /**
         * Damage caused by standing in lava
         */
        LAVA,
        /**
         * Damage caused by running out of air underwater
         */
        DROWNING,
        /**
         * Block explosion damage
         */
        BLOCK_EXPLOSION,
        /**
         * Entity explosion damage
         */
        ENTITY_EXPLOSION,
        /**
         * Damage caused by falling into the void
         */
        VOID,
        /**
         * Player commits suicide
         */
        SUICIDE,
        /**
         * Potion or spell damage
         */
        MAGIC,
        /**
         * Plugins
         */
        CUSTOM,
        /**
         * Damage caused by being struck by lightning
         */
        LIGHTNING,
        /**
         * Damage caused by hunger
         */
        HUNGER,
        /**
         * Damage caused by Wither
         */
        WITHER,
        /**
         * Damage caused by weather like a snowman takes damage by rain
         */
        WEATHER,
        /**
         * Damage caused by thorns
         */
        THORNS,
        /**
         * Damage caused by falling block
         */
        FALLING_BLOCK,
        /**
         * Damage caused by flying into wall
         */
        FLYING_INTO_WALL,
        /**
         * Damage caused when an entity steps on a hot block, like {@link cn.nukkit.block.BlockID#MAGMA}
         */
        HOT_FLOOR,
        /**
         * Damage caused by fireworks
         */
        FIREWORKS,
        /**
         * Damage caused by temperature
         */
        FREEZING,
        /**
         * Damage caused by no reason (eg: /damage command with cause NONE)
         */
        NONE,
        /**
         * Damage caused by a lot of (>24) entities colliding together
         */
        COLLIDE,
        /**
         * Damage caused by mace attack
         */
        MACE_SMASH,
        /**
         * Damage caused by ageing
         */
        AGE
    }
}
