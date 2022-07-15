package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

/**
 * 控制器是用来控制实体的行为的，比如移动、跳跃、攻击等等的具体实现。<br/>
 * 对于不同实体，可以提供不同的控制器，以实现上述行为的特殊实现。
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IController {
    /**
     * 实施行为
     * @param entity 目标实体
     * @return 是否成功实施行为
     */
    boolean control(EntityIntelligent entity);
}
