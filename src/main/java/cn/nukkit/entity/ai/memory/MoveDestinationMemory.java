package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

/**
 * MoveDestinationMemory用于存储实体的寻路过程中的某一时刻的目标移动位置
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class MoveDestinationMemory extends Vector3Memory {
    public MoveDestinationMemory(Vector3 vector3){
        super(vector3);
    }
}
