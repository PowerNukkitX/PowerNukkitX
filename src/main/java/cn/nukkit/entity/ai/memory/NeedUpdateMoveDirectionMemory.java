package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;

/**
 * NeedUpdateMoveDirectionMemory标记一个实体需要新的目标移动位置 <br/>
 * 行为组{@link IBehaviorGroup}将提供新的移动位置，并从记忆存储器{@link IMemoryStorage}中删除此记忆
 * 与{@link MoveDirectionMemory}搭配使用
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NeedUpdateMoveDirectionMemory extends BooleanMemory {
    public NeedUpdateMoveDirectionMemory(Boolean data) {
        super(data);
    }
}
