package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.CrossBowShootExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EntityPillager extends EntityIllager implements EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return PILLAGER;
    }
    

    public EntityPillager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PILLAGER_IDLE, 0.8f, 1.2f, 0.8f, 0.8f), new RandomSoundEvaluator(), 5, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 30, 80), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 30, 80), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 3, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 15, true, 30, 80), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
        setItemInHand(Item.get(Item.CROSSBOW));
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(24);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.35f);
    }

    @Override
    public String getOriginalName() {
        return "Pillager";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("pillager", "monster", "illager", "mob");
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return super.attackTarget(entity) || entity instanceof EntityGolem;
    }
}
