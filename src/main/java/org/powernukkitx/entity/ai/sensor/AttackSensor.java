package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;

public class AttackSensor implements ISensor {

    protected final MemoryType<Entity> entityMemory;

    public AttackSensor() {
        this(CoreMemoryTypes.ATTACK_TARGET);
    }

    public AttackSensor(MemoryType<Entity> entityMemory) {
        this.entityMemory = entityMemory;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        entity.getMemoryStorage().put(entityMemory, entity.getMemoryStorage().get(CoreMemoryTypes.BE_ATTACKED_EVENT).getEntity());
    }
}
