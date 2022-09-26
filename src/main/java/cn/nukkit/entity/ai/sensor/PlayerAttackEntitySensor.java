package cn.nukkit.entity.ai.sensor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.PlayerAttackEntityMemory;
import cn.nukkit.entity.passive.EntityTamable;

import java.util.Objects;

public class PlayerAttackEntitySensor implements ISensor {
    protected int period;
    protected boolean changeTarget;

    public PlayerAttackEntitySensor(int period, boolean changeTarget) {
        this.period = period;
        this.changeTarget = changeTarget;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        if (entity instanceof EntityTamable entityTamable) {
            var player = entityTamable.getOwner();
            if (player != null) {
                var currentMemory = Objects.requireNonNull(entity.getMemoryStorage()).get(PlayerAttackEntityMemory.class);
                if (!changeTarget) {
                    if (currentMemory.getData() != null && currentMemory.getData().isAlive()) return;
                    else currentMemory.setData(null);
                }
                if (player.getLastBeAttackEntity() != null) {
                    currentMemory.setData(player.getLastBeAttackEntity());
                } else if (player.getLastAttackEntity() != null) {
                    currentMemory.setData(player.getLastAttackEntity());
                } else currentMemory.setData(null);
            }
        }
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
