package org.powernukkitx.entity.mob;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.BreezeJumpExecutor;
import org.powernukkitx.entity.ai.executor.BreezeShootExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EntityBreeze extends EntityMob {
    @Override @NotNull public String getIdentifier() {
        return BREEZE;
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("breeze", "monster", "mob");
    }

    public EntityBreeze(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .coreBehaviors(
                        new Behavior(new BreezeShootExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 2, 1),
                        new Behavior(new BreezeShootExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.4f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1)
                )
                .behaviors(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_BREEZE_IDLE_AIR), all(new RandomSoundEvaluator(), entity -> !isOnGround()), 7, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_BREEZE_IDLE_GROUND), all(new RandomSoundEvaluator(), entity -> isOnGround()), 6, 1),
                        new Behavior(new BreezeJumpExecutor(), all(any(Entity::isOnGround, Entity::isInsideOfWater), entity -> getRiding() == null, entity -> !isInsideOfLava()), 5, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 1.2f, true), all(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 24)), 4, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 1.2f, true), all(new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), new DistanceEvaluator(CoreMemoryTypes.NEAREST_PLAYER, 24)), 3, 1),
                        new Behavior(new FlatRandomRoamExecutor(1f, 12, 100, false, -1, true, 10), none(), 1, 1)
                )
                .sensors(new NearestPlayerSensor(24, 0, 20))
                .controllers(new WalkController(), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    @Override
    public float getHeight() {
        return 1.77F;
    }

    @Override
    public float getWidth() {
        return 0.6F;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(30);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.4f);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        int min = 1 + looting;
        int max = 2 + (looting * 2);

        int amount = Utils.rand(min, max);

        return new Item[]{
                Item.get(Item.BREEZE_ROD, 0, amount)
        };
    }

    @Override
    public Integer getExperienceDrops() {
        return 10;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.FALL) {
            return false;
        }
        return super.attack(source);
    }
}
