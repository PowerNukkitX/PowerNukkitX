package cn.nukkit.entity.ai.sensor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityOwnable;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

public class EntityAttackedByOwnerSensor implements ISensor {
    protected int period;
    protected boolean changeTarget;
    /**
     * @deprecated 
     */
    

    public EntityAttackedByOwnerSensor(int period, boolean changeTarget) {
        this.period = period;
        this.changeTarget = changeTarget;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sense(EntityIntelligent entity) {
        if (entity instanceof EntityOwnable entityTamable) {
            var $1 = entityTamable.getOwner();
            if (player != null) {
                var $2 = entity.getMemoryStorage().get(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER);
                if (!changeTarget) {
                    if (current != null && current.isAlive()) return;
                    else entity.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER);
                }
                if (player.getLastBeAttackEntity() != null) {
                    entity.getMemoryStorage().put(CoreMemoryTypes.ENTITY_ATTACKING_OWNER, player.getLastBeAttackEntity());
                } else if (player.getLastAttackEntity() != null) {
                    entity.getMemoryStorage().put(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER, player.getLastAttackEntity());
                } else entity.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER);
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPeriod() {
        return period;
    }
}
