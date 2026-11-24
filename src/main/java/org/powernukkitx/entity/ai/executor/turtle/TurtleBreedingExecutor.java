package org.powernukkitx.entity.ai.executor.turtle;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.components.BreedableComponent;

import java.util.concurrent.ThreadLocalRandom;

public final class TurtleBreedingExecutor extends BreedingExecutor {
    /**
     * Creates a breeding behavior with the turtle-specific mating range and delay.
     */
    public TurtleBreedingExecutor() {
        super(8, 60, 0.1f);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        EntityIntelligent spouse = getSpouse(entity);
        if (spouse != null) {
            if (!spouse.isAlive() || spouse.isPregnant()
                    || !spouse.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)) {
                onInterrupt(entity);
                return false;
            }
            if (entity.distanceSquared(spouse) > 9) setBreedingTick(entity, -1);
        }
        boolean result = super.execute(entity);
        if (result && entity.isInsideOfWater()) entity.setMovementSpeed(entity.getDefaultUnderWaterSpeed());
        return result;
    }

    @Override
    protected boolean isValidMatePair(EntityIntelligent a, BreedableComponent ac, EntityIntelligent b, BreedableComponent bc) {
        return a.isAlive() && b.isAlive() && !a.isPregnant() && !b.isPregnant()
                && super.isValidMatePair(a, ac, b, bc);
    }

    @Override
    protected void breed(EntityIntelligent a, EntityIntelligent b) {
        if (!a.isAlive() || !b.isAlive() || a.distanceSquared(b) > 9 || a.isPregnant() || b.isPregnant()) return;
        super.breed(a, b);
        a.level.dropExpOrb(a, ThreadLocalRandom.current().nextInt(1, 8));
    }
}
