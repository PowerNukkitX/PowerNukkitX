package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * NeedUpdateMoveDestinationMemory用于标记一个实体是否需要新的目标移动位置（与MoveDestinationMemory搭配使用）
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NeedUpdateMoveDestinationMemory extends BooleanMemory {
    public NeedUpdateMoveDestinationMemory(Boolean data){
        super(data);
    }
}
