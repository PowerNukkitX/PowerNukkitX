package cn.nukkit.entity.mob;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.executor.BlazeShootExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
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
        this.setMaxHealth(1);
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
