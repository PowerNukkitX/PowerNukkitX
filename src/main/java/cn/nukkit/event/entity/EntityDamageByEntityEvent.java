package cn.nukkit.event.entity;

import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.potion.Effect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityDamageByEntityEvent extends EntityDamageEvent {

    @Nonnull
    private final Entity damager;

    private float knockBack;

    @Nullable
    private Enchantment[] enchantments;

    public EntityDamageByEntityEvent(@Nonnull Entity damager, @Nonnull Entity entity, @Nonnull DamageCause cause, float damage) {
        this(damager, entity, cause, damage, 0.3f);
    }

    public EntityDamageByEntityEvent(@Nonnull Entity damager, @Nonnull Entity entity, @Nonnull DamageCause cause, Map<DamageModifier, Float> modifiers) {
        this(damager, entity, cause, modifiers, 0.3f);
    }

    public EntityDamageByEntityEvent(@Nonnull Entity damager, @Nonnull Entity entity, @Nonnull DamageCause cause, float damage, float knockBack) {
        super(entity, cause, damage);
        this.damager = damager;
        this.knockBack = knockBack;
        this.addAttackerModifiers(damager);
    }

    public EntityDamageByEntityEvent(@Nonnull Entity damager, @Nonnull Entity entity, @Nonnull DamageCause cause, @Nonnull Map<DamageModifier, Float> modifiers, float knockBack) {
        this(damager, entity, cause, modifiers, knockBack, null);
    }

    @Since("FUTURE")
    public EntityDamageByEntityEvent(@Nonnull Entity damager, @Nonnull Entity entity, @Nonnull DamageCause cause, @Nonnull Map<DamageModifier, Float> modifiers, float knockBack, @Nullable Enchantment[] enchantments) {
        super(entity, cause, modifiers);
        if (enchantments != null) {
            enchantments = enchantments.length == 0 ? Enchantment.EMPTY_ARRAY : enchantments.clone();
            for (Enchantment enchantment : enchantments) {
                if (enchantment != null) {
                    addSideEffects(enchantment.getAttackSideEffects(damager, entity));
                }
            }
        }
        this.damager = damager;
        this.knockBack = knockBack;
        this.enchantments = enchantments;
        this.addAttackerModifiers(damager);
    }

    protected void addAttackerModifiers(Entity damager) {
        if (damager.hasEffect(Effect.STRENGTH)) {
            this.setDamage((float) (this.getDamage(DamageModifier.BASE) * 0.3 * (damager.getEffect(Effect.STRENGTH).getAmplifier() + 1)), DamageModifier.STRENGTH);
        }

        if (damager.hasEffect(Effect.WEAKNESS)) {
            this.setDamage(-(float) (this.getDamage(DamageModifier.BASE) * 0.2 * (damager.getEffect(Effect.WEAKNESS).getAmplifier() + 1)), DamageModifier.WEAKNESS);
        }
    }

    @Nonnull
    public Entity getDamager() {
        return damager;
    }

    public float getKnockBack() {
        return knockBack;
    }

    public void setKnockBack(float knockBack) {
        this.knockBack = knockBack;
    }

    @Since("FUTURE")
    @Nullable
    public Enchantment[] getWeaponEnchantments() {
        if (enchantments == null) {
            return null;
        }
        return enchantments.length > 0? enchantments.clone() : Enchantment.EMPTY_ARRAY;
    }
}
