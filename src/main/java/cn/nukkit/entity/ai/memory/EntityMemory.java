package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntityMemory<E extends Entity> extends Vector3Memory<E> {
    public EntityMemory(E entity) {
        super(entity);
    }
}
