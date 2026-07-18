package org.powernukkitx.entity.mob;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LiftController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.BlazeShootExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.FlyingPosEvaluator;
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

/**
 * @author PikyCZ, Buddelbubi
 */
public class EntityBlaze extends EntityMob implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return BLAZE;
    }

    public EntityBlaze(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_BLAZE_BREATHE), new RandomSoundEvaluator(), 5, 1),
                        new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 1, false, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_PLAYER, 1)
                        ), 4, 1),
                        new Behavior(new BlazeShootExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 100, 40), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 3, 1),
                        new Behavior(new BlazeShootExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 100, 40), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20)),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{4f, 6f, 9f};
        super.initEntity();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        EntityDamageEvent.DamageCause cause = source.getCause();
        if (cause == EntityDamageEvent.DamageCause.LAVA
                || cause == EntityDamageEvent.DamageCause.HOT_FLOOR
                || cause == EntityDamageEvent.DamageCause.FIRE
                || cause == EntityDamageEvent.DamageCause.FIRE_TICK)
            return false;
        return super.attack(source);
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(20);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.23f);
    }

    @Override
    public String getOriginalName() {
        return "Blaze";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("blaze", "monster", "mob");
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public int getFrostbiteInjury() {
        return 5;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        float chance = 0.5f + (0.25f * looting);
        chance = Math.min(chance, 1.0f);

        if (Utils.rand(0f, 1f) < chance) {
            int amount = Utils.rand(1, 1 + looting);
            return new Item[]{Item.get(Item.BLAZE_ROD, 0, amount)};
        }

        return Item.EMPTY_ARRAY;
    }

    @Override
    public Integer getExperienceDrops() {
        return 10;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(currentTick % 10 == 0) {
            if(level.isRaining() && !this.isUnderBlock()) {
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.WEATHER, 1));
            }
        }
        return super.onUpdate(currentTick);
    }
}
