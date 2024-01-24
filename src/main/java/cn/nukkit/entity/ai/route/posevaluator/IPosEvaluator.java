package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * 坐标评估器用于寻路器评估坐标 <br/>
 * 通过编写特定的坐标评估器，可以自定义寻路器的寻路策略
 * <p>
 * {@link IPosEvaluator} are used by {@link cn.nukkit.entity.ai.route.finder.IRouteFinder IRouteFinder} to evaluate coordinates<br>
 * By writing specific coordinate {@link IPosEvaluator}, {@link cn.nukkit.entity.ai.route.finder.IRouteFinder IRouteFinder}'s pathfinding strategy can be customized
 */


public interface IPosEvaluator {
    /**
     * 返回目标坐标是否可以作为路径点，通常用于返回非整数坐标点（飞行和游泳）的实体 <br>
     * 如果此使用此评估器的寻路器返回非整数坐标点，才需要实现此方法。
     * <p>
     * Returns whether the target coordinates can be used as a waypoint, usually used for entities that return non-integer coordinates (flying and swimming)<br>
     * Only need to implement this method if the pathfinder using this evaluator returns non-integer coordinates.
     *
     * @param entity 目标实体
     * @param pos    评估坐标
     * @return 是否可以作为路径点
     */
    default boolean evalPos(@NotNull EntityIntelligent entity, @NotNull Vector3 pos) {
        return true;
    }

    /**
     * 返回此方块是否可以作为脚下站立的方块，通常用于返回整数坐标点（行走）的实体 <br>
     * 如果此使用此评估器的寻路器只返回整数坐标点，才需要实现此方块。
     * <p>
     * Returns whether this block can be used as a standing block, typically used for entities that return integer coordinates (walking)<br>
     * Only need to implement this if the pathfinder using this evaluator only returns integer coordinates.
     *
     * @param entity 目标实体
     * @param block  评估方块
     * @return 是否可以作为脚下站立的方块
     */
    default boolean evalStandingBlock(@NotNull EntityIntelligent entity, @NotNull Block block) {
        return true;
    }
}
