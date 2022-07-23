package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockCheckEvaluator implements IBehaviorEvaluator{

    protected int blockId;
    protected Vector3 offsetVec;

    public BlockCheckEvaluator(int blockId, Vector3 offsetVec){
        this.blockId = blockId;
        this.offsetVec = offsetVec;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return entity.add(offsetVec).getLevelBlock().getId() == blockId;
    }
}
