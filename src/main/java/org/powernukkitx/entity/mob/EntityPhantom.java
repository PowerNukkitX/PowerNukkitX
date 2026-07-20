package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntitySmite;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LiftController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.CircleAboveTargetExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.FlyingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author PetteriM1
 */
public class EntityPhantom extends EntityMob implements EntityFlyable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return PHANTOM;
    }

    public EntityPhantom(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PHANTOM_IDLE, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator()), 6, 1),
                        new Behavior(new CircleAboveTargetExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, true), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.LAST_ATTACK_ENTITY),
                                not(new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, Utils.rand(200, 400)))
                        ), 5, 1),
                        new Behavior(new CircleAboveTargetExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.4f, true), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.LAST_ATTACK_ENTITY),
                                not(new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, Utils.rand(200, 400)))
                        ), 4, 1),
                        new Behavior(new PhantomMeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.5f, 64, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 3, 1),
                        new Behavior(new PhantomMeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.5f, 64, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(64, 0, 20),
                        new NearestTargetEntitySensor<>(0, 64, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    @Override
    public boolean isEnablePitch() {
        return false;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.diffHandDamage = new float[]{4f, 6f, 9f};

    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(20);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    public String getOriginalName() {
        return "Phantom";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("phantom", "undead", "monster", "mob");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 1) == 0) {
            return Item.EMPTY_ARRAY;
        }

        int amount = Utils.rand(0, 1 + looting);
        if (amount <= 0) {
            return Item.EMPTY_ARRAY;
        }

        return new Item[]{
                Item.get(ItemID.PHANTOM_MEMBRANE, 0, amount)
        };
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
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }

    private static class PhantomMeleeAttackExecutor extends MeleeAttackExecutor {

        public PhantomMeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
            super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown, 2.5f);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            entity.level.addSound(entity, Sound.MOB_PHANTOM_SWOOP);
        }
    }
}
