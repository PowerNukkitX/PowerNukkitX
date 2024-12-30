package cn.nukkit.entity.mob;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.BreezeJumpExecutor;
import cn.nukkit.entity.ai.executor.BreezeShootExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EntityBreeze extends EntityMob {
    @Override @NotNull public String getIdentifier() {
        return BREEZE;
    }

    public EntityBreeze(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new BreezeShootExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 2, 1),
                        new Behavior(new BreezeShootExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.4f, 15, true, 30, 20), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1)
                ),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_BREEZE_IDLE_AIR), all(new RandomSoundEvaluator(), entity -> !isOnGround()), 7, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_BREEZE_IDLE_GROUND), all(new RandomSoundEvaluator(), entity -> isOnGround()), 6, 1),
                        new Behavior(new BreezeJumpExecutor(), all(any(Entity::isOnGround, Entity::isInsideOfWater), entity -> getRiding() == null, entity -> !isInsideOfLava()), 5, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 1.2f, true), all(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 24)), 4, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 1.2f, true), all(new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), new DistanceEvaluator(CoreMemoryTypes.NEAREST_PLAYER, 24)), 3, 1),
                        new Behavior(new FlatRandomRoamExecutor(1f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(24, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();
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
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.BREEZE_ROD, 0, Utils.rand(1, 2))};
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
