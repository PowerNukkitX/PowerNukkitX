package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.BlazeShootExecutor;
import cn.nukkit.entity.ai.executor.GhastShootExecutor;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.projectile.EntityFireball;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_GHAST_MOAN), new RandomSoundEvaluator(), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(
                        new Behavior(new GhastShootExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 64, true, 60, 10), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 2, 1),
                        new Behavior(new GhastShootExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 28, true, 60, 10), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(64, 0, 20)),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
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
    public String getOriginalName() {
        return "Ghast";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{
                Item.get(Item.GHAST_TEAR, 0, Utils.rand(0, 1)),
                Item.get(Item.GUNPOWDER, 0, Utils.rand(0, 2))
        };
    }

    @Override
    public Integer getExperienceDrops() {
        return 5;
    }
}
