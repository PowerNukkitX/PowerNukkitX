package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NearestBlockIncementExecutor implements IBehaviorExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        if(!entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK)) {
            entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_BLOCK, entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK).up());
        }
        return true;
    }
}
