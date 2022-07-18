package cn.nukkit.entity.ai.route.blockevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;

/**
 * 方块评估器用于寻路器评估方块 <br/>
 * 通过编写特定的方块评估器，可以自定义寻路器的寻路策略
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBlockEvaluator {
    /**
     * 返回目标方块是否可以作为路径点
     *
     * @param entity 目标实体
     * @param block  评估方块
     * @return 是否可以作为路径点
     */
    boolean evalBlock(EntityIntelligent entity, Block block);
}
