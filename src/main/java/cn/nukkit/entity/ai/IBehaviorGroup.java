package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.message.Message;

/**
 * 行为组接口
 * 一个行为组可以包含多个行为
 * 当评估时，会将评估下发到包含的所有行为，并返回响应且优先级最高的行为（可以为多个）
 * 接着，调度器将会启用该行为的执行器（若此行为没有在运行），如果正在运行的行为的优先级低于此行为，将会被中断
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehaviorGroup {
    /**
     * @param entity
     * @param message
     * 向行为组发送消息，行为组将会评估所有行为
     */
    void message(EntityIntelligent entity, Message message);
}
