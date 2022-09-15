package cn.nukkit.entity.ai.memory;

import cn.nukkit.entity.Entity;

public class NearestEntityMemory extends EntityMemory<Entity> {
    public NearestEntityMemory() {
        super(null);
    }

    public NearestEntityMemory(Entity entity) {
        super(entity);
    }
}
