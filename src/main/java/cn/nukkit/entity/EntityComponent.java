package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.memory.IMemoryStorage;

/**
 * 继承了此接口的接口为一个实体组件<br>
 * 实体组件的实现使用default方法承载逻辑，相关值则使用记忆存储器存储
 * <p>
 * The interface that inherits this interface is an entity component<br>
 * The implementation of the entity component uses the default method to carry the logic, and the related values are stored in memory
 */
@PowerNukkitXOnly
@Since("1.19.63-r1")
public interface EntityComponent {
    IMemoryStorage getMemoryStorage();

    default Entity asEntity() {
        return getMemoryStorage().getEntity();
    }
}
