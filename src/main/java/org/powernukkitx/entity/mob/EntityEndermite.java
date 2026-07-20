package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockSoulSand;
import org.powernukkitx.entity.EntityArthropod;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestEntitySensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author Box.
 */
public class EntityEndermite extends EntityMob implements EntityWalkable, EntityArthropod {

    @Override
    @NotNull public String getIdentifier() {
        return ENDERMITE;
    }

    public EntityEndermite(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ENDERMITE_SAY), new RandomSoundEvaluator(), 6, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 16, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.3f, 16, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 16, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(16, 0, 0),
                        new NearestEntitySensor(EntityIronGolem.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 16, 0),
                        new NearestEntitySensor(EntityEnderman.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 16, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{2f, 2f, 3f};
        super.initEntity();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(currentTick%10 == 0) {
            if(getLevelBlock().down() instanceof BlockSoulSand) {
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.SUFFOCATION, 1));
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.3f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(8);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    public String getOriginalName() {
        return "Endermite";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("endermite", "arthropod", "monster", "lightweight", "mob");
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public Integer getExperienceDrops() {
        return 3;
    }
}
