package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.component.impl.EntitySittingComponent;

/**
 * 可坐下实体接口<p>
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public interface EntityCanSit {
    default boolean isSitting() {
        return getSettingComponent().isSitting();
    }

    default void setSitting(boolean sitting) {
        getSettingComponent().setSitting(sitting);
    }

    default EntitySittingComponent getSettingComponent(){
        return ((Entity) this).getComponentGroup().getComponent(EntitySittingComponent.class);
    }
}
