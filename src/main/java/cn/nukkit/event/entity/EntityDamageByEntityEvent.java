package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityDamageByEntityEvent extends EntityDamageEvent {
    private @NotNull final Entity damager;

    private float knockBack;
    private @Nullable Enchantment[] enchantments;

    public EntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity entity, @NotNull DamageCause cause, float damage) {
        this(damager, entity, cause, damage, 0.3f);
    }

    public EntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity entity, @NotNull DamageCause cause, Map<DamageModifier, Float> modifiers) {
        this(damager, entity, cause, modifiers, 0.3f);
    }

    public EntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity entity, @NotNull DamageCause cause, float damage, float knockBack) {
        super(entity, cause, damage);
        this.damager = damager;
        this.knockBack = knockBack;
        this.addAttackerModifiers(damager);
    }

    public EntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity entity, @NotNull DamageCause cause, @NotNull Map<DamageModifier, Float> modifiers, float knockBack) {
        this(damager, entity, cause, modifiers, knockBack, null);
    }

    public EntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity entity, @NotNull DamageCause cause, @NotNull Map<DamageModifier, Float> modifiers, float knockBack, @Nullable Enchantment[] enchantments) {
        super(entity, cause, modifiers);
        this.damager = damager;
        this.knockBack = knockBack;
        this.enchantments = enchantments;
        this.addAttackerModifiers(damager);
    }

    protected void addAttackerModifiers(Entity damager) {
        if (damager.hasEffect(EffectType.STRENGTH)) {
            this.setDamage((float) (this.getDamage(DamageModifier.BASE) * 0.3 * damager.getEffect(EffectType.STRENGTH).getLevel()), DamageModifier.STRENGTH);
        }

        if (damager.hasEffect(EffectType.WEAKNESS)) {
            this.setDamage(-(float) (this.getDamage(DamageModifier.BASE) * 0.2 * damager.getEffect(EffectType.WEAKNESS).getLevel()), DamageModifier.WEAKNESS);
        }
    }

    @NotNull public Entity getDamager() {
        return damager;
    }

    public float getKnockBack() {
        return knockBack;
    }

    public void setKnockBack(float knockBack) {
        this.knockBack = knockBack;
    }

    public @Nullable Enchantment[] getWeaponEnchantments() {
        if (enchantments == null) {
            return null;
        }
        return enchantments.length > 0 ? enchantments.clone() : Enchantment.EMPTY_ARRAY;
    }
}
