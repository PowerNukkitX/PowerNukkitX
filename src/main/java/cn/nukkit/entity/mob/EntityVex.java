package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.MeleeAttackExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityVex extends EntityMob implements EntityFlyable {


    @Override
    @NotNull public String getIdentifier() {
        return VEX;
    }

    public EntityVex(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Getter
    @Setter
    private EntityEvocationIllager illager;
    private int start_damage_timer = ThreadLocalRandom.current().nextInt(30, 120);


    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_VEX_AMBIENT), new RandomSoundEvaluator(), 5, 1),
                        new Behavior(new VexMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 80, Integer.MAX_VALUE)
                        ), 4, 1),
                        new Behavior(new VexMeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 40, true, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 80, Integer.MAX_VALUE)
                        ), 3, 1),
                        new Behavior(new VexMeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 40, false, 30), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_ATTACK_TIME, 80, Integer.MAX_VALUE)
                        ), 2, 1),
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(70, 0, 20),
                        new NearestTargetEntitySensor<>(0, 70, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case VILLAGER ->
                entity instanceof EntityVillager villager && !villager.isBaby();
            case IRON_GOLEM, WANDERING_TRADER -> true;
            default -> false;
        };
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(14);
        super.initEntity();
        getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_TIME, getLevel().getTick());
        this.setItemInHand(Item.get(Item.IRON_SWORD));
        this.diffHandDamage = new float[]{5.5f, 9f, 13.5f};
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            return false;
        }
        return super.attack(source);
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public String getOriginalName() {
        return "Vex";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public Item[] getDrops() {
        if(getItemInHand() instanceof ItemTool tool) {
            tool.setDamage(ThreadLocalRandom.current().nextInt(tool.getMaxDurability()));
            return new Item[] {
                tool
            };
        }
        return super.getDrops();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(closed) return true;
        if(getIllager() != null) {
            if(this.distanceSquared(illager) > 56.25d) {
                setMoveTarget(illager);
                setLookTarget(illager);
            }
            if(ticksLived%20 == 0) {
                if(ticksLived >= start_damage_timer*20) {
                    this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.AGE, 1));
                }
            }
        }
        return super.onUpdate(currentTick);
    }

    private class VexMeleeAttackExecutor extends MeleeAttackExecutor {

        public VexMeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
            super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown, 1);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            entity.setDataFlag(EntityFlag.CHARGING);
            entity.level.addSound(entity, Sound.MOB_VEX_CHARGE);
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            entity.setDataFlag(EntityFlag.CHARGING, false);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            entity.setDataFlag(EntityFlag.CHARGING, false);
        }
    }

}
