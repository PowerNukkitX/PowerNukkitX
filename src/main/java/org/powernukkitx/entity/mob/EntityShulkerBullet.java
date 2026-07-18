package org.powernukkitx.entity.mob;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LiftController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.FlyingPosEvaluator;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EntityShulkerBullet extends EntityMob implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return SHULKER_BULLET;
    }

    public EntityShulkerBullet(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.2f, true), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 1, 1)
                ),
                Set.of(),
                Set.of(new SpaceMoveController(), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setHealthMax(1);
        super.initEntity();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        kill();
        return true;
    }

    @Override
    public float getWidth() {
        return 0.3125f;
    }

    @Override
    public float getHeight() {
        return 0.3125f;
    }

    @Override
    public String getOriginalName() {
        return "Shulker Bullet";
    }

    @Override
    public boolean onUpdate(int currentTick) {

        return super.onUpdate(currentTick);
    }

    @Override
    protected boolean onCollide(int currentTick, List<Entity> collidingEntities) {
        close();
        for(Entity entity : collidingEntities) {
            if(entity.attack(new EntityDamageByEntityEvent(this, entity, EntityDamageEvent.DamageCause.CONTACT, 4))) {
                getLevel().addSound(entity, Sound.MOB_SHULKER_BULLET_HIT);
                entity.addEffect(Effect.get(EffectType.LEVITATION).setDuration(200));
            }
        }
        return true;
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }
}
