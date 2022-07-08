package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;

public class TestBaseMoveExecutor extends BaseMoveExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        move(entity, new Vector3(0, 1, 0));
        return false;
    }
}
