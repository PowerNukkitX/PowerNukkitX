package cn.nukkit.entity.ai.memory.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.memory.NearestEntityMemory;

/**
 * 保存狼最近的骷髅类怪物.
 * <p>
 * Save the wolf nearest skeleton-like monster.
 */
@PowerNukkitXOnly
@Since("1.19.21-r5")
public class WolfNearestSkeletonMemory extends NearestEntityMemory {
    public WolfNearestSkeletonMemory() {
        super(null);
    }

    public WolfNearestSkeletonMemory(Entity entity) {
        super(entity);
    }
}
