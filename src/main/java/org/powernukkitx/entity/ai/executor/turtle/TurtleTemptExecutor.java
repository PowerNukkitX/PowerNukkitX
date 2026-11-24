package org.powernukkitx.entity.ai.executor.turtle;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.TemptExecutor;

import java.util.Set;

public final class TurtleTemptExecutor extends TemptExecutor {
    /**
     * Creates a behavior that follows a player and adjusts speed while swimming.
     *
     * @param temptItems item IDs that entice the turtle
     */
    public TurtleTemptExecutor(@NotNull Set<String> temptItems) {
        super(1.1f, true, false, false, 10, 2.0f, null, temptItems);
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        boolean result = super.execute(entity);
        if (result && entity.isInsideOfWater()) {
            entity.setMovementSpeed(entity.getDefaultUnderWaterSpeed() * 1.1f);
        }
        return result;
    }
}
