package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class AttackTargetMemory extends EntityMemory<Entity> {

    public AttackTargetMemory() {
        super(null);
    }

    public AttackTargetMemory(Entity entity) {
        super(entity);
    }
}
