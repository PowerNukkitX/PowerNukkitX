package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockTurtleEgg;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.JumpExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityTransformEvent;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityHusk extends EntityZombie {

    @Override
    @NotNull
    public String getIdentifier() {
        return HUSK;
    }

    public EntityHusk(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private static final String NBT_HUSK_RIDER = "IsHuskRider";


    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .behaviors(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_HUSK_AMBIENT, isBaby() ? 1.3f : 0.8f, isBaby() ? 1.7f : 1.2f, 1, 1), new RandomSoundEvaluator(), 7, 1),
                        new Behavior(new JumpExecutor(), all(entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_BLOCK), entity -> entity.getCollisionBlocks().stream().anyMatch(block -> block instanceof BlockTurtleEgg)), 6, 1, 10),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.3f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK), 5, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 10, Effect.get(EffectType.HUNGER).setDuration(140)),new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 10, Effect.get(EffectType.HUNGER).setDuration(140)), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 3, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, true, 30, Effect.get(EffectType.HUNGER).setDuration(140)), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                )
                .sensors(
                        new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new BlockSensor(BlockTurtleEgg.class, CoreMemoryTypes.NEAREST_BLOCK, 11, 15, 10)

                )
                .controllers(new WalkController(), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
        this.setDataProperty(ActorDataTypes.AMBIENT_SOUND_INTERVAL, 8f);
        this.setDataProperty(ActorDataTypes.AMBIENT_SOUND_INTERVAL_RANGE, 16f);
        this.setDataProperty(ActorDataTypes.AMBIENT_SOUND_EVENT_NAME, "ambient");
    }

    @Override
    public float getWidth() {
        return this.isBaby() ? 0.3f : 0.6f;
    }

    @Override
    public float getHeight() {
        return this.isBaby() ? 0.95f : 1.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(20);
    }

    @Override
    public String getOriginalName() {
        return "Husk";
    }

    @Override
    public Set<String> typeFamily() {
        if (this.nbt == null || !this.getNbt().getBoolean(NBT_HUSK_RIDER)) return Set.of("husk", "zombie", "undead", "monster", "mob");
        return Set.of("husk_rider", "husk", "zombie", "undead", "monster", "mob");
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public double getFloatingForceFactor() {
        return 0;
    }

    @Override
    protected boolean transform() {
        this.saveNBT();
        Entity zombie = new EntityZombie(this.getChunk(), this.getNbt().copy().remove("Health"));
        EntityTransformEvent event = new EntityTransformEvent(this, zombie);
        server.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            zombie.close();
            return false;
        } else {
            this.close();
            zombie.spawnToAll();
            zombie.level.addSound(zombie, Sound.MOB_HUSK_CONVERT_TO_ZOMBIE);
            return true;
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source instanceof EntityDamageByEntityEvent ev) {
            Entity damager = ev.getDamager();

            if (damager instanceof EntityProjectile proj) {
                Entity shooter = proj.shootingEntity;
                if (shooter != null && shooter.riding != null && this.riding != null && this.riding == shooter.riding) {
                    ev.setCancelled(true);
                    return false;
                }
            }
        }
        return super.attack(source);
    }
}
