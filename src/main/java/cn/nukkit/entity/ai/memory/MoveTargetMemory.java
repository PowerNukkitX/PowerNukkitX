package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

/**
 * 与MoveDirectionMemory不同，此Memory存储最终目标位置，而不是当前目标位置
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class MoveTargetMemory extends Vector3Memory {
    public MoveTargetMemory(Vector3 vector3) {
        super(vector3);
    }
}
