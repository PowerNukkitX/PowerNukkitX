package cn.nukkit.entity.ai.route.blockevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class FlyingBlockEvaluator implements IBlockEvaluator{
    @Override
    public int evalBlock(EntityIntelligent entity, Block block) {
        return 0;
    }
}
