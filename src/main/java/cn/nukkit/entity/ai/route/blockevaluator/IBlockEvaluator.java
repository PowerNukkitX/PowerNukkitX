package cn.nukkit.entity.ai.route.blockevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;

/**
 * 方块评估器用于寻路器评估方块
 * 通过编写自己的方块评估器，可以自定义寻路器的寻路策略
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBlockEvaluator {
    /**
     * 评估方块
     * @param block
     * 方块坐标
     * @return int
     * 返回移动到此方块的代价，大的代价会指示寻路器寻找更符合要求的其他路径
     * 若返回-1，则代表此方块不可选取（代价无限大）
     */
    int evalBlock(Block block);
}
