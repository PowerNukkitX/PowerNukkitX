package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class LookTargetMemory extends Vector3Memory{

    public LookTargetMemory() {
        super(null);
    }

    public LookTargetMemory(Vector3 vector3) {
        super(vector3);
    }
}
