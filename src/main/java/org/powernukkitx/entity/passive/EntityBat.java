package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LiftController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.FlyingPosEvaluator;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityBat extends EntityAnimal implements EntityFlyable {
    @Override
    @NotNull public String getIdentifier() {
        return BAT;
    }

    public EntityBat(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new SpaceRandomRoamExecutor(0.3f, 12, 100, 20, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(6);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    public String getOriginalName() {
        return "Bat";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("bat", "mob");
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }
}
