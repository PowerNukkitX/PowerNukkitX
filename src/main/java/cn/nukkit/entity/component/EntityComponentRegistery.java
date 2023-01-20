package cn.nukkit.entity.component;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityAngryable;
import cn.nukkit.entity.EntityCanSit;
import cn.nukkit.entity.EntityTamable;
import cn.nukkit.entity.component.impl.EntityAngryComponent;
import cn.nukkit.entity.component.impl.EntitySittingComponent;
import cn.nukkit.entity.component.impl.EntityTameComponent;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体组件注册表<p/>
 * 通过将一个特定接口绑定到一个特定组件上后，实体将在实例化时依据类实现的接口自动创建组件对象
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public final class EntityComponentRegistery {

    @Getter
    private static final Map<Class<?>, Class<? extends EntityComponent>> COMPONENTS_REGISTRY;

    static {
        COMPONENTS_REGISTRY = new HashMap<>();
        registerDefaultComponents();
    }

    public static void register(Class<?> interfaceClazz, Class<? extends EntityComponent> componentClazz) {
        COMPONENTS_REGISTRY.put(interfaceClazz, componentClazz);
    }

    public static Class<? extends EntityComponent> getInterfaceBoundEntityComponent(Class<?> interfaceClazz) {
        return COMPONENTS_REGISTRY.get(interfaceClazz);
    }

    private static void registerDefaultComponents() {
        register(EntityTamable.class, EntityTameComponent.class);
        register(EntityAngryable.class, EntityAngryComponent.class);
        register(EntityCanSit.class, EntitySittingComponent.class);
    }
}
