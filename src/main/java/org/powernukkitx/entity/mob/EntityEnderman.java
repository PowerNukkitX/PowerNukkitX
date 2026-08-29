package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.EndermanBlockExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.StaringAttackTargetExecutor;
import org.powernukkitx.entity.ai.executor.TeleportExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestEntitySensor;
import org.powernukkitx.entity.ai.sensor.PlayerStaringSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        return BehaviorGroup.builder(this)
                .coreBehaviors(
                        new Behavior(new StaringAttackTargetExecutor(), none(), 1, 1, 1, true)
                )
                .behaviors(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ENDERMEN_IDLE, 0.8f, 1.2f, 1, 1), all(not(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET)), new RandomSoundEvaluator()), 6, 1, 1, true),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_ENDERMEN_SCREAM, 0.8f, 1.2f, 1, 1), all(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), new RandomSoundEvaluator(10, 7)), 5, 1, 1, true),
                        new Behavior(new TeleportExecutor(16, 5, 16), any(
                                all(entity -> getLevel().isRaining(),
                                        entity -> !isUnderBlock(),
                                        entity -> getLevel().getTick()%10 == 0),
                                entity -> hasWaterAt(0),
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
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
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
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                )
                .sensors(
                        new PlayerStaringSensor(64, 20, false),
                        new NearestEntitySensor(EntityEndermite.class, CoreMemoryTypes.NEAREST_ENDERMITE, 64, 0)
                )
                .controllers(new WalkController(), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    @Override
    protected void initEntity() {
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
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(40);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        float behaviorMovement = this.isAngry() ? 0.45f : 0.3f;
        return MovementComponent.value(behaviorMovement);
    }

    @Override
    public String getOriginalName() {
        return "Enderman";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("enderman", "monster", "mob");
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return this.getDataFlag(ActorFlags.ANGRY);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        float pearlChance = 0.5f + (0.05f * looting);
        pearlChance = Math.min(pearlChance, 1.0f);

        if (Utils.rand(0f, 1f) < pearlChance) {
            int amount = Utils.rand(1, 1 + looting);
            drops.add(Item.get(Item.ENDER_PEARL, 0, amount));
        }

        Item hand = getItemInHand();
        if (!hand.isNull()) {
            drops.add(hand);
        }

        return drops.toArray(Item.EMPTY_ARRAY);
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
