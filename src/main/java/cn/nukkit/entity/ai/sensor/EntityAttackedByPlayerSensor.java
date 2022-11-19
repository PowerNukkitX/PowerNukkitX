package cn.nukkit.entity.ai.sensor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityTamable;

public class EntityAttackedByPlayerSensor implements ISensor {
    protected int period;
    protected boolean changeTarget;

    public EntityAttackedByPlayerSensor(int period, boolean changeTarget) {
        this.period = period;
        this.changeTarget = changeTarget;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        if (entity instanceof EntityTamable entityTamable) {
            var player = entityTamable.getOwner();
            if (player != null) {
                var current = entity.getMemoryStorage().get(CoreMemoryTypes.ENTITY_ATTACKED_BY_PLAYER);
                if (!changeTarget) {
                    if (current != null && current.isAlive()) return;
                    else entity.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_ATTACKED_BY_PLAYER);
                }
                //todo: 这里对于 CoreMemoryTypes.ENTITY_ATTACKED_BY_PLAYER 的使用存在二义性，等待解决
                if (player.getLastBeAttackEntity() != null) {
                    entity.getMemoryStorage().put(CoreMemoryTypes.ENTITY_ATTACKED_BY_PLAYER, player.getLastBeAttackEntity());
                } else if (player.getLastAttackEntity() != null) {
                    entity.getMemoryStorage().put(CoreMemoryTypes.ENTITY_ATTACKED_BY_PLAYER, player.getLastAttackEntity());
                } else entity.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_ATTACKED_BY_PLAYER);
            }
        }
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
