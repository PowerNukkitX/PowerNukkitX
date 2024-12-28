package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.mob.EntitySlime;
import cn.nukkit.entity.passive.EntityRabbit;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JumpExecutor implements IBehaviorExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        entity.setMotion(new Vector3(0, entity.getJumpingMotion(0.4), 0));
        return true;
    }
}
