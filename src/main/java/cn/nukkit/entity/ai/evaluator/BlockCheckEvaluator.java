package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;


public class BlockCheckEvaluator implements IBehaviorEvaluator {

    protected String blockId;
    protected Vector3 offsetVec;
    /**
     * @deprecated 
     */
    

    public BlockCheckEvaluator(String blockId, Vector3 offsetVec) {
        this.blockId = blockId;
        this.offsetVec = offsetVec;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean evaluate(EntityIntelligent entity) {
        return entity.level.getTickCachedBlock(entity.add(offsetVec)).getId().equals(blockId);
    }
}
