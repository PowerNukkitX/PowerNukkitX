package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * NeedUpdateTargetMemory用于标记一个实体是否需要重新规划路线（通常在目的地更改时）
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NeedUpdateTargetMemory extends BooleanMemory {
    public NeedUpdateTargetMemory(Boolean data) {
        super(data);
    }
}
