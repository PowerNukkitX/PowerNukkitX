package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.level.Sound;

public class StaringAttackTargetExecutor implements IBehaviorExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        if(entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)) {
            if(!entity.getMemoryStorage().isEmpty(CoreMemoryTypes.STARING_PLAYER) && new EntityCheckEvaluator(CoreMemoryTypes.STARING_PLAYER).evaluate(entity)) {
                entity.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity.getMemoryStorage().get(CoreMemoryTypes.STARING_PLAYER));
                entity.level.addSound(entity, Sound.MOB_ENDERMEN_STARE);
            } else if(!entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_ENDERMITE)) {
                entity.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_ENDERMITE));
            }
        } else {
            if(entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof Player player) {
                if(!player.isOnline()) {
                    entity.getMemoryStorage().clear(CoreMemoryTypes.STARING_PLAYER);
                    entity.getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
                }
            }
        }
        if(entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)) {
            if(entity.getDataFlag(EntityFlag.ANGRY)) {
                entity.setDataFlag(EntityFlag.ANGRY, false);
            }
        } else {
            if(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET).evaluate(entity)) {
                if (!entity.getDataFlag(EntityFlag.ANGRY)) {
                    entity.setDataFlag(EntityFlag.ANGRY);
                }
            } else entity.getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
        }
        return true;
    }
}
