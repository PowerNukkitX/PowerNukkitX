package cn.nukkit.entity.component;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.component.exception.EntityComponentNotFoundException;

import java.util.Map;

/**
 * 实体组件组
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public interface EntityComponentGroup {

    /**
     * 获取已注册的组件
     *
     * @return 已注册的组件
     */
    Map<Class<? extends EntityComponent>, EntityComponent> getComponents();

    default <T extends EntityComponent> T getComponent(Class<T> clazz) {
        var component = (T) getComponents().get(clazz);
        if (component == null) {
            throw new EntityComponentNotFoundException("Entity component not found: " + clazz.getSimpleName());
        }
        return component;
    }

    default <T extends EntityComponent> boolean hasComponent(Class<T> clazz) {
        return getComponents().containsKey(clazz);
    }

    default void onInitEntity() {
        getComponents().values().forEach(EntityComponent::onInitEntity);
    }

    default void onSaveNBT() {
        getComponents().values().forEach(EntityComponent::onSaveNBT);
    }
}
