package org.powernukkitx.entity.mob;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LiftController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.GhastShootExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.FlyingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.projectile.EntityFireball;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityGhast extends EntityMob implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return GHAST;
    }

    public EntityGhast(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .coreBehaviors(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_GHAST_MOAN), new RandomSoundEvaluator(), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 200, false, -1, true, 10), none(), 1, 1)
                )
                .behaviors(
                        new Behavior(new GhastShootExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 64, true, 60, 10), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 2, 1),
                        new Behavior(new GhastShootExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 28, true, 60, 10), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 1, 1)
                )
                .sensors(new NearestPlayerSensor(64, 0, 20))
                .controllers(new SpaceMoveController(), new LookController(true, true), new LiftController())
                .routeFinder(new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this))
                .build();
    }

    @Override
    public void kill() {
        Arrays.stream(getLevel().getEntities()).filter(entity -> {
            if(entity instanceof EntityFireball fireball) {
                return fireball.shootingEntity == this && !fireball.closed;
            }
            return false;
        }).forEach(Entity::close);
        super.kill();
    }

    @Override
    public float getWidth() {
        return 4;
    }

    @Override
    public float getHeight() {
        return 4;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(10);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.3f);
    }

    @Override
    public String getOriginalName() {
        return "Ghast";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("ghast", "monster", "mob");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        List<Item> drops = new ArrayList<>();

        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        if (Utils.rand(0, 1) == 1) {
            int amount = Utils.rand(0, 1 + looting);
            if (amount > 0) {
                drops.add(Item.get(Item.GHAST_TEAR, 0, amount));
            }
        }

        if (Utils.rand(0, 2) != 0) {
            int amount = Utils.rand(0, 2 + looting);
            if (amount > 0) {
                drops.add(Item.get(Item.GUNPOWDER, 0, amount));
            }
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

}
