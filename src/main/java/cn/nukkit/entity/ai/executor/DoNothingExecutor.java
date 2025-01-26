package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;

public class DoNothingExecutor implements IBehaviorExecutor {

    public DoNothingExecutor() {
    }
    @Override
    public boolean execute(EntityIntelligent entity) {
        return true;
    }
}
