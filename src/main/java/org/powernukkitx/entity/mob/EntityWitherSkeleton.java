package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntitySmite;
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
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityWitherSkeleton extends EntityMob implements EntityWalkable, EntitySmite {

    @Override
    @NotNull
    public String getIdentifier() {
        return WITHER_SKELETON;
    }

    public EntityWitherSkeleton(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .coreBehaviors(
                        new Behavior(
                                entity -> {
                                    var storage = getMemoryStorage();
                                    if (storage.notEmpty(CoreMemoryTypes.ATTACK_TARGET)) return false;
                                    Entity attackTarget = null;
                                    if (storage.notEmpty(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET) && storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET).isAlive()) {
                                        attackTarget = storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
                                    }
                                    storage.put(CoreMemoryTypes.ATTACK_TARGET, attackTarget);
                                    return false;
                                },
                                entity -> true, 20
                        )
                )
                .behaviors(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_WITHER_AMBIENT), new RandomSoundEvaluator(), 5, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 10, Effect.get(EffectType.WITHER).setDuration(200)), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 10, Effect.get(EffectType.WITHER).setDuration(200)), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                )
                .sensors(new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                )
                .controllers(new WalkController(), new LookController(true, true))
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }


    // The Wither Skeleton will attack players, snow golems, baby turtles, iron golems, piglins, or piglin brutes within 16 tiles of it.
    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case EntityID.SNOW_GOLEM, EntityID.IRON_GOLEM,
                 EntityID.TURTLE, EntityID.PIGLIN,
                 EntityID.PIGLIN_BRUTE -> true;
            default -> false;
        };
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{5f, 8f, 12f};
        super.initEntity();
        // Determine if the Wither Skeleton is wielding a stone sword; if not, give it the stone sword.
        if (this.getItemInHand() != Item.get(Item.STONE_SWORD)) {
            this.setItemInHand(Item.get(Item.STONE_SWORD));
        }
        // Set the Withered Skeleton to play an idle sound when it's idle.
        this.setDataProperty(ActorDataTypes.AMBIENT_SOUND_EVENT_NAME, "ambient");
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 2.4f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(20);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    public String getOriginalName() {
        return "Wither Skeleton";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("wither", "monster", "undead", "skeleton", "mob");
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    // The probability of dropping a sword is 8.5%, and the probability of dropping a head is 2.5%.
    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        List<Item> drops = new ArrayList<>();
        drops.add(Item.get(Item.BONE, 0, Utils.rand(0, 2)));
        if (Utils.rand(0, 2) == 0) {
            drops.add(Item.get(Item.COAL, 0, 1));
        }
        // The probability of obtaining a stone sword is 8.5%.
        if (Utils.rand(0, 200) <= 17) {
            drops.add(Item.get(Item.STONE_SWORD, Utils.rand(0, 131), 1));
        }
        // The probability of losing your head is 2.5%.
        if (Utils.rand(0, 40) == 1) {
            drops.add(Item.get(BlockID.SKULL, 1, 1));
        }
        return drops.toArray(Item.EMPTY_ARRAY);
    }
}
