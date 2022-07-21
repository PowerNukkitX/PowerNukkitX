package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;

/**
 * 坐标评估器用于寻路器评估坐标 <br/>
 * 通过编写特定的坐标评估器，可以自定义寻路器的寻路策略
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IPosEvaluator {
    /**
     * 返回目标坐标是否可以作为路径点
     *
     * @param entity 目标实体
     * @param pos  评估坐标
     * @return 是否可以作为路径点
     */
    boolean evalPos(EntityIntelligent entity, Vector3 pos);
}
