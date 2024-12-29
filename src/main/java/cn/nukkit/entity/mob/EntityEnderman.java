package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AttackCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.PlayerCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.EndermanBlockExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.StaringAttackTargetExecutor;
import cn.nukkit.entity.ai.executor.TeleportExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.ai.sensor.PlayerStaringSensor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityEnderman extends EntityMob implements EntityWalkable {

    @Override
    @NotNull
    public String getIdentifier() {
        return ENDERMAN;
    }

    public EntityEnderman(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new StaringAttackTargetExecutor(), (entity -> true), 1, 1, 1, true)
                ),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ENDERMEN_IDLE, 0.8f, 1.2f, 1, 1), all(
                                entity -> getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET),
                                entity -> getLevel().getTick()%10 == 0,
                                new ProbabilityEvaluator(1, 10)
                        ), 6, 1, 1, true),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ENDERMEN_SCREAM, 0.8f, 1.2f, 1, 1), all(
                                entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET),
                                entity -> getLevel().getTick()%10 == 0,
                                new ProbabilityEvaluator(1, 7)
                        ), 5, 1, 1, true),
                        new Behavior(new TeleportExecutor(16, 5, 16), any(
                                all(entity -> getLevel().isRaining(),
                                        entity -> !isUnderBlock(),
                                        entity -> getLevel().getTick()%10 == 0),
                                entity -> isInsideOfWater(),
                                all(
                                        entity -> getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET),
                                        entity -> getLevel().getTick()%20==0,
                                        new ProbabilityEvaluator(2, 25)),
                                all(
                                        entity -> !getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET),
                                        new ProbabilityEvaluator(1, 20),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 10)
                                )
                        ), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.45f, 64, true, 30), all(
                                new AttackCheckEvaluator(),
                                any(
                                        entity -> getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof Player holder && holder.getInventory() != null && !holder.getInventory().getHelmet().getId().equals(Block.CARVED_PUMPKIN),
                                        entity -> getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET) instanceof EntityIntelligent
                                )
                        ), 3, 1),
                        new Behavior(new EndermanBlockExecutor(), all(
                                entity -> getLevel().getGameRules().getBoolean(GameRule.MOB_GRIEFING),
                                entity -> getLevel().getTick()%60 == 0,
                                new ProbabilityEvaluator(1,20)
                        ), 2, 1, 1, true),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(
                        new PlayerStaringSensor(64, 30),
                        new NearestEntitySensor(EntityEndermite.class, CoreMemoryTypes.NEAREST_ENDERMITE, 64, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(40);
        this.diffHandDamage = new float[]{4f, 7f, 10f};
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public String getOriginalName() {
        return "Enderman";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return this.getDataFlag(EntityFlag.ANGRY);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.ENDER_PEARL, 0, Utils.rand(0, 1)), getItemInHand()};
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if(source instanceof EntityDamageByEntityEvent event) {
                if(event.getDamager() instanceof EntityProjectile projectile){
                    getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, projectile.shootingEntity);
                }
            }
            new TeleportExecutor(16, 5, 16).execute(this);
            source.setCancelled();
            return false;
        }
        return super.attack(source);
    }
}
