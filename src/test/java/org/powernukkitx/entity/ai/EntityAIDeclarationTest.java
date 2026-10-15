package org.powernukkitx.entity.ai;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The declaration order is what decides priorities, so these check the chain turns into the right
 * priorities, and that running behaviors get interrupted or stopped as they should.
 */
public class EntityAIDeclarationTest {

    private static EntityIntelligent zombie;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Level level = ServerMockFixture.level;
        level.getChunk(0, 0, true);
        zombie = (EntityIntelligent) Entity.createEntity("minecraft:zombie", new Position(0.5, 64, 0.5, level));
    }

    private static IBehaviorExecutor executor() {
        return entity -> true;
    }

    @Test
    void declarationOrderSetsPriorities() {
        IBehaviorGroup group = EntityAI.of(zombie)
                .behavior(executor())
                .behavior(executor()).alongsidePrevious()
                .behavior(executor())
                .behavior(executor())
                .build();

        List<Integer> priorities = group.getBehaviors().stream().map(behavior -> behavior.getPriority()).sorted().toList();
        Assertions.assertEquals(List.of(1, 2, 3, 3), priorities);
    }

    @Test
    void evaluateOnceDisablesReevaluation() {
        IBehaviorGroup group = EntityAI.of(zombie)
                .behavior(executor())
                .behavior(executor()).evaluateOnce()
                .build();

        long reevaluated = group.getBehaviors().stream().filter(behavior -> behavior.shouldReevaluate()).count();
        Assertions.assertEquals(1, reevaluated);
    }

    @Test
    void interruptsRunningBehaviorWhenItsEvaluatorFails() {
        var passing = new AtomicBoolean(true);
        var interrupted = new AtomicBoolean();
        var stopped = new AtomicBoolean();
        IBehaviorExecutor executor = new IBehaviorExecutor() {
            @Override
            public boolean execute(EntityIntelligent entity) {
                return true;
            }

            @Override
            public void onInterrupt(EntityIntelligent entity) {
                interrupted.set(true);
            }

            @Override
            public void onStop(EntityIntelligent entity) {
                stopped.set(true);
            }
        };
        IBehaviorGroup group = EntityAI.of(zombie).behavior(executor).when(entity -> passing.get()).build();

        group.evaluateBehaviors(zombie);
        Assertions.assertEquals(1, group.getRunningBehaviors().size());

        passing.set(false);
        group.tickRunningBehaviors(zombie);
        Assertions.assertTrue(interrupted.get());
        Assertions.assertFalse(stopped.get());
        Assertions.assertTrue(group.getRunningBehaviors().isEmpty());
    }

    @Test
    void stopsRunningBehaviorWhenItsExecutorFinishes() {
        var stopped = new AtomicBoolean();
        var interrupted = new AtomicBoolean();
        var executions = new AtomicInteger();
        IBehaviorExecutor executor = new IBehaviorExecutor() {
            @Override
            public boolean execute(EntityIntelligent entity) {
                executions.incrementAndGet();
                return false;
            }

            @Override
            public void onInterrupt(EntityIntelligent entity) {
                interrupted.set(true);
            }

            @Override
            public void onStop(EntityIntelligent entity) {
                stopped.set(true);
            }
        };
        IBehaviorGroup group = EntityAI.of(zombie).behavior(executor).build();

        group.evaluateBehaviors(zombie);
        group.tickRunningBehaviors(zombie);
        Assertions.assertEquals(1, executions.get());
        Assertions.assertTrue(stopped.get());
        Assertions.assertFalse(interrupted.get());
    }
}
