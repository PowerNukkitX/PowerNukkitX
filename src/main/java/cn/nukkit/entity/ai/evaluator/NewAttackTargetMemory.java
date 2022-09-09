package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.memory.BooleanMemory;

@PowerNukkitXOnly
@Since("1.19.21-r4")
public class NewAttackTargetMemory extends BooleanMemory {

    public NewAttackTargetMemory() {
        super(null);
    }

    public NewAttackTargetMemory(Boolean data) {
        super(data);
    }
}
