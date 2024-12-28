package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
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
