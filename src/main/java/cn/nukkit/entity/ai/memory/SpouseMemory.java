package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SpouseMemory<E extends Entity> extends EntityMemory<E>{

    public SpouseMemory() {
        super(null);
    }

    public SpouseMemory(E spouse) {
        super(spouse);
    }
}
