package org.powernukkitx.entity.ai.executor.camel;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.EntityControl;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.passive.EntityCamel;
import org.powernukkitx.utils.Utils;

/**
 * Camel idle behavior that makes the entity sit down for a random duration.
 *
 * When started, the camel sits if there is no rider and the location allows
 * sitting. The sitting state lasts for a random time based on the configured
 * minimum duration, after which the camel stands up again.
 * 
 * @author Curse
 */
public class CamelSittingExecutor implements EntityControl, IBehaviorExecutor {

    private final int MIN_SITTING;
    private int ticks = 0;

    public CamelSittingExecutor(int minSitting) {
        MIN_SITTING = minSitting;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        return ticks-- >= 0;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        if (!(entity instanceof EntityCamel camel)) return;
        if (camel.getRider() != null) return;
        if (!camel.canSitHere()) return;

        ticks = Utils.rand(MIN_SITTING, MIN_SITTING + 10) * 20;

        camel.sitDown();
        removeRouteTarget(entity);
        removeLookTarget(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    private void stop(EntityIntelligent entity) {
        if (entity instanceof EntityCamel camel) {
            camel.standUp();
        }
    }
}
