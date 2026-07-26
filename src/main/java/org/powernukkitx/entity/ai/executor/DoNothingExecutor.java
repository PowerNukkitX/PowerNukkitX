package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;

public class DoNothingExecutor implements IBehaviorExecutor {

    public DoNothingExecutor() {
    }
    @Override
    public boolean execute(EntityIntelligent entity) {
        return true;
    }
}
