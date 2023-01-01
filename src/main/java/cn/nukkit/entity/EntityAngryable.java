package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.component.impl.EntityAngryComponent;

/**
 * 可生气实体<p>
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public interface EntityAngryable {
    default boolean isAngry() {
        return getAngryComponent().isAngry();
    }

    default void setAngry(boolean angry) {
        getAngryComponent().setAngry(angry);
    }

    default EntityAngryComponent getAngryComponent(){
        return ((Entity) this).getComponentGroup().getComponent(EntityAngryComponent.class);
    }
}
