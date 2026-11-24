package org.powernukkitx.entity.ai.executor.turtle;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;

public final class TurtleLookExecutor extends LookAtTargetExecutor {
    /**
     * Creates a behavior that watches nearby living players.
     */
    public TurtleLookExecutor() {
        super(CoreMemoryTypes.NEAREST_PLAYER, 100);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
        return player != null && player.isAlive() && entity.distanceSquared(player) <= 36
                && super.execute(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        super.onStop(entity);
        removeLookTarget(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        super.onInterrupt(entity);
        removeLookTarget(entity);
    }
}
