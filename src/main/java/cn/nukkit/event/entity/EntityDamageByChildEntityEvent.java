package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityDamageByChildEntityEvent extends EntityDamageByEntityEvent {
    private final Entity childEntity;

    public EntityDamageByChildEntityEvent(Entity damager, Entity childEntity, Entity entity, DamageCause cause, float damage) {
        super(damager, entity, cause, damage);
        this.childEntity = childEntity;
    }

    public EntityDamageByChildEntityEvent(Entity damager, Entity childEntity, Entity entity, DamageCause cause, @NotNull Map<DamageModifier, Float> modifiers, float knockBack) {
        super(damager, entity, cause, modifiers, knockBack);
        this.childEntity = childEntity;
    }

    public EntityDamageByChildEntityEvent(Entity damager, Entity childEntity, Entity entity, DamageCause cause, @NotNull Map<DamageModifier, Float> modifiers, float knockBack, @Nullable Enchantment[] enchantments) {
        super(damager, entity, cause, modifiers, knockBack, enchantments);
        this.childEntity = childEntity;
    }

    public Entity getChild() {
        return childEntity;
    }
}
