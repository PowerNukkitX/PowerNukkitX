package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * 坐标评估器用于寻路器评估坐标 <br/>
 * 通过编写特定的坐标评估器，可以自定义寻路器的寻路策略
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IPosEvaluator {
    /**
     * 返回目标坐标是否可以作为路径点，通常用于返回非整数坐标点（飞行和游泳）的实体
     *
     * @param entity 目标实体
     * @param pos  评估坐标
     * @return 是否可以作为路径点
     */
    boolean evalPos(@NotNull EntityIntelligent entity, @NotNull Vector3 pos);

    /**
     * 返回此方块是否可以作为脚下站立的方块，通常用于返回整数坐标点（行走）的实体
     *
     * @param entity 目标实体
     * @param block 评估方块
     * @return 是否可以作为脚下站立的方块
     */
    boolean evalStandingBlock(@NotNull EntityIntelligent entity, @NotNull Block block);
}
